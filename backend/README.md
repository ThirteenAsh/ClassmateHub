# 同学录管理系统 - 后端

##### **版本号：v1.0.2**

> 基于 Spring Boot 3 + MyBatis + Sa-Token 的完整同学录系统后端，提供班级管理、同学信息管理、用户认证、广场功能等核心功能。
>
> 运行`RunBackend.bat`即可快速启动后端服务(需Java及Maven环境支持)

## 项目概述
这是一个基于 Spring Boot 3 + MyBatis 的同学录系统后端，集成 Sa-Token 权限认证和 OpenAPI 文档，采用标准三层架构设计，支持班级管理、同学信息管理、用户认证、广场功能等核心功能。系统已具备完整的用户权限体系和数据管理功能。

## 技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 基础语言 | Java | 17 | 开发语言 |
| 框架 | Spring Boot | 3.5.10 | 应用框架 |
| 权限认证 | Sa-Token | 1.44.0 | 用户认证与权限管理 |
| 持久层 | MyBatis | 3.0.5 | ORM框架 |
| API文档 | SpringDoc OpenAPI | 2.8.8 | 自动生成API文档 |
| 参数校验 | Validation | - | 请求参数校验 |
| 数据库 | MySQL | 8.x | 数据存储 |
| 构建工具 | Maven | 3.9+ | 依赖管理 |
| 代码简化 | Lombok | 1.18.22 | 减少样板代码 |

## 架构设计

### 1. 三层架构

```
Controller 控制层 → Service 服务层 → Mapper 数据访问层 → Database 数据库
```

- **控制层（Controller）**：处理HTTP请求，参数校验，调用服务层方法，返回统一响应
- **服务层（Service）**：实现业务逻辑，处理业务规则，调用数据访问层方法
- **数据访问层（Mapper）**：负责数据库操作，执行SQL语句

### 2. 安全架构

```
请求 → Sa-Token拦截器 → 权限校验 → Controller → Service → 数据库
```

- **Sa-Token**：负责用户认证、权限校验和Token管理
- **全局异常处理**：统一处理业务异常和系统异常
- **参数校验**：使用Validation进行请求参数合法性校验

## 项目结构

```
src/main/java/com/thirteenash/
├── controller/        # 控制器层
│   ├── AuthController.java                # 认证管理控制器
│   ├── ClassController.java               # 班级管理控制器
│   ├── StudentProfileController.java      # 同学信息控制器
│   ├── ProfileController.java             # 个人中心控制器
│   ├── SquareController.java              # 广场信息控制器
│   └── AdminController.java               # 管理员控制器
├── service/           # 服务层
│   ├── AuthService.java                   # 认证服务接口
│   ├── UserService.java                   # 用户服务接口
│   ├── IClassService.java                 # 班级服务接口
│   ├── IStudentProfileService.java        # 同学信息服务接口
│   ├── ProfileService.java                # 个人中心服务接口
│   ├── SquareService.java                 # 广场服务接口
│   └── impl/                              # 服务层实现
│       ├── AuthServiceImpl.java           # 认证服务实现
│       ├── UserServiceImpl.java           # 用户服务实现
│       ├── ClassServiceImpl.java          # 班级服务实现
│       ├── StudentProfileServiceImpl.java # 同学信息服务实现
│       ├── ProfileServiceImpl.java        # 个人中心服务实现
│       ├── SquareServiceImpl.java         # 广场服务实现
│       └── StpInterfaceImpl.java          # Sa-Token权限实现
├── mapper/            # 数据访问层
│   ├── AuthMapper.java                    # 认证Mapper
│   ├── ClassMapper.java                   # 班级Mapper
│   └── StudentProfileMapper.java          # 同学信息Mapper
├── entity/            # 实体类
│   ├── Users.java                         # 用户实体
│   ├── Clazz.java                         # 班级实体
│   └── StudentProfile.java                # 同学档案实体
├── dto/               # 数据传输对象
│   ├── UserLoginRequestDTO.java           # 用户登录请求
│   ├── UserRegisterDTO.java               # 用户注册请求
│   ├── ChangePasswordRequestDTO.java      # 修改密码请求
│   ├── CreateClassRequestDTO.java         # 创建班级请求
│   ├── UpdateClassRequestDTO.java         # 更新班级请求
│   ├── CreateStudentProfileRequestDTO.java # 创建同学信息请求
│   ├── UpdateStudentProfileRequestDTO.java # 更新同学信息请求
│   ├── StudentPageQueryDTO.java           # 学生分页查询请求
│   ├── SquareStudentDTO.java              # 广场学生信息DTO
│   └── PageRequestDTO.java                # 分页请求
├── vo/                # 响应对象
│   ├── UserLoginInfo.java                 # 用户登录信息响应
│   ├── ClassInfoVO.java                   # 班级信息响应
│   ├── StudentProfileVO.java              # 同学信息响应
│   └── StudentStatisticsVO.java           # 学生统计信息响应
├── common/            # 公共组件
│   ├── exception/                          # 异常处理
│   │   ├── BusinessException.java         # 业务异常
│   │   ├── GlobalExceptionHandler.java    # 全局异常处理器
│   │   └── ClassException.java            # 班级异常
│   └── response/                           # 响应封装
│       ├── Result.java                    # 统一响应格式
│       └── PageResponse.java              # 分页响应格式
└── config/            # 配置类
    ├── WebConfig.java                      # Web配置类
    ├── OpenAPIConfig.java                  # OpenAPI文档配置
    └── SaTokenConfigure.java               # Sa-Token配置
```

