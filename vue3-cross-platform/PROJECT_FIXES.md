# 项目问题修复总结

## 📋 修复概述

本次修复主要解决了项目中的跨平台兼容性问题、硬编码路径问题以及配置安全问题。

---

## ✅ 已修复的问题

### 1. 后端硬编码路径问题

**问题**: `BackendApplication.java` 中使用了硬编码的绝对路径，导致在不同机器上无法正常运行。

**修复位置**: `packages/backend/src/main/java/com/yourschool/campussystem/BackendApplication.java`

**修复内容**:
- 将硬编码路径 `c:\\Users\\m1346\\Desktop\\web_app\\...` 改为使用 `System.getProperty("user.dir")` 获取相对路径
- 使用 `java.nio.file.Paths.get()` 构建跨平台兼容的路径
- 添加目录创建逻辑，确保日志目录存在

**修复前**:
```java
java.io.FileWriter fw = new java.io.FileWriter("c:\\Users\\m1346\\Desktop\\web_app\\...");
```

**修复后**:
```java
String userDir = System.getProperty("user.dir");
String logPath = java.nio.file.Paths.get(userDir, ".cursor", "debug.log").toString();
java.io.File logFile = new java.io.File(logPath);
logFile.getParentFile().mkdirs();
```

---

### 2. 跨平台存储兼容性问题

**问题**: 
- `storage.ts` 文件为空，缺少存储工具实现
- `auth.ts` 直接使用 `localStorage` 和 `sessionStorage`，在 UniApp 环境中不可用

**修复位置**: 
- `packages/common/src/utils/storage.ts` (新建)
- `packages/common/src/utils/auth.ts` (修改)

**修复内容**:
- 创建了跨平台存储工具 `storage.ts`，自动检测运行环境（Web/UniApp）
- Web 环境使用 `localStorage`
- UniApp 环境使用 `uni.setStorageSync` / `uni.getStorageSync`
- 更新 `auth.ts` 使用新的存储工具，保持接口兼容性

**新增功能**:
```typescript
// 自动检测环境
const isUniApp = typeof uni !== 'undefined'
const storage = isUniApp ? new UniAppStorage() : new WebStorage()

// 统一的存储接口
export function getStorage(key: string): string | null
export function setStorage(key: string, value: string): void
export function getStorageJSON<T>(key: string): T | null
export function setStorageJSON(key: string, value: any): void
```

---

### 3. 跨平台导航兼容性问题

**问题**: `request.ts` 中使用了 `window.location.href`，在 UniApp 环境中不可用。

**修复位置**: `packages/common/src/utils/request.ts`

**修复内容**:
- 添加环境检测，区分 Web 和 UniApp 环境
- Web 环境使用 `window.location.href`
- UniApp 环境使用 `uni.reLaunch()`

**修复前**:
```typescript
window.location.href = '/login'
```

**修复后**:
```typescript
if (typeof window !== 'undefined') {
  // Web 环境
  window.location.href = '/login'
} else if (typeof uni !== 'undefined') {
  // UniApp 环境
  uni.reLaunch({ url: '/pages/login/login' })
}
```

---

### 4. 配置文件安全问题

**问题**: `application.properties` 中硬编码了数据库密码等敏感信息。

**修复位置**: 
- `packages/backend/src/main/resources/application.properties` (修改)
- `packages/backend/src/main/resources/application.properties.example` (新建)

**修复内容**:
- 将敏感配置改为使用环境变量，支持通过环境变量覆盖
- 创建示例配置文件，提供配置模板
- 添加配置说明注释

**修复前**:
```properties
spring.datasource.password=Ymz2006060311@
```

**修复后**:
```properties
# 支持环境变量覆盖
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/campus_db?...}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

**使用方式**:
```bash
# 通过环境变量设置
export DB_PASSWORD=your_password
mvn spring-boot:run

# 或在启动时设置
DB_PASSWORD=your_password mvn spring-boot:run
```

---

## 📝 修复文件清单

### 新建文件
1. `packages/common/src/utils/storage.ts` - 跨平台存储工具
2. `packages/backend/src/main/resources/application.properties.example` - 配置示例文件
3. `PROJECT_FIXES.md` - 本修复文档

### 修改文件
1. `packages/backend/src/main/java/com/yourschool/campussystem/BackendApplication.java` - 修复硬编码路径
2. `packages/common/src/utils/auth.ts` - 使用跨平台存储工具
3. `packages/common/src/utils/request.ts` - 修复跨平台导航
4. `packages/backend/src/main/resources/application.properties` - 使用环境变量

---

## 🔍 验证方法

### 1. 验证存储功能
```typescript
// 在 Web 和 UniApp 环境中测试
import { setStorage, getStorage } from '@campus/common'

setStorage('test', 'value')
console.log(getStorage('test')) // 应该输出 'value'
```

### 2. 验证导航功能
```typescript
// 在 request.ts 中，401 错误应该能正确跳转
// Web: 跳转到 /login
// UniApp: 跳转到 /pages/login/login
```

### 3. 验证后端路径
```bash
# 在不同目录下启动后端，应该能正常创建日志文件
cd packages/backend
mvn spring-boot:run
# 检查 .cursor/debug.log 是否正常创建
```

### 4. 验证配置环境变量
```bash
# 测试环境变量覆盖
export DB_PASSWORD=test_password
mvn spring-boot:run
# 检查日志确认使用的密码
```

---

## ⚠️ 注意事项

### 1. 数据库配置
- 开发环境：可以直接在 `application.properties` 中配置
- 生产环境：**强烈建议**使用环境变量，不要将密码提交到代码仓库

### 2. UniApp 存储
- UniApp 环境中的存储是持久化的，没有 `sessionStorage` 的概念
- `remember` 参数在 UniApp 中保留以保持接口兼容性，但实际效果相同

### 3. 路径兼容性
- 后端日志路径现在使用相对路径，日志文件会创建在项目根目录的 `.cursor` 文件夹中
- 确保有写入权限

---

## 🚀 后续建议

### 1. 安全性增强
- [ ] 使用 Spring Boot 的配置加密功能
- [ ] 将敏感配置移到外部配置文件（不提交到 Git）
- [ ] 使用密钥管理服务（如 Vault）

### 2. 跨平台优化
- [ ] 统一错误处理机制
- [ ] 添加平台检测工具函数
- [ ] 创建统一的导航工具

### 3. 代码质量
- [ ] 添加单元测试
- [ ] 添加集成测试
- [ ] 完善 TypeScript 类型定义

---

## 📚 相关文档

- [错误修复说明](./packages/ERROR_FIXES.md)
- [技术栈检查报告](./TECH_STACK_CHECK_REPORT.md)
- [依赖安装问题解决](../依赖安装问题解决.md)

---

**修复完成时间**: 2024年
**修复人员**: AI Assistant
**验证状态**: ✅ 已通过基础验证
