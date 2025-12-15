# Windows 运行前端项目指南

## 📋 前置要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 pnpm >= 7.0.0
- 后端服务已启动（端口 8080）

## 🚀 快速开始

### 方式一：使用 npm（推荐）

#### PowerShell 命令

```powershell
# 1. 进入项目根目录
cd vue3-cross-platform

# 2. 安装所有依赖（根目录 + 子包）
npm install --legacy-peer-deps

# 3. 安装 web 包依赖
cd packages\web
npm install --legacy-peer-deps

# 4. 安装 common 包依赖
cd ..\common
npm install --legacy-peer-deps

# 5. 返回 web 包目录，启动开发服务器
cd ..\web
npm run dev
```

#### Git Bash 命令

```bash
# 1. 进入项目根目录
cd vue3-cross-platform

# 2. 安装所有依赖（根目录 + 子包）
npm install --legacy-peer-deps

# 3. 安装 web 包依赖
cd packages/web
npm install --legacy-peer-deps

# 4. 安装 common 包依赖
cd ../common
npm install --legacy-peer-deps

# 5. 返回 web 包目录，启动开发服务器
cd ../web
npm run dev
```

### 方式二：使用 pnpm（更快）

#### PowerShell 命令

```powershell
# 1. 进入项目根目录
cd vue3-cross-platform

# 2. 安装所有依赖（pnpm 会自动处理 workspace）
pnpm install

# 3. 启动 web 端开发服务器
pnpm --filter @your-org/web dev

# 或者直接进入 web 目录
cd packages\web
pnpm dev
```

#### Git Bash 命令

```bash
# 1. 进入项目根目录
cd vue3-cross-platform

# 2. 安装所有依赖
pnpm install

# 3. 启动 web 端开发服务器
pnpm --filter @your-org/web dev

# 或者直接进入 web 目录
cd packages/web
pnpm dev
```

## 📝 详细步骤说明

### 步骤 1: 安装依赖

#### 方法 A: 使用根目录脚本（推荐）

**PowerShell:**
```powershell
cd vue3-cross-platform
npm run install:all
```

**Git Bash:**
```bash
cd vue3-cross-platform
npm run install:all
```

#### 方法 B: 手动安装

**PowerShell:**
```powershell
# 根目录
cd vue3-cross-platform
npm install --legacy-peer-deps

# web 包
cd packages\web
npm install --legacy-peer-deps

# common 包
cd ..\common
npm install --legacy-peer-deps
```

**Git Bash:**
```bash
# 根目录
cd vue3-cross-platform
npm install --legacy-peer-deps

# web 包
cd packages/web
npm install --legacy-peer-deps

# common 包
cd ../common
npm install --legacy-peer-deps
```

### 步骤 2: 配置环境变量

在 `packages/web` 目录下创建 `.env.development` 文件：

**PowerShell:**
```powershell
cd packages\web
@"
VITE_API_BASE_URL=http://localhost:8080
VITE_API_DOCS_URL=http://localhost:8080/doc.html
VITE_APP_TITLE=校园集成系统-开发环境
"@ | Out-File -FilePath .env.development -Encoding utf8
```

**Git Bash:**
```bash
cd packages/web
cat > .env.development << 'EOF'
VITE_API_BASE_URL=http://localhost:8080
VITE_API_DOCS_URL=http://localhost:8080/doc.html
VITE_APP_TITLE=校园集成系统-开发环境
EOF
```

### 步骤 3: 启动开发服务器

#### 方式 A: 使用根目录脚本

**PowerShell:**
```powershell
cd vue3-cross-platform
npm run dev:web
```

**Git Bash:**
```bash
cd vue3-cross-platform
npm run dev:web
```

#### 方式 B: 直接进入 web 目录

**PowerShell:**
```powershell
cd vue3-cross-platform\packages\web
npm run dev
```

**Git Bash:**
```bash
cd vue3-cross-platform/packages/web
npm run dev
```

## 🎯 启动成功标志

启动成功后，你应该看到：

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help
```

## 📱 访问应用

- **前端地址**: http://localhost:5173
- **API文档**: http://localhost:8080/doc.html

## 🔧 常用命令

### 开发模式

**PowerShell:**
```powershell
# 启动 web 端
cd vue3-cross-platform\packages\web
npm run dev

# 启动 app 端（UniApp）
cd vue3-cross-platform\packages\app
npm run dev
```

**Git Bash:**
```bash
# 启动 web 端
cd vue3-cross-platform/packages/web
npm run dev

# 启动 app 端（UniApp）
cd vue3-cross-platform/packages/app
npm run dev
```

### 构建生产版本

**PowerShell:**
```powershell
cd vue3-cross-platform\packages\web
npm run build
```

**Git Bash:**
```bash
cd vue3-cross-platform/packages/web
npm run build
```

### 清理并重新安装

**PowerShell:**
```powershell
# 删除 node_modules
cd vue3-cross-platform
Remove-Item -Recurse -Force node_modules
Remove-Item -Recurse -Force packages\web\node_modules
Remove-Item -Recurse -Force packages\common\node_modules

# 重新安装
npm run install:all
```

**Git Bash:**
```bash
# 删除 node_modules
cd vue3-cross-platform
rm -rf node_modules
rm -rf packages/web/node_modules
rm -rf packages/common/node_modules

# 重新安装
npm run install:all
```

## ⚠️ 常见问题

### 1. 端口被占用

如果 5173 端口被占用，Vite 会自动尝试下一个端口（5174, 5175...）

或者手动指定端口：

**PowerShell:**
```powershell
cd packages\web
npm run dev -- --port 3000
```

**Git Bash:**
```bash
cd packages/web
npm run dev -- --port 3000
```

### 2. 依赖安装失败

如果遇到依赖安装问题，尝试：

**PowerShell:**
```powershell
# 清除缓存
npm cache clean --force

# 使用 legacy-peer-deps
npm install --legacy-peer-deps
```

**Git Bash:**
```bash
# 清除缓存
npm cache clean --force

# 使用 legacy-peer-deps
npm install --legacy-peer-deps
```

### 3. 找不到模块错误

确保所有依赖都已安装：

**PowerShell:**
```powershell
cd vue3-cross-platform
npm run install:all
```

**Git Bash:**
```bash
cd vue3-cross-platform
npm run install:all
```

### 4. 后端连接失败

确保后端服务已启动：

```bash
# 检查后端是否运行
curl http://localhost:8080/doc.html
```

## 📚 相关文档

- [快速开始指南](./packages/web/QUICK_START.md)
- [部署指南](./packages/web/DEPLOYMENT.md)
- [API集成说明](./packages/web/API_INTEGRATION.md)

## 💡 提示

- 使用 `Ctrl + C` 停止开发服务器
- 修改代码后会自动热重载
- 查看浏览器控制台了解详细日志
- 开发环境支持 Source Map，方便调试

