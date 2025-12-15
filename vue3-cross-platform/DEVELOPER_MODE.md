# 开发者模式使用说明

## 📋 概述

开发者模式是一个特殊的权限模式，允许开发者绕过所有权限检查，访问和测试系统中的所有功能和界面。这对于开发和调试非常有用。

## ✨ 功能特性

- ✅ **绕过所有权限检查**：开发者可以访问所有需要登录或特定角色的页面
- ✅ **拥有所有角色权限**：自动通过所有角色权限验证
- ✅ **拥有所有功能权限**：可以执行所有操作（浏览、申请、发布等）
- ✅ **跨平台支持**：同时支持Web端和App端（uni-app）

## 🚀 如何启用开发者模式

### 方法一：通过浏览器控制台（Web端）- 推荐 ⭐

这是最简单直接的方法：

1. 打开浏览器开发者工具（F12）
2. 切换到"控制台"（Console）标签页
3. 输入以下代码并回车：

```javascript
// 方式1：最简单的方法（推荐）
localStorage.setItem('campus_system_developer_mode', 'true');
location.reload();
```

执行后页面会自动刷新，开发者模式就会生效。

**验证是否启用成功**：
刷新后，在控制台输入：
```javascript
console.log('开发者模式:', localStorage.getItem('campus_system_developer_mode'));
// 应该输出: 开发者模式: true
```

### 方法二：通过Vue DevTools（如果已安装）

如果你安装了Vue DevTools浏览器扩展，可以：

1. 打开Vue DevTools
2. 找到Pinia标签页
3. 找到`user` store
4. 调用`enableDeveloperMode()`方法

或者直接在控制台执行：

```javascript
// 尝试通过Vue实例获取store（需要Vue DevTools）
const app = document.querySelector('#app').__vue_app__;
if (app) {
  const pinia = app.config.globalProperties.$pinia;
  if (pinia) {
    const { useUserStore } = await import('/src/common/src/pinia/user.ts');
    const userStore = useUserStore();
    userStore.enableDeveloperMode();
  }
}
```

**注意**：这种方式比较复杂，推荐使用方法一。

### 方法三：在代码中临时添加（开发环境）

在应用的入口文件中（如 `main.ts` 或 `App.vue`）添加：

```typescript
// 仅在开发环境启用
if (import.meta.env.DEV) {
  import('@campus/common').then(({ useUserStore }) => {
    const userStore = useUserStore();
    // 取消注释下面这行来启用开发者模式
    // userStore.enableDeveloperMode();
  });
}
```

### 方法四：App端（uni-app）

在uni-app中，可以通过以下方式启用：

```javascript
// 在App.vue或main.js中
import { useUserStore } from '@/common/store/user';

// 开发环境自动启用
if (process.env.NODE_ENV === 'development') {
  const userStore = useUserStore();
  // 取消注释下面这行来启用开发者模式
  // userStore.enableDeveloperMode();
}
```

或者直接在控制台执行：

```javascript
// 设置存储
uni.setStorageSync('campus_system_developer_mode', 'true');
// 重新加载应用
uni.reLaunch({ url: '/pages/index/index' });
```

## 🔧 如何禁用开发者模式

### Web端（最简单）

在浏览器控制台执行：

```javascript
// 直接删除localStorage并刷新页面
localStorage.removeItem('campus_system_developer_mode');
location.reload();
```

或者使用书签栏脚本（如果已创建）一键切换。

### App端

```javascript
// 方式1：通过store
const userStore = useUserStore();
userStore.disableDeveloperMode();

// 方式2：直接删除存储
uni.removeStorageSync('campus_system_developer_mode');
uni.reLaunch({ url: '/pages/index/index' });
```

## 📝 使用示例

### 检查开发者模式状态

**在浏览器控制台**：

```javascript
// 方式1：直接检查localStorage
console.log('开发者模式:', localStorage.getItem('campus_system_developer_mode') === 'true' ? '✅ 已启用' : '❌ 已禁用');

// 方式2：检查页面中的实际状态（需要页面已加载）
// 注意：这种方式需要在Vue组件中才能使用
```

