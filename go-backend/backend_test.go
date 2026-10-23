package main

import (
	"bytes"
	"crypto/rand"
	"database/sql"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"os"
	"regexp"
	"strings"
	"testing"
	"time"

	"github.com/go-sql-driver/mysql"
)

type testResponse struct {
	Code    int             `json:"code"`
	Message string          `json:"message"`
	Data    json.RawMessage `json:"data"`
}
type fixture struct {
	db      *sql.DB
	app     *app
	handler http.Handler
}

// 必须显式提供测试连接；只创建/删除 classmatehub_go_test_* 临时库。
// 测试的业务表从原 SQL 提取，避免用自建测试结构掩盖兼容问题。
func newFixture(t *testing.T) *fixture {
	t.Helper()
	dsn := os.Getenv("TEST_MYSQL_DSN")
	if dsn == "" {
		t.Skip("未设置 TEST_MYSQL_DSN，跳过 MySQL 集成测试")
	}
	configuration, err := mysql.ParseDSN(dsn)
	if err != nil {
		t.Fatal("TEST_MYSQL_DSN 格式错误")
	}
	configuration.DBName = ""
	configuration.ParseTime = true
	configuration.ClientFoundRows = true
	configuration.Loc = time.FixedZone("Asia/Shanghai", 8*60*60)
	configuration.Params = map[string]string{"charset": "utf8mb4"}
	admin, err := sql.Open("mysql", configuration.FormatDSN())
	if err != nil {
		t.Fatal(err)
	}
	var suffix [6]byte
	if _, err := rand.Read(suffix[:]); err != nil {
		t.Fatal(err)
	}
	name := "classmatehub_go_test_" + hex.EncodeToString(suffix[:])
	if _, err := admin.Exec("CREATE DATABASE `" + name + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"); err != nil {
		admin.Close()
		t.Fatal(err)
	}
	t.Cleanup(func() {
		if _, err := admin.Exec("DROP DATABASE `" + name + "`"); err != nil {
			t.Error(err)
		}
		admin.Close()
	})
	configuration.DBName = name
	db, err := sql.Open("mysql", configuration.FormatDSN())
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { db.Close() })
	schema, err := os.ReadFile("../docs/classmatehub.sql")
	if err != nil {
		t.Fatal(err)
	}
	creates := regexp.MustCompile("(?s)CREATE TABLE .*?;").FindAllString(string(schema), -1)
	if len(creates) != 3 {
		t.Fatal("原 SQL 应包含三张业务表")
	}
	for _, statement := range creates {
		mustExec(t, db, statement)
	}
	mustExec(t, db, `INSERT INTO clazz (id, name, description, create_time) VALUES
		(1, '一班', NULL, '2020-01-02 03:04:05'), (2, '二班', '二班描述', '2020-01-03 03:04:05'), (12, '其他', '', '2020-01-04 03:04:05')`)
	// 管理员直接写入 users，明确不创建管理员档案。
	mustExec(t, db, `INSERT INTO users (id, username, password, role) VALUES
		(1000, 'admin', 'admin123', 'admin'), (2001, 'alpha', 'pass123', 'user'),
		(2002, 'beta', 'pass123', 'user'), (2003, 'gamma', 'pass123', 'user'),
		(2004, 'other', 'pass123', 'user'), (2005, 'empty', 'pass123', 'user')`)
	mustExec(t, db, `INSERT INTO student_profile
		(id, user_id, clazz_id, name, gender, birth_date, phone, university, qq, email, message) VALUES
		(3001, 2001, 1, '张甲', '男', '2000-02-29', '13800000001', '甲大学', '12345', NULL, NULL),
		(3002, 2002, 1, '张乙', '女', '2001-03-01', '13800000002', '乙大学', '23456', '', ''),
		(3003, 2003, 2, '李丙', '女', '2002-04-01', '13800000003', '丙大学', '34567', NULL, NULL),
		(3004, 2004, 12, '王丁', '男', '2003-05-01', '13800000004', '丁大学', '45678', NULL, NULL)`)
	cfg := config{location: configuration.Loc, tokenTimeout: 30 * 24 * time.Hour}
	a := newApp(db, cfg)
	return &fixture{db, a, a.routes()}
}

