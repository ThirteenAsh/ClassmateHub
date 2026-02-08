# 同学录系统 API 文档（v0.1）

## 1. 基本信息

- Base URL：`/api/v1`
- 数据格式：`application/json; charset=utf-8`
- 鉴权方式：JWT（`Authorization: Bearer <token>`）
- 角色：
  - `ADMIN`：系统拥有者，最高权限
  - `STUDENT`：普通用户，只能管理自己的信息
  - `GUEST`：预留（只读，未来扩展）

------

## 2. 通用约定

### 2.1 请求头

- `Authorization: Bearer <JWT>`（除登录/注册等公开接口外）

### 2.2 统一响应结构

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

- `code = 0`：成功
- `code != 0`：失败（见错误码）

### 2.3 分页参数（通用）

- `page`：默认 1
- `pageSize`：默认 20，最大 100

分页响应示例：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [],
    "page": 1,
    "pageSize": 20,
    "total": 0
  }
}
```

------

## 3. 数据模型

### 3.1 用户 User

```json
{
  "id": "u_123",
  "username": "tom",
  "role": "ADMIN",
  "status": "NORMAL",
  "createTime": "2026-02-07T10:00:00Z"
}
```

- `role`: `ADMIN` / `STUDENT`
- `status`: `NORMAL` / `DISABLED`

### 3.2 班级 Class

```json
{
  "id": "c_001",
  "name": "2019级计算机1班",
  "description": "大学时期",
  "startYear": 2019,
  "endYear": 2023
}
```



### 3.3 可见性 Visibility

- `PUBLIC`：所有人可见
- `CLASS_ONLY`：同班可见
- `PRIVATE`：仅本人 & ADMIN 可见

### 3.4 同学档案 Profile（支持字段级可见性）

> 推荐：每个字段携带 `value + visibility`，返回时按权限过滤（STUDENT 不得看到他人 PRIVATE 字段；非同班不得看到 CLASS_ONLY 字段）。

示例：

```json
{
  "userId": "u_200",
  "classId": "c_001",
  "basic": {
    "name": { "value": "张三", "visibility": "PUBLIC" },
    "gender": { "value": "男", "visibility": "CLASS_ONLY" },
    "birthday": { "value": "2000-01-01", "visibility": "PRIVATE" },
    "hometown": { "value": "台北", "visibility": "PUBLIC" }
  },
  "contact": {
    "phone": { "value": "09xx-xxx-xxx", "visibility": "PRIVATE" },
    "wechat": { "value": "zhangsanwx", "visibility": "CLASS_ONLY" },
    "qq": { "value": "123456", "visibility": "PRIVATE" }
  },
  "education": {
    "school": { "value": "某某大学", "visibility": "PUBLIC" },
    "major": { "value": "计算机", "visibility": "PUBLIC" },
    "graduationDestination": { "value": "软件工程师", "visibility": "CLASS_ONLY" }
  },
  "personal": {
    "hobby": { "value": "摄影", "visibility": "PUBLIC" },
    "motto": { "value": "保持热爱", "visibility": "CLASS_ONLY" },
    "memory": { "value": "最难忘的事...", "visibility": "PRIVATE" }
  },
  "updateTime": "2026-02-07T10:00:00Z"
}
```

------

## 4. 鉴权与认证

### 4.1 登录

**POST** `/auth/login`

请求：

```json
{
  "username": "tom",
  "password": "123456"
}
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOi...",
    "expiresIn": 7200,
    "user": {
      "id": "u_123",
      "username": "tom",
      "role": "ADMIN",
      "status": "NORMAL"
    }
  }
}
```

### 4.2 注册（可选：仅 ADMIN 可创建）

**POST** `/auth/register`

权限：`ADMIN`（或配置允许公开注册）

请求：

```json
{
  "username": "alice",
  "password": "123456",
  "role": "STUDENT"
}
```

响应：返回新用户信息（不返回密码）。

### 4.3 修改密码

**POST** `/auth/password/change`

请求：

```json
{
  "oldPassword": "123456",
  "newPassword": "abc12345"
}
```

### 4.4 管理员重置密码

**POST** `/admin/users/{userId}/password/reset`

权限：`ADMIN`

请求：

```json
{
  "newPassword": "123456"
}
```

------

## 5. 用户管理（ADMIN）

### 5.1 用户列表

**GET** `/admin/users?page=1&pageSize=20&keyword=tom`

权限：`ADMIN`

响应：分页 `items` 为 User 列表。

### 5.2 禁用/启用用户

**PATCH** `/admin/users/{userId}/status`

权限：`ADMIN`

请求：

```json
{ "status": "DISABLED" }
```

------

## 6. 班级 / 分组管理

### 6.1 创建班级

**POST** `/classes`

权限：`ADMIN`

请求：

```json
{
  "name": "2019级计算机1班",
  "description": "大学时期",
  "startYear": 2019,
  "endYear": 2023
}
```

### 6.2 班级列表

**GET** `/classes?page=1&pageSize=20`

权限：登录用户（`ADMIN` / `STUDENT`）

> STUDENT：建议只返回自己所属班级；ADMIN 返回全部。

### 6.3 获取班级详情

**GET** `/classes/{classId}`

### 6.4 更新班级信息

**PUT** `/classes/{classId}`

权限：`ADMIN`

### 6.5 班级成员管理（添加/移除）

**POST** `/classes/{classId}/members`

权限：`ADMIN`

请求（批量添加）：

```json
{ "userIds": ["u_200", "u_201"] }
```

**DELETE** `/classes/{classId}/members/{userId}`

权限：`ADMIN`

------

## 7. 同学信息管理（核心）

### 7.1 获取同学列表（可搜索/筛选）

**GET** `/profiles?classId=c_001&name=张&page=1&pageSize=20`

权限：登录用户

筛选建议（可选）：

- `classId`
- `name`
- `graduationDestination`
- `hobbyKeyword`
- `city`（如果你后续加城市字段）

响应：分页，`items` 为经过可见性过滤后的 Profile 简版（建议返回 name/头像/部分公开字段）。

### 7.2 获取某个同学档案详情

**GET** `/profiles/{userId}`

权限规则：

- `ADMIN`：可查看全部（含 PRIVATE）
- `STUDENT`：
  - 查看自己：全部
  - 查看他人：按可见性过滤（PRIVATE 不返回；CLASS_ONLY 需同班）

### 7.3 创建/初始化自己的档案

**POST** `/profiles/me`

权限：`STUDENT` / `ADMIN`

> 通常注册后可自动创建；此接口用于补建。

请求：可空或带基础字段。

### 7.4 更新自己的档案（字段级更新）

**PATCH** `/profiles/me`

请求（示例：只改手机号并设为 PRIVATE）：

```json
{
  "contact": {
    "phone": { "value": "09xx-xxx-xxx", "visibility": "PRIVATE" }
  }
}
```

说明：

- 采用“部分更新”语义：只更新传入字段
- 未传入字段保持不变

### 7.5 ADMIN 更新任意用户档案

**PATCH** `/admin/profiles/{userId}`

权限：`ADMIN`

------

## 8. 隐私与可见性（可选独立接口）

> 如果你希望“统一批量改可见性”，可以提供独立接口。

### 8.1 批量更新字段可见性（我的档案）

**PATCH** `/profiles/me/visibility`

请求：

```json
{
  "paths": [
    { "path": "contact.phone", "visibility": "PRIVATE" },
    { "path": "personal.hobby", "visibility": "PUBLIC" }
  ]
}
```

------

## 9. 照片与附件(先不做)

### 9.1 上传照片

**POST** `/files/avatar`

- `multipart/form-data`
- 字段：`file`

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "fileId": "f_001",
    "url": "/static/f_001.jpg"
  }
}
```

