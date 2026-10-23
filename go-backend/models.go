package main

import (
	"database/sql"
	"time"
)

type result struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Data    any    `json:"data"`
}

type pageResponse[T any] struct {
	Items    []T   `json:"items"`
	Page     int   `json:"page"`
	PageSize int   `json:"pageSize"`
	Total    int64 `json:"total"`
}

type user struct {
	ID       int64
	Username string
	Role     string
}

type classInfo struct {
	ID          int64   `json:"id"`
	Name        string  `json:"name"`
	Description *string `json:"description"`
	CreateTime  *string `json:"createTime"`
	UpdateTime  *string `json:"updateTime"`
}

type basicInfo struct {
	Name       string `json:"name"`
	Gender     string `json:"gender"`
	BirthDate  string `json:"birthDate"`
	University string `json:"university"`
}

type contactInfo struct {
	Phone  string  `json:"phone"`
	Email  *string `json:"email"`
	Wechat *string `json:"wechat"`
	QQ     string  `json:"qq"`
}

type studentInfo struct {
	ID         int64       `json:"id"`
	ClazzID    int64       `json:"clazzId"`
	Basic      basicInfo   `json:"basic"`
	Contact    contactInfo `json:"contact"`
	Message    *string     `json:"message"`
	Avatar     *string     `json:"avatar"`
	CreateTime string      `json:"createTime"`
}

type profile struct {
	ID         int64     `json:"id"`
	UserID     *int64    `json:"userId"`
	ClazzID    int64     `json:"clazzId"`
	Name       string    `json:"name"`
	Gender     string    `json:"gender"`
	BirthDate  string    `json:"birthDate"`
	Phone      string    `json:"phone"`
	University string    `json:"university"`
	Email      *string   `json:"email"`
	Wechat     *string   `json:"wechat"`
	QQ         string    `json:"qq"`
	Message    *string   `json:"message"`
	Avatar     *string   `json:"avatar"`
	Created    time.Time `json:"-"`
	Updated    time.Time `json:"-"`
}

func (p profile) student() studentInfo {
	return studentInfo{ID: p.ID, ClazzID: p.ClazzID,
		Basic:   basicInfo{p.Name, p.Gender, p.BirthDate, p.University},
		Contact: contactInfo{p.Phone, p.Email, p.Wechat, p.QQ}, Message: p.Message,
		Avatar: p.Avatar, CreateTime: dateTime(p.Created)}
}

type classRequest struct {
	Name        *string `json:"name"`
	Description *string `json:"description"`
	CreateTime  *string `json:"createTime"`
}

// 指针保留原 Java DTO 的局部更新语义：省略或 null 都不覆盖原字段，空串可以清空选填字段。
type profileRequest struct {
	ClazzID    *int64  `json:"clazzId"`
	Name       *string `json:"name"`
	Gender     *string `json:"gender"`
	BirthDate  *string `json:"birthDate"`
	Phone      *string `json:"phone"`
	University *string `json:"university"`
	Email      *string `json:"email"`
	Wechat     *string `json:"wechat"`
	QQ         *string `json:"qq"`
	Message    *string `json:"message"`
	Avatar     *string `json:"avatar"`
}

type studentRequest struct {
	Basic *struct {
		Name       *string `json:"name"`
		Gender     *string `json:"gender"`
		BirthDate  *string `json:"birthDate"`
		University *string `json:"university"`
	} `json:"basic"`
	Contact *struct {
		Phone  *string `json:"phone"`
		Email  *string `json:"email"`
		Wechat *string `json:"wechat"`
		QQ     *string `json:"qq"`
	} `json:"contact"`
	ClazzID *int64  `json:"clazzId"`
	Message *string `json:"message"`
	Avatar  *string `json:"avatar"`
}

func (s studentRequest) fields() profileRequest {
	p := profileRequest{ClazzID: s.ClazzID, Message: s.Message, Avatar: s.Avatar}
	if s.Basic != nil {
		p.Name, p.Gender, p.BirthDate, p.University = s.Basic.Name, s.Basic.Gender, s.Basic.BirthDate, s.Basic.University
	}
	if s.Contact != nil {
		p.Phone, p.Email, p.Wechat, p.QQ = s.Contact.Phone, s.Contact.Email, s.Contact.Wechat, s.Contact.QQ
	}
	return p
}

func dateTime(t time.Time) string { return t.Format("2006-01-02T15:04:05") }

func nullableString(s sql.NullString) *string {
	if !s.Valid {
		return nil
	}
	return &s.String
}

func nullableTime(t sql.NullTime) *string {
	if !t.Valid {
		return nil
	}
	s := dateTime(t.Time)
	return &s
}
