# 校园集成系统 - Web前端

## 项目简介

这是校园集成系统的Web前端项目，基于 Vue 3 + Vite + TypeScript + Vue Router + Pinia 构建。

## 技术栈

- **框架**: Vue 3
- **构建工具**: Vite
- **语言**: TypeScript
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **UI框架**: 待定

## 项目结构

```
web/
├── src/
│   ├── pages/          # 页面组件
│   ├── components/     # 公共组件
│   ├── router/         # 路由配置
│   ├── utils/          # 工具函数
│   └── views/          # 视图组件
├── public/             # 静态资源
└── package.json        # 项目配置
```

## 环境配置

### 开发环境

后端API地址：`http://localhost:8080`

API文档地址：`http://localhost:8080/doc.html` (Knife4j)

### 环境变量

项目使用环境变量来配置API地址：

- `VITE_API_BASE_URL`: 后端API基础地址
- `VITE_API_DOCS_URL`: API文档地址
- `VITE_APP_TITLE`: 应用标题

环境变量文件：
- `.env.development`: 开发环境配置
- `.env.production`: 生产环境配置

## 安装依赖

```bash
npm install
# 或
pnpm install
```

## 启动开发服务器

```bash
npm run dev
# 或
pnpm dev
```

开发服务器将在 `http://localhost:5173` 启动。

## 构建生产版本

```bash
npm run build
# 或
pnpm build
```

构建产物将输出到 `dist` 目录。

## API连接配置

### 代理配置

项目已配置Vite代理，解决开发环境的跨域问题：

- `/api/*` → `http://localhost:8080/api/*`
- `/doc.html` → `http://localhost:8080/doc.html`
- `/v3/api-docs` → `http://localhost:8080/v3/api-docs`

### 请求封装

所有API请求都通过 `@your-org/common` 包中的 `request.ts` 进行封装：

- 自动添加JWT token到请求头
- 统一处理响应格式
- 统一错误处理
- 请求/响应日志（开发环境）

### 使用示例

```typescript
import { login, getUserInfo } from '@your-org/common/api'

// 登录
const result = await login({
  username: 'admin',
  password: '123456'
})

// 获取用户信息
const userInfo = await getUserInfo()
```

## API模块

项目包含以下API模块：

1. **认证模块** (`api/auth`): 登录、注册、用户信息等
2. **校园卡模块** (`api/ecard`): 余额查询、消费记录、游客卡申请等
3. **二手交易模块** (`api/secondhand`): 商品发布、查询、收藏等
4. **兼职模块** (`api/parttime`): 兼职发布、报名、管理等
5. **行程管理模块** (`api/schedule`): 个人/团队行程、同步等

## 开发注意事项

1. **后端服务**: 确保后端服务在 `http://localhost:8080` 运行
2. **数据库**: 确保MySQL数据库已启动并配置正确
3. **跨域**: 开发环境使用Vite代理，生产环境需要后端配置CORS
4. **Token管理**: Token存储在sessionStorage或localStorage中

## 相关链接

- [后端API文档](http://localhost:8080/doc.html)
- [项目根目录README](../README.md)
