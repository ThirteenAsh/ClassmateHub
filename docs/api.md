# ClassmateHub API

Go 后端保留原 Spring Boot 业务接口，默认地址 `http://localhost:8080/api/v1`，前端无需修改。

## 认证

| 方法 | 路径 | 权限 | 请求 |
| --- | --- | --- | --- |
| POST | `/auth/login` | 公开 | `{username,password}` |
| POST | `/auth/register` | 公开 | `{username,password}`，用户名 1–20 字符，密码 6–20 字符 |
| POST | `/auth/logout` | 登录 | 无 |
| POST | `/auth/change-password` | 登录 | `{oldPassword,newPassword}` |

登录返回 `{id,username,role,token}` 并设置 `satoken` Cookie / 响应头。请求兼容 Bearer、satoken 请求头、Cookie 和 satoken 查询参数。注册角色固定为 user；密码明文保存。

## 班级

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/admin/classes` | admin | `page=0&size=10` |
| POST | `/admin/classes` | admin | `{name,description?,createTime?}` |
| GET | `/admin/classes/{id}` | admin | 班级详情 |
| PUT | `/admin/classes/{id}` | admin | 局部更新，格式同创建 |
| DELETE | `/admin/classes/{id}` | admin | 删除无关联档案或用户的班级 |
| GET | `/classes/list` | user / admin | `page=0&size=10` |
| GET | `/classes/detail/{id}` | user / admin | 班级详情 |

返回 `{id,name,description,createTime,updateTime}`。日期时间使用 `YYYY-MM-DDTHH:mm:ss`。

## 同学管理

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/students` | admin | 支持 page、size、name、gender、classId，姓名模糊匹配 |
| POST | `/students` | admin | 新增档案 |
| GET | `/students/{id}` | admin | 档案详情 |
| PUT | `/students/{id}` | admin | 局部更新，可改班级 |
| DELETE | `/students/{id}` | admin | 同一事务删除档案和关联账号；独立档案只删档案 |
| GET | `/students/statistics/summary` | user / admin | 全站汇总统计 |

创建和更新请求：

```json
{
  "clazzId": 1,
  "basic": {"name":"张三","gender":"男","birthDate":"2000-01-01","university":"某大学"},
  "contact": {"phone":"13800000000","qq":"123456","email":"","wechat":""},
  "message": "",
  "avatar": ""
}
```

返回 `{id,clazzId,basic,contact,message,avatar,createTime}`。管理员新增以 user_id=0 保存独立档案，不创建账号。更新不改变账号归属。普通用户在个人中心创建的档案自动关联自己的账号。

统计返回 `{totalCount,classStatistics,genderRatios}`，班级项为 `{classId,className,studentCount}`，性别项为 `{gender,count,ratio}`。比例单位为百分比，四舍五入至两位小数。零人数班级仍返回。

## 个人资料和广场

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/profile/me` | user / admin | 无档案返回 data:null |
| POST | `/profile` | user / admin | 为当前账号创建唯一档案 |
| PUT | `/profile` | user / admin | 更新自己，不能自行改班 |
| GET | `/square` | 登录 | 普通用户仅本班，管理员全部 |

个人资料采用扁平字段 `{id,userId,clazzId,name,gender,birthDate,phone,university,email,wechat,qq,message,avatar}`。创建和更新不接受客户端控制 id / userId。出生日期为 YYYY-MM-DD。更新中省略或 null 的字段不覆盖原值，空串可清空选填字段。

广场每项为 `{username,name,gender,birthDate,phone,qq,university}`。管理员不要求个人档案；普通用户无档案返回 code=500 和“请先完善个人信息”。“其他”班级 ID 为 12，保留原 code=401 隐私提示，HTTP 状态仍为 200。

## 管理员操作

| 方法 | 路径 | 权限 | 请求 |
| --- | --- | --- | --- |
| POST | `/admin/change-password` | admin | `{studentId,newPassword}`，修改关联账号密码 |
| POST | `/admin/set-admin` | admin | `{studentId}`，设置关联账号为 admin |

独立档案没有登录账号，不能执行这两种操作。每次请求从数据库读取角色，普通用户不能调用管理接口。

## 响应

```json
{"code":0,"message":"success","data":null}
```

成功为 code=0，未登录 401，无权限 403，输入校验 400，用户名重复 1001，登录密码错误 -1，原业务异常 500。业务响应沿用 HTTP 200，前端检查业务码。

分页返回 `{items:[],page:0,pageSize:10,total:0}`，数组不返回 null；选填数据库 NULL 保留为 JSON null。

运行中的文档入口：`/v3/api-docs`、`/swagger-ui.html`。
