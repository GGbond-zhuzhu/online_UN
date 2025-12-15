# 前后端交互测试指南

## 测试准备

### 1. 启动后端服务

**方式一：使用Maven命令**
```bash
cd vue3-cross-platform/packages/backend
mvn spring-boot:run
```

**方式二：使用IDE**
- 在IDE中打开 `BackendApplication.java`
- 运行 `main` 方法

**验证后端启动：**
- 后端服务应该在 `http://localhost:8080` 启动
- 访问 `http://localhost:8080/doc.html` 应该能看到API文档（Knife4j）

### 2. 启动前端服务

**方式一：使用npm**
```bash
cd vue3-cross-platform/packages/web
npm install  # 如果还没安装依赖
npm run dev
```

**方式二：使用pnpm（如果项目使用pnpm）**
```bash
cd vue3-cross-platform
pnpm install  # 如果还没安装依赖
pnpm --filter web dev
```

**验证前端启动：**
- 前端服务应该在 `http://localhost:5173` 启动
- 浏览器会自动打开，或手动访问 `http://localhost:5173`

---

## 测试用例

### 测试1：账号密码登录

**测试步骤：**
1. 访问 `http://localhost:5173/login`
2. 选择"账号登录"标签
3. 输入用户名和密码
4. 点击"登录"按钮

**预期结果：**
- ✅ 请求发送到 `/api/user/login`
- ✅ 请求头包含 `Content-Type: application/json`
- ✅ 请求体包含 `{username: "...", password: "..."}`
- ✅ 响应状态码为 200
- ✅ 响应格式为 `{code: 200, msg: "登录成功", data: {...}}`
- ✅ Token保存到localStorage（key: `campus_system_token`）
- ✅ 用户信息保存到localStorage（key: `campus_system_user_info`）
- ✅ 显示成功弹窗
- ✅ 5秒后自动跳转到首页

**测试数据：**
- 如果数据库中有测试用户，使用真实用户名和密码
- 如果没有，需要先注册一个用户

**浏览器控制台检查：**
```javascript
// 检查Token是否保存
localStorage.getItem('campus_system_token')

// 检查用户信息是否保存
JSON.parse(localStorage.getItem('campus_system_user_info') || '{}')
```

**网络请求检查：**
1. 打开浏览器开发者工具（F12）
2. 切换到 `Network` 标签
3. 执行登录操作
4. 查看请求：
   - 请求URL：`http://localhost:5173/api/user/login`（通过Vite代理）
   - 实际请求：`http://localhost:8080/api/user/login`
   - 请求方法：POST
   - 请求头：`Content-Type: application/json`
   - 请求体：`{"username":"test","password":"123456"}`
   - 响应状态：200 OK
   - 响应体：`{"code":200,"msg":"登录成功","data":{...}}`

---

### 测试2：邮箱验证码登录

**测试步骤：**
1. 访问 `http://localhost:5173/email-login`
2. 输入邮箱地址（例如：`test@example.com`）
3. 点击"发送验证码"按钮
4. 等待验证码（模拟模式会在alert中显示）
5. 输入验证码
6. 点击"登录"按钮

**预期结果：**

**发送验证码：**
- ✅ 请求发送到 `/api/auth/email/send-code`
- ✅ 请求体包含 `{email: "test@example.com"}`
- ✅ 响应包含 `codeId`、`code`（模拟模式）、`expireTime`
- ✅ 显示验证码（模拟模式）或"验证码已发送到您的邮箱"
- ✅ 开始60秒倒计时

**邮箱登录：**
- ✅ 请求发送到 `/api/auth/email/login`
- ✅ 请求体包含 `{email: "...", code: "...", codeId: "..."}`
- ✅ 响应状态码为 200
- ✅ Token保存到localStorage
- ✅ 用户信息保存到localStorage
- ✅ 显示"登录成功"提示
- ✅ 跳转到首页

**测试数据：**
- 邮箱地址：`test@example.com`（或任何有效邮箱格式）
- 验证码：从发送验证码的响应中获取（模拟模式）

**浏览器控制台检查：**
```javascript
// 检查验证码发送请求
// 在Network标签中查看 POST /api/auth/email/send-code

// 检查邮箱登录请求
// 在Network标签中查看 POST /api/auth/email/login
```

