package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"
)

type app struct {
	db       *sql.DB
	cfg      config
	sessions *sessionStore
}

func newApp(db *sql.DB, cfg config) *app {
	return &app{db: db, cfg: cfg, sessions: newSessionStore(cfg.tokenTimeout)}
}

type apiError struct {
	code    int
	message string
}

func (e *apiError) Error() string   { return e.message }
func business(message string) error { return &apiError{500, message} }
func invalid(message string) error  { return &apiError{400, message} }
func success(data any) result       { return result{0, "success", data} }

type handler func(http.ResponseWriter, *http.Request, *user) (result, error)

func (a *app) routes() http.Handler {
	mux := http.NewServeMux()
	public := func(pattern string, h handler) { mux.HandleFunc(pattern, a.handle(h, false)) }
	login := func(pattern string, h handler, roles ...string) { mux.HandleFunc(pattern, a.handle(h, true, roles...)) }
	public("POST /api/v1/auth/login", a.login)
	public("POST /api/v1/auth/register", a.register)
	login("POST /api/v1/auth/logout", a.logout)
	login("POST /api/v1/auth/change-password", a.changePassword)
	login("GET /api/v1/admin/classes", a.listClasses, "admin")
	login("POST /api/v1/admin/classes", a.createClass, "admin")
	login("GET /api/v1/admin/classes/{id}", a.getClass, "admin")
	login("PUT /api/v1/admin/classes/{id}", a.updateClass, "admin")
	login("DELETE /api/v1/admin/classes/{id}", a.deleteClass, "admin")
	login("GET /api/v1/classes/list", a.listClasses, "admin", "user")
	login("GET /api/v1/classes/detail/{id}", a.getClass, "admin", "user")
	login("GET /api/v1/students", a.listStudents, "admin")
	login("POST /api/v1/students", a.createStudent, "admin")
	login("GET /api/v1/students/{id}", a.getStudent, "admin")
	login("PUT /api/v1/students/{id}", a.updateStudent, "admin")
	login("DELETE /api/v1/students/{id}", a.deleteStudent, "admin")
	login("GET /api/v1/students/statistics/summary", a.statistics, "admin", "user")
	login("GET /api/v1/profile/me", a.myProfile, "admin", "user")
	login("POST /api/v1/profile", a.createProfile, "admin", "user")
	login("PUT /api/v1/profile", a.updateProfile, "admin", "user")
	login("GET /api/v1/square", a.square)
	login("POST /api/v1/admin/change-password", a.adminChangePassword, "admin")
	login("POST /api/v1/admin/set-admin", a.setAdmin, "admin")
	mux.HandleFunc("GET /v3/api-docs", a.apiDocs)
	mux.HandleFunc("GET /swagger-ui.html", a.swagger)
	mux.HandleFunc("GET /swagger-ui/index.html", a.swagger)
	mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		writeResult(w, http.StatusNotFound, result{404, "接口不存在", nil})
	})
	return mux
}

func (a *app) handle(h handler, authenticated bool, roles ...string) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Cache-Control", "no-store")
		defer func() {
			if recovered := recover(); recovered != nil {
				log.Printf("请求处理异常: %s %s", r.Method, r.URL.Path)
				writeResult(w, http.StatusOK, result{500, "系统繁忙，请稍后再试", nil})
			}
		}()
		ctx, cancel := context.WithTimeout(r.Context(), 10*time.Second)
		defer cancel()
		r = r.WithContext(ctx)
		var current *user
		var err error
		if authenticated {
			current, err = a.authenticate(r)
			if err == nil && len(roles) > 0 {
				allowed := false
				for _, role := range roles {
					if current.Role == role {
						allowed = true
						break
					}
				}
				if !allowed {
					err = &apiError{403, "权限不足，你没有该角色权限，无法执行此操作"}
				}
			}
		}
		response := success(nil)
		if err == nil {
			response, err = h(w, r, current)
		}
		if err != nil {
			var api *apiError
			if errors.As(err, &api) {
				response = result{api.code, api.message, nil}
			} else {
				log.Printf("数据库或系统异常: %s %s: %v", r.Method, r.URL.Path, err)
				response = result{500, "系统繁忙，请稍后再试", nil}
			}
		}
		// 原 GlobalExceptionHandler 使用 HTTP 200 + 业务码；广场的 code=401 不能触发前端强制登出。
		writeResult(w, http.StatusOK, response)
	}
}

func writeResult(w http.ResponseWriter, status int, response result) {
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.WriteHeader(status)
	if err := json.NewEncoder(w).Encode(response); err != nil {
		log.Printf("响应写入失败: %v", err)
	}
}

func decode(w http.ResponseWriter, r *http.Request, value any) error {
	r.Body = http.MaxBytesReader(w, r.Body, 1<<20)
	decoder := json.NewDecoder(r.Body)
	if err := decoder.Decode(value); err != nil {
		return invalid("请求数据格式错误")
	}
	var extra any
	if err := decoder.Decode(&extra); err != io.EOF {
		return invalid("请求数据格式错误")
	}
	return nil
}

func pathID(r *http.Request) (int64, error) {
	id, err := strconv.ParseInt(r.PathValue("id"), 10, 64)
	if err != nil || id <= 0 {
		return 0, invalid("ID 必须为正整数")
	}
	return id, nil
}

func pagination(r *http.Request) (int, int, error) {
	parse := func(key string, fallback int) (int, error) {
		value := r.URL.Query().Get(key)
		if value == "" {
			return fallback, nil
		}
		n, err := strconv.Atoi(value)
		if err != nil {
			return 0, invalid(key + " 必须为整数")
		}
		return n, nil
	}
	page, err := parse("page", 0)
	if err != nil {
		return 0, 0, err
	}
	size, err := parse("size", 10)
	if err != nil {
		return 0, 0, err
	}
	if page < 0 || size < 1 || int64(page) > (1<<63-1)/int64(size) {
		return 0, 0, invalid("页码不能为负数，每页大小必须为正整数")
	}
	return page, size, nil
}

func parseDateTime(value string, loc *time.Location) (time.Time, error) {
	for _, layout := range []string{"2006-01-02T15:04:05.999999999", "2006-01-02T15:04", "2006-01-02 15:04:05", time.RFC3339Nano} {
		if parsed, err := time.ParseInLocation(layout, value, loc); err == nil {
			return parsed.In(loc), nil
		}
	}
	return time.Time{}, invalid("创建时间格式错误")
}

func blank(value string) bool { return strings.TrimSpace(value) == "" }

func (a *app) now() time.Time { return time.Now().In(a.cfg.location).Truncate(time.Second) }

func requireChanged(res sql.Result, message string) error {
	count, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if count < 1 {
		return business(message)
	}
	return nil
}
