# API接口测试指南

## 📋 测试前准备

### 1. 启动后端服务
```bash
cd vue3-cross-platform/packages/backend
cmd /c "mvnw.cmd spring-boot:run"
```

### 2. 确认服务启动成功
- 访问：http://localhost:8080/doc.html （Knife4j API文档）
- 访问：http://localhost:8080/swagger-ui.html （Swagger UI）

### 3. 测试账号
- 用户名：`zhangsan`，密码：`123456`（学生）
- 用户名：`lisi`，密码：`123456`（教师）
- 用户名：`admin`，密码：`123456`（管理员）

---

## 🧪 API测试用例

### 1. 用户登录测试

#### 1.1 学生登录
```bash
curl -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
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

#### 1.2 管理员登录
```bash
curl -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

---

### 2. 用户信息测试

#### 2.1 获取当前用户信息
```bash
curl -X GET "http://localhost:8080/api/user/info" \
  -H "Authorization: Bearer {TOKEN}"
```

**说明**：将 `{TOKEN}` 替换为登录返回的token

---

### 3. 校园卡功能测试

#### 3.1 获取校园卡信息
```bash
curl -X GET "http://localhost:8080/api/ecard/info" \
  -H "Authorization: Bearer {TOKEN}"
```

#### 3.2 查询消费记录
```bash
curl -X GET "http://localhost:8080/api/ecard/records?page=1&size=10" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 4. 二手交易功能测试

#### 4.1 获取二手商品列表
```bash
curl -X GET "http://localhost:8080/api/secondhand/list?page=1&size=10" \
  -H "Authorization: Bearer {TOKEN}"
```

#### 4.2 获取商品详情
```bash
curl -X GET "http://localhost:8080/api/secondhand/1" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 5. 兼职功能测试

#### 5.1 获取兼职列表
```bash
curl -X GET "http://localhost:8080/api/parttime/list?page=1&size=10" \
  -H "Authorization: Bearer {TOKEN}"
```

#### 5.2 获取兼职详情
```bash
curl -X GET "http://localhost:8080/api/parttime/1" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 6. 行程管理功能测试

#### 6.1 获取个人行程列表
```bash
curl -X GET "http://localhost:8080/api/schedule/personal?page=1&size=10" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 7. 通用功能测试

#### 7.1 获取帮助分类
```bash
curl -X GET "http://localhost:8080/api/common/help/categories"
```

#### 7.2 获取公告列表
```bash
curl -X GET "http://localhost:8080/api/common/announcements?page=1&size=10"
```

---

### 8. 管理员功能测试（需要管理员token）

#### 8.1 获取用户列表
```bash
curl -X GET "http://localhost:8080/api/admin/users?page=1&size=10" \
  -H "Authorization: Bearer {ADMIN_TOKEN}"
```

#### 8.2 获取平台统计
```bash
curl -X GET "http://localhost:8080/api/admin/statistics" \
  -H "Authorization: Bearer {ADMIN_TOKEN}"
```

---

## 📝 测试脚本

### PowerShell测试脚本

```powershell
# 设置基础URL
$baseUrl = "http://localhost:8080"

# 1. 登录获取token
$loginBody = @{
    username = "zhangsan"
    password = "123456"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/user/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $loginBody

$token = $loginResponse.data.token
Write-Host "登录成功，Token: $token"

# 2. 获取用户信息
$headers = @{
    Authorization = "Bearer $token"
}

$userInfo = Invoke-RestMethod -Uri "$baseUrl/api/user/info" `
    -Method GET `
    -Headers $headers

Write-Host "用户信息:"
$userInfo | ConvertTo-Json -Depth 3

# 3. 获取校园卡信息
$ecardInfo = Invoke-RestMethod -Uri "$baseUrl/api/ecard/info" `
    -Method GET `
    -Headers $headers

Write-Host "校园卡信息:"
$ecardInfo | ConvertTo-Json -Depth 3

# 4. 获取二手商品列表
$goodsList = Invoke-RestMethod -Uri "$baseUrl/api/secondhand/list?page=1&size=10" `
    -Method GET `
    -Headers $headers

Write-Host "二手商品列表:"
$goodsList | ConvertTo-Json -Depth 3
```

---

## ✅ 测试检查清单

### 基础功能测试
- [ ] 用户登录
- [ ] 获取用户信息
- [ ] 用户注册（可选）

### 校园卡功能测试
- [ ] 获取校园卡信息
- [ ] 查询消费记录
- [ ] 校园卡消费（需要定位）

### 二手交易功能测试
- [ ] 获取商品列表
- [ ] 获取商品详情
- [ ] 发布商品（需要登录）
- [ ] 收藏商品

### 兼职功能测试
- [ ] 获取兼职列表
- [ ] 获取兼职详情
- [ ] 报名兼职（需要登录）

### 行程管理功能测试
- [ ] 获取个人行程
- [ ] 创建行程（需要登录）

### 通用功能测试
- [ ] 获取帮助分类
- [ ] 获取公告列表
- [ ] 文件上传

### 管理员功能测试
- [ ] 获取用户列表
- [ ] 获取平台统计
- [ ] 审核认证申请

---

## 🔍 常见问题排查

### 1. 401未授权错误
- 检查token是否正确
- 检查token是否过期
- 检查请求头格式：`Authorization: Bearer {TOKEN}`

### 2. 500服务器错误
- 检查数据库连接
- 检查数据库表是否存在
- 查看后端日志

### 3. 404未找到
- 检查URL路径是否正确
- 检查后端服务是否启动
- 检查端口号是否为8080

### 4. 数据库连接错误
- 检查MySQL服务是否启动
- 检查数据库密码是否正确
- 检查数据库名称是否为`campus_db`

---

## 📊 测试报告模板

```
测试时间：2024-XX-XX
测试人员：XXX

测试结果：
- 登录接口：✅ 通过
- 用户信息接口：✅ 通过
- 校园卡接口：✅ 通过
- 二手交易接口：✅ 通过
- 兼职接口：✅ 通过
- 行程管理接口：✅ 通过
- 通用功能接口：✅ 通过
- 管理员接口：✅ 通过

发现问题：
1. XXX接口返回500错误（已修复）
2. XXX接口响应时间较长（待优化）

建议：
1. 添加接口响应时间监控
2. 完善错误提示信息
```
