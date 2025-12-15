# 后端功能测试指南

## 📋 测试准备

### 1. 启动后端服务

#### Windows系统：
```bash
cd packages/backend
mvnw.cmd spring-boot:run
```

#### Linux/Mac系统：
```bash
cd packages/backend
./mvnw spring-boot:run
```

### 2. 访问API文档

启动成功后，访问 Knife4j API文档：
```
http://localhost:8080/doc.html
```

### 3. 数据库准备

确保MySQL数据库已启动，并执行初始化脚本：
```bash
mysql -u root -p < src/main/resources/init.sql
```

---

## 🧪 功能测试清单

### 一、用户认证模块

#### 1.1 用户注册
- **接口**：`POST /api/user/register`
- **测试数据**：
```json
{
  "username": "testuser001",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "测试用户",
  "role": "TOURIST",
  "schoolId": 1
}
```
- **预期结果**：返回用户ID

#### 1.2 用户登录
- **接口**：`POST /api/user/login`
- **测试数据**：
```json
{
  "username": "testuser001",
  "password": "123456"
}
```
- **预期结果**：返回JWT token和用户信息

#### 1.3 邮箱登录
- **步骤1**：发送验证码
  - **接口**：`POST /api/auth/email/send-code`
  - **参数**：`email=test@example.com`
  - **预期结果**：返回codeId和expireTime（模拟模式下还会返回code）

- **步骤2**：邮箱登录
  - **接口**：`POST /api/auth/email/login`
  - **参数**：
    - `email=test@example.com`
    - `code=123456`（从步骤1获取）
    - `codeId=xxx`（从步骤1获取）
  - **预期结果**：返回JWT token和用户信息

---

### 二、身份认证模块

#### 2.1 学生身份认证
- **接口**：`POST /api/auth/student/apply`
- **测试数据**（使用init.sql中的测试数据）：
  - `studentId=20230001`
  - `verificationCode=123456`（模拟验证码）
  - `name=张三`
  - `idCard=110101199001011234`
  - `schoolId=1`
- **预期结果**：认证申请提交成功

#### 2.2 教师身份认证
- **接口**：`POST /api/auth/teacher/apply`
- **测试数据**（使用init.sql中的测试数据）：
  - `teacherId=T001`
  - `name=张教授`
  - `idCard=110101197001011234`
  - `schoolId=1`
  - `certificateFile=xxx`（上传文件）
- **预期结果**：认证申请提交成功

#### 2.3 游客活体检测
- **接口**：`POST /api/auth/visitor/face-detect`
- **测试数据**：
  - `faceImage=data:image/jpeg;base64,xxx`（Base64编码的人脸图片）
  - `name=游客001`
  - `phone=13800138000`
  - `idCard=110101199001011234`
  - `reason=参观校园`
- **预期结果**：活体检测通过，返回检测结果

---

### 三、校园卡模块

#### 3.1 查询校园卡信息
- **接口**：`GET /api/ecard/info`
- **需要登录**：是
- **预期结果**：返回校园卡信息（余额、状态等）

#### 3.2 校园卡消费
- **接口**：`POST /api/ecard/consume`
- **测试数据**：
```json
{
  "amount": 10.00,
  "merchantId": 1,
  "location": "食堂A"
}
```
- **预期结果**：消费成功，返回消费记录

---

### 四、二手交易模块

#### 4.1 发布商品
- **接口**：`POST /api/secondhand/publish`
- **测试数据**：
```json
{
  "title": "二手笔记本电脑",
  "description": "9成新，配置良好",
  "price": 2000.00,
  "originalPrice": 3500.00,
  "category": "ELECTRONICS",
  "imageUrls": ["url1", "url2"]
}
```
- **预期结果**：商品发布成功

#### 4.2 商品列表查询
- **接口**：`GET /api/secondhand/list`
- **参数**：
  - `page=1`
  - `size=10`
  - `category=ELECTRONICS`（可选）
- **预期结果**：返回商品列表

#### 4.3 记录浏览行为
- **接口**：`POST /api/secondhand/browse/{id}`
- **参数**：`id=1`（商品ID）
- **预期结果**：浏览记录已保存

#### 4.4 获取浏览记录
- **接口**：`GET /api/secondhand/browse-history`
- **参数**：
  - `page=1`
  - `size=10`
- **预期结果**：返回浏览记录列表

---

### 五、兼职管理模块

#### 5.1 发布兼职
- **接口**：`POST /api/parttime/publish`
- **测试数据**：
```json
{
  "title": "校园推广专员",
  "description": "负责校园推广工作",
  "salaryPerHour": 150.00,
  "location": "校内",
  "workTime": "周末",
  "recruitCount": 5
}
```
- **预期结果**：兼职发布成功

#### 5.2 收藏兼职
- **接口**：`POST /api/parttime/favorite/{id}`
- **参数**：`id=1`（兼职ID）
- **预期结果**：收藏成功

#### 5.3 获取收藏列表
- **接口**：`GET /api/parttime/favorites`
- **参数**：
  - `page=1`
  - `size=10`
- **预期结果**：返回收藏列表

#### 5.4 记录浏览行为
- **接口**：`POST /api/parttime/browse/{id}`
- **参数**：`id=1`（兼职ID）
- **预期结果**：浏览记录已保存

---

### 六、行程管理模块

