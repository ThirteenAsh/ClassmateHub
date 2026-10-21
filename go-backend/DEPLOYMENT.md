# 宝塔部署记录

部署日期：2026-10-11（Asia/Shanghai）。

| 项目 | 当前配置 |
| --- | --- |
| 访问域名 | https://classmatehub.bytecraft.xin |
| 宝塔管理入口 | 网站 → Go 项目 |
| 项目名称 | `classmatehub_go` |
| 可执行文件 | `/www/wwwroot/classmatehub-go-backend/classmatehub` |
| 工作目录 | `/www/wwwroot/classmatehub-go-backend` |
| 配置文件 | `/www/wwwroot/classmatehub-go-backend/.env` |
| 运行用户 | `www` |
| 后端端口 | `8080` |
| 开机启动 | 已启用 |
| 应用日志 | `/www/wwwlogs/go/classmatehub_go.log` |

Go 项目管理后端进程。部署完成时复用了原站点的 `/api/v1/` 反向代理，没有上传前端构建文件。原 Java 项目 `backend-0` 保留用于回退，运行切换应确保同一端口只有一个后端。

## 前端文件核对

2026-10-11 再次只读核对服务器后确认：

- `/www/wwwroot/classmatehub/` 当前只有 `backend-1.0.2.jar`，没有前端入口和资源文件。
- 在现有网站目录中未找到同学录前端构建产物；本地构建产物位于项目的 `frontend/dist/`，尚未上传。
- 当前生效的站点配置是 `/www/server/panel/vhost/nginx/go_classmatehub_go.conf`，域名全站转发至 `127.0.0.1:8080`，未配置前端静态目录。
- 原 `/www/server/panel/vhost/nginx/java_backend-0.conf` 当前已不存在。

此前“云端沿用现成前端”的说法未核实文件是否存在，现予以更正。部署前端还需要上传 `frontend/dist/` 的内容，并配置静态目录、Vue 路由回退和 `/api/v1/` 后端反向代理。本次仅核对文件与配置，没有检查前端页面。

使用云端原 `classmatehub` 数据库和现有数据库账号，明文密码方案不变。凭据只存于服务器受限权限配置文件，不写入本仓库。用户已完成数据库备份，本次没有重复备份或导入任何数据库数据。

后端验证通过：宝塔重启管理、HTTPS API、现有管理员和普通用户登录、无档案管理员查看全部同学、管理接口越权拦截、广场班级隔离及统计。部署前后业务表校验值一致。未检查前端页面。

回退配置保存在服务器：

`/www/backup/classmatehub-go-deploy/20261011-141513/deployment-state.json`

回退时在宝塔停止 Go 项目，再恢复并启动原 Java 项目。原 JAR 保留；当前站点配置已发生变化，回退时还需核对和恢复代理配置。
