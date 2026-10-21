package main

import (
	"encoding/json"
	"net/http"
	"strings"
)

// 保留原 OpenAPI 文档入口，不引入文档生成框架。
func (a *app) apiDocs(w http.ResponseWriter, r *http.Request) {
	paths := map[string]any{}
	for _, endpoint := range []struct {
		method, path, summary, request string
		public                         bool
	}{
		{"post", "/api/v1/auth/login", "用户登录", "Credentials", true},
		{"post", "/api/v1/auth/register", "用户注册", "Credentials", true},
		{"post", "/api/v1/auth/logout", "退出登录", "", false},
		{"post", "/api/v1/auth/change-password", "修改自己的密码", "ChangePassword", false},
		{"get", "/api/v1/admin/classes", "班级列表（管理员）", "", false},
		{"post", "/api/v1/admin/classes", "创建班级（管理员）", "Class", false},
		{"get", "/api/v1/admin/classes/{id}", "班级详情（管理员）", "", false},
		{"put", "/api/v1/admin/classes/{id}", "更新班级（管理员）", "Class", false},
		{"delete", "/api/v1/admin/classes/{id}", "删除班级（管理员）", "", false},
		{"get", "/api/v1/classes/list", "班级列表", "", false},
		{"get", "/api/v1/classes/detail/{id}", "班级详情", "", false},
		{"get", "/api/v1/students", "同学列表（管理员）", "", false},
		{"post", "/api/v1/students", "新增同学（管理员）", "Student", false},
		{"get", "/api/v1/students/{id}", "同学详情（管理员）", "", false},
		{"put", "/api/v1/students/{id}", "更新同学（管理员）", "Student", false},
		{"delete", "/api/v1/students/{id}", "删除同学及关联账号（管理员）", "", false},
		{"get", "/api/v1/students/statistics/summary", "同学统计", "", false},
		{"get", "/api/v1/profile/me", "当前账号的个人资料", "", false},
		{"post", "/api/v1/profile", "创建个人资料", "Profile", false},
		{"put", "/api/v1/profile", "更新个人资料", "Profile", false},
		{"get", "/api/v1/square", "广场（普通用户仅本班，管理员全部）", "", false},
		{"post", "/api/v1/admin/change-password", "重置同学密码（管理员）", "AdminPassword", false},
		{"post", "/api/v1/admin/set-admin", "设置管理员（管理员）", "AdminRole", false},
	} {
		operation := map[string]any{"summary": endpoint.summary, "responses": map[string]any{
			"200": map[string]any{"description": "统一响应：code=0 成功，失败检查 code 和 message；data 可为 null"}}}
		if endpoint.public {
			operation["security"] = []any{}
		}
		parameters := []any{}
		if strings.Contains(endpoint.path, "{id}") {
			parameters = append(parameters, map[string]any{"name": "id", "in": "path", "required": true,
				"schema": map[string]any{"type": "integer", "format": "int64", "minimum": 1}})
		}
		if endpoint.method == "get" && (endpoint.path == "/api/v1/admin/classes" || endpoint.path == "/api/v1/classes/list" || endpoint.path == "/api/v1/students") {
			parameters = append(parameters,
				map[string]any{"name": "page", "in": "query", "schema": map[string]any{"type": "integer", "default": 0, "minimum": 0}},
				map[string]any{"name": "size", "in": "query", "schema": map[string]any{"type": "integer", "default": 10, "minimum": 1}})
			if endpoint.path == "/api/v1/students" {
				for _, name := range []string{"name", "gender", "classId"} {
					typ := "string"
					if name == "classId" {
						typ = "integer"
					}
					parameters = append(parameters, map[string]any{"name": name, "in": "query", "schema": map[string]any{"type": typ}})
				}
			}
		}
		if len(parameters) > 0 {
			operation["parameters"] = parameters
		}
		if endpoint.request != "" {
			operation["requestBody"] = map[string]any{"required": true, "content": map[string]any{"application/json": map[string]any{
				"schema": map[string]any{"$ref": "#/components/schemas/" + endpoint.request}}}}
		}
		item, exists := paths[endpoint.path].(map[string]any)
		if !exists {
			item = map[string]any{}
			paths[endpoint.path] = item
		}
		item[endpoint.method] = operation
	}
	object := func(properties map[string]any) any { return map[string]any{"type": "object", "properties": properties} }
	str := map[string]any{"type": "string"}
	id := map[string]any{"type": "integer", "format": "int64"}
	profileProperties := map[string]any{"clazzId": id, "name": str, "gender": str, "birthDate": map[string]any{"type": "string", "format": "date"},
		"phone": str, "university": str, "email": str, "wechat": str, "qq": str, "message": str, "avatar": str}
	document := map[string]any{"openapi": "3.0.3", "info": map[string]any{"title": "ClassmateHub API", "version": "1.0.2-go"},
		"paths": paths, "security": []any{map[string]any{"bearerAuth": []string{}}, map[string]any{"saToken": []string{}}, map[string]any{"cookieAuth": []string{}}},
		"components": map[string]any{"securitySchemes": map[string]any{
			"bearerAuth": map[string]any{"type": "http", "scheme": "bearer"}, "saToken": map[string]any{"type": "apiKey", "in": "header", "name": "satoken"},
			"cookieAuth": map[string]any{"type": "apiKey", "in": "cookie", "name": "satoken"}}, "schemas": map[string]any{
			"Credentials":    object(map[string]any{"username": str, "password": str}),
			"ChangePassword": object(map[string]any{"oldPassword": str, "newPassword": str}),
			"AdminPassword":  object(map[string]any{"studentId": id, "newPassword": str}), "AdminRole": object(map[string]any{"studentId": id}),
			"Class":   object(map[string]any{"name": str, "description": str, "createTime": map[string]any{"type": "string", "example": "2026-10-11T13:00:00"}}),
			"Profile": object(profileProperties), "Student": object(map[string]any{"clazzId": id, "message": str, "avatar": str,
				"basic":   object(map[string]any{"name": str, "gender": str, "birthDate": profileProperties["birthDate"], "university": str}),
				"contact": object(map[string]any{"phone": str, "email": str, "wechat": str, "qq": str})}),
		}}}
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	if err := json.NewEncoder(w).Encode(document); err != nil {
		return
	}
}

func (a *app) swagger(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	_, _ = w.Write([]byte(`<!doctype html><html lang="zh-CN"><meta charset="utf-8"><title>ClassmateHub API</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.32.0/swagger-ui.css">
<div id="swagger-ui"><a href="/v3/api-docs">查看 OpenAPI JSON</a></div>
<script src="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5.32.0/swagger-ui-bundle.js"></script>
<script>if(window.SwaggerUIBundle)SwaggerUIBundle({url:'/v3/api-docs',dom_id:'#swagger-ui'});</script></html>`))
}
