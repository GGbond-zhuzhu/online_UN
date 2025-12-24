# vue3-cross-platform（校园集成系统）
## 技术栈
- 双端前端：UniApp+Vue3+TS+Pinia（App）/ Vue3+Vite+TS+VueRouter（Web）
- 后端：SpringBoot3.x+MyBatis-Plus+MySQL+JWT+Knife4j

## 核心结构
- packages/app：UniApp 移动端子包（适配小程序/APP）
- packages/common：双端公共核心（组件/API/状态/权限）
- packages/web：Vue3+Vite 网页端子包（PC 端）

## 脚本说明
- pnpm dev:app：启动 App 端开发模式
- pnpm dev:web：启动 Web 端开发模式
- pnpm build:app：构建 App 端（UniApp 打包）
- pnpm build:web：构建 Web 端（Vite 打包）

## 微信小程序开发

### 生成微信小程序文件夹

微信小程序文件夹已成功生成在 `packages/app/dist` 目录下。

#### 构建命令
```bash
# 在项目根目录执行
cd packages/app
npm run build:mp-weixin

# 或者从根目录执行
pnpm build:mp-weixin
```

#### 输出目录
构建完成后，微信小程序的所有文件都在 `packages/app/dist` 目录下，包括：
- `app.js`、`app.json`、`App.wxml`、`app.wxss` - 小程序主文件
- `project.config.json` - 微信开发者工具配置文件
- `pages/` - 所有页面的编译后文件（.js, .json, .wxml, .wxss）
- `common/`、`modules/`、`static/` - 公共模块和静态资源

#### 使用微信开发者工具打开

1. **打开微信开发者工具**
   - 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)

2. **导入项目**
   - 打开微信开发者工具
   - 选择"导入项目"
   - 项目目录选择：`vue3-cross-platform/packages/app/dist`
   - AppID：`wxe16c7bf9c5a51a85`（已在 `project.config.json` 中配置）
   - 项目名称：`上大学Online`

3. **开始开发**
   - 导入后即可在微信开发者工具中预览和调试小程序
   - 修改源代码后，需要重新运行构建命令生成新的小程序文件

#### 开发模式（实时编译）

如果需要实时编译，可以使用开发模式：
```bash
cd packages/app
npm run dev:mp-weixin
```

开发模式会监听文件变化并自动重新编译，但需要配置微信开发者工具指向正确的输出目录。

#### 注意事项

1. **AppID 配置**：当前配置的 AppID 为 `wxe16c7bf9c5a51a85`，如需更换，请修改 `packages/app/manifest.json` 中的 `mp-weixin.appid` 字段

2. **重新构建**：每次修改源代码后，需要重新运行构建命令才能看到最新效果

3. **静态资源**：静态资源已自动复制到 `dist/static` 目录，可直接使用

4. **主题配置**：主题文件 `theme.json` 已自动复制到 `dist` 目录
