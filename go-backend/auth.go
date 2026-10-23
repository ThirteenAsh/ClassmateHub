package main

import (
	"database/sql"
	"errors"
	"net/http"
	"unicode/utf16"
	"unicode/utf8"

	"github.com/go-sql-driver/mysql"
)

func (a *app) authenticate(r *http.Request) (*user, error) {
	unauthorized := &apiError{401, "未登录，请先登录"}
	id, ok := a.sessions.lookup(requestToken(r))
	if !ok {
		return nil, unauthorized
	}
	u := &user{}
	err := a.db.QueryRowContext(r.Context(), "SELECT id, username, role FROM users WHERE id = ?", id).Scan(&u.ID, &u.Username, &u.Role)
	if errors.Is(err, sql.ErrNoRows) {
		a.sessions.revoke(id)
		return nil, unauthorized
	}
	if err != nil {
		return nil, err
	}
	return u, nil
}

type credentials struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func (a *app) login(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body credentials
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	if blank(body.Username) || body.Password == "" {
		return result{-1, "用户名/密码错误！", nil}, nil
	}
	u := user{}
	// 按原 mapper 使用数据库的用户名和密码比较规则，保留现有明文密码。
	err := a.db.QueryRowContext(r.Context(), "SELECT id, username, role FROM users WHERE username = ? AND password = ?", body.Username, body.Password).Scan(&u.ID, &u.Username, &u.Role)
	if errors.Is(err, sql.ErrNoRows) {
		return result{-1, "用户名/密码错误！", nil}, nil
	}
	if err != nil {
		return result{}, err
	}
	token, expires, err := a.sessions.issue(u.ID)
	if err != nil {
		return result{}, err
	}
	a.setCookie(w, token, expires)
	w.Header().Set("satoken", token)
	return success(struct {
		ID       int64  `json:"id"`
		Username string `json:"username"`
		Role     string `json:"role"`
		Token    string `json:"token"`
	}{u.ID, u.Username, u.Role, token}), nil
}

func (a *app) register(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body credentials
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	if blank(body.Username) {
		return result{}, invalid("用户名不能为空")
	}
	if len(utf16.Encode([]rune(body.Username))) > 20 {
		return result{}, invalid("用户名长度必须在1-20个字符之间")
	}
	if blank(body.Password) {
		return result{}, invalid("密码不能为空")
	}
	length := len(utf16.Encode([]rune(body.Password)))
	if length < 6 || length > 20 {
		return result{}, invalid("密码长度必须在6-20个字符之间")
	}
	res, err := a.db.ExecContext(r.Context(), "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')", body.Username, body.Password)
	if err != nil {
		var duplicate *mysql.MySQLError
		if errors.As(err, &duplicate) && duplicate.Number == 1062 {
			return result{}, &apiError{1001, "用户名已存在"}
		}
		return result{}, err
	}
	id, err := res.LastInsertId()
	if err != nil {
		return result{}, err
	}
	return result{0, "注册成功", map[string]any{"userId": id, "username": body.Username}}, nil
}

func (a *app) logout(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	a.sessions.revoke(u.ID)
	http.SetCookie(w, &http.Cookie{Name: "satoken", Path: "/", Value: "", HttpOnly: true,
		Secure: a.cfg.cookieSecure, SameSite: http.SameSiteLaxMode, MaxAge: -1})
	return success("退出成功"), nil
}

func validatePassword(password string) error {
	if blank(password) {
		return invalid("新密码不能为空")
	}
	// 原前端的修改密码表单要求至少 6 位；列最大为 255 个字符。
	if utf8.RuneCountInString(password) < 6 || utf8.RuneCountInString(password) > 255 {
		return invalid("新密码长度必须在6-255个字符之间")
	}
	return nil
}

func (a *app) changePassword(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	var body struct {
		Old string `json:"oldPassword"`
		New string `json:"newPassword"`
	}
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	if err := validatePassword(body.New); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	var password string
	if err := tx.QueryRowContext(r.Context(), "SELECT password FROM users WHERE id = ? FOR UPDATE", u.ID).Scan(&password); err != nil {
		return result{}, err
	}
	if password != body.Old {
		return result{}, business("原密码错误")
	}
	if body.Old == body.New {
		return result{}, business("新密码不能与原密码相同")
	}
	if _, err := tx.ExecContext(r.Context(), "UPDATE users SET password = ? WHERE id = ?", body.New, u.ID); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success("修改密码成功"), nil
}

func (a *app) adminChangePassword(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body struct {
		StudentID int64  `json:"studentId"`
		New       string `json:"newPassword"`
	}
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	if err := validatePassword(body.New); err != nil {
		return result{}, err
	}
	return a.updateStudentUser(r, body.StudentID, "UPDATE users SET password = ? WHERE id = ?", body.New, "密码修改成功")
}

func (a *app) setAdmin(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body struct {
		StudentID int64 `json:"studentId"`
	}
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	return a.updateStudentUser(r, body.StudentID, "UPDATE users SET role = ? WHERE id = ?", "admin", "设置管理员成功")
}

func (a *app) updateStudentUser(r *http.Request, studentID int64, query, value, message string) (result, error) {
	if studentID <= 0 {
		return result{}, invalid("学生 ID 必须为正整数")
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	var userID sql.NullInt64
	err = tx.QueryRowContext(r.Context(), "SELECT user_id FROM student_profile WHERE id = ? FOR UPDATE", studentID).Scan(&userID)
	if errors.Is(err, sql.ErrNoRows) {
		return result{}, business("学生不存在")
	}
	if err != nil {
		return result{}, err
	}
	if !userID.Valid || userID.Int64 <= 0 {
		return result{}, business("学生未关联用户")
	}
	res, err := tx.ExecContext(r.Context(), query, value, userID.Int64)
	if err != nil {
		return result{}, err
	}
	if err := requireChanged(res, "学生关联的用户不存在"); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(message), nil
}