## 核心功能模块

### 1. 用户认证模块
- 用户注册（用户名、密码、确认密码）
- 用户登录（用户名、密码验证，返回Token）
- 用户登出（清除Token）
- 修改密码（原密码、新密码）
- 权限控制（普通用户、管理员角色）

### 2. 班级管理模块
- 创建班级
- 获取班级列表（分页、筛选）
- 获取班级详情
- 更新班级信息
- 删除班级

### 3. 同学信息管理模块
- 创建同学信息
- 获取同学列表（分页、筛选、排序）
- 获取同学详情
- 更新同学信息
- 删除同学信息
- 个人中心管理

### 4. 广场功能模块
- 获取广场同学列表（展示公开信息）
- 查看其他同学基本信息

### 5. 管理员功能模块
- 管理员直接修改用户密码
- 将用户设置为管理员角色
- 管理班级和同学信息

### 6. 统一响应与异常处理
- 统一响应格式：`{"code": 200, "message": "成功", "data": {...}}`
- 全局异常捕获与处理
- 业务异常定义与使用
- 参数校验与验证

## 配置说明

### 依赖配置（pom.xml）

核心依赖：
- Spring Boot Starter Web
- Spring Boot Starter MyBatis
- MySQL Connector
- Lombok

## API接口规范

### 1. 路径前缀
所有API接口统一使用 `/api/v1` 作为前缀，例如：
- 班级接口：`/api/v1/classes/*`
- 同学信息接口：`/api/v1/students/*`

