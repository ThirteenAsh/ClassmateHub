# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码库中工作的指导。

## 项目概览

**ClassmateHub** - 一个完整的前后端分离的同学录管理系统。

**项目类型**: 全栈 Web 应用  
**架构**: 前后端分离  
**当前版本**: 后端 v1.0.2, 前端 v0.2.1

## 快速开始命令

### 完整项目设置
```bash
# 后端设置和运行
cd backend
RunBackend.bat

# 前端设置和运行（新终端）
cd frontend
RunFrontend.bat

# 或手动运行:
cd backend && mvn spring-boot:run
cd frontend && npm run dev
```

### 后端命令
```bash
cd backend

# 构建和打包
mvn clean package

# 运行应用
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试
mvn test -Dtest=ClassName#methodName

# 初始化数据库
mysql -u root -p classmatehub < db.sql
```

### 前端命令
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## 项目架构

### 高层级结构
```
ClassmateHub/
├── backend/                    # Spring Boot 后端 (Java 17)
│   ├── src/main/java/         # Java 源代码
│   ├── src/main/resources/    # 配置文件
│   └── pom.xml               # Maven 依赖
├── frontend/                   # Vue 3 + TypeScript 前端
│   ├── src/                  # Vue 组件和逻辑
│   ├── public/               # 静态资源
│   └── package.json          # npm 依赖
└── docs/                      # 项目文档
```

### 技术栈

**后端 (Spring Boot 3.5.10)**:
- Java 17
- Spring Boot 3.5.10
- MyBatis 3.0.5 (ORM框架)
- Sa-Token 1.44.0 (权限认证)
- MySQL 8.x (数据库)
- Maven (构建工具)
- SpringDoc OpenAPI (API文档)

**前端 (Vue 3)**:
- Vue.js 3 (渐进式框架)
- TypeScript (类型安全)
- Element Plus (UI组件库)
- Vue Router (路由管理)
- Axios (HTTP客户端)
- Vite (构建工具)

### 通信流程
```
前端 (Vue 3) ↔ HTTP/REST API ↔ 后端 (Spring Boot) ↔ MySQL 数据库
```

## API 架构

### 基础 URL 结构
- **开发环境**: `http://localhost:8080/api/v1` (后端), `http://localhost:5173` (前端)
- **API 前缀**: 所有后端 API 使用 `/api/v1` 前缀
- **Swagger 文档**: `http://localhost:8080/swagger-ui.html`

### API 分类
- `/api/v1/auth/*` - 认证相关接口
- `/api/v1/classes/*` - 班级管理
- `/api/v1/students/*` - 学生档案管理
- `/api/v1/square` - 公共广场功能
- `/api/v1/admin/*` - 管理员操作
- `/api/v1/profile/*` - 个人中心管理

### 响应格式
```json
{
  "code": 200,
  "message": "成功消息",
  "data": { ... }
}
```

## 后端架构

### 分层结构
```
控制器 → 服务 → 映射器 → 数据库
```

### 核心组件

**控制器** (`backend/src/main/java/com/thirteenash/controller/`)
- `AuthController` - 用户认证（登录、注册、登出）
- `ClassController` - 班级管理操作
- `StudentProfileController` - 学生信息增删改查
- `ProfileController` - 个人中心管理
- `SquareController` - 公共广场功能
- `AdminController` - 管理员操作

**服务层** (`backend/src/main/java/com/thirteenash/service/`)
- 基于接口的服务层，实现在 `impl/` 目录
- `StpInterfaceImpl` - Sa-Token 权限实现

**映射器** (`backend/src/main/java/com/thirteenash/mapper/`)
- MyBatis 映射器接口
- XML 映射文件在 `backend/src/main/resources/mapper/`

**实体类** (`backend/src/main/java/com/thirteenash/entity/`)
- `Users` - 用户认证和角色数据
- `Clazz` - 班级实体
- `StudentProfile` - 学生档案实体

**通用组件** (`backend/src/main/java/com/thirteenash/common/`)
- `GlobalExceptionHandler` - 统一异常处理
- `Result` - 统一 API 响应格式
- `BusinessException` - 自定义业务异常

## 前端架构

### 组件结构
```
src/
├── views/                     # 页面组件
│   ├── HomeView.vue          # 主仪表板
│   ├── ClassesView.vue       # 班级管理
│   ├── StudentsView.vue      # 学生管理
│   └── LoginView.vue         # 认证页面
├── components/               # 可复用组件
│   ├── HeaderNav.vue         # 导航头部
│   └── BottomNav.vue         # 导航底部
├── router/                   # Vue Router 配置
├── api/                      # API 服务层
│   ├── auth.ts               # 认证相关 API
│   └── index.ts              # 通用 API 方法
├── stores/                   # Pinia 状态管理
│   └── auth.ts               # 认证状态
└── plugins/                  # Vue 插件
    └── element.ts            # Element Plus 配置
```

### 状态管理
- **Pinia** 用于集中式状态管理
- `auth.ts` 存储处理用户认证状态
- 使用 `pinia-plugin-persistedstate` 实现状态持久化