func mustExec(t *testing.T, db *sql.DB, query string, args ...any) {
	t.Helper()
	if _, err := db.Exec(query, args...); err != nil {
		t.Fatal(err)
	}
}

func (f *fixture) request(t *testing.T, method, path, token string, body any) (testResponse, *httptest.ResponseRecorder) {
	t.Helper()
	var data []byte
	var err error
	if body != nil {
		data, err = json.Marshal(body)
		if err != nil {
			t.Fatal(err)
		}
	}
	r := httptest.NewRequest(method, path, bytes.NewReader(data))
	r.Header.Set("Content-Type", "application/json")
	if token != "" {
		r.Header.Set("Authorization", "Bearer "+token)
	}
	w := httptest.NewRecorder()
	f.handler.ServeHTTP(w, r)
	var response testResponse
	if err := json.Unmarshal(w.Body.Bytes(), &response); err != nil {
		t.Fatalf("响应不是 JSON: %s", w.Body.String())
	}
	if w.Code != 200 {
		t.Fatalf("意外 HTTP 状态 %d: %s", w.Code, w.Body.String())
	}
	return response, w
}

func (f *fixture) login(t *testing.T, username, password string) string {
	t.Helper()
	response, _ := f.request(t, "POST", "/api/v1/auth/login", "", credentials{username, password})
	assertCode(t, response, 0)
	var data struct {
		Token string `json:"token"`
	}
	if err := json.Unmarshal(response.Data, &data); err != nil || data.Token == "" {
		t.Fatal("登录未返回 token")
	}
	return data.Token
}

func assertCode(t *testing.T, response testResponse, expected int) {
	t.Helper()
	if response.Code != expected {
		t.Fatalf("业务码 %d，期望 %d，消息 %s", response.Code, expected, response.Message)
	}
}

func dataAs[T any](t *testing.T, response testResponse) T {
	t.Helper()
	var data T
	if err := json.Unmarshal(response.Data, &data); err != nil {
		t.Fatal(err)
	}
	return data
}

func flatProfile(classID int64, name string) map[string]any {
	return map[string]any{"clazzId": classID, "name": name, "gender": "男", "birthDate": "2000-02-29",
		"phone": "13812345678", "university": "测试大学", "qq": "123456"}
}

func nestedStudent(classID int64, name string) map[string]any {
	return map[string]any{"clazzId": classID,
		"basic":   map[string]any{"name": name, "gender": "男", "birthDate": "2000-02-29", "university": "测试大学"},
		"contact": map[string]any{"phone": "13812345678", "qq": "123456"}}
}

func TestAuthorization(t *testing.T) {
	f := newFixture(t)
	u := f.login(t, "alpha", "pass123")
	adminRoutes := []struct{ method, path string }{
		{"GET", "/api/v1/admin/classes"}, {"POST", "/api/v1/admin/classes"},
		{"GET", "/api/v1/admin/classes/1"}, {"PUT", "/api/v1/admin/classes/1"}, {"DELETE", "/api/v1/admin/classes/1"},
		{"GET", "/api/v1/students"}, {"POST", "/api/v1/students"},
		{"GET", "/api/v1/students/3001"}, {"PUT", "/api/v1/students/3001"}, {"DELETE", "/api/v1/students/3001"},
		{"POST", "/api/v1/admin/change-password"}, {"POST", "/api/v1/admin/set-admin"},
	}
	for _, route := range adminRoutes {
		t.Run(route.method+route.path, func(t *testing.T) {
			response, _ := f.request(t, route.method, route.path, "", nil)
			assertCode(t, response, 401)
			response, _ = f.request(t, route.method, route.path, u, nil)
			assertCode(t, response, 403)
		})
	}
	for _, route := range []struct{ method, path string }{
		{"GET", "/api/v1/classes/list"}, {"GET", "/api/v1/classes/detail/1"}, {"GET", "/api/v1/students/statistics/summary"},
		{"GET", "/api/v1/profile/me"}, {"POST", "/api/v1/profile"}, {"PUT", "/api/v1/profile"},
		{"GET", "/api/v1/square"}, {"POST", "/api/v1/auth/change-password"}, {"POST", "/api/v1/auth/logout"},
	} {
		response, _ := f.request(t, route.method, route.path, "", nil)
		assertCode(t, response, 401)
	}
	var role, password string
	if err := f.db.QueryRow("SELECT role, password FROM users WHERE id=2001").Scan(&role, &password); err != nil {
		t.Fatal(err)
	}
	if role != "user" || password != "pass123" {
		t.Fatal("越权请求改变了用户数据")
	}
}