> 存储建议：本地或 MinIO；数据库存路径和元数据。

------

## 10. 留言与回忆（可选）

### 10.1 给某位同学留言

**POST** `/messages`

请求：

```json
{
  "toUserId": "u_200",
  "content": "好久不见！",
  "visibility": "CLASS_ONLY"
}
```

### 10.2 查看与我相关留言

**GET** `/messages?type=inbox&page=1&pageSize=20`

- `type=inbox`：发给我的
- `type=outbox`：我发出的

### 10.3 管理员审核/删除留言（可选）

**DELETE** `/admin/messages/{messageId}`

权限：`ADMIN`

------

## 11. 系统管理（ADMIN）

### 11.1 数据导出（CSV/Excel）

**GET** `/admin/export/profiles?classId=c_001&format=csv`

权限：`ADMIN`

响应：文件下载（或返回导出任务信息）

### 11.2 操作日志（可选）

**GET** `/admin/audit-logs?page=1&pageSize=20`

------

## 12. 错误码建议

| code  | 含义                     |
| ----- | ------------------------ |
| 0     | ok                       |
| 40001 | 参数错误                 |
| 40100 | 未登录 / token 无效      |
| 40300 | 无权限                   |
| 40400 | 资源不存在               |
| 40900 | 资源冲突（如用户名重复） |
| 50000 | 服务器异常               |

错误响应示例：

```json
{
  "code": 40300,
  "message": "forbidden",
  "data": null
}
```