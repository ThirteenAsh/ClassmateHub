package main

import (
	"context"
	"database/sql"
	"errors"
	"net/http"
	"time"
	"unicode/utf8"
)

type scanner interface{ Scan(...any) error }
type querier interface {
	QueryRowContext(context.Context, string, ...any) *sql.Row
}

const classColumns = "id, name, description, create_time, update_time"

func scanClass(row scanner) (classInfo, error) {
	c := classInfo{}
	var description sql.NullString
	var created, updated sql.NullTime
	err := row.Scan(&c.ID, &c.Name, &description, &created, &updated)
	c.Description, c.CreateTime, c.UpdateTime = nullableString(description), nullableTime(created), nullableTime(updated)
	return c, err
}

func findClass(ctx context.Context, q querier, id int64, lock bool) (classInfo, error) {
	query := "SELECT " + classColumns + " FROM clazz WHERE id = ?"
	if lock {
		query += " FOR UPDATE"
	}
	c, err := scanClass(q.QueryRowContext(ctx, query, id))
	if errors.Is(err, sql.ErrNoRows) {
		return c, business("班级不存在")
	}
	return c, err
}

func (a *app) listClasses(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	page, size, err := pagination(r)
	if err != nil {
		return result{}, err
	}
	var total int64
	if err := a.db.QueryRowContext(r.Context(), "SELECT COUNT(*) FROM clazz").Scan(&total); err != nil {
		return result{}, err
	}
	rows, err := a.db.QueryContext(r.Context(), "SELECT "+classColumns+" FROM clazz ORDER BY id ASC LIMIT ? OFFSET ?", size, int64(page)*int64(size))
	if err != nil {
		return result{}, err
	}
	defer rows.Close()
	items := []classInfo{}
	for rows.Next() {
		item, err := scanClass(rows)
		if err != nil {
			return result{}, err
		}
		items = append(items, item)
	}
	return success(pageResponse[classInfo]{items, page, size, total}), rows.Err()
}

func (a *app) getClass(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	c, err := findClass(r.Context(), a.db, id, false)
	return success(c), err
}

func validateClassName(name *string) error {
	if name == nil || blank(*name) {
		return invalid("班级名称不能为空")
	}
	if utf8.RuneCountInString(*name) > 100 {
		return invalid("班级名称不能超过100个字符")
	}
	return nil
}

func (a *app) createClass(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	var body classRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	if err := validateClassName(body.Name); err != nil {
		return result{}, err
	}
	now, created := a.now(), a.now()
	var err error
	if body.CreateTime != nil {
		created, err = parseDateTime(*body.CreateTime, a.cfg.location)
		if err != nil {
			return result{}, err
		}
	}
	res, err := a.db.ExecContext(r.Context(), "INSERT INTO clazz (name, description, create_time, update_time) VALUES (?, ?, ?, ?)", body.Name, body.Description, created, now)
	if err != nil {
		return result{}, err
	}
	id, err := res.LastInsertId()
	if err != nil {
		return result{}, err
	}
	c, err := findClass(r.Context(), a.db, id, false)
	return success(c), err
}

func (a *app) updateClass(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	var body classRequest
	if err := decode(w, r, &body); err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	c, err := findClass(r.Context(), tx, id, true)
	if err != nil {
		return result{}, err
	}
	if body.Name != nil {
		if err := validateClassName(body.Name); err != nil {
			return result{}, err
		}
		c.Name = *body.Name
	}
	if body.Description != nil {
		c.Description = body.Description
	}
	var created *time.Time
	if c.CreateTime != nil {
		value, err := parseDateTime(*c.CreateTime, a.cfg.location)
		if err != nil {
			return result{}, err
		}
		created = &value
	}
	if body.CreateTime != nil {
		value, err := parseDateTime(*body.CreateTime, a.cfg.location)
		if err != nil {
			return result{}, err
		}
		created = &value
	}
	res, err := tx.ExecContext(r.Context(), "UPDATE clazz SET name = ?, description = ?, create_time = ?, update_time = ? WHERE id = ?", c.Name, c.Description, created, a.now(), id)
	if err != nil {
		return result{}, err
	}
	if err := requireChanged(res, "更新班级失败"); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(true), nil
}

func (a *app) deleteClass(w http.ResponseWriter, r *http.Request, _ *user) (result, error) {
	id, err := pathID(r)
	if err != nil {
		return result{}, err
	}
	tx, err := a.db.BeginTx(r.Context(), nil)
	if err != nil {
		return result{}, err
	}
	defer tx.Rollback()
	if _, err := findClass(r.Context(), tx, id, true); err != nil {
		return result{}, err
	}
	var count int64
	if err := tx.QueryRowContext(r.Context(), "SELECT (SELECT COUNT(*) FROM student_profile WHERE clazz_id = ?) + (SELECT COUNT(*) FROM users WHERE class_id = ?)", id, id).Scan(&count); err != nil {
		return result{}, err
	}
	if count > 0 {
		return result{}, business("班级仍有关联同学或用户，无法删除")
	}
	if _, err := tx.ExecContext(r.Context(), "DELETE FROM clazz WHERE id = ?", id); err != nil {
		return result{}, err
	}
	if err := tx.Commit(); err != nil {
		return result{}, err
	}
	return success(true), nil
}
