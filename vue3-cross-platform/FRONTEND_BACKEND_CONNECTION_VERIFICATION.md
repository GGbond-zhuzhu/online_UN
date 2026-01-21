# 前后端连接配置验证报告

## 📋 验证时间
2024-01-15

## ✅ 配置检查结果

### 1. Web端配置 ✅

#### Vite代理配置
**文件：** `packages/web/vite.config.ts`

**配置内容：**
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '/api')
  },
  '/doc.html': {
    target: 'http://localhost:8080',
    changeOrigin: true
  },
  '/v3/api-docs': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

**状态：** ✅ 配置正确
- ✅ 代理路径：`/api` → `http://localhost:8080/api`
- ✅ 支持跨域：`changeOrigin: true`
- ✅ API文档代理：已配置 `/doc.html` 和 `/v3/api-docs`
- ✅ 代理日志：已配置错误和请求日志

#### BaseURL配置
**文件：** `packages/web/vite.config.ts`

**配置内容：**
```typescript
'import.meta.env.VITE_API_BASE_URL': JSON.stringify(
  process.env.VITE_API_BASE_URL || 'http://localhost:8080'
)
```

**状态：** ✅ 配置正确
- ✅ 默认值：`http://localhost:8080`
- ✅ 支持环境变量覆盖

### 2. App端配置 ✅

#### BaseURL配置
**文件：** `packages/common/src/utils/request.ts`

**配置内容：**
```typescript
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
```

**状态：** ✅ 配置正确
- ✅ 默认值：`http://localhost:8080`
- ✅ 支持环境变量覆盖
- ⚠️ **注意**：微信小程序不能使用 `localhost`，需要使用实际IP地址

#### UniApp请求适配器
**文件：** `packages/common/src/utils/request.ts`

**功能：**
- ✅ 自动添加 JWT Token 到请求头
- ✅ 统一处理响应格式
- ✅ 统一错误处理
- ✅ 支持GET参数拼接（兼容微信小程序）

**状态：** ✅ 配置正确

### 3. 后端配置 ✅

#### CORS配置
**文件：** `packages/backend/src/main/java/com/yourschool/campussystem/config/WebMvcConfig.java`

**配置内容：**
```java
registry.addMapping("/**")
    .allowedOriginPatterns("*")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true)
    .maxAge(3600);
```

**状态：** ✅ 配置正确
- ✅ 允许所有源：`allowedOriginPatterns("*")`
- ✅ 支持所有HTTP方法
- ✅ 允许所有请求头
- ✅ 允许携带凭证（Cookie等）

#### 拦截器配置
**文件：** `packages/backend/src/main/java/com/yourschool/campussystem/config/WebMvcConfig.java`

**排除路径：**
```java
.excludePathPatterns(
    "/api/user/login",      // 登录接口
    "/api/user/register",  // 注册接口
    "/api/common/**",       // 通用接口
    "/api/auth/visitor/**", // 游客接口
    "/api/auth/email/**",   // 邮箱登录接口
    "/doc.html",            // API文档
    "/swagger-ui/**",       // Swagger UI
    "/v3/api-docs/**",      // API文档JSON
    "/webjars/**",          // 静态资源
    "/uploads/**",          // 上传文件
    "/files/**"             // 文件访问
)
```

**状态：** ✅ 配置正确
- ✅ 登录/注册接口已排除
- ✅ 邮箱登录接口已排除
- ✅ 通用接口已排除
- ✅ API文档路径已排除

#### 服务器端口配置
**文件：** `packages/backend/src/main/resources/application.properties`

**配置内容：**
```properties
server.port=8080
```

**状态：** ✅ 配置正确
- ✅ 端口：8080
- ✅ 与前端配置一致

## 🔍 API路径匹配检查

### ✅ 已验证的接口路径

| 前端调用 | 后端接口 | 状态 |
|---------|---------|------|
| `/api/user/login` | `/api/user/login` | ✅ 匹配 |
| `/api/user/register` | `/api/user/register` | ✅ 匹配 |
| `/api/user/info` | `/api/user/info` | ✅ 匹配 |
| `/api/user/change-password` | `/api/user/change-password` | ✅ 匹配 |
| `/api/user/logout` | `/api/user/logout` | ✅ 匹配 |
| `/api/auth/email/send-code` | `/api/auth/email/send-code` | ✅ 匹配 |
| `/api/auth/email/login` | `/api/auth/email/login` | ✅ 匹配 |
| `/api/ecard/info` | `/api/ecard/info` | ✅ 匹配 |
| `/api/secondhand/list` | `/api/secondhand/list` | ✅ 匹配 |
| `/api/parttime/list` | `/api/parttime/list` | ✅ 匹配 |
| `/api/schedule/team/sync/{id}` | `/api/schedule/team/sync/{id}` | ✅ 匹配（已修复） |

## 🔧 请求/响应格式检查

### ✅ 请求格式

