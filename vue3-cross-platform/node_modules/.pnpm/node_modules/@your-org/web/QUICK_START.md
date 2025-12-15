# 快速开始指南

## 🚀 5分钟快速启动

### 前置条件

- ✅ Node.js >= 16.0.0 已安装
- ✅ 后端服务已启动（端口8080）
- ✅ MySQL数据库已配置并运行

### 步骤1: 安装依赖

```bash
# 在项目根目录
cd vue3-cross-platform
npm install
# 或
pnpm install
```

### 步骤2: 配置环境变量

创建 `.env.development` 文件（参考 `ENV_SETUP.md`）：

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_DOCS_URL=http://localhost:8080/doc.html
VITE_APP_TITLE=校园集成系统-开发环境
```

### 步骤3: 启动开发服务器

```bash
cd packages/web
npm run dev
```

### 步骤4: 访问应用

- 前端地址: http://localhost:5173
- API文档: http://localhost:8080/doc.html

## ✅ 验证连接

打开浏览器控制台，应该看到：

```
🚀 Vue 3 + Vite 启动成功！
📡 API地址: http://localhost:8080
📚 API文档: http://localhost:8080/doc.html
```

## 🔍 测试API连接

在浏览器控制台执行：

```javascript
// 测试API连接（需要先登录获取token）
fetch('http://localhost:8080/api/user/info', {
  headers: {
    'Authorization': 'Bearer YOUR_TOKEN'
  }
})
.then(res => res.json())
.then(data => console.log('API响应:', data))
```

## 📝 下一步

1. 查看 [README.md](./README.md) 了解项目结构
2. 查看 [DEPLOYMENT.md](./DEPLOYMENT.md) 了解部署详情
3. 查看 [API文档](http://localhost:8080/doc.html) 了解后端接口

## ❓ 遇到问题？

1. 检查后端服务是否运行：访问 http://localhost:8080/doc.html
2. 检查环境变量配置是否正确
3. 查看浏览器控制台的错误信息
4. 参考 [DEPLOYMENT.md](./DEPLOYMENT.md) 中的常见问题部分

