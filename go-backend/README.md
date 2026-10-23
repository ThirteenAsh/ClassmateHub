# ClassmateHub Go 后端

现有 Spring Boot 后端的 Go 实现，保留 `/api/v1` 接口和 `8080` 默认端口，前端无需修改。直接复用云服务器原 MySQL 数据库，启动时不会建表、导入本地数据或修改表结构。

云端已部署到宝塔 Go 项目 `classmatehub_go`，实际路径和验证记录见 [部署记录](DEPLOYMENT.md)。

## 启动

需要 Go 1.25 或更高版本，以及原来的 `users`、`clazz`、`student_profile` 三张表。

```powershell
cd go-backend
Copy-Item .env.example .env
# 编辑 .env，填写实际数据库连接
go run .
```

Windows 可运行本目录的 `RunBackend.bat`。Linux 云服务器可执行：

```sh
cd go-backend
cp .env.example .env
# 编辑 .env，填写云端数据库配置
go build -trimpath -o classmatehub .
./classmatehub
```

环境变量优先于 `.env`；`.env` 从进程工作目录读取。服务管理器的工作目录应设为 `go-backend`，或直接设置环境变量。

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `PORT` | `8080` | HTTP 端口 |
| `DB_HOST` / `DB_PORT` | `127.0.0.1` / `3306` | MySQL 地址 |
| `DB_NAME` | `classmatehub` | 原数据库名称 |
| `DB_USER` / `DB_PASSWORD` | `root` / `123456` | 原项目本地配置；云端填写实际值 |
| `DB_DSN` | 未设置 | 可选完整 MySQL DSN，覆盖 DB_* 连接参数 |
| `TZ` | `Asia/Shanghai` | 与原云端后端保持一致的时区 |
| `TOKEN_TIMEOUT` | `2592000` | 会话有效秒数，默认 30 天 |
| `COOKIE_SECURE` | `false` | HTTPS 部署设为 true |

DSN 示例：`user:password@tcp(127.0.0.1:3306)/classmatehub?charset=utf8mb4`。连接池最多 5 个连接，不引入 Redis、消息队列或额外表。

## 保留的业务规则

- 保留原表、字段、主键 ID 和账号关联；密码按现有方案明文保存和比较。
- 注册固定为 `user`。管理员直接由 `users.role='admin'` 识别，不要求个人档案，能查看所有班级同学；不自动生成管理员账号或档案。
- 普通用户在广场仅查看自己档案所属班级；`clazz_id=12` 的“其他”班级保留原隐私提示。
- 个人资料只操作登录账号自己的档案，忽略客户端伪造的 id/userId，不能自行改班；管理员可改班。
- 班级管理、同学管理、重置密码和设置管理员均检查 admin 角色。每次请求从数据库读取角色，修改即时生效。
- 保留统一响应、业务码、零起点分页、NULL、日期格式和局部更新语义。业务错误沿用 HTTP 200，包括广场的特殊 code=401 提示。
- 管理员新增表单不包含账号字段，以 `user_id=0` 保存独立档案，兼容原表 NOT NULL 约束和原广场对非正用户 ID 的处理，不挂到管理员账号，不创建新账号。无关联账号的档案不能重置账号密码或设置管理员。
- 删除档案与关联账号使用同一事务，失败完整回滚，成功后账号会话失效。独立档案只删除档案。关联账号存在其他档案时拒绝删除；班级仍有关联档案或用户时拒绝删除，避免悬空关联。

上述权限检查、有效关联校验和事务补齐了原实现的数据/权限缺口。会话仍保存在内存中，登录返回 `data.token` 并设置 `satoken` Cookie / 响应头，兼容前端 Bearer 及原 Sa-Token 请求。重启和从 Java 切换后需重新登录，不实现旧会话迁移。

## 云端切换

1. 保留原 Java 服务用于回退，备份云端数据库。Go 直接连接原库，**不要向生产导入本地 SQL**。`docs/classmatehub.sql` 带有 DROP TABLE，仅用于全新空库和测试。
2. 填写云端数据库、端口、时区配置，HTTPS 部署设置 COOKIE_SECURE=true。
3. 构建并用其他端口验证，如 `PORT=8082 ./classmatehub`，核对管理员、普通用户、个人资料、广场和统计。
4. 停止原 Java 后端，让 Go 使用 8080，或切换反向代理上游。原域名、前端、接口路径和数据库保持一致。
5. 用户重新登录。回退时停止 Go，恢复 Java 和原代理上游；原表结构和明文密码格式保持兼容。

Windows 构建常见 Linux x86-64 云服务器程序：

```powershell
$env:GOOS = 'linux'
$env:GOARCH = 'amd64'
$env:CGO_ENABLED = '0'
go build -trimpath -o classmatehub-linux-amd64 .
```

## 验证

```sh
go test ./...
go vet ./...
```

完整 MySQL 回归需要显式设置测试连接，账号需有建库权限。测试从原 SQL 提取业务表，只创建和删除随机 `classmatehub_go_test_*` 临时库，不修改现有业务库。未设置连接时跳过数据库测试。

```powershell
$env:TEST_MYSQL_DSN = 'root:你的测试密码@tcp(127.0.0.1:3306)/'
go test -count=1 -v ./...
```

覆盖越权拦截、无档案管理员、本班隐私、个人资料归属、明文密码、分页筛选、统计、角色变更、账号删除和删除失败回滚。接口清单见 [API 文档](../docs/api.md)。运行后可访问 `/v3/api-docs` 和 `/swagger-ui.html`；Swagger 页面资源使用 CDN，无外网时直接读取 OpenAPI JSON。