### 2. 统一响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "成功",
  "data": {...}
}
```

#### 失败响应

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 3. 用户认证接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/v1/auth/login | 用户登录 | 公开 |
| POST | /api/v1/auth/register | 用户注册 | 公开 |
| POST | /api/v1/auth/logout | 用户登出 | 登录 |
| POST | /api/v1/auth/change-password | 修改密码 | 登录 |

### 4. 班级管理接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/v1/classes | 创建班级 | 登录 |
| GET | /api/v1/classes | 获取班级列表（分页） | 登录 |
| GET | /api/v1/classes/{classId} | 获取班级详情 | 登录 |
| PUT | /api/v1/classes/{classId} | 更新班级信息 | 登录 |
| DELETE | /api/v1/classes/{classId} | 删除班级 | 管理员 |

### 5. 同学信息接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/v1/students | 创建同学信息 | 登录 |
| GET | /api/v1/students | 获取同学列表（分页、筛选） | 登录 |
| GET | /api/v1/students/{id} | 获取同学详情 | 登录 |
| PUT | /api/v1/students/{id} | 更新同学信息 | 登录 |
| DELETE | /api/v1/students/{id} | 删除同学信息 | 登录 |

### 6. 广场功能接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/v1/square | 获取广场同学列表 | 登录 |

### 7. 管理员接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/v1/admin/change-password | 修改用户密码 | 管理员 |
| POST | /api/v1/admin/set-admin | 设置管理员 | 管理员 |

### 8. API文档
- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI JSON**: `/v3/api-docs`

## 数据库设计

### 1. 用户表（users）

| 字段名 | 数据类型 | 描述 | 约束 |
|-------|---------|------|------|
| id | BIGINT | 用户ID（主键） | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | NOT NULL, UNIQUE |
| password | VARCHAR(255) | 密码 | NOT NULL |
| role | VARCHAR(30) | 用户角色 | NOT NULL (user/admin) |
| classId | INT | 班级ID | NOT NULL |

### 2. 班级表（clazz）

| 字段名 | 数据类型 | 描述 | 约束 |
|-------|---------|------|------|
| id | BIGINT | 班级ID（主键） | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | 班级名称 | NOT NULL |
| description | TEXT | 班级描述 | NULLABLE |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

### 3. 同学档案表（student_profile）

| 字段名 | 数据类型 | 描述 | 约束 |
|-------|---------|------|------|
| id | BIGINT | 档案ID（主键） | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL, INDEX |
| clazz_id | BIGINT | 班级ID | NOT NULL, INDEX |
| name | VARCHAR(20) | 姓名 | NOT NULL |
| gender | VARCHAR(10) | 性别（男/女） | NOT NULL |
| birth_date | DATE | 出生日期 | NOT NULL |
| phone | VARCHAR(20) | 电话 | NOT NULL |
| email | VARCHAR(100) | 邮箱 | NULLABLE |
| wechat | VARCHAR(50) | 微信号 | NULLABLE |
| qq | VARCHAR(20) | QQ号 | NOT NULL |
| message | VARCHAR(256) | 留言 | NOT NULL |
| avatar | VARCHAR(500) | 头像URL | NULLABLE |
| university | VARCHAR(255) | 毕业院校 | NOT NULL |
| create_time | DATETIME | 创建时间 | NOT NULL, INDEX |
| update_time | DATETIME | 更新时间 | NOT NULL, INDEX |

### 4. 索引设计
- `student_profile` 表：user_id、clazz_id、create_time、name 字段均建立索引，优化查询性能

## 开发规范

### 1. 命名规范
- 类名：大驼峰命名法（PascalCase）
- 方法名：小驼峰命名法（camelCase）
- 变量名：小驼峰命名法（camelCase）
- 常量名：全大写，下划线分隔（UPPER_CASE）
- 包名：小写，点分隔（lowercase.dot.separated）

### 2. 代码风格
- 使用4个空格缩进
- 方法长度控制在50行以内
- 类长度控制在500行以内
- 合理使用注释，解释复杂逻辑
- 使用Lombok减少样板代码

### 3. 异常处理
- 业务异常使用 `BusinessException`
- 统一由 `GlobalExceptionHandler` 处理
- 不要在业务代码中捕获异常后直接返回错误信息

### 4. 安全规范
- 防止SQL注入、XSS等安全问题
- 敏感信息不要直接返回给客户端

## 部署说明

### 1. 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.x

### 2. 部署步骤
1. 克隆项目
2. 配置数据库连接（修改 `application.yml`）
3. 构建项目：`mvn clean package`
4. 运行项目：`java -jar backend-0.0.1-SNAPSHOT.jar`

### 3. 开发环境启动
```bash
# 进入后端目录
cd backend

# 构建项目
mvn clean compile

# 启动项目
mvn spring-boot:run
```

## 总结

基于 Spring Boot 3 + MyBatis + Sa-Token 构建的完整同学录系统后端，已实现用户认证、班级管理、同学信息管理、广场功能和管理员功能等核心模块。系统集成 OpenAPI 文档、参数校验、全局异常处理等基础设施，具备完善的安全机制和权限控制，可直接用于生产环境部署或作为学习参考项目。
