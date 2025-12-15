# 快速测试指南

## 🚀 快速启动测试

### 步骤1：启动后端服务

**打开新的终端窗口：**
```powershell
cd vue3-cross-platform\packages\backend
mvn spring-boot:run
```

**等待后端启动完成：**
- 看到 `Started BackendApplication` 表示启动成功
- 后端运行在 `http://localhost:8080`
- API文档地址：`http://localhost:8080/doc.html`

### 步骤2：启动前端服务

**打开另一个新的终端窗口：**
```powershell
cd vue3-cross-platform\packages\web
npm run dev
```

**等待前端启动完成：**
- 看到 `Local: http://localhost:5173` 表示启动成功
- 浏览器会自动打开，或手动访问 `http://localhost:5173`

---

## ✅ 快速测试清单

### 测试1：检查服务是否启动

- [ ] 访问 `http://localhost:8080/doc.html` - 应该看到API文档页面
- [ ] 访问 `http://localhost:5173` - 应该看到前端首页

### 测试2：账号密码登录

1. 访问 `http://localhost:5173/login`
2. 输入用户名和密码（如果没有测试用户，先注册一个）
3. 点击"登录"按钮
4. 检查：
   - [ ] 浏览器控制台（F12）的Network标签中有 `/api/user/login` 请求
   - [ ] 请求状态为 200
   - [ ] 响应包含 `code: 200` 和 `token`
   - [ ] localStorage中有 `campus_system_token`
   - [ ] localStorage中有 `campus_system_user_info`
   - [ ] 显示成功弹窗
   - [ ] 5秒后跳转到首页

### 测试3：邮箱验证码登录

1. 访问 `http://localhost:5173/email-login`
2. 输入邮箱地址（例如：`test@example.com`）
3. 点击"发送验证码"
4. 检查：
   - [ ] 浏览器控制台有 `/api/auth/email/send-code` 请求
   - [ ] 响应中包含验证码（模拟模式）
   - [ ] 显示验证码或"验证码已发送"提示
   - [ ] 开始60秒倒计时
5. 输入验证码，点击"登录"
6. 检查：
   - [ ] 浏览器控制台有 `/api/auth/email/login` 请求
   - [ ] 请求状态为 200
   - [ ] Token和用户信息保存成功
   - [ ] 跳转到首页

### 测试4：用户注册

1. 访问 `http://localhost:5173/register`
2. 填写注册表单
3. 点击"注册"按钮
4. 检查：
   - [ ] 浏览器控制台有 `/api/user/register` 请求
   - [ ] 请求状态为 200
   - [ ] 显示成功提示
   - [ ] 5秒后跳转到首页

---

## 🔍 调试技巧

### 查看网络请求

1. 打开浏览器开发者工具（F12）
2. 切换到 `Network` 标签
3. 执行操作（登录、注册等）
4. 查看请求详情：
   - **Headers**：查看请求头和响应头
   - **Payload**：查看请求体（JSON格式）
   - **Response**：查看响应体（JSON格式）

### 查看控制台日志

**前端日志（开发环境）：**
- 请求发送时会打印：`📤 请求发送:`
- 响应接收时会打印：`📥 响应接收:`

**检查Token和用户信息：**
```javascript
// 在浏览器控制台执行
console.log('Token:', localStorage.getItem('campus_system_token'))
console.log('用户信息:', JSON.parse(localStorage.getItem('campus_system_user_info') || '{}'))
```

### 清除认证信息（重新测试）

```javascript
// 在浏览器控制台执行
localStorage.removeItem('campus_system_token')
localStorage.removeItem('campus_system_user_info')
console.log('认证信息已清除')
```

---

## ⚠️ 常见问题

### 问题1：后端启动失败

**可能原因：**
- 数据库未启动
- 数据库连接配置错误
- 端口8080被占用

**解决方案：**
1. 检查MySQL是否启动
2. 检查 `application.properties` 中的数据库配置
3. 检查端口8080是否被占用：`netstat -ano | findstr :8080`

### 问题2：前端启动失败

**可能原因：**
- 依赖未安装
- 端口5173被占用

**解决方案：**
1. 安装依赖：`npm install`
2. 检查端口5173是否被占用：`netstat -ano | findstr :5173`

### 问题3：CORS错误

**症状：**
- 浏览器控制台显示CORS错误

**解决方案：**
- ✅ 已配置Vite代理，确保请求以 `/api` 开头
- ✅ 后端CORS配置已正确

### 问题4：404错误

**症状：**
- 请求返回404

**解决方案：**
1. 检查后端接口路径是否正确
2. 检查前端API调用路径是否正确
3. 检查Vite代理配置

### 问题5：401未授权错误

**症状：**
- 请求返回401

**解决方案：**
1. 检查Token是否正确保存
2. 检查请求头是否包含Token
3. 重新登录获取新Token

---

## 📝 测试结果记录

**测试时间：** ___________  
**测试人员：** ___________

| 测试项 | 状态 | 备注 |
|--------|------|------|
| 后端服务启动 | ⬜ | |
| 前端服务启动 | ⬜ | |
| 账号密码登录 | ⬜ | |
| 邮箱验证码登录 | ⬜ | |
| 用户注册 | ⬜ | |
| Token保存 | ⬜ | |
| 用户信息保存 | ⬜ | |

**发现的问题：**
1. _________________________________
2. _________________________________

---

## 📚 详细文档

更多详细信息请参考：
- [完整测试指南](./TEST_FRONTEND_BACKEND.md)
- [API集成完成报告](./API_INTEGRATION_COMPLETE.md)
- [前后端交互检查报告](./FRONTEND_BACKEND_INTEGRATION_CHECK_V2.md)

---

**祝测试顺利！** 🎉