func TestAdminWithoutProfileAndSquarePrivacy(t *testing.T) {
	f := newFixture(t)
	admin := f.login(t, "admin", "admin123")
	response, _ := f.request(t, "GET", "/api/v1/profile/me", admin, nil)
	assertCode(t, response, 0)
	if string(response.Data) != "null" {
		t.Fatal("不应给管理员生成档案")
	}
	response, _ = f.request(t, "GET", "/api/v1/square", admin, nil)
	assertCode(t, response, 0)
	all := dataAs[[]map[string]any](t, response)
	if len(all) != 4 {
		t.Fatal("无档案管理员应能看到所有班级的四份档案")
	}
	for _, item := range all {
		if len(item) != 7 {
			t.Fatalf("广场包含多余的私密字段: %v", item)
		}
	}
	alpha := f.login(t, "alpha", "pass123")
	response, _ = f.request(t, "GET", "/api/v1/square?classId=2&userId=2003", alpha, nil)
	assertCode(t, response, 0)
	classmates := dataAs[[]map[string]any](t, response)
	if len(classmates) != 2 {
		t.Fatal("普通用户必须仅看到本班")
	}
	for _, item := range classmates {
		if item["username"] != "alpha" && item["username"] != "beta" {
			t.Fatal("泄露了其他班级信息")
		}
	}
	other := f.login(t, "other", "pass123")
	response, _ = f.request(t, "GET", "/api/v1/square", other, nil)
	assertCode(t, response, 401)
	if !strings.Contains(response.Message, "其他") {
		t.Fatal("其他班级提示丢失")
	}
	empty := f.login(t, "empty", "pass123")
	response, _ = f.request(t, "GET", "/api/v1/square", empty, nil)
	assertCode(t, response, 500)
	if response.Message != "请先完善个人信息" {
		t.Fatal(response.Message)
	}
}

func TestAuthAndPlaintextPasswords(t *testing.T) {
	f := newFixture(t)
	for _, body := range []credentials{{"", "123456"}, {"new", "123"}, {strings.Repeat("a", 21), "123456"}} {
		response, _ := f.request(t, "POST", "/api/v1/auth/register", "", body)
		assertCode(t, response, 400)
	}
	response, _ := f.request(t, "POST", "/api/v1/auth/register", "", map[string]any{"username": "new", "password": "明文123456", "role": "admin"})
	assertCode(t, response, 0)
	if response.Message != "注册成功" {
		t.Fatal(response.Message)
	}
	var password, role string
	if err := f.db.QueryRow("SELECT password, role FROM users WHERE username='new'").Scan(&password, &role); err != nil {
		t.Fatal(err)
	}
	if password != "明文123456" || role != "user" {
		t.Fatal("必须明文存储并固定注册为 user")
	}
	response, _ = f.request(t, "POST", "/api/v1/auth/register", "", credentials{"NEW", "123456"})
	assertCode(t, response, 1001)
	response, _ = f.request(t, "POST", "/api/v1/auth/login", "", credentials{"new", "wrong"})
	assertCode(t, response, -1)
	token := f.login(t, "new", "明文123456")
	response, recorder := f.request(t, "POST", "/api/v1/auth/login", "", credentials{"new", "明文123456"})
	assertCode(t, response, 0)
	if dataAs[map[string]any](t, response)["token"] != token {
		t.Fatal("有效会话应复用 token")
	}
	if len(recorder.Result().Cookies()) != 1 || recorder.Result().Cookies()[0].Name != "satoken" {
		t.Fatal("未设置兼容的 satoken Cookie")
	}
	for _, body := range []map[string]string{{"oldPassword": "wrong", "newPassword": "new123"}, {"oldPassword": "明文123456", "newPassword": "明文123456"}} {
		response, _ = f.request(t, "POST", "/api/v1/auth/change-password", token, body)
		assertCode(t, response, 500)
	}
	response, _ = f.request(t, "POST", "/api/v1/auth/change-password", token, map[string]string{"oldPassword": "明文123456", "newPassword": "new123"})
	assertCode(t, response, 0)
	if err := f.db.QueryRow("SELECT password FROM users WHERE username='new'").Scan(&password); err != nil {
		t.Fatal(err)
	}
	if password != "new123" {
		t.Fatal("修改后必须仍为明文密码")
	}
	response, _ = f.request(t, "POST", "/api/v1/auth/login", "", credentials{"new", "明文123456"})
	assertCode(t, response, -1)
	f.login(t, "new", "new123")
	response, _ = f.request(t, "POST", "/api/v1/auth/logout", token, map[string]any{})
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", "/api/v1/profile/me", token, nil)
	assertCode(t, response, 401)
}

