# 前端部署指南

## 📋 部署前准备

### 1. 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 pnpm >= 7.0.0
- 后端服务已启动（默认端口：8080）

### 2. 安装依赖

```bash
# 在项目根目录安装所有依赖
cd vue3-cross-platform
npm install
# 或
pnpm install
```

## 🚀 开发环境部署

### 步骤1: 启动后端服务

确保后端服务已启动并运行在 `http://localhost:8080`

```bash
cd packages/backend
# 使用Maven启动
mvn spring-boot:run
# 或直接运行jar包
java -jar target/campus-system-0.0.1-SNAPSHOT.jar
```

### 步骤2: 配置环境变量

创建 `.env.development` 文件（如果不存在）：

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_DOCS_URL=http://localhost:8080/doc.html
VITE_APP_TITLE=校园集成系统-开发环境
```

### 步骤3: 启动前端开发服务器

```bash
cd packages/web
npm run dev
# 或
pnpm dev
```

前端服务将在 `http://localhost:5173` 启动。

### 步骤4: 验证连接

1. 打开浏览器访问 `http://localhost:5173`
2. 打开浏览器控制台，应该能看到：
   - ✅ Vue 3 + Vite 启动成功！
   - ✅ API地址: http://localhost:8080
   - ✅ API文档: http://localhost:8080/doc.html
3. 访问API文档：`http://localhost:8080/doc.html` 查看所有接口

## 🏗️ 生产环境部署

### 步骤1: 配置生产环境变量

创建 `.env.production` 文件：

```env
VITE_API_BASE_URL=http://your-production-api.com
VITE_API_DOCS_URL=http://your-production-api.com/doc.html
VITE_APP_TITLE=校园集成系统
```

**注意**: 将 `your-production-api.com` 替换为实际的生产环境API地址。

### 步骤2: 构建生产版本

```bash
cd packages/web
npm run build
# 或
pnpm build
```

构建产物将输出到 `dist` 目录。

### 步骤3: 部署到服务器

#### 方式1: 使用Nginx部署

1. 将 `dist` 目录内容复制到Nginx的静态文件目录：

```bash
# 复制构建产物
cp -r dist/* /usr/share/nginx/html/
```

2. 配置Nginx（`/etc/nginx/conf.d/campus-system.conf`）：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /usr/share/nginx/html;
    index index.html;

    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API代理（如果需要）
    location /api {
        proxy_pass http://your-backend-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

3. 重启Nginx：

```bash
sudo nginx -t  # 测试配置
sudo systemctl restart nginx  # 重启服务
```

#### 方式2: 使用Docker部署

创建 `Dockerfile`：

```dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

构建和运行：

```bash
docker build -t campus-system-web .
docker run -d -p 80:80 campus-system-web
```

## 🔧 配置说明

### API地址配置

- **开发环境**: 通过Vite代理自动转发到后端
- **生产环境**: 需要配置 `VITE_API_BASE_URL` 环境变量

### 跨域配置

- **开发环境**: Vite已配置代理，无需额外配置
- **生产环境**: 
  - 如果前后端同域：无需配置
  - 如果前后端不同域：需要后端配置CORS

### Token管理

- Token存储在 `sessionStorage`（关闭浏览器后清除）或 `localStorage`（持久化）
- Token自动添加到所有API请求的 `Authorization` 头中

## 🐛 常见问题

### 1. 无法连接到后端API

**问题**: 前端无法访问后端接口

**解决方案**:
- 检查后端服务是否启动
- 检查 `VITE_API_BASE_URL` 配置是否正确
- 检查网络连接和防火墙设置
- 查看浏览器控制台的错误信息

### 2. 跨域错误

**问题**: 浏览器控制台显示CORS错误

**解决方案**:
- 开发环境：检查Vite代理配置
- 生产环境：确保后端已配置CORS允许前端域名

### 3. Token失效

**问题**: 请求返回401未授权

**解决方案**:
- Token可能已过期，需要重新登录
- 检查Token是否正确存储在localStorage/sessionStorage中
- 检查后端JWT配置是否正确

### 4. 构建失败

**问题**: `npm run build` 失败

**解决方案**:
- 检查Node.js版本是否符合要求
- 清除缓存：`rm -rf node_modules && npm install`
- 检查TypeScript类型错误

## 📚 相关文档

- [项目README](./README.md)
- [后端部署文档](../backend/README.md)
- [API文档](http://localhost:8080/doc.html)

## 🆘 获取帮助

如果遇到问题，请：

1. 查看浏览器控制台的错误信息
2. 查看后端日志
3. 检查网络请求（浏览器开发者工具 -> Network）
4. 参考项目文档

