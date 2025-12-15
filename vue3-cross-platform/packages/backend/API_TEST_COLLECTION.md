# API测试集合

## 📋 测试前准备

### 1. 启动服务
```bash
cd packages/backend
mvnw.cmd spring-boot:run
```

### 2. 访问API文档
http://localhost:8080/doc.html

---

## 🔑 测试步骤（按顺序执行）

### 第一步：用户注册和登录

#### 1. 注册用户
```http
POST http://localhost:8080/api/user/register
Content-Type: application/json

{
  "username": "testuser001",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "测试用户001",
  "role": "TOURIST",
  "schoolId": 1
}
```

**保存返回的userId**

#### 2. 用户登录
```http
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "username": "testuser001",
  "password": "123456"
}
```

**保存返回的token**（后续所有需要认证的接口都需要在Header中携带）

---

### 第二步：邮箱登录测试

#### 1. 发送邮箱验证码
```http
POST http://localhost:8080/api/auth/email/send-code
Content-Type: application/x-www-form-urlencoded

email=test@example.com
```

**保存返回的codeId和code**（模拟模式下会返回code）

#### 2. 邮箱登录
```http
POST http://localhost:8080/api/auth/email/login
Content-Type: application/x-www-form-urlencoded

email=test@example.com&code=123456&codeId=xxx
```

---

### 第三步：身份认证测试

#### 1. 学生身份认证（使用init.sql中的测试数据）
```http
POST http://localhost:8080/api/auth/student/apply
Authorization: Bearer YOUR_TOKEN
Content-Type: application/x-www-form-urlencoded

studentId=20230001&verificationCode=123456&name=张三&idCard=110101199001011234&schoolId=1
```

#### 2. 查询认证申请记录
```http
GET http://localhost:8080/api/auth/apply/records?page=1&size=10
Authorization: Bearer YOUR_TOKEN
```

---

### 第四步：浏览记录测试

#### 1. 发布二手商品
```http
POST http://localhost:8080/api/secondhand/publish
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "测试商品001",
  "description": "这是一个测试商品",
  "price": 100.00,
  "category": "BOOKS",
  "imageUrls": []
}
```

**保存返回的商品ID**

#### 2. 记录浏览行为
```http
POST http://localhost:8080/api/secondhand/browse/1
Authorization: Bearer YOUR_TOKEN
```

#### 3. 获取浏览记录
```http
GET http://localhost:8080/api/secondhand/browse-history?page=1&size=10
Authorization: Bearer YOUR_TOKEN
```

#### 4. 删除浏览记录
```http
DELETE http://localhost:8080/api/secondhand/browse-history/1
Authorization: Bearer YOUR_TOKEN
```

---

### 第五步：兼职收藏测试

#### 1. 发布兼职（需要教师或高校角色）
```http
POST http://localhost:8080/api/parttime/publish
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "校园推广专员",
  "description": "负责校园推广工作",
  "salaryPerHour": 150.00,
  "location": "校内",
  "workTime": "周末",
  "recruitCount": 5
}
```

**保存返回的兼职ID**

#### 2. 收藏兼职
```http
POST http://localhost:8080/api/parttime/favorite/1
Authorization: Bearer YOUR_TOKEN
```

#### 3. 获取收藏列表
```http
GET http://localhost:8080/api/parttime/favorites?page=1&size=10
Authorization: Bearer YOUR_TOKEN
```

#### 4. 取消收藏
```http
DELETE http://localhost:8080/api/parttime/favorite/1
Authorization: Bearer YOUR_TOKEN
```

---

### 第六步：消息和聊天测试

#### 1. 获取消息列表
```http
GET http://localhost:8080/api/messages/list?type=ALL&page=1&size=10
Authorization: Bearer YOUR_TOKEN
```

#### 2. 获取未读消息数量
```http
GET http://localhost:8080/api/messages/unread-count
Authorization: Bearer YOUR_TOKEN
```

