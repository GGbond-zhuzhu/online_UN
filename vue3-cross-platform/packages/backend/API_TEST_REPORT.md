# API接口测试报告

## 📋 测试准备

### 1. 启动后端服务
```bash
cd vue3-cross-platform/packages/backend
cmd /c "mvnw.cmd spring-boot:run"
```

等待服务启动完成（约30-60秒），看到以下日志表示启动成功：
```
Started BackendApplication in X.XXX seconds
```

### 2. 访问API文档
- Knife4j文档：http://localhost:8080/doc.html
- Swagger UI：http://localhost:8080/swagger-ui.html

### 3. 测试账号
- 学生：`zhangsan` / `123456`
- 教师：`lisi` / `123456`
- 管理员：`admin` / `123456`

---

## 🧪 测试用例

### 测试1：用户登录

**请求**：
```bash
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456"
}
```

**预期响应**：
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "张三",
      "role": "STUDENT"
    }
  }
}
```

**PowerShell命令**：
```powershell
$body = '{"username":"zhangsan","password":"123456"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/user/login" -Method POST -ContentType "application/json" -Body $body
```

---

### 测试2：获取用户信息（需要Token）

**请求**：
```bash
GET http://localhost:8080/api/user/info
Authorization: Bearer {TOKEN}
```

**PowerShell命令**：
```powershell
$token = "YOUR_TOKEN_HERE"
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/user/info" -Method GET -Headers $headers
```

---

### 测试3：获取校园卡信息

**请求**：
```bash
GET http://localhost:8080/api/ecard/info
Authorization: Bearer {TOKEN}
```

**PowerShell命令**：
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/ecard/info" -Method GET -Headers $headers
```

---

### 测试4：获取二手商品列表

**请求**：
```bash
GET http://localhost:8080/api/secondhand/list?page=1&size=10
Authorization: Bearer {TOKEN}
```

**PowerShell命令**：
```powershell
$url = "http://localhost:8080/api/secondhand/list?page=1&size=10"
Invoke-RestMethod -Uri $url -Method GET -Headers $headers
```

---

### 测试5：获取兼职列表

**请求**：
```bash
GET http://localhost:8080/api/parttime/list?page=1&size=10
Authorization: Bearer {TOKEN}
```

**PowerShell命令**：
```powershell
$url = "http://localhost:8080/api/parttime/list?page=1&size=10"
Invoke-RestMethod -Uri $url -Method GET -Headers $headers
```

---

### 测试6：获取帮助分类（无需认证）

**请求**：
```bash
GET http://localhost:8080/api/common/help/categories
```

**PowerShell命令**：
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/common/help/categories" -Method GET
```

---

### 测试7：管理员功能 - 获取用户列表

**请求**：
```bash
GET http://localhost:8080/api/admin/users?page=1&size=10
Authorization: Bearer {ADMIN_TOKEN}
```

**说明**：需要使用管理员账号登录获取token

---

### 测试8：管理员功能 - 获取平台统计

**请求**：
```bash
GET http://localhost:8080/api/admin/statistics
Authorization: Bearer {ADMIN_TOKEN}
```

---

## 📊 测试结果记录

### 基础功能测试

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 用户登录 | POST | /api/user/login | ⏳ | 待测试 |
| 获取用户信息 | GET | /api/user/info | ⏳ | 待测试 |
| 获取校园卡信息 | GET | /api/ecard/info | ⏳ | 待测试 |
| 查询消费记录 | GET | /api/ecard/records | ⏳ | 待测试 |
| 获取二手商品列表 | GET | /api/secondhand/list | ⏳ | 待测试 |
| 获取商品详情 | GET | /api/secondhand/{id} | ⏳ | 待测试 |
| 获取兼职列表 | GET | /api/parttime/list | ⏳ | 待测试 |
| 获取兼职详情 | GET | /api/parttime/{id} | ⏳ | 待测试 |
| 获取个人行程 | GET | /api/schedule/personal | ⏳ | 待测试 |
| 获取帮助分类 | GET | /api/common/help/categories | ⏳ | 待测试 |
| 获取公告列表 | GET | /api/common/announcements | ⏳ | 待测试 |

### 管理员功能测试

| 接口 | 方法 | 路径 | 状态 | 备注 |
|------|------|------|------|------|
| 获取用户列表 | GET | /api/admin/users | ⏳ | 待测试 |
| 获取平台统计 | GET | /api/admin/statistics | ⏳ | 待测试 |
| 审核认证申请 | PUT | /api/admin/auth-applies/{id}/review | ⏳ | 待测试 |

---

## 🔍 测试步骤

### 步骤1：启动后端服务
1. 打开终端，进入后端目录
2. 运行启动命令
3. 等待服务启动完成

### 步骤2：测试登录接口
1. 使用PowerShell或Postman测试登录接口
2. 保存返回的token

### 步骤3：测试需要认证的接口
1. 在请求头中添加Authorization: Bearer {TOKEN}
2. 测试各个功能模块的接口

### 步骤4：测试管理员接口
1. 使用管理员账号登录
2. 获取管理员token
3. 测试管理员功能接口

---

## 📝 测试注意事项

1. **服务启动时间**：后端服务启动需要30-60秒，请耐心等待
2. **Token有效期**：JWT token有有效期，如果过期需要重新登录
3. **数据库连接**：确保MySQL服务已启动，数据库密码正确
4. **端口占用**：如果8080端口被占用，需要先关闭占用进程
5. **编码问题**：PowerShell脚本中的中文字符可能有编码问题，建议使用英文

---

## 🛠️ 使用Postman测试

如果PowerShell测试有问题，可以使用Postman：

1. **导入API文档**：
   - 访问：http://localhost:8080/v3/api-docs
   - 复制JSON内容
   - 在Postman中导入OpenAPI文档

2. **设置环境变量**：
   - 创建环境变量：`baseUrl = http://localhost:8080`
   - 创建环境变量：`token`（登录后设置）

3. **测试流程**：
   - 先测试登录接口，获取token
   - 在环境变量中设置token
   - 测试其他需要认证的接口

---

## ✅ 测试检查清单

- [ ] 后端服务已启动
- [ ] 可以访问API文档（http://localhost:8080/doc.html）
- [ ] 登录接口测试通过
- [ ] 获取用户信息接口测试通过
- [ ] 校园卡功能接口测试通过
- [ ] 二手交易功能接口测试通过
- [ ] 兼职功能接口测试通过
- [ ] 行程管理功能接口测试通过
- [ ] 通用功能接口测试通过
- [ ] 管理员功能接口测试通过

---

## 📞 问题排查

### 问题1：无法连接到服务器
- 检查后端服务是否启动
- 检查端口8080是否被占用
- 检查防火墙设置

### 问题2：401未授权错误
- 检查token是否正确
- 检查token是否过期
- 检查请求头格式

### 问题3：500服务器错误
- 查看后端日志
- 检查数据库连接
- 检查数据库表是否存在

### 问题4：404未找到
- 检查URL路径是否正确
- 检查Controller路径映射

---

**测试时间**：待填写  
**测试人员**：待填写  
**测试结果**：待填写
