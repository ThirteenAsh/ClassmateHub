# ClassmateHub 前端项目

##### 版本号 v0.2.0

> 本次更新删除了大量冗余代码，优化了图形化界面，完成了班级及同学管理的开发

## 项目概述

ClassmateHub 是一个现代化的同学录管理系统，旨在帮助用户连接和管理同学关系，提供便捷的社交网络功能。系统采用前后端分离架构，前端基于 Vue 3 + TypeScript 开发，后端基于 Java 开发。

## 项目目标

- 提供一个易用、美观的同学录界面
- 管理部分：支持班级管理和学生信息管理
- 用户部分：支持查看个人资料，支持查看同班的同学信息
- 提供统一、规范的 API 接口
- 确保系统的安全性和可靠性

## 技术栈

| 类别        | 技术         | 简要说明                        |
| ----------- | ------------ | ------------------------------- |
| 前端框架    | Vue.js 3     | 渐进式 JavaScript 框架          |
| UI 组件库   | Element Plus | 为 Vue 3 设计的桌面端组件库     |
| 路由管理    | Vue Router   | Vue.js 官方路由管理器           |
| 构建工具    | Vite         | 下一代前端构建工具              |
| 类型检查    | TypeScript   | JavaScript 的超集，提供类型安全 |
| HTTP 客户端 | Axios        | 基于 Promise 的 HTTP 客户端     |

## 功能特性

- ✅ 班级管理（创建、编辑、删除班级）
- ✅ 学生信息管理（添加、编辑、删除学生信息）
- ✅ 同学列表展示（支持分页）
- ✅ 美观的主页面导航
- ✅ 响应式设计，支持多种设备
- ✅ 国际化支持（中文）
- ✅ 现代化的用户界面
- ✅ 统一的 API 响应结构
- ✅ TypeScript 类型安全

## 项目结构

```
frontend/
├── public/                 # 静态资源
│   └── favicon.ico         # 网站图标
├── src/                    # 源代码目录
│   ├── views/              # 页面视图组件
│   │   ├── HomeView.vue    # 主页面
│   │   ├── ClassesView.vue # 班级管理页面
│   │   └── StudentsView.vue# 学生管理页面
│   ├── router/             # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── api/                # API 接口定义
│   │   └── index.ts       # API 方法封装
│   ├── plugins/            # 插件配置
│   │   └── element.ts     # Element Plus 配置
│   ├── App.vue             # 根组件
│   └── main.ts             # 应用入口文件
├── docs/                   # 项目文档
│   └── 用于管理和保存同学信息的全栈 Web 同学录.md
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
git clone https://github.com/ThirteenAsh/ClassmateHub.git
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

开发服务器将在 `http://localhost:5173` 上运行。

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

- **代理配置**: 开发环境中，`/api` 路径下的请求会被代理到后端服务，用于后端接口调试。前端实际使用的 API 基础路径是 `/api/v1`。

### TypeScript 配置

- 使用 Node.js 模块解析策略
- 支持路径别名 (`@` 映射到 `src` 目录)
- 严格的类型检查

## 路由配置

项目包含以下主要路由：

- `/` - 主页面（提供班级管理和学生信息的快速导航）
- `/classes` - 班级管理页面
- `/students` - 学生管理页面

## API 接口

### 班级管理 API

- `GET /api/v1/classes` - 获取班级列表（支持分页）
- `POST /api/v1/classes` - 创建班级
- `PUT /api/v1/classes/:id` - 更新班级
- `DELETE /api/v1/classes/:id` - 删除班级

### 学生管理 API

- `GET /api/v1/students` - 获取学生列表（支持分页）
- `POST /api/v1/students` - 创建学生档案
- `PUT /api/v1/students/:id` - 更新学生档案
- `DELETE /api/v1/students/:id` - 删除学生档案

## UI 组件

使用 Element Plus 组件库，包含：

- 表单组件：Input、Select、Form、DatePicker 等
- 数据展示：Table、Tag、Descriptions 等
- 反馈组件：Alert、MessageBox、Notification 等
- 导航组件：Pagination、Card 等

## 开发规范

### 代码风格

- 使用 TypeScript 进行类型检查
- 遵循 Vue 3 Composition API 规范
- 使用 `<script setup>` 语法糖

### 组件命名

- 页面组件以 `View` 结尾（如 `HomeView.vue`）

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

## 常见问题

### 开发环境常见问题

1. **模块解析错误**
   - 确保 `tsconfig.json` 中的 `moduleResolution` 设置为 `node`

2. **代理配置不生效**
   - 检查 `vite.config.ts` 中的代理设置
   - 确保后端服务在指定端口运行

3. **API 路径错误**
   - 前端使用的 API 基础路径为 `/api/v1`
   - 确保后端服务也使用相同的路径结构

4. **响应结构不匹配**
   - 所有 API 响应应遵循统一的格式，包含 `code`、`message` 和 `data` 字段
   - 分页响应应包含 `items`、`page`、`pageSize` 和 `total` 字段

### 生产环境部署

1. **资源路径问题**
   - 检查 `vite.config.ts` 中的 `base` 配置
   - 确保静态资源路径正确

## 许可证

[MIT License](LICENSE)

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至 [ thirteenashwbw@gmail.com](mailto:1984885454@qq.com)
