# 环境变量配置说明

## 开发环境配置

请在 `packages/web` 目录下创建 `.env.development` 文件，内容如下：

```env
# ==================== 开发环境配置 ====================
# API基础地址
VITE_API_BASE_URL=http://localhost:8080/api

# API文档地址
VITE_API_DOCS_URL=http://localhost:8080/doc.html

# 应用标题
VITE_APP_TITLE=上大学Online
```

## 生产环境配置

请在 `packages/web` 目录下创建 `.env.production` 文件，内容如下：

```env
# ==================== 生产环境配置 ====================
# API基础地址（生产环境需要替换为实际的后端地址）
VITE_API_BASE_URL=https://api.yourschool.com/api

# API文档地址（生产环境通常不对外开放）
VITE_API_DOCS_URL=https://api.yourschool.com/doc.html

# 应用标题
VITE_APP_TITLE=上大学Online
```

## 注意事项

1. `.env` 文件通常会被 `.gitignore` 忽略，不会提交到代码仓库
2. 生产环境部署时，请根据实际后端地址修改 `VITE_API_BASE_URL`
3. 环境变量必须以 `VITE_` 开头才能在客户端代码中访问
