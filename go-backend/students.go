package main

import (
	"database/sql"
	"errors"
	"math"
	"net/http"
	"strconv"
	"strings"
)

func (a *app) listStudents(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	page, size, err := pagination(r)
	if err != nil {
		return result{}, err
	}
	filters, args := []string{}, []any{}
	if name := r.URL.Query().Get("name"); name != "" {
		filters = append(filters, "name LIKE ?")
		args = append(args, "%"+name+"%")
	}
	if gender := r.URL.Query().Get("gender"); gender != "" {
		filters = append(filters, "gender = ?")
		args = append(args, gender)
	}
	if value := r.URL.Query().Get("classId"); value != "" {
		id, err := strconv.ParseInt(value, 10, 64)
		if err != nil || id <= 0 {
			return result{}, invalid("班级 ID 必须为正整数")
		}
		filters = append(filters, "clazz_id = ?")
		args = append(args, id)
	}
	where := ""
	if len(filters) > 0 {
		where = " WHERE " + strings.Join(filters, " AND ")
	}
	var total int64
	if err := a.db.QueryRowContext(r.Context(), "SELECT COUNT(*) FROM student_profile"+where, args...).Scan(&total); err != nil {
		return result{}, err
	}
	pageArgs := append(append([]any{}, args...), size, int64(page)*int64(size))
	rows, err := a.db.QueryContext(r.Context(), "SELECT "+profileColumns+" FROM student_profile"+where+" ORDER BY id ASC LIMIT ? OFFSET ?", pageArgs...)
	if err != nil {
		return result{}, err
	}
	defer rows.Close()
	items := []studentInfo{}
	for rows.Next() {
		p, err := scanProfile(rows)
		if err != nil {
			return result{}, err
		}
		items = append(items, p.student())
	}
	return success(pageResponse[studentInfo]{items, page, size, total}), rows.Err()
}

func (a *app) getStudent(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	p, err := findProfile(r.Context(), a.db, id, false, false)
	if errors.Is(err, sql.ErrNoRows) {
		return result{}, business("同学信息不存在")
	}
	return success(p.student()), err
}

func (a *app) createStudent(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body studentRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	// 原表 user_id NOT NULL，原广场代码已把 <=0 作为无关联账号处理。
	// 管理员表单不包含账号：以 0 保存独立档案，不给管理员创建档案或生成虚假账号。
	userID := int64(0)
	p := profile{UserID: &userID, Created: a.now(), Updated: a.now()}
	mergeProfile(&p, body.fields())
	if err := validateProfile(p); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	if _, err := findClass(r.Context(), tx, p.ClazzID, true); err != nil {
		return result{}, err
	}
	if err := insertProfile(r.Context(), tx, &p); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(p.student()), nil
}

func (a *app) updateStudent(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	var body studentRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	p, err := findProfile(r.Context(), tx, id, false, true)
	if errors.Is(err, sql.ErrNoRows) {
		return result{}, business("同学信息不存在")
	}
	if err != nil {
		return result{}, err
	}
	mergeProfile(&p, body.fields())
	if err := validateProfile(p); err != nil {
		return result{}, err
	}
	if _, err := findClass(r.Context(), tx, p.ClazzID, true); err != nil {
		return result{}, err
	}
	p.Updated = a.now()
	if err := updateProfileRow(r.Context(), tx, p); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(true), nil
}

func (a *app) deleteStudent(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	p, err := findProfile(r.Context(), tx, id, false, true)
	if errors.Is(err, sql.ErrNoRows) {
		return result{}, business("同学信息不存在")
	}
	if err != nil {
		return result{}, err
	}
	linked := p.UserID != nil && *p.UserID > 0
	if linked {
		var others int64
		if err := tx.QueryRowContext(r.Context(), "SELECT COUNT(*) FROM student_profile WHERE user_id = ? AND id <> ?", *p.UserID, id).Scan(&others); err != nil {
			return result{}, err
		}
		if others > 0 {
			return result{}, business("关联用户存在其他档案，无法删除")
		}
	}
	// 同一事务删除档案和关联账号，任何一步失败都会回滚。
	if _, err := tx.ExecContext(r.Context(), "DELETE FROM student_profile WHERE id = ?", id); err != nil {
		return result{}, err
	}
	if linked {
		if _, err := tx.ExecContext(r.Context(), "DELETE FROM users WHERE id = ?", *p.UserID); err != nil {
			return result{}, err
		}
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	if linked {
		a.sessions.revoke(*p.UserID)
	}
	return success(true), nil
}

func (a *app) statistics(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	type classStatistic struct {
		ClassID      int64  `json:"classId"`
		ClassName    string `json:"className"`
		StudentCount int64  `json:"studentCount"`
	}
	type genderRatio struct {
		Gender string  `json:"gender"`
		Count  int64   `json:"count"`
		Ratio  float64 `json:"ratio"`
	}
	data := struct {
		TotalCount      int64            `json:"totalCount"`
		ClassStatistics []classStatistic `json:"classStatistics"`
		GenderRatios    []genderRatio    `json:"genderRatios"`
	}{ClassStatistics: []classStatistic{}, GenderRatios: []genderRatio{}}
	// 同一快照保证总数、班级人数和性别比例相互一致。
	tx, err := a.db.BeginTx(r.Context(), &sql.TxOptions{Isolation: sql.LevelRepeatableRead, ReadOnly: true})
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	if err := tx.QueryRowContext(r.Context(), "SELECT COUNT(*) FROM student_profile").Scan(&data.TotalCount); err != nil {
		return result{}, err
	}
	rows, err := tx.QueryContext(r.Context(), `SELECT c.id, c.name, COUNT(sp.id) FROM clazz c
		LEFT JOIN student_profile sp ON c.id = sp.clazz_id GROUP BY c.id, c.name ORDER BY c.id ASC`)
	if err != nil {
		return result{}, err
	}
	for rows.Next() {
		item := classStatistic{}
		if err := rows.Scan(&item.ClassID, &item.ClassName, &item.StudentCount); err != nil {
			rows.Close()
			return result{}, err
		}
		data.ClassStatistics = append(data.ClassStatistics, item)
	}
	err = rows.Err()
	rows.Close()
	if err != nil {
		return result{}, err
	}
	rows, err = tx.QueryContext(r.Context(), "SELECT gender, COUNT(*) FROM student_profile GROUP BY gender ORDER BY gender ASC")
	if err != nil {
		return result{}, err
	}
	for rows.Next() {
		item := genderRatio{}
		if err := rows.Scan(&item.Gender, &item.Count); err != nil {
			rows.Close()
			return result{}, err
		}
		if data.TotalCount > 0 {
			item.Ratio = math.Round(float64(item.Count)/float64(data.TotalCount)*10000) / 100
		}
		data.GenderRatios = append(data.GenderRatios, item)
	}
	err = rows.Err()
	rows.Close()
	if err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(data), nil
}