---

### 测试3：用户注册

**测试步骤：**
1. 访问 `http://localhost:5173/register`
2. 填写注册表单：
   - 用户名：4-20位字符（字母、数字、下划线）
   - 手机号：11位数字
   - 邮箱：可选
   - 密码：6-20位，包含大小写字母、数字、特殊字符
   - 确认密码：与密码一致
   - 验证码：任意6位数字（目前后端可能不验证）
   - 同意服务条款：勾选
3. 点击"注册"按钮

**预期结果：**
- ✅ 请求发送到 `/api/user/register`
- ✅ 请求体包含注册信息
- ✅ 响应状态码为 200
- ✅ 用户信息保存到localStorage
- ✅ 显示成功提示
- ✅ 5秒后自动跳转到首页

**测试数据：**
```json
{
  "username": "testuser123",
  "phone": "13800138000",
  "email": "test@example.com",
  "password": "Test123!@#",
  "confirmPassword": "Test123!@#",
  "verifyCode": "123456"
}
```

**浏览器控制台检查：**
```javascript
// 检查注册请求
// 在Network标签中查看 POST /api/user/register

// 检查用户信息是否保存
JSON.parse(localStorage.getItem('campus_system_user_info') || '{}')
```

---

## 错误场景测试

### 错误1：登录失败（错误的用户名或密码）

**测试步骤：**
1. 访问登录页面
2. 输入错误的用户名或密码
3. 点击"登录"按钮

**预期结果：**
- ✅ 请求发送到 `/api/user/login`
- ✅ 响应状态码为 200，但 `code` 不为 200（例如：400）
- ✅ 显示错误提示："登录失败，请检查账号密码"或后端返回的具体错误信息
- ✅ 不保存Token和用户信息
- ✅ 不跳转页面

### 错误2：验证码错误

**测试步骤：**
1. 访问邮箱登录页面
2. 发送验证码
3. 输入错误的验证码
4. 点击"登录"按钮

**预期结果：**
- ✅ 请求发送到 `/api/auth/email/login`
- ✅ 响应状态码为 200，但 `code` 不为 200（例如：400）
- ✅ 显示错误提示："登录失败，请检查验证码是否正确"或后端返回的具体错误信息
- ✅ 不保存Token和用户信息
- ✅ 不跳转页面

### 错误3：网络错误

**测试步骤：**
1. 停止后端服务
2. 在前端执行登录操作

**预期结果：**
- ✅ 请求失败（网络错误）
- ✅ 显示错误提示："网络连接失败，请检查网络"或类似信息
- ✅ 不保存Token和用户信息
- ✅ 不跳转页面

---

## 调试技巧

### 1. 查看网络请求

**浏览器开发者工具：**
1. 打开开发者工具（F12）
2. 切换到 `Network` 标签
3. 刷新页面或执行操作
4. 查看请求详情：
   - Headers：请求头和响应头
   - Payload：请求体
   - Response：响应体
   - Preview：格式化的响应

### 2. 查看控制台日志

**前端日志：**
- 开发环境下，请求和响应会自动打印到控制台
- 查看 `📤 请求发送:` 和 `📥 响应接收:` 日志

**后端日志：**
- 查看后端控制台输出
- 查看日志文件（如果有配置）

### 3. 检查Token和用户信息

**浏览器控制台：**
```javascript
// 检查Token
localStorage.getItem('campus_system_token')

// 检查用户信息
JSON.parse(localStorage.getItem('campus_system_user_info') || '{}')

// 清除所有认证信息（用于重新测试）
localStorage.removeItem('campus_system_token')
localStorage.removeItem('campus_system_user_info')
```

### 4. 使用API文档测试

**访问API文档：**
- 地址：`http://localhost:8080/doc.html`
- 在API文档中可以直接测试接口
- 查看接口的请求参数和响应格式

---

## 常见问题排查

### 问题1：CORS错误

**症状：**
- 浏览器控制台显示CORS错误
- 请求被阻止