### 路由结构
- `/` - 主页仪表板
- `/classes` - 班级管理
- `/students` - 学生管理
- `/login` - 用户认证

## 数据库架构

### 数据表 (MySQL)
- `users` - 用户认证和角色数据
- `clazz` - 班级信息
- `student_profile` - 学生详细档案

### 关键关系
- `users.classId` → `clazz.id`
- `student_profile.user_id` → `users.id`
- `student_profile.clazz_id` → `clazz.id`

### 数据库配置
**文件**: `backend/src/main/resources/application.yml`
- **URL**: `jdbc:mysql://localhost:3306/classmatehub`
- **默认凭据**: root/123456
- **连接池**: HikariCP，最大连接池大小 20

## 安全与认证

### 后端安全 (Sa-Token)
- 基于 Token 的认证
- 基于角色的访问控制（用户/管理员角色）
- 会话管理
- 通过 `@SaCheckLogin` 和 `@SaCheckRole("admin")` 进行权限检查

### 前端安全
- Token 存储在 Pinia 存储中并持久化
- Axios 请求中自动注入 Token
- 受保护路由的守卫

## 开发工作流程

### 添加新功能
1. **后端优先**:
   - 在控制器中设计 API 端点
   - 实现服务层逻辑
   - 添加 MyBatis 映射器方法
   - 通过 Swagger UI 测试

2. **前端集成**:
   - 在 `src/api/` 中添加 API 方法
   - 创建/更新 Vue 组件
   - 根据需要更新路由
   - 测试集成

### 数据库变更
1. 修改 `backend/db.sql` 进行架构变更
2. 更新相应的实体类
3. 调整映射器 XML 文件
4. 测试数据库操作

### 代码风格指南

**后端 (Java)**:
- 缩进: 4 个空格
- 方法长度: < 50 行
- 类长度: < 500 行
- 命名: 类使用 PascalCase，方法和变量使用 camelCase

**前端 (TypeScript/Vue)**:
- 使用 TypeScript 严格类型检查
- 遵循 Vue 3 Composition API 和 `<script setup>` 语法
- 页面组件名称以 `View` 结尾
- 使用 Element Plus 组件库

## 测试

### 后端测试
- 单元测试: `backend/src/test/java/`
- 运行命令: `cd backend && mvn test`
- MyBatis 提供集成测试支持

### 前端测试
- 可以使用 Vitest 添加组件测试
- 使用模拟服务进行 API 集成测试

## 配置文件

### 后端配置
- `backend/pom.xml` - Maven 依赖和构建配置
- `backend/src/main/resources/application.yml` - 应用设置
- `backend/db.sql` - 数据库架构和初始数据

### 前端配置
- `frontend/package.json` - npm 依赖和脚本
- `frontend/vite.config.ts` - Vite 构建配置
- `frontend/tsconfig.json` - TypeScript 配置

## 故障排除

### 常见问题

**后端问题**:
- **数据库连接**: 检查 MySQL 服务和 `application.yml` 中的凭据
- **端口冲突**: 在 `application.yml` 中更改 `server.port`（默认: 8080）
- **MyBatis 错误**: 为 `com.thirteenash.mapper` 启用调试日志
- **认证失败**: 验证 Sa-Token 配置

**前端问题**:
- **API 代理不工作**: 检查 `vite.config.ts` 代理设置
- **模块解析错误**: 验证 `tsconfig.json` 中 `moduleResolution` 设置为 `node`
- **API 路径错误**: 确保后端使用 `/api/v1` 前缀
- **构建错误**: 使用 `npm run build` 检查 TypeScript 编译

### 日志和调试
- **后端**: 在 `application.yml` 中启用 DEBUG 日志
- **前端**: 浏览器开发者工具和 Vue DevTools
- **API 调用**: 检查浏览器开发者工具中的 Network 选项卡

## 部署

### 开发环境
- 后端: `mvn spring-boot:run` (端口 8080)
- 前端: `npm run dev` (端口 5173)
- API 代理在 `vite.config.ts` 中配置

### 生产构建
```bash
# 后端
cd backend
mvn clean package
java -jar target/backend-1.0.2.jar

# 前端
cd frontend
npm run build
# 将 dist/ 目录内容部署到 Web 服务器
```

## 项目状态

### 当前功能
- ✅ 用户认证和授权
- ✅ 班级管理（增删改查操作）
- ✅ 学生档案管理
- ✅ 基于角色的访问控制（管理员/用户）
- ✅ 响应式前端界面
- ✅ API 文档（Swagger）
- ✅ 数据库集成（MySQL + MyBatis）

### 未来规划
- 🔄 消息/评论系统
- 🔄 文件和头像上传
- 🔄 通知系统
- 🔄 数据导出功能
- 🔄 搜索和筛选
- 🔄 移动端优化

## 资源

### 文档
- **API 文档**: `http://localhost:8080/swagger-ui.html`
- **后端 README**: `backend/README.md`
- **前端 README**: `frontend/README.md`
- **项目概览**: `README.md`

### 外部链接
- **GitHub 仓库**: `https://github.com/ThirteenAsh/ClassmateHub`
- **联系方式**: thirteenashwbw@gmail.com