func TestProfileOwnershipAndUpdates(t *testing.T) {
	f := newFixture(t)
	alpha := f.login(t, "alpha", "pass123")
	response, _ := f.request(t, "PUT", "/api/v1/profile", alpha, map[string]any{"userId": 2002, "id": 3002, "name": "张甲更新", "email": ""})
	assertCode(t, response, 0)
	p := dataAs[profile](t, response)
	if p.ID != 3001 || p.UserID == nil || *p.UserID != 2001 || p.Name != "张甲更新" || p.Phone != "13800000001" {
		t.Fatal("局部更新或所属账号错误")
	}
	var otherName string
	if err := f.db.QueryRow("SELECT name FROM student_profile WHERE id=3002").Scan(&otherName); err != nil {
		t.Fatal(err)
	}
	if otherName != "张乙" {
		t.Fatal("修改了其他用户的档案")
	}
	response, _ = f.request(t, "PUT", "/api/v1/profile", alpha, map[string]any{"clazzId": 2})
	assertCode(t, response, 500)
	if response.Message != "班级信息不可修改，请联系管理员" {
		t.Fatal(response.Message)
	}
	response, _ = f.request(t, "POST", "/api/v1/profile", alpha, flatProfile(1, "重复"))
	assertCode(t, response, 500)
	empty := f.login(t, "empty", "pass123")
	response, _ = f.request(t, "PUT", "/api/v1/profile", empty, map[string]any{"name": "不存在"})
	assertCode(t, response, 500)
	response, _ = f.request(t, "POST", "/api/v1/profile", empty, flatProfile(999, "新同学"))
	assertCode(t, response, 500)
	response, _ = f.request(t, "POST", "/api/v1/profile", empty, flatProfile(2, "新同学"))
	assertCode(t, response, 0)
	p = dataAs[profile](t, response)
	if p.UserID == nil || *p.UserID != 2005 || p.BirthDate != "2000-02-29" || p.Email != nil {
		t.Fatal("账号、日期或 NULL 值不兼容")
	}
	response, _ = f.request(t, "POST", "/api/v1/profile", empty, flatProfile(2, "重复"))
	assertCode(t, response, 500)
	var count int
	if err := f.db.QueryRow("SELECT COUNT(*) FROM student_profile WHERE user_id=2005").Scan(&count); err != nil {
		t.Fatal(err)
	}
	if count != 1 {
		t.Fatal("生成了重复档案")
	}
}

