# 快速测试指南

## 🚀 快速启动测试

### 1. 启动后端服务

```bash
cd packages/backend
mvnw.cmd spring-boot:run
```

### 2. 访问API文档

浏览器打开：http://localhost:8080/doc.html

---

## ✅ 核心功能快速测试

### 测试流程1：用户注册和登录

1. **注册用户**
   - 接口：`POST /api/user/register`
   - 参数：
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

2. **用户登录**
   - 接口：`POST /api/user/login`
   - 参数：
     ```json
     {
       "username": "test001",
       "password": "123456"
     }
     ```
   - **保存返回的token**，后续请求需要携带

3. **获取用户信息**
   - 接口：`GET /api/user/info`
   - Header：`Authorization: Bearer YOUR_TOKEN`

---

### 测试流程2：邮箱登录

1. **发送验证码**
   - 接口：`POST /api/auth/email/send-code`
   - 参数：`email=test@example.com`
   - **保存返回的code和codeId**（模拟模式下会返回code）

2. **邮箱登录**
   - 接口：`POST /api/auth/email/login`
   - 参数：
     - `email=test@example.com`
     - `code=123456`（从步骤1获取）
     - `codeId=xxx`（从步骤1获取）

---

### 测试流程3：学生身份认证

1. **学生认证申请**
   - 接口：`POST /api/auth/student/apply`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数（使用init.sql中的测试数据）：
     - `studentId=20230001`
     - `verificationCode=123456`
     - `name=张三`
     - `idCard=110101199001011234`
     - `schoolId=1`

---

### 测试流程4：浏览记录和收藏

1. **发布二手商品**
   - 接口：`POST /api/secondhand/publish`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：
     ```json
     {
       "title": "测试商品",
       "description": "测试描述",
       "price": 100.00,
       "category": "BOOKS",
       "imageUrls": []
     }
     ```
   - **保存返回的商品ID**

2. **记录浏览行为**
   - 接口：`POST /api/secondhand/browse/{id}`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：`id=1`（商品ID）

3. **获取浏览记录**
   - 接口：`GET /api/secondhand/browse-history`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：`page=1&size=10`

---

### 测试流程5：消息和聊天

1. **获取消息列表**
   - 接口：`GET /api/messages/list`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：`page=1&size=10`

2. **获取聊天会话列表**
   - 接口：`GET /api/chat/conversations`
   - Header：`Authorization: Bearer YOUR_TOKEN`

3. **发送聊天消息**
   - 接口：`POST /api/chat/send`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：
     - `targetUserId=2`（另一个用户ID）
     - `content=你好`
     - `type=TEXT`

---

### 测试流程6：行程管理

1. **创建个人行程**
   - 接口：`POST /api/schedule/personal/create`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：
     ```json
     {
       "title": "测试行程",
       "startTime": "2024-12-20T10:00:00",
       "endTime": "2024-12-20T11:00:00",
       "type": "MEETING",
       "remindType": "FIVE_MINUTES"
     }
     ```

2. **手动录入课程表**
   - 接口：`POST /api/schedule/import/manual`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：
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

3. **创建团队**
   - 接口：`POST /api/schedule/team/create`
   - Header：`Authorization: Bearer YOUR_TOKEN`
   - 参数：
     ```json
     {
       "name": "测试团队",
       "description": "测试团队描述",
       "needApprove": true
     }
     ```
   - **保存返回的邀请码**

4. **通过邀请码加入团队**
   - 接口：`POST /api/schedule/team/join-by-code`
   - Header：`Authorization: Bearer YOUR_TOKEN`（使用另一个用户的token）
   - 参数：`inviteCode=ABC123`（从步骤3获取）

---

## 🔧 使用Knife4j测试

1. 打开 http://localhost:8080/doc.html
2. 找到对应的接口分组
3. 点击"调试"按钮
4. 填写请求参数
5. 点击"发送请求"
6. 查看响应结果

---

## 📋 测试检查清单

- [ ] 用户注册和登录
- [ ] 邮箱登录
- [ ] 学生身份认证
- [ ] 教师身份认证
- [ ] 游客活体检测
- [ ] 校园卡查询和消费
- [ ] 二手商品发布和浏览记录
- [ ] 兼职发布和收藏
- [ ] 消息中心功能
- [ ] 聊天功能
- [ ] 个人行程管理
- [ ] 团队管理和邀请
- [ ] 课程表导入
- [ ] 提醒管理

---

**提示**：建议使用Knife4j进行测试，界面友好，操作简单。