#### Web端（axios）
- ✅ Content-Type: `application/json;charset=UTF-8`
- ✅ 自动添加 Authorization: `Bearer {token}`
- ✅ 请求体：JSON格式

#### App端（uni.request）
- ✅ Content-Type: `application/json;charset=UTF-8`
- ✅ 自动添加 Authorization: `Bearer {token}`
- ✅ 请求体：JSON格式
- ✅ GET参数：手动拼接URL（兼容微信小程序）

### ✅ 响应格式

**统一响应格式：**
```typescript
{
  code: number,    // 200表示成功，其他表示错误
  msg: string,     // 提示信息
  data: T          // 响应数据
}
```

**状态：** ✅ 格式一致
- ✅ 前端自动解析 `data` 字段
- ✅ 统一错误处理
- ✅ 401自动处理（演示模式已禁用跳转）

## 🧪 连接测试步骤

### 1. 启动后端服务

```bash
cd packages/backend
mvn spring-boot:run
# 或
./mvnw spring-boot:run
```

**验证：**
- ✅ 服务启动在 `http://localhost:8080`
- ✅ 访问 `http://localhost:8080/doc.html` 可以看到API文档

### 2. 启动Web端服务

```bash
cd packages/web
npm run dev
# 或
pnpm dev
```

**验证：**
- ✅ 服务启动在 `http://localhost:5173`
- ✅ 浏览器自动打开
- ✅ 控制台可以看到代理日志

### 3. 启动App端服务（可选）

```bash
cd packages/app
npm run dev:mp-weixin
# 或
pnpm dev:mp-weixin
```

**验证：**
- ✅ 微信开发者工具可以打开项目
- ⚠️ **注意**：需要配置实际IP地址，不能使用 `localhost`

### 4. 测试API连接

#### 测试登录接口
```javascript
// 在浏览器控制台执行
fetch('/api/user/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'test',
    password: '123456'
  })
})
.then(res => res.json())
.then(data => console.log('登录成功:', data))
.catch(err => console.error('登录失败:', err))
```

#### 测试获取用户信息（需要先登录）
```javascript
// 在浏览器控制台执行（需要先登录获取token）
fetch('/api/user/info', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN_HERE'
  }
})
.then(res => res.json())
.then(data => console.log('用户信息:', data))
.catch(err => console.error('获取失败:', err))
```

## ⚠️ 注意事项

### 1. 微信小程序特殊配置

**问题：** 微信小程序不能使用 `localhost` 访问后端

**解决方案：**
1. 获取本机IP地址（Windows: `ipconfig`, Mac/Linux: `ifconfig`）
2. 在 `.env` 文件中配置：
   ```
   VITE_API_BASE_URL=http://192.168.1.100:8080
   ```
3. 确保手机和电脑在同一局域网

### 2. 跨域问题

**Web端：** 使用Vite代理，不存在跨域问题
**App端：** 后端已配置CORS，支持跨域

### 3. Token存储

**Web端：** 使用 `localStorage` 存储Token
**App端：** 使用 `uni.setStorageSync` 存储Token

### 4. 环境变量配置

**开发环境：**
- Web端：使用Vite代理，不需要配置环境变量
- App端：需要配置 `VITE_API_BASE_URL`

**生产环境：**
- 需要配置实际的后端API地址
- 建议使用环境变量或配置文件

## 📊 验证结果总结

| 配置项 | Web端 | App端 | 后端 | 状态 |
|--------|-------|-------|------|------|
| BaseURL配置 | ✅ | ✅ | ✅ | ✅ 通过 |
| 代理配置 | ✅ | N/A | N/A | ✅ 通过 |
| CORS配置 | N/A | N/A | ✅ | ✅ 通过 |
| 拦截器配置 | N/A | N/A | ✅ | ✅ 通过 |
| Token处理 | ✅ | ✅ | ✅ | ✅ 通过 |
| 请求格式 | ✅ | ✅ | ✅ | ✅ 通过 |
| 响应格式 | ✅ | ✅ | ✅ | ✅ 通过 |
| 错误处理 | ✅ | ✅ | ✅ | ✅ 通过 |

## 🚀 下一步行动

1. ✅ **配置检查** - 已完成
2. ⏳ **实际测试** - 需要启动服务进行测试
3. ⏳ **性能优化** - 可以优化请求超时时间等
4. ⏳ **安全加固** - 生产环境需要加强安全配置

## 📚 相关文档

- [API接口检查与修复报告](./API_INTERFACE_CHECK_AND_FIX.md)
- [前后端交互检查总结](./FRONTEND_BACKEND_INTEGRATION_SUMMARY.md)
- [Web端快速开始](../packages/web/QUICK_START.md)
- [后端API文档](../packages/backend/API_DOCUMENTATION.md)

---

**验证时间：** 2024-01-15  
**验证状态：** ✅ 所有配置检查通过，待实际测试验证