**解决方案：**
- ✅ 检查后端CORS配置（`WebMvcConfig.java`）
- ✅ 检查Vite代理配置（`vite.config.js`）
- ✅ 确保前端请求通过Vite代理（`/api`开头）

### 问题2：404错误

**症状：**
- 请求返回404
- 接口路径不存在

**解决方案：**
- ✅ 检查后端接口路径是否正确
- ✅ 检查前端API调用路径是否正确
- ✅ 检查Vite代理配置是否正确

### 问题3：401未授权错误

**症状：**
- 请求返回401
- Token无效或过期

**解决方案：**
- ✅ 检查Token是否正确保存
- ✅ 检查请求头是否包含Token
- ✅ 检查后端拦截器配置
- ✅ 重新登录获取新Token

### 问题4：500服务器错误

**症状：**
- 请求返回500
- 后端处理出错

**解决方案：**
- ✅ 查看后端控制台错误日志
- ✅ 检查数据库连接是否正常
- ✅ 检查请求参数格式是否正确
- ✅ 检查后端代码逻辑

### 问题5：TypeScript类型错误

**症状：**
- IDE显示类型错误
- 但运行时正常

**解决方案：**
- ⚠️ 这是已知问题，不影响运行时
- ✅ Vite会正确处理路径别名
- ✅ 可以暂时忽略TypeScript警告
- ✅ 后续可以优化TypeScript配置

---

## 测试检查清单

### 基础功能测试

- [ ] 后端服务正常启动（端口8080）
- [ ] 前端服务正常启动（端口5173）
- [ ] API文档可以访问（`http://localhost:8080/doc.html`）
- [ ] 账号密码登录功能正常
- [ ] 邮箱验证码登录功能正常
- [ ] 用户注册功能正常

### 数据存储测试

- [ ] Token正确保存到localStorage
- [ ] 用户信息正确保存到localStorage
- [ ] Token在请求头中自动添加
- [ ] 登录后可以访问需要认证的页面

### 错误处理测试

- [ ] 错误的用户名密码显示错误提示
- [ ] 错误的验证码显示错误提示
- [ ] 网络错误显示友好提示
- [ ] 服务器错误显示友好提示

### 网络请求测试

- [ ] 请求通过Vite代理转发
- [ ] 请求头包含正确的Content-Type
- [ ] 请求体格式正确（JSON）
- [ ] 响应格式正确（ApiResponse）
- [ ] 错误响应正确处理

---

## 测试脚本

### 快速测试脚本（浏览器控制台）

```javascript
// 测试登录API
async function testLogin() {
  try {
    const response = await fetch('http://localhost:8080/api/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: 'test',
        password: '123456'
      })
    })
    const data = await response.json()
    console.log('登录响应:', data)
    return data
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 测试邮箱验证码API
async function testSendEmailCode() {
  try {
    const response = await fetch('http://localhost:8080/api/auth/email/send-code', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: 'test@example.com'
      })
    })
    const data = await response.json()
    console.log('验证码响应:', data)
    return data
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}

// 执行测试
// testLogin()
// testSendEmailCode()
```

---

## 测试报告模板

### 测试结果记录

**测试时间：** 2024-01-15  
**测试人员：** [姓名]  
**测试环境：** 开发环境

| 测试项 | 状态 | 备注 |
|--------|------|------|
| 后端服务启动 | ✅/❌ | |
| 前端服务启动 | ✅/❌ | |
| 账号密码登录 | ✅/❌ | |
| 邮箱验证码登录 | ✅/❌ | |
| 用户注册 | ✅/❌ | |
| Token保存 | ✅/❌ | |
| 用户信息保存 | ✅/❌ | |
| 错误处理 | ✅/❌ | |

**发现的问题：**
1. [问题描述]
2. [问题描述]

**建议：**
1. [建议内容]
2. [建议内容]

---

## 下一步

测试完成后，如果发现问题：
1. 记录问题详情
2. 检查前后端日志
3. 修复问题
4. 重新测试

如果测试通过：
1. 可以继续开发其他功能
2. 可以优化用户体验（如添加Toast组件替代alert）
3. 可以添加更多测试用例

---

**测试文档创建时间：** 2024-01-15  
**最后更新：** 2024-01-15