func TestClassesAndStudentCRUD(t *testing.T) {
	f := newFixture(t)
	admin := f.login(t, "admin", "admin123")
	response, _ := f.request(t, "GET", "/api/v1/admin/classes?page=0&size=2", admin, nil)
	assertCode(t, response, 0)
	page := dataAs[pageResponse[classInfo]](t, response)
	if page.Page != 0 || page.PageSize != 2 || page.Total != 3 || len(page.Items) != 2 || page.Items[0].ID != 1 || page.Items[0].Description != nil {
		t.Fatal("班级分页或 NULL 数据不兼容")
	}
	alpha := f.login(t, "alpha", "pass123")
	response, _ = f.request(t, "GET", "/api/v1/classes/detail/1", alpha, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "POST", "/api/v1/admin/classes", admin, map[string]any{"name": "新班", "description": "描述", "createTime": "2020-08-01T13:14:15"})
	assertCode(t, response, 0)
	clazz := dataAs[classInfo](t, response)
	if clazz.CreateTime == nil || *clazz.CreateTime != "2020-08-01T13:14:15" {
		t.Fatal("班级创建时间不兼容")
	}
	classPath := fmt.Sprintf("/api/v1/admin/classes/%d", clazz.ID)
	response, _ = f.request(t, "PUT", classPath, admin, map[string]any{"description": ""})
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", classPath, admin, nil)
	assertCode(t, response, 0)
	if dataAs[classInfo](t, response).Name != "新班" {
		t.Fatal("局部更新覆盖了班级名称")
	}
	response, _ = f.request(t, "POST", "/api/v1/students", admin, nestedStudent(clazz.ID, "独立同学"))
	assertCode(t, response, 0)
	student := dataAs[studentInfo](t, response)
	var linked int64
	if err := f.db.QueryRow("SELECT user_id FROM student_profile WHERE id=?", student.ID).Scan(&linked); err != nil {
		t.Fatal(err)
	}
	if linked != 0 {
		t.Fatal("管理员新增不应挂到管理员账号或生成账号")
	}
	studentPath := fmt.Sprintf("/api/v1/students/%d", student.ID)
	response, _ = f.request(t, "GET", studentPath, admin, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "POST", "/api/v1/admin/set-admin", admin, map[string]any{"studentId": student.ID})
	assertCode(t, response, 500)
	response, _ = f.request(t, "PUT", studentPath, admin, map[string]any{"clazzId": 2, "basic": map[string]string{"name": "更新同学"}})
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", studentPath, admin, nil)
	updated := dataAs[studentInfo](t, response)
	if updated.ClazzID != 2 || updated.Basic.Name != "更新同学" || updated.Contact.Phone != student.Contact.Phone {
		t.Fatal("管理员改班或局部更新错误")
	}
	response, _ = f.request(t, "DELETE", "/api/v1/admin/classes/2", admin, nil)
	assertCode(t, response, 500)
	response, _ = f.request(t, "DELETE", classPath, admin, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "DELETE", studentPath, admin, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", studentPath, admin, nil)
	assertCode(t, response, 500)
	var adminCount int
	if err := f.db.QueryRow("SELECT COUNT(*) FROM users WHERE id=1000").Scan(&adminCount); err != nil {
		t.Fatal(err)
	}
	if adminCount != 1 {
		t.Fatal("删除独立档案误删管理员")
	}
}

func TestStudentFiltersAndStatistics(t *testing.T) {
	f := newFixture(t)
	admin := f.login(t, "admin", "admin123")
	response, _ := f.request(t, "GET", "/api/v1/students?name=张&gender=女&classId=1&page=0&size=10", admin, nil)
	assertCode(t, response, 0)
	page := dataAs[pageResponse[studentInfo]](t, response)
	if page.Total != 1 || len(page.Items) != 1 || page.Items[0].ID != 3002 {
		t.Fatal("筛选与总数不一致")
	}
	response, _ = f.request(t, "GET", "/api/v1/students?name=%27%20OR%201%3D1%20--", admin, nil)
	assertCode(t, response, 0)
	if dataAs[pageResponse[studentInfo]](t, response).Total != 0 {
		t.Fatal("筛选 SQL 未参数化")
	}
	response, _ = f.request(t, "GET", "/api/v1/students?page=10&size=10", admin, nil)
	assertCode(t, response, 0)
	if !bytes.Contains(response.Data, []byte(`"items":[]`)) {
		t.Fatal("空列表必须是 []，不能为 null")
	}
	for _, query := range []string{"page=-1", "size=0", "page=x", "classId=x", "page=9223372036854775807&size=2"} {
		response, _ = f.request(t, "GET", "/api/v1/students?"+query, admin, nil)
		assertCode(t, response, 400)
	}
	alpha := f.login(t, "alpha", "pass123")
	response, _ = f.request(t, "GET", "/api/v1/students/statistics/summary", alpha, nil)
	assertCode(t, response, 0)
	var summary struct {
		TotalCount      int
		ClassStatistics []struct {
			ClassID      int
			StudentCount int
		}
		GenderRatios []struct {
			Count int
			Ratio float64
		}
	}
	if err := json.Unmarshal(response.Data, &summary); err != nil {
		t.Fatal(err)
	}
	if summary.TotalCount != 4 || len(summary.ClassStatistics) != 3 || summary.ClassStatistics[0].StudentCount != 2 || len(summary.GenderRatios) != 2 {
		t.Fatal("统计总数或班级数据错误")
	}
	for _, ratio := range summary.GenderRatios {
		if ratio.Count != 2 || ratio.Ratio != 50 {
			t.Fatal("性别比例错误")
		}
	}
	mustExec(t, f.db, "DELETE FROM student_profile")
	response, _ = f.request(t, "GET", "/api/v1/students/statistics/summary", admin, nil)
	assertCode(t, response, 0)
	if !bytes.Contains(response.Data, []byte(`"genderRatios":[]`)) {
		t.Fatal("零人数性别比例不兼容")
	}
}

func TestAdminOperationsAndLiveRoles(t *testing.T) {
	f := newFixture(t)
	admin := f.login(t, "admin", "admin123")
	beta := f.login(t, "beta", "pass123")
	response, _ := f.request(t, "POST", "/api/v1/admin/change-password", admin, map[string]any{"studentId": 3002, "newPassword": "reset123"})
	assertCode(t, response, 0)
	var password string
	if err := f.db.QueryRow("SELECT password FROM users WHERE id=2002").Scan(&password); err != nil {
		t.Fatal(err)
	}
	if password != "reset123" {
		t.Fatal("管理员密码重置必须写入关联账号的明文密码")
	}
	response, _ = f.request(t, "POST", "/api/v1/auth/login", "", credentials{"beta", "pass123"})
	assertCode(t, response, -1)
	f.login(t, "beta", "reset123")
	response, _ = f.request(t, "POST", "/api/v1/admin/set-admin", admin, map[string]any{"studentId": 3002})
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", "/api/v1/students", beta, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "POST", "/api/v1/admin/set-admin", admin, map[string]any{"studentId": 3002})
	assertCode(t, response, 0)
	mustExec(t, f.db, "UPDATE users SET role='user' WHERE id=2002")
	response, _ = f.request(t, "GET", "/api/v1/students", beta, nil)
	assertCode(t, response, 403)
	mustExec(t, f.db, "DELETE FROM users WHERE id=2002")
	response, _ = f.request(t, "GET", "/api/v1/profile/me", beta, nil)
	assertCode(t, response, 401)
}

func TestDeleteStudentIsAtomicAndRevokesLogin(t *testing.T) {
	f := newFixture(t)
	admin := f.login(t, "admin", "admin123")
	alpha := f.login(t, "alpha", "pass123")
	mustExec(t, f.db, `CREATE TRIGGER fail_user_delete BEFORE DELETE ON users FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='test rollback'`)
	response, _ := f.request(t, "DELETE", "/api/v1/students/3001", admin, nil)
	assertCode(t, response, 500)
	for _, query := range []string{"SELECT COUNT(*) FROM student_profile WHERE id=3001", "SELECT COUNT(*) FROM users WHERE id=2001"} {
		var count int
		if err := f.db.QueryRow(query).Scan(&count); err != nil || count != 1 {
			t.Fatal("失败的删除未完整回滚")
		}
	}
	response, _ = f.request(t, "GET", "/api/v1/profile/me", alpha, nil)
	assertCode(t, response, 0)
	mustExec(t, f.db, "DROP TRIGGER fail_user_delete")
	response, _ = f.request(t, "DELETE", "/api/v1/students/3001", admin, nil)
	assertCode(t, response, 0)
	response, _ = f.request(t, "GET", "/api/v1/profile/me", alpha, nil)
	assertCode(t, response, 401)
	for _, query := range []string{"SELECT COUNT(*) FROM student_profile WHERE id=3001", "SELECT COUNT(*) FROM users WHERE id=2001"} {
		var count int
		if err := f.db.QueryRow(query).Scan(&count); err != nil || count != 0 {
			t.Fatal("删除成功后仍有关联数据")
		}
	}
}

func TestCookieAndTokenHeaderCompatibility(t *testing.T) {
	f := newFixture(t)
	token := f.login(t, "alpha", "pass123")
	for _, style := range []string{"cookie", "satoken", "bearer", "query"} {
		t.Run(style, func(t *testing.T) {
			path := "/api/v1/profile/me"
			if style == "query" {
				path += "?satoken=" + token
			}
			r := httptest.NewRequest("GET", path, nil)
			switch style {
			case "cookie":
				r.AddCookie(&http.Cookie{Name: "satoken", Value: token})
			case "satoken":
				r.Header.Set("satoken", token)
			case "bearer":
				r.Header.Set("Authorization", "Bearer "+token)
			}
			w := httptest.NewRecorder()
			f.handler.ServeHTTP(w, r)
			var response testResponse
			if err := json.Unmarshal(w.Body.Bytes(), &response); err != nil {
				t.Fatal(err)
			}
			assertCode(t, response, 0)
		})
	}
	r := httptest.NewRequest("GET", "/api/v1/profile/me", nil)
	r.AddCookie(&http.Cookie{Name: "satoken", Value: token})
	r.Header.Set("Authorization", "Bearer forged")
	w := httptest.NewRecorder()
	f.handler.ServeHTTP(w, r)
	var response testResponse
	if err := json.Unmarshal(w.Body.Bytes(), &response); err != nil {
		t.Fatal(err)
	}
	assertCode(t, response, 401)
}

func TestSessionExpiration(t *testing.T) {
	s := newSessionStore(time.Hour)
	token, _, err := s.issue(1)
	if err != nil {
		t.Fatal(err)
	}
	if id, ok := s.lookup(token); !ok || id != 1 {
		t.Fatal("会话无效")
	}
	s.entries[token] = session{1, time.Now().Add(-time.Second)}
	if _, ok := s.lookup(token); ok {
		t.Fatal("过期 token 仍可使用")
	}
	token, _, err = s.issue(1)
	if err != nil {
		t.Fatal(err)
	}
	s.revoke(1)
	if _, ok := s.lookup(token); ok {
		t.Fatal("退出后 token 仍可使用")
	}
}

func TestJSONValidationAndDocs(t *testing.T) {
	a := newApp(nil, config{tokenTimeout: time.Hour})
	handler := a.routes()
	for _, body := range []string{"", "null", "{", `{} {}`, strings.Repeat(" ", 1<<20) + `{}`} {
		r := httptest.NewRequest("POST", "/api/v1/auth/register", strings.NewReader(body))
		w := httptest.NewRecorder()
		handler.ServeHTTP(w, r)
		var response testResponse
		if err := json.Unmarshal(w.Body.Bytes(), &response); err != nil {
			t.Fatal(err)
		}
		assertCode(t, response, 400)
	}
	w := httptest.NewRecorder()
	handler.ServeHTTP(w, httptest.NewRequest("GET", "/v3/api-docs", nil))
	var spec struct{ Paths map[string]map[string]any }
	if err := json.Unmarshal(w.Body.Bytes(), &spec); err != nil {
		t.Fatal(err)
	}
	operations := 0
	for _, path := range spec.Paths {
		operations += len(path)
	}
	if operations != 23 {
		t.Fatalf("OpenAPI 应列出23个原有接口，实际 %d", operations)
	}
}
