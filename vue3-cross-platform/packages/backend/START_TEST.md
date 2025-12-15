# 启动和测试指南

## 🚀 快速启动

### Windows系统

```powershell
# 1. 进入后端目录
cd c:\Users\m1346\Desktop\web_app\vue3-cross-platform\packages\backend

# 2. 启动MySQL数据库（如果未启动）
# 确保MySQL服务正在运行

# 3. 启动后端服务
.\mvnw.cmd spring-boot:run
```

### 启动成功标志
看到以下日志表示启动成功：
```
Started BackendApplication in X.XXX seconds
```

---

## 📖 访问API文档

启动成功后，在浏览器中打开：
```
http://localhost:8080/doc.html
```

这是Knife4j（Swagger UI）文档，可以：
- 查看所有API接口
- 在线测试接口
- 查看请求/响应示例

---

## ✅ 快速功能测试

### 测试1：用户注册和登录（必测）

1. **打开Knife4j**：http://localhost:8080/doc.html
2. **找到"用户管理"分组**
3. **测试注册接口**：
   - 点击 `POST /api/user/register`
   - 点击"调试"
   - 填写测试数据：
     ```json
     {
       "username": "test001",
       "password": "123456",
       "confirmPassword": "123456",
       "nickname": "测试用户",
       "role": "TOURIST",
       "schoolId": 1
     }
     ```
   - 点击"发送请求"
   - **保存返回的userId**

4. **测试登录接口**：
   - 点击 `POST /api/user/login`
   - 填写：
     ```json
     {
       "username": "test001",
       "password": "123456"
     }
     ```
   - 点击"发送请求"
   - **保存返回的token**（后续接口需要）

5. **测试获取用户信息**：
   - 点击 `GET /api/user/info`
   - 点击"调试"
   - 在"请求头"中添加：
     - Key: `Authorization`
     - Value: `Bearer YOUR_TOKEN`（替换为步骤4获取的token）
   - 点击"发送请求"

---

### 测试2：邮箱登录（必测）

1. **发送验证码**：
   - 接口：`POST /api/auth/email/send-code`
   - 参数：`email=test@example.com`
   - **保存返回的code和codeId**（模拟模式下会返回code）

2. **邮箱登录**：
   - 接口：`POST /api/auth/email/login`
   - 参数：
     - `email=test@example.com`
     - `code=123456`（从步骤1获取）
     - `codeId=xxx`（从步骤1获取）

---

### 测试3：浏览记录功能（必测）

1. **发布商品**：
   - 接口：`POST /api/secondhand/publish`
   - Header：添加 `Authorization: Bearer YOUR_TOKEN`
   - Body：
     ```json
     {
       "title": "测试商品",
       "description": "测试",
       "price": 100.00,
       "category": "BOOKS",
       "imageUrls": []
     }
     ```
   - **保存返回的商品ID**

2. **记录浏览**：
   - 接口：`POST /api/secondhand/browse/{id}`
   - 将`{id}`替换为步骤1的商品ID
   - Header：添加token

3. **查看浏览记录**：
   - 接口：`GET /api/secondhand/browse-history`
   - Header：添加token
   - 应该能看到刚才浏览的商品

---

### 测试4：消息和聊天（必测）

1. **获取消息列表**：
   - 接口：`GET /api/messages/list`
   - Header：添加token

2. **获取聊天会话**：
   - 接口：`GET /api/chat/conversations`
   - Header：添加token

3. **发送消息**（需要两个用户）：
   - 先注册第二个用户
   - 接口：`POST /api/chat/send`
   - Header：添加第一个用户的token
   - 参数：`targetUserId=2`（第二个用户的ID）

---

### 测试5：行程管理（必测）

1. **创建个人行程**：
   - 接口：`POST /api/schedule/personal/create`
   - Header：添加token
   - Body：
     ```json
     {
       "title": "测试行程",
       "startTime": "2024-12-20T10:00:00",
       "endTime": "2024-12-20T11:00:00",
       "type": "MEETING"
     }
     ```

2. **手动录入课程表**：
   - 接口：`POST /api/schedule/import/manual`
   - Header：添加token
   - Body：
     ```json
     {
       "semester": "2024-2025-1",
       "courses": [
         {
           "courseName": "高等数学",
           "dayOfWeek": 1,
           "timeSlot": "1-2节",
           "location": "教学楼A101",
           "teacher": "张老师"
         }
       ]
     }
     ```

3. **创建团队**：
   - 接口：`POST /api/schedule/team/create`
   - Header：添加token
   - Body：
     ```json
     {
       "name": "测试团队",
       "description": "测试",
       "needApprove": true
     }
     ```
   - **保存返回的邀请码**

4. **通过邀请码加入**（使用第二个用户的token）：
   - 接口：`POST /api/schedule/team/join-by-code`
   - 参数：`inviteCode=xxx`（从步骤3获取）

---

## 🔍 测试要点

### 正常流程测试
- ✅ 所有接口能正常返回200状态码
- ✅ 返回的数据格式正确
- ✅ 分页功能正常
- ✅ 筛选功能正常

### 异常流程测试
- ✅ 无效token返回401
- ✅ 无效参数返回400
- ✅ 权限不足返回403
- ✅ 资源不存在返回404

### 数据一致性测试
- ✅ 创建后能查询到
- ✅ 更新后数据正确
- ✅ 删除后查询不到
- ✅ 关联数据正确

---

## 📊 测试数据

### 使用init.sql中的测试数据

**学生认证测试**：
- 学号：`20230001`
- 姓名：`张三`
- 身份证：`110101199001011234`
- 学校ID：`1`

**教师认证测试**：
- 工号：`T001`
- 姓名：`张教授`
- 身份证：`110101197001011234`
- 学校ID：`1`

---

## 🐛 常见问题

### 1. 服务启动失败
**问题**：端口被占用
**解决**：修改 `application.properties` 中的 `server.port=8081`

**问题**：数据库连接失败
**解决**：检查MySQL是否启动，检查数据库配置

### 2. 接口返回401
**问题**：未携带token或token过期
**解决**：重新登录获取新token

### 3. 接口返回500
**问题**：服务器内部错误
**解决**：查看后端日志，检查数据库和外部API配置

---

## 📝 测试记录模板

```
测试时间：___________
测试人员：___________

测试项          状态    备注
─────────────────────────────────
用户注册         [ ]     
用户登录         [ ]     
邮箱登录         [ ]     
学生认证         [ ]     
浏览记录         [ ]     
消息聊天         [ ]     
行程管理         [ ]     
团队管理         [ ]     
```

---

**提示**：建议使用Knife4j进行测试，所有接口都有详细文档和示例。
