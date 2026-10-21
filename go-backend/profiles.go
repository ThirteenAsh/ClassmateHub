package main

import (
	"context"
	"database/sql"
	"errors"
	"net/http"
	"strings"
	"time"
	"unicode/utf8"
)

const profileColumns = "id, user_id, clazz_id, name, gender, birth_date, phone, university, email, wechat, qq, message, avatar, create_time, update_time"

func scanProfile(row scanner) (profile, error) {
	p := profile{}
	var userID sql.NullInt64
	var birth time.Time
	var email, wechat, message, avatar sql.NullString
	err := row.Scan(&p.ID, &userID, &p.ClazzID, &p.Name, &p.Gender, &birth, &p.Phone, &p.University,
		&email, &wechat, &p.QQ, &message, &avatar, &p.Created, &p.Updated)
	if userID.Valid {
		p.UserID = &userID.Int64
	}
	p.BirthDate = birth.Format("2006-01-02")
	p.Email, p.Wechat, p.Message, p.Avatar = nullableString(email), nullableString(wechat), nullableString(message), nullableString(avatar)
	return p, err
}

func findProfile(ctx context.Context, q querier, id int64, byUser, lock bool) (profile, error) {
	column := "id"
	if byUser {
		column = "user_id"
	}
	query := "SELECT " + profileColumns + " FROM student_profile WHERE " + column + " = ? ORDER BY id ASC LIMIT 1"
	if lock {
		query += " FOR UPDATE"
	}
	return scanProfile(q.QueryRowContext(ctx, query, id))
}

func mergeProfile(p *profile, body profileRequest) {
	if body.ClazzID != nil {
		p.ClazzID = *body.ClazzID
	}
	for _, field := range []struct {
		target *string
		source *string
	}{
		{&p.Name, body.Name}, {&p.Gender, body.Gender}, {&p.BirthDate, body.BirthDate},
		{&p.Phone, body.Phone}, {&p.University, body.University}, {&p.QQ, body.QQ},
	} {
		if field.source != nil {
			*field.target = *field.source
		}
	}
	if body.Email != nil {
		p.Email = body.Email
	}
	if body.Wechat != nil {
		p.Wechat = body.Wechat
	}
	if body.Message != nil {
		p.Message = body.Message
	}
	if body.Avatar != nil {
		p.Avatar = body.Avatar
	}
}

func validateProfile(p profile) error {
	for _, field := range []struct {
		name, value string
		max         int
	}{
		{"姓名", p.Name, 20}, {"性别", p.Gender, 10}, {"电话", p.Phone, 20},
		{"大学名称", p.University, 255}, {"QQ号", p.QQ, 20},
	} {
		if blank(field.value) {
			return invalid(field.name + "不能为空")
		}
		if utf8.RuneCountInString(field.value) > field.max {
			return invalid(field.name + "长度超出限制")
		}
	}
	if p.ClazzID <= 0 {
		return invalid("请选择班级")
	}
	if p.Gender != "男" && p.Gender != "女" {
		return invalid("性别必须为男或女")
	}
	if _, err := time.Parse("2006-01-02", p.BirthDate); err != nil {
		return invalid("出生日期格式错误，应为 YYYY-MM-DD")
	}
	for _, field := range []struct {
		name  string
		value *string
		max   int
	}{
		{"邮箱", p.Email, 100}, {"微信号", p.Wechat, 50}, {"留言", p.Message, 256}, {"头像地址", p.Avatar, 500},
	} {
		if field.value != nil && utf8.RuneCountInString(*field.value) > field.max {
			return invalid(field.name + "长度超出限制")
		}
	}
	return nil
}

func lockUser(ctx context.Context, tx *sql.Tx, id int64) error {
	var found int64
	err := tx.QueryRowContext(ctx, "SELECT id FROM users WHERE id = ? FOR UPDATE", id).Scan(&found)
	if errors.Is(err, sql.ErrNoRows) {
		return business("用户不存在")
	}
	return err
}

func insertProfile(ctx context.Context, tx *sql.Tx, p *profile) error {
	res, err := tx.ExecContext(ctx, `INSERT INTO student_profile
		(user_id, clazz_id, name, gender, birth_date, phone, university, email, wechat, qq, message, avatar, create_time, update_time)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`, p.UserID, p.ClazzID, p.Name, p.Gender, p.BirthDate,
		p.Phone, p.University, p.Email, p.Wechat, p.QQ, p.Message, p.Avatar, p.Created, p.Updated)
	if err != nil {
		return err
	}
	p.ID, err = res.LastInsertId()
	return err
}

