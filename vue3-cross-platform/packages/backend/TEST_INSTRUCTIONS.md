# 测试执行说明

## 🚀 服务启动状态

服务正在后台启动中，请稍候...

### 检查服务是否启动成功

1. **查看启动日志**：
   - 服务启动需要1-2分钟（首次启动可能需要更长时间）
   - 看到 `Started BackendApplication` 表示启动成功

2. **访问API文档**：
   - 浏览器打开：http://localhost:8080/doc.html
   - 如果能正常打开，说明服务已启动

3. **检查端口**：
   ```powershell
   netstat -ano | findstr :8080
   ```
   - 如果看到LISTENING，说明服务正在运行

---

## 📋 推荐测试方式

### 方式1：使用Knife4j（推荐）

1. **打开浏览器**，访问：http://localhost:8080/doc.html
2. **找到对应的接口分组**
3. **点击"调试"按钮**
4. **填写请求参数**
5. **点击"发送请求"**
6. **查看响应结果**

**优点**：
- 界面友好，操作简单
- 自动生成请求格式
- 可以直接查看响应
- 支持认证token设置

---

### 方式2：使用Postman

1. **导入API文档**（如果有OpenAPI/Swagger文档）
2. **或手动创建请求**
3. **设置请求头**（如Authorization）
4. **发送请求并查看响应**

---

### 方式3：使用curl命令（PowerShell）

```powershell
# 用户注册
$body = @{
    username = "testuser001"
    password = "123456"
    confirmPassword = "123456"
    nickname = "测试用户"
    role = "TOURIST"
    schoolId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/user/register" `
    -Method POST `
    -Body $body `
    -ContentType "application/json"

# 用户登录
$loginBody = @{
    username = "testuser001"
    password = "123456"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/user/login" `
    -Method POST `
    -Body $loginBody `
    -ContentType "application/json"

# 保存token
$token = $loginResponse.data.token

# 获取用户信息（需要token）
$headers = @{
    Authorization = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/user/info" `
    -Method GET `
    -Headers $headers
```

---

## ✅ 测试优先级

### 高优先级（核心功能）
1. ✅ 用户注册和登录
2. ✅ 邮箱登录
3. ✅ 浏览记录功能
4. ✅ 消息和聊天功能

### 中优先级（重要功能）
5. ✅ 收藏功能
6. ✅ 行程管理
7. ✅ 团队管理

### 低优先级（辅助功能）
8. ✅ 其他功能测试

---

## 🔍 测试检查点

### 功能测试
- [ ] 接口能正常响应
- [ ] 返回数据格式正确
- [ ] 分页功能正常
- [ ] 筛选功能正常

### 安全测试
- [ ] 未登录访问返回401
- [ ] Token过期返回401
- [ ] 权限不足返回403

### 数据测试
- [ ] 创建后能查询到
- [ ] 更新后数据正确
- [ ] 删除后查询不到

---

## 📝 测试记录

建议记录以下信息：
- 测试接口名称
- 测试时间
- 请求参数
- 响应结果
- 是否通过
- 问题描述（如有）

---

## 🆘 如果服务启动失败

### 常见问题

1. **端口被占用**
   - 修改 `application.properties` 中的 `server.port=8081`
   - 或关闭占用8080端口的程序

2. **数据库连接失败**
   - 检查MySQL是否启动
   - 检查数据库配置是否正确
   - 检查数据库是否存在

3. **编译错误**
   - 查看控制台错误信息
   - 检查Java版本（需要Java 17+）
   - 检查Maven依赖是否下载完整

4. **表结构不匹配**
   - 检查 `init.sql` 是否已执行
   - 或设置 `spring.sql.init.continue-on-error=true`

---

**提示**：服务启动后，建议优先使用Knife4j进行测试，操作最简单。
