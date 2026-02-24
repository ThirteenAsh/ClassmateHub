# 同学录管理系统 - 后端

##### **版本号：v0.2.0**

> 本次更新删除大量冗余代码，完成了班级管理及同学管理的接口开发

## 项目概述
这是一个基于 Spring Boot 3 + MyBatis 的同学录系统后端，采用标准三层架构设计，支持班级管理和同学信息管理以及用户预览等核心功能。目前仍在开发中。期待加入 thirteenashwbw@gmail.com

## 技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 基础语言 | Java | 17 | 开发语言 |
| 框架 | Spring Boot | 3.5.10 | 应用框架 |
| 持久层 | MyBatis | 3.0.5 | ORM框架 |
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

## 项目结构

```
src/main/java/com/thirteenash/
├── controller/        # 控制器层
│   ├── ClassController.java              # 班级管理控制器
│   └── StudentProfileController.java     # 同学信息控制器
├── service/           # 服务层
│   ├── IClassService.java                # 班级服务接口
│   ├── IStudentProfileService.java       # 同学信息服务接口
│   └── impl/                              # 服务层实现
│       ├── ClassServiceImpl.java         # 班级服务实现
│       └── StudentProfileServiceImpl.java # 同学信息服务实现
├── mapper/            # 数据访问层
│   ├── ClassMapper.java                  # 班级Mapper
│   └── StudentProfileMapper.java         # 同学信息Mapper
├── entity/            # 实体类
│   ├── Clazz.java                         # 班级实体
│   └── StudentProfile.java                # 同学档案实体
├── dto/               # 数据传输对象
│   ├── CreateClassRequestDTO.java        # 创建班级请求
│   ├── UpdateClassRequestDTO.java        # 更新班级请求
│   ├── CreateStudentProfileRequestDTO.java # 创建同学信息请求
│   ├── UpdateStudentProfileRequestDTO.java # 更新同学信息请求
│   └── PageRequestDTO.java                # 分页请求
├── vo/                # 响应对象
│   ├── ClassInfoVO.java                   # 班级信息响应
│   └── StudentProfileVO.java              # 同学信息响应
├── common/            # 公共组件
│   ├── exception/                          # 异常处理
│   │   ├── BusinessException.java         # 业务异常
│   │   └── GlobalExceptionHandler.java    # 全局异常处理器
│   └── response/                           # 响应封装
│       ├── Result.java                    # 统一响应格式
│       └── PageResponse.java              # 分页响应格式
└── config/            # 配置类
    └── WebConfig.java                      # Web配置类
```

## 核心功能模块

### 1. 班级管理模块
- 创建班级
- 获取班级列表（分页）
- 获取班级详情
- 更新班级信息
- 删除班级

### 2. 同学信息管理模块
- 创建同学信息
- 获取同学列表（分页）
- 获取同学详情
- 更新同学信息
- 删除同学信息

### 3. 统一响应与异常处理
- 统一响应格式：`{"code": 200, "message": "成功", "data": {...}}`
- 全局异常捕获与处理
- 业务异常定义与使用

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

### 3. 班级管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/v1/classes | 创建班级 |
| GET | /api/v1/classes | 获取班级列表（分页） |
| GET | /api/v1/classes/{classId} | 获取班级详情 |
| PUT | /api/v1/classes/{classId} | 更新班级信息 |
| DELETE | /api/v1/classes/{classId} | 删除班级 |

### 4. 同学信息接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/v1/students | 创建同学信息 |
| GET | /api/v1/students | 获取同学列表（分页） |
| GET | /api/v1/students/{id} | 获取同学详情 |
| PUT | /api/v1/students/{id} | 更新同学信息 |
| DELETE | /api/v1/students/{id} | 删除同学信息 |

## 数据库设计

### 1. 班级表（clazz）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 班级ID（主键） |
| name | VARCHAR | 班级名称 |
| description | VARCHAR | 班级描述 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 2. 同学档案表（student_profile）

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 档案ID（主键） |
| clazz_id | BIGINT | 班级ID |
| name | VARCHAR | 姓名 |
| gender | VARCHAR | 性别 |
| birth_date | DATE | 出生日期 |
| phone | VARCHAR | 手机号 |
| email | VARCHAR | 邮箱 |
| wechat | VARCHAR | 微信号 |
| qq | VARCHAR | QQ号 |
| message | VARCHAR | 个人留言 |
| avatar | VARCHAR | 头像URL |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

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

本次更新删除大量冗余代码，完成了班级管理及同学管理的接口开发