func updateProfileRow(ctx context.Context, tx *sql.Tx, p profile) error {
	res, err := tx.ExecContext(ctx, `UPDATE student_profile SET clazz_id = ?, name = ?, gender = ?, birth_date = ?,
		phone = ?, university = ?, email = ?, wechat = ?, qq = ?, message = ?, avatar = ?, update_time = ? WHERE id = ?`,
		p.ClazzID, p.Name, p.Gender, p.BirthDate, p.Phone, p.University, p.Email, p.Wechat, p.QQ,
		p.Message, p.Avatar, p.Updated, p.ID)
	if err != nil {
		return err
	}
	return requireChanged(res, "更新个人信息失败")
}

func (a *app) myProfile(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	p, err := findProfile(r.Context(), a.db, u.ID, true, false)
	if errors.Is(err, sql.ErrNoRows) {
		return success(nil), nil
	}
	return success(p), err
}

func (a *app) createProfile(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	var body profileRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	p := profile{UserID: &u.ID, Created: a.now(), Updated: a.now()}
	mergeProfile(&p, body)
	if err := validateProfile(p); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	if err := lockUser(r.Context(), tx, u.ID); err != nil {
		return result{}, err
	}
	if _, err := findProfile(r.Context(), tx, u.ID, true, true); err == nil {
		return result{}, business("个人信息已存在，无法再次创建")
	} else if !errors.Is(err, sql.ErrNoRows) {
		return result{}, err
	}
	if _, err := findClass(r.Context(), tx, p.ClazzID, true); err != nil {
		return result{}, err
	}
	if err := insertProfile(r.Context(), tx, &p); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(p), nil
}

func (a *app) updateProfile(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	var body profileRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	if err := lockUser(r.Context(), tx, u.ID); err != nil {
		return result{}, err
	}
	p, err := findProfile(r.Context(), tx, u.ID, true, true)
	if errors.Is(err, sql.ErrNoRows) {
		return result{}, business("个人信息不存在")
	}
	if err != nil {
		return result{}, err
	}
	if body.ClazzID != nil && *body.ClazzID != p.ClazzID {
		return result{}, business("班级信息不可修改，请联系管理员")
	}
	mergeProfile(&p, body)
	if err := validateProfile(p); err != nil {
		return result{}, err
	}
	p.Updated = a.now()
	if err := updateProfileRow(r.Context(), tx, p); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(p), nil
}

func (a *app) square(w http.ResponseWriter, r *http.Request, u *user) (result, error) {
	query := `SELECT u.username, sp.name, sp.gender, sp.birth_date, sp.phone, sp.qq, sp.university
		FROM student_profile sp LEFT JOIN users u ON u.id = sp.user_id`
	args := []any{}
	if strings.EqualFold(u.Role, "admin") {
		query += " ORDER BY sp.create_time DESC"
	} else {
		p, err := findProfile(r.Context(), a.db, u.ID, true, false)
		if errors.Is(err, sql.ErrNoRows) {
			return result{}, business("请先完善个人信息")
		}
		if err != nil {
			return result{}, err
		}
		if p.ClazzID == 12 {
			return result{}, &apiError{401, "您的班级为“其他”，为保护同学隐私，这里不会显示其他班级的同学信息，很感谢你填写的内容，我会永远记住你哒~（如果您是因为错误填写看到这个页面，请联系管理员修改您的班级~）"}
		}
		query += " WHERE sp.clazz_id = ? ORDER BY sp.id ASC"
		args = append(args, p.ClazzID)
	}
	rows, err := a.db.QueryContext(r.Context(), query, args...)
	if err != nil {
		return result{}, err
	}
	defer rows.Close()
	type squareStudent struct {
		Username   *string `json:"username"`
		Name       string  `json:"name"`
		Gender     string  `json:"gender"`
		BirthDate  string  `json:"birthDate"`
		Phone      string  `json:"phone"`
		QQ         string  `json:"qq"`
		University string  `json:"university"`
	}
	items := []squareStudent{}
	for rows.Next() {
		item := squareStudent{}
		var username sql.NullString
		var birth time.Time
		if err := rows.Scan(&username, &item.Name, &item.Gender, &birth, &item.Phone, &item.QQ, &item.University); err != nil {
			return result{}, err
		}
		item.Username, item.BirthDate = nullableString(username), birth.Format("2006-01-02")
		items = append(items, item)
	}
	return success(items), rows.Err()
}