### 快速切换脚本

创建一个可以复制粘贴的脚本：

```javascript
// 一键切换开发者模式（复制到控制台执行）
(function(){
  const key = 'campus_system_developer_mode';
  const isEnabled = localStorage.getItem(key) === 'true';
  localStorage.setItem(key, isEnabled ? 'false' : 'true');
  console.log('✅ 开发者模式已' + (isEnabled ? '禁用' : '启用'));
  console.log('🔄 正在刷新页面...');
  setTimeout(() => location.reload(), 500);
})();
```

## ⚠️ 注意事项

1. **仅用于开发环境**：开发者模式应该只在开发和测试时使用，**不要在生产环境中启用**
2. **安全性**：开发者模式会绕过所有权限检查，请确保只在安全的环境中使用
3. **存储持久化**：开发者模式状态会保存在localStorage（Web）或uni存储（App）中，清除浏览器数据会清除该状态
4. **不影响后端**：开发者模式只影响前端权限检查，后端API的权限验证仍然有效

## 🔍 技术实现

### 权限检查逻辑

开发者模式在以下位置生效：

1. **路由守卫**：`packages/web/src/router/index.ts` 和 `packages/app/src/router/index.ts`
   - 开发者模式会绕过所有路由权限检查

2. **权限检查方法**：`packages/common/src/pinia/user.ts`
   - `hasPermission()`: 开发者模式返回 `true`
   - `hasRole()`: 开发者模式返回 `true`

3. **用户状态管理**：
   - `isDeveloper`: 计算属性，返回开发者模式状态
   - `enableDeveloperMode()`: 启用开发者模式
   - `disableDeveloperMode()`: 禁用开发者模式
   - `toggleDeveloperMode()`: 切换开发者模式

### 存储键名

- Web端：`campus_system_developer_mode` (localStorage)
- App端：`campus_system_developer_mode` (uni.setStorageSync)

## 🎯 快速启用脚本

### 方法1：浏览器书签栏脚本（一键切换）

创建一个书签栏脚本，点击即可启用/禁用开发者模式：

1. 在浏览器中新建书签
2. 名称填写：`切换开发者模式`
3. 网址填写以下代码：

```javascript
javascript:(function(){
  const key = 'campus_system_developer_mode';
  const current = localStorage.getItem(key) === 'true';
  localStorage.setItem(key, current ? 'false' : 'true');
  alert('开发者模式已' + (current ? '禁用' : '启用') + '，页面将自动刷新');
  location.reload();
})();
```

使用时只需点击这个书签，就会自动切换开发者模式并刷新页面。

### 方法2：控制台快捷命令

在浏览器控制台直接执行（最简单）：

```javascript
// 启用开发者模式
localStorage.setItem('campus_system_developer_mode', 'true'); location.reload();

// 禁用开发者模式
localStorage.removeItem('campus_system_developer_mode'); location.reload();

// 检查状态
console.log('开发者模式:', localStorage.getItem('campus_system_developer_mode') === 'true' ? '已启用' : '已禁用');
```

### 方法3：添加到代码中（开发环境自动启用）

在 `packages/web/src/main.ts` 中添加：

```typescript
// 开发环境自动启用开发者模式（可选）
if (import.meta.env.DEV) {
  // 取消下面这行的注释来启用
  // localStorage.setItem('campus_system_developer_mode', 'true');
}
```

## 📚 相关文件

- `packages/common/src/pinia/user.ts` - 用户状态管理（包含开发者模式逻辑）
- `packages/web/src/router/index.ts` - Web端路由守卫
- `packages/app/src/router/index.ts` - App端路由守卫
- `packages/app/src/common/store/user.ts` - App端用户状态管理

## 💡 提示

- 启用开发者模式后，控制台会显示确认消息：`✅ 开发者模式已启用 - 您拥有所有权限`
- 禁用时会显示：`❌ 开发者模式已禁用`
- 开发者模式状态会在页面刷新后保持（因为存储在localStorage中）

