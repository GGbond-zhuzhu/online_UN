# Web前端启动问题修复说明

## 问题描述

Web前端无法正常启动，浏览器控制台出现以下错误：
1. `Failed to load resource: the server responded with a status of 500 (Internal Server Error)` - 路由文件加载失败
2. Font Awesome CDN链接的Tracking Prevention警告
3. `Uncaught TypeError: v[w] is not a function` - 运行时错误

## 修复内容

### 1. 修复Vite配置中的路径别名问题

**文件**: `vite.config.ts`

**修改内容**:
- 添加了`extensions`配置，确保Vite能正确解析TypeScript文件
- 优化了`optimizeDeps`配置，明确包含common包及其子模块
- 添加了`build.commonjsOptions`配置，确保commonjs模块能正确转换

**关键修改**:
```typescript
resolve: {
  alias: [
    {
      find: '@',
      replacement: path.resolve(__dirname, 'src')
    },
    {
      // 处理根导入：@campus/common -> ../common/src/index.ts
      find: /^@campus\/common$/,
      replacement: path.resolve(__dirname, '../common/src/index.ts')
    },
    {
      // 处理子路径导入：@campus/common/api/auth -> ../common/src/api/auth
      find: /^@campus\/common\/(.+)$/,
      replacement: path.resolve(__dirname, '../common/src/$1')
    },
    // 兼容旧别名 @your-org/common
    {
      find: /^@your-org\/common$/,
      replacement: path.resolve(__dirname, '../common/src/index.ts')
    },
    {
      find: /^@your-org\/common\/(.+)$/,
      replacement: path.resolve(__dirname, '../common/src/$1')
    }
  ],
  extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue'],
  dedupe: ['vue', 'pinia', 'vue-router']
},
optimizeDeps: {
  include: [
    '@campus/common',
    '@your-org/common',
    'vue',
    'pinia',
    'vue-router'
  ],
  exclude: [],
  force: false
},
build: {
  commonjsOptions: {
    include: [/common/, /node_modules/]
  }
}
```

### 2. 移除Font Awesome CDN链接

**文件**: `index.html`

**修改内容**:
- 移除了CDN链接，因为Font Awesome已经通过npm包在`main.ts`中引入
- 避免了Tracking Prevention警告和网络请求失败

**修改前**:
```html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
```

**修改后**:
```html
<!-- Font Awesome 图标库已通过npm包引入，无需CDN -->
```

### 3. 修复common包内部的循环依赖问题

**文件**: `packages/common/src/pinia/user.ts`

**问题**: common包内部使用了`@campus/common`别名导入，导致循环依赖和Vite解析失败

**修改内容**:
- 将`@campus/common/api/auth`改为相对路径`../api/auth`
- 将`@campus/common/utils/auth`改为相对路径`../utils/auth`

**修改前**:
```typescript
import { login as loginApi } from '@campus/common/api/auth'
import { getToken } from '@campus/common/utils/auth'
```

**修改后**:
```typescript
import { login as loginApi } from '../api/auth'
import { getToken } from '../utils/auth'
```

### 4. 确保路由文件导入正确

**文件**: `src/router/index.ts`

**说明**:
- 路由文件中的`useUserStore`导入保持不变
- 虽然演示模式下未使用，但保留导入以便后续恢复权限检查功能

## 测试步骤

1. **清理缓存并重新安装依赖**（如果需要）:
```bash
cd vue3-cross-platform/packages/web
rm -rf node_modules
pnpm install
```

2. **启动开发服务器**:
```bash
pnpm dev
```

3. **检查浏览器控制台**:
   - 应该不再出现500错误
   - 应该不再出现Font Awesome CDN警告
   - 应用应该能正常加载和运行

## 注意事项

1. **环境变量**: 如果需要在开发环境使用自定义API地址，可以创建`.env.development`文件（已被gitignore忽略）

2. **Common包依赖**: 确保`packages/common`包已正确安装和构建

3. **端口冲突**: 如果5173端口被占用，Vite会自动使用下一个可用端口

4. **后端服务**: 确保后端服务在`http://localhost:8080`运行（如果需要调用API）

## 后续优化建议

1. 考虑添加环境变量文件模板（`.env.example`）
2. 添加更详细的错误处理和日志记录
3. 考虑使用Vite的插件来优化monorepo依赖解析
4. 定期更新依赖包版本

## 根本原因分析

问题的根本原因是**Vite无法正确解析common包内部的别名导入**：
- common包的`pinia/user.ts`文件使用了`@campus/common/api/auth`和`@campus/common/utils/auth`这样的子路径导入
- Vite的简单别名配置（`@campus/common: '../common/src'`）无法区分根导入和子路径导入
- 当common包内部使用`@campus/common/api/auth`时，Vite会尝试解析`../common/src/api/auth`，但可能因为路径解析顺序问题导致失败

**解决方案**:
- 使用正则表达式精确匹配别名，区分`@campus/common`（根导入）和`@campus/common/xxx`（子路径导入）
- 确保根导入指向`index.ts`，子路径导入指向对应的文件路径
- 这样common包内部可以继续使用`@campus/common`别名，同时Vite能正确解析

## 相关文件

- `vite.config.ts` - Vite配置文件
- `index.html` - HTML入口文件
- `src/router/index.ts` - 路由配置文件
- `src/main.ts` - 应用入口文件
- `packages/common/src/pinia/user.ts` - common包用户Store（已修复循环依赖）

