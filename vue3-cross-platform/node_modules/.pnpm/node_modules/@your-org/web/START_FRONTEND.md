# 启动前端服务指南

## ✅ 当前状态检查

### 后端服务状态
- ✅ **后端已启动**: `http://localhost:8080` (端口8080正在监听)
- ✅ **API文档可用**: http://localhost:8080/doc.html
- ✅ **百度AI已配置**: 人脸识别功能已初始化

### 前端配置状态
- ✅ **代理配置**: Vite已配置代理，自动转发API请求到后端
- ✅ **API封装**: 已配置axios请求封装，自动添加Token
- ✅ **跨域处理**: 已通过Vite代理解决

## 🚀 启动前端服务

### 在Git Bash中执行：

```bash
cd vue3-cross-platform/packages/web
npm run dev
```

或者使用pnpm：

```bash
cd vue3-cross-platform/packages/web
pnpm dev
```

### 启动成功后

前端服务将在 **http://localhost:5173** 启动，浏览器会自动打开。

## ✅ 验证前后端连接

### 1. 检查控制台输出

启动前端后，浏览器控制台应该显示：

```
🚀 Vue 3 + Vite 启动成功！
📡 API地址: http://localhost:8080
📚 API文档: http://localhost:8080/doc.html
```

### 2. 测试API调用

打开浏览器开发者工具（F12），切换到"网络"标签页：

1. **测试登录接口**：
   - 访问登录页面
   - 输入用户名和密码
   - 点击登录
   - 在"网络"标签页中应该能看到：
     - 请求URL: `http://localhost:5173/api/user/login`
     - 实际转发到: `http://localhost:8080/api/user/login`
     - 状态码: 200（成功）

2. **查看请求日志**：
   - 控制台会显示请求和响应日志：
     ```
     📤 请求发送: { url: '/api/user/login', method: 'post', ... }
     📥 响应接收: { url: '/api/user/login', code: 200, msg: '登录成功', ... }
     ```

## 🔧 前端API配置说明

### 1. 代理配置（vite.config.ts）

```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  }
}
```

**作用**：
- 所有 `/api/*` 请求自动转发到 `http://localhost:8080/api/*`
- 解决开发环境的跨域问题
- 无需修改前端代码中的API地址

### 2. 请求封装（common/src/utils/request.ts）

**功能**：
- ✅ 自动添加JWT Token到请求头
- ✅ 统一处理响应格式
- ✅ 统一错误处理
- ✅ 自动跳转登录（Token过期时）
- ✅ 开发环境请求/响应日志

**Token管理**：
- Token存储在 `localStorage` 中
- 登录成功后自动保存Token
- 后续请求自动携带Token

## 📋 已实现的API接口映射

### 用户管理
- ✅ `POST /api/user/login` - 用户登录
- ✅ `POST /api/user/register` - 用户注册
- ✅ `GET /api/user/info` - 获取用户信息
- ✅ `PUT /api/user/info` - 更新用户信息
- ✅ `POST /api/user/change-password` - 修改密码
- ✅ `POST /api/user/logout` - 退出登录

### 身份认证
- ✅ `POST /api/auth/email/send-code` - 发送邮箱验证码
- ✅ `POST /api/auth/email/login` - 邮箱登录
- ✅ `POST /api/auth/visitor/face-detect` - 游客刷脸活体检测

### 浏览记录
- ✅ `POST /api/secondhand/browse/{id}` - 记录商品浏览
- ✅ `GET /api/secondhand/browse-history` - 获取浏览记录
- ✅ `POST /api/parttime/browse/{id}` - 记录岗位浏览
- ✅ `GET /api/parttime/browse-history` - 获取浏览记录

### 收藏管理
- ✅ `POST /api/parttime/favorite/{id}` - 收藏兼职
- ✅ `DELETE /api/parttime/favorite/{id}` - 取消收藏
- ✅ `GET /api/parttime/favorites` - 获取收藏列表

### 消息和聊天
- ✅ `GET /api/messages/list` - 获取消息列表
- ✅ `PUT /api/messages/{id}/read` - 标记已读
- ✅ `GET /api/chat/conversations` - 获取会话列表
- ✅ `POST /api/chat/send` - 发送消息

## 🧪 快速测试流程

### 1. 启动前端
```bash
cd vue3-cross-platform/packages/web
npm run dev
```

### 2. 访问前端
打开浏览器访问：http://localhost:5173

### 3. 测试登录
- 打开登录页面
- 如果没有账号，先注册
- 登录后查看Token是否保存成功

### 4. 测试功能
- 浏览商品/岗位（自动记录浏览记录）
- 查看浏览记录页面
- 收藏/取消收藏兼职
- 查看消息中心
- 测试聊天功能

## ⚠️ 注意事项

1. **后端必须先启动**
   - 确保后端服务在 `http://localhost:8080` 运行
   - 如果后端端口不同，需要修改 `vite.config.ts` 中的代理配置

2. **Token管理**
   - Token存储在localStorage中
   - 清除浏览器缓存会清除Token
   - Token过期会自动跳转到登录页

3. **网络请求**
   - 开发环境通过Vite代理转发请求
   - 生产环境需要后端配置CORS
   - 查看网络请求时，URL显示为 `/api/...`，实际转发到后端

4. **错误处理**
   - 401错误：Token过期，自动跳转登录
   - 403错误：权限不足
   - 500错误：服务器错误，查看后端日志

## 🎯 下一步

1. ✅ 后端服务已启动
2. 🚀 **启动前端服务**: `cd vue3-cross-platform/packages/web && npm run dev`
3. 🌐 **访问前端**: http://localhost:5173
4. 🧪 **测试功能**: 登录、注册、浏览、收藏等
5. 📊 **查看日志**: 浏览器控制台和网络面板

## 📚 相关文档

- [前后端联调准备就绪](./FRONTEND_BACKEND_READY.md)
- [API集成文档](./API_INTEGRATION.md)
- [后端API文档](../backend/API_DOCUMENTATION.md)

