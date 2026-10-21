# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码库中工作的指导。

## 项目概览

**ClassmateHub Backend** - 一个完整的同学录管理系统后端，使用 Spring Boot 3、MyBatis 和 Sa-Token 认证构建。

**当前版本**: v1.0.2  
**Java 版本**: 17  
**框架**: Spring Boot 3.5.10

## 快速开始命令

### 构建和运行
```bash
# 清理并构建项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 或运行构建的 JAR 包
java -jar target/backend-1.0.2.jar

# 快速启动脚本（Windows）
RunBackend.bat
```

### 开发
```bash
# 编译项目
mvn clean compile

# 运行（未配置热重载，需要重启）
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试
mvn test -Dtest=ClassName#methodName
```

### 数据库
```bash
# 初始化数据库（运行 db.sql）
mysql -u root -p classmatehub < db.sql
```

## 项目架构

### 分层架构
```
控制器 → 服务 → 映射器 → 数据库
```

### 核心组件

**控制器** (`src/main/java/com/thirteenash/controller/`)
- `AuthController` - 用户认证（登录、注册、登出、修改密码）
- `ClassController` - 班级管理操作
- `StudentProfileController` - 学生信息增删改查
- `ProfileController` - 个人中心管理
- `SquareController` - 公共广场功能
- `AdminController` - 管理员操作

**服务层** (`src/main/java/com/thirteenash/service/`)
- 基于接口的服务层，实现在 `impl/` 目录
- `StpInterfaceImpl` - Sa-Token 权限实现

**映射器** (`src/main/java/com/thirteenash/mapper/`)
- MyBatis 映射器接口用于数据库操作
- XML 映射文件在 `src/main/resources/mapper/`

**实体类** (`src/main/java/com/thirteenash/entity/`)
- `Users` - 用户实体，包含认证字段
- `Clazz` - 班级实体
- `StudentProfile` - 学生档案实体

**DTO** (`src/main/java/com/thirteenash/dto/`)
- 请求 DTO 用于 API 输入验证
- 响应 DTO 用于 API 输出格式化

**通用组件** (`src/main/java/com/thirteenash/common/`)
- `GlobalExceptionHandler` - 集中式异常处理
- `Result` - 统一 API 响应格式
- `BusinessException` - 自定义业务异常

## 配置

### 数据库配置
**文件**: `src/main/resources/application.yml`
- MySQL 数据库: `jdbc:mysql://localhost:3306/classmatehub`
- 默认凭据: root/123456
- 连接池: HikariCP，最大连接池大小 20

### API 文档
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### API 端点结构
所有 API 使用 `/api/v1` 前缀:
- `/api/v1/auth/*` - 认证端点
- `/api/v1/classes/*` - 班级管理
- `/api/v1/students/*` - 学生档案管理
- `/api/v1/square` - 公共广场
- `/api/v1/admin/*` - 管理员操作

## 安全与认证

**Sa-Token** 实现提供:
- 基于 Token 的认证
- 基于角色的访问控制（用户/管理员角色）
- 会话管理

**认证流程**:
1. 用户登录 → Token 生成
2. 通过 Sa-Token 拦截器进行 Token 验证
3. 基于角色的权限检查

## 数据库架构

### 数据表
- `users` - 用户认证和角色数据
- `clazz` - 班级信息
- `student_profile` - 学生详细档案

### 关键关系
- `users.classId` → `clazz.id`
- `student_profile.user_id` → `users.id`
- `student_profile.clazz_id` → `clazz.id`

## 开发指南

### 代码风格
- **缩进**: 4 个空格
- **方法长度**: < 50 行
- **类长度**: < 500 行
- **命名**: 类使用 PascalCase，方法和变量使用 camelCase

### 异常处理
- 使用 `BusinessException` 处理业务逻辑错误
- 所有异常由 `GlobalExceptionHandler` 处理
- 业务代码中不要捕获异常后静默返回

### 响应格式
```json
{
  "code": 200,
  "message": "成功消息",
  "data": { ... }
}
```

### MyBatis 最佳实践
- 映射器接口在 `com.thirteenash.mapper`
- XML 映射文件在 `resources/mapper/`
- 实体别名在 `application.yml` 中配置
- 启用驼峰命名映射

## 常见开发任务

### 添加新的 API 端点
1. 在 `dto/` 中创建请求/响应 DTO
2. 在相应的控制器中添加控制器方法
3. 创建服务接口和实现
4. 添加映射器方法和 XML SQL
5. 通过 Swagger UI 测试

### 数据库变更
1. 修改 `db.sql` 进行架构变更
2. 更新相应的实体类
3. 调整映射器 XML 文件
4. 重新构建并测试

### 添加认证
- 使用 `@SaCheckLogin` 标注需要登录的端点
- 使用 `@SaCheckRole("admin")` 标注仅管理员访问的端点
- 在 `StpInterfaceImpl` 中自定义权限

## 测试
- 单元测试: `src/test/java/`
- 运行命令: `mvn test`
- MyBatis 提供集成测试支持

## 故障排除

### 常见问题
- **数据库连接**: 检查 MySQL 服务和 `application.yml` 中的凭据
- **端口冲突**: 在 `application.yml` 中更改 `server.port`
- **MyBatis 错误**: 为 `com.thirteenash.mapper` 启用调试日志
- **认证失败**: 验证 `SaTokenConfigure` 中的 Sa-Token 配置

### 日志
- MyBatis SQL 日志: 为 `com.thirteenash.mapper` 设置 DEBUG 级别
- Spring Web 日志: 为 `org.springframework.web` 设置 DEBUG 级别