#### 3. 标记消息已读
```http
PUT http://localhost:8080/api/messages/1/read
Authorization: Bearer YOUR_TOKEN
```

#### 4. 获取聊天会话列表
```http
GET http://localhost:8080/api/chat/conversations
Authorization: Bearer YOUR_TOKEN
```

#### 5. 发送聊天消息（需要两个用户）
```http
POST http://localhost:8080/api/chat/send
Authorization: Bearer YOUR_TOKEN
Content-Type: application/x-www-form-urlencoded

targetUserId=2&content=你好，这是测试消息&type=TEXT
```

#### 6. 获取聊天消息列表
```http
GET http://localhost:8080/api/chat/messages?targetUserId=2&page=1&size=20
Authorization: Bearer YOUR_TOKEN
```

---

### 第七步：行程管理测试

#### 1. 创建个人行程
```http
POST http://localhost:8080/api/schedule/personal/create
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "高等数学课",
  "description": "第一教学楼A101",
  "startTime": "2024-12-20T08:00:00",
  "endTime": "2024-12-20T09:45:00",
  "location": "第一教学楼A101",
  "type": "CLASS",
  "remindType": "FIVE_MINUTES"
}
```

#### 2. 手动录入课程表
```http
POST http://localhost:8080/api/schedule/import/manual
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "semester": "2024-2025-1",
  "courses": [
    {
      "courseName": "高等数学",
      "dayOfWeek": 1,
      "timeSlot": "1-2节",
      "location": "教学楼A101",
      "teacher": "张老师"
    },
    {
      "courseName": "线性代数",
      "dayOfWeek": 3,
      "timeSlot": "3-4节",
      "location": "教学楼B201",
      "teacher": "李老师"
    }
  ]
}
```

#### 3. 创建团队
```http
POST http://localhost:8080/api/schedule/team/create
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "name": "项目组A",
  "description": "软件开发项目组",
  "needApprove": true,
  "maxMembers": 10
}
```

**保存返回的邀请码（inviteCode）**

#### 4. 通过邀请码加入团队（使用另一个用户的token）
```http
POST http://localhost:8080/api/schedule/team/join-by-code
Authorization: Bearer ANOTHER_USER_TOKEN
Content-Type: application/x-www-form-urlencoded

inviteCode=ABC123
```

#### 5. 获取团队邀请列表
```http
GET http://localhost:8080/api/schedule/team/invitations
Authorization: Bearer ANOTHER_USER_TOKEN
```

#### 6. 处理团队邀请
```http
POST http://localhost:8080/api/schedule/team/invitation/1/process?action=accept
Authorization: Bearer ANOTHER_USER_TOKEN
```

#### 7. 更新提醒状态
```http
PUT http://localhost:8080/api/schedule/reminder/1/status?enabled=true
Authorization: Bearer YOUR_TOKEN
```

---

## 📊 测试数据参考

### init.sql中的测试数据

**学生信息（清华大学，school_id=1）**：
- 学号：20230001，姓名：张三，身份证：110101199001011234
- 学号：20230002，姓名：李四，身份证：110101199002021234
- ...（共18条）

**教师信息（清华大学，school_id=1）**：
- 工号：T001，姓名：张教授，身份证：110101197001011234
- 工号：T002，姓名：李副教授，身份证：110101198002021234
- ...（共13条）

---

## ✅ 测试检查点

### 功能测试
- [ ] 用户注册和登录正常
- [ ] 邮箱登录正常（模拟模式）
- [ ] 学生身份认证正常
- [ ] 浏览记录功能正常
- [ ] 收藏功能正常
- [ ] 消息和聊天功能正常
- [ ] 行程管理功能正常
- [ ] 团队邀请功能正常

### 异常测试
- [ ] 无效token返回401
- [ ] 无效参数返回400
- [ ] 权限不足返回403
- [ ] 资源不存在返回404

---

**提示**：建议使用Knife4j进行测试，所有接口都有详细的文档和示例。