#### 6.1 创建个人行程
- **接口**：`POST /api/schedule/personal/create`
- **测试数据**：
```json
{
  "title": "高等数学课",
  "description": "第一教学楼A101",
  "startTime": "2024-12-16T08:00:00",
  "endTime": "2024-12-16T09:45:00",
  "location": "第一教学楼A101",
  "type": "CLASS",
  "remindType": "FIVE_MINUTES"
}
```
- **预期结果**：行程创建成功

#### 6.2 手动录入课程表
- **接口**：`POST /api/schedule/import/manual`
- **测试数据**：
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
- **预期结果**：返回导入数量（importedCount）和行程ID列表

#### 6.3 创建团队
- **接口**：`POST /api/schedule/team/create`
- **测试数据**：
```json
{
  "name": "项目组A",
  "description": "软件开发项目组",
  "needApprove": true,
  "maxMembers": 10
}
```
- **预期结果**：团队创建成功，返回团队信息和邀请码

#### 6.4 通过邀请码加入团队
- **接口**：`POST /api/schedule/team/join-by-code`
- **参数**：`inviteCode=ABC123`（从创建团队接口获取）
- **预期结果**：加入团队成功（或提交申请成功）

#### 6.5 获取团队邀请列表
- **接口**：`GET /api/schedule/team/invitations`
- **预期结果**：返回收到的团队邀请列表

#### 6.6 处理团队邀请
- **接口**：`POST /api/schedule/team/invitation/{id}/process`
- **参数**：
  - `id=1`（邀请ID）
  - `action=accept`（或`reject`）
- **预期结果**：邀请处理成功

#### 6.7 更新提醒状态
- **接口**：`PUT /api/schedule/reminder/{id}/status`
- **参数**：
  - `id=1`（行程ID）
  - `enabled=true`（或`false`）
- **预期结果**：提醒状态更新成功

---

### 七、消息和聊天模块

#### 7.1 获取消息列表
- **接口**：`GET /api/messages/list`
- **参数**：
  - `type=ALL`（可选：ALL/SYSTEM/PARTTIME/SECONDHAND）
  - `page=1`
  - `size=10`
- **预期结果**：返回消息列表

#### 7.2 标记消息已读
- **接口**：`PUT /api/messages/{id}/read`
- **参数**：`id=1`（消息ID）
- **预期结果**：标记成功

#### 7.3 获取聊天会话列表
- **接口**：`GET /api/chat/conversations`
- **预期结果**：返回聊天会话列表

#### 7.4 发送聊天消息
- **接口**：`POST /api/chat/send`
- **参数**：
  - `targetUserId=2`
  - `content=你好，请问这个商品还在吗？`
  - `type=TEXT`
- **预期结果**：消息发送成功

#### 7.5 获取聊天消息列表
- **接口**：`GET /api/chat/messages`
- **参数**：
  - `conversationId=1`（可选）
  - `targetUserId=2`（可选，如果没有conversationId）
  - `page=1`
  - `size=20`
- **预期结果**：返回聊天消息列表

---

## 🔍 测试工具推荐

### 1. Knife4j（Swagger UI）
- **地址**：http://localhost:8080/doc.html
- **功能**：可视化API文档，支持在线测试

### 2. Postman
- **功能**：API测试工具
- **使用**：导入API文档或手动创建请求

### 3. curl命令
- **示例**：
```bash
# 用户登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser001","password":"123456"}'

# 获取用户信息（需要token）
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📝 测试数据准备

### 测试用户
使用 `init.sql` 中已创建的测试数据：
- **学生信息**：18条（清华大学和北京大学）
- **教师信息**：13条（清华大学和北京大学）

### 测试学校
- **学校ID=1**：清华大学
- **学校ID=2**：北京大学

---

## ⚠️ 注意事项

1. **JWT Token**：
   - 登录后获取的token需要在后续请求的Header中携带
   - Header格式：`Authorization: Bearer YOUR_TOKEN`

2. **邮件服务**：
   - 邮箱登录功能在邮件服务未配置时为模拟模式
   - 模拟模式下，验证码会在响应中返回（仅开发/测试环境）

3. **外部API**：
   - 百度AI人脸识别：需要配置API Key
   - 高德地图IP定位：需要配置API Key
   - 天眼查企业验证：需要配置API Token
   - 支付接口：当前为模拟实现（生成无效二维码）

4. **数据库**：
   - 确保MySQL服务已启动
   - 确保已执行init.sql初始化脚本
   - 测试数据已包含在init.sql中

---

## 🐛 常见问题排查

### 1. 服务启动失败
- 检查MySQL是否启动
- 检查application.properties中的数据库配置
- 检查端口8080是否被占用

### 2. 接口返回401未授权
- 检查是否携带JWT token
- 检查token是否过期
- 重新登录获取新token

### 3. 接口返回500错误
- 查看后端日志
- 检查数据库连接
- 检查外部API配置

### 4. 邮箱验证码收不到
- 检查邮件服务配置（application.properties）
- 模拟模式下验证码会在响应中返回

---

## 📊 测试结果记录

建议记录以下信息：
- 测试接口名称
- 测试时间
- 请求参数
- 响应结果
- 是否通过
- 问题描述（如有）

---

**创建时间**：2024年12月  
**文档维护**：AI Assistant
