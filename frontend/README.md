# ClassmateHub 前端项目

ClassmateHub 是一个现代化的同学录管理系统，旨在帮助用户连接和管理同学关系，提供便捷的社交网络功能。

## 技术栈

- **前端框架**: [Vue.js 3](https://vuejs.org/) - 渐进式 JavaScript 框架
- **UI 组件库**: [Element Plus](https://element-plus.org/) - 为 Vue 3 设计的桌面端组件库
- **状态管理**: [Pinia](https://pinia.vuejs.org/) - 轻量级 Vue 状态管理库
- **路由管理**: [Vue Router](https://router.vuejs.org/) - Vue.js 官方路由管理器
- **构建工具**: [Vite](https://vitejs.dev/) - 下一代前端构建工具
- **类型检查**: [TypeScript](https://www.typescriptlang.org/) - JavaScript 的超集，提供类型安全
- **HTTP 客户端**: [Axios](https://axios-http.com/) - 基于 Promise 的 HTTP 客户端

## 功能特性

- ✅ 用户注册与登录
- ✅ 个人信息管理
- ✅ 同学列表展示
- ✅ 课程管理
- ✅ 响应式设计，支持多种设备
- ✅ 国际化支持（中文）
- ✅ 现代化的用户界面

## 项目结构

```
frontend/
├── public/                 # 静态资源
│   └── favicon.ico         # 网站图标
├── src/                    # 源代码目录
│   ├── assets/             # 静态资源（图片、样式等）
│   ├── components/         # 可复用的 Vue 组件
│   ├── views/              # 页面视图组件
│   ├── router/             # 路由配置
│   ├── stores/             # Pinia 状态管理
│   ├── api/                # API 接口定义
│   ├── utils/              # 工具函数
│   ├── types/              # TypeScript 类型定义
│   ├── App.vue             # 根组件
│   └── main.ts             # 应用入口文件
├── index.html              # 主 HTML 文件
├── package.json            # 项目依赖和脚本
├── tsconfig.json           # TypeScript 配置
└── vite.config.ts          # Vite 配置文件
```

## 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0 或 yarn >= 1.22.0

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd frontend
```

### 2. 安装依赖

```bash
npm install
```

### 3. 启动开发服务器

```bash
npm run dev
```

开发服务器将在 `http://localhost:3000` 上运行。

### 4. 构建生产版本

```bash
npm run build
```

构建产物将生成在 `dist/` 目录下。

### 5. 预览生产构建

```bash
npm run preview
```

## 配置说明

### 环境变量

项目通过 Vite 的环境变量系统进行配置，相关配置在 `vite.config.ts` 中：

- **代理配置**: 开发环境中，`/api` 路径下的请求会被代理到 `http://localhost:8080`，用于后端接口调试。

### TypeScript 配置

- 使用 Node.js 模块解析策略
- 支持路径别名 (`@` 映射到 `src` 目录)
- 严格的类型检查

## API 接口

所有 API 请求都通过 `src/api/` 目录下的模块进行管理：

- **认证接口**: `/api/auth` - 用户注册、登录等
- **用户接口**: `/api/users` - 用户信息管理
- **班级接口**: `/api/classes` - 班级和学生管理

## 路由配置

项目包含以下主要路由：

- `/` - 首页
- `/login` - 登录页面
- `/register` - 注册页面
- `/profile` - 个人资料页面
- `/classes` - 班级管理页面
- `/students` - 学生管理页面

## 状态管理

使用 Pinia 进行状态管理，包含：

- **用户状态**: 认证信息、用户资料
- **应用状态**: 全局配置、UI 状态

## UI 组件

使用 Element Plus 组件库，包含：

- 表单组件：Input、Select、Form、Checkbox、Radio 等
- 数据展示：Table、Tag、Progress、Tree 等
- 反馈组件：Alert、MessageBox、Notification 等
- 导航组件：Pagination、Steps、Dropdown 等

## 开发规范

### 代码风格

- 使用 ESLint 和 Prettier 进行代码格式化
- 使用 TypeScript 进行类型检查
- 遵循 Vue 3 Composition API 规范

### 组件命名

- 页面组件以 `View` 结尾（如 `LoginView.vue`）
- 通用组件以功能命名（如 `UserCard.vue`）

### 提交规范

- 使用约定式提交（Conventional Commits）
- 提交信息应清晰描述变更内容

## 部署

### 静态部署

构建后的项目可作为静态网站部署：

```bash
npm run build
```

将 `dist/` 目录下的文件部署到 Web 服务器即可。

### Docker 部署

（如果需要，可以添加 Dockerfile）

## 贡献指南

1. Fork 仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 常见问题

### 开发环境常见问题

1. **模块解析错误**
   - 确保 `tsconfig.json` 中的 `moduleResolution` 设置为 `node`

2. **代理配置不生效**
   - 检查 `vite.config.ts` 中的代理设置
   - 确保后端服务在指定端口运行

### 生产环境部署

1. **资源路径问题**
   - 检查 `vite.config.ts` 中的 `base` 配置
   - 确保静态资源路径正确

## 许可证

[MIT License](LICENSE)

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至 [admin@classmatehub.com](mailto:admin@classmatehub.com)