# 后端功能测试结果

## 🚀 服务启动

### 启动命令
```powershell
cd packages/backend
.\mvnw.cmd spring-boot:run
```

### 启动检查
- [ ] 服务是否成功启动（端口8080）
- [ ] 是否可以访问 http://localhost:8080/doc.html
- [ ] 数据库连接是否正常
- [ ] 是否有启动错误

---

## ✅ 功能测试结果

### 一、用户认证模块

#### 1.1 用户注册
- **接口**：`POST /api/user/register`
- **测试时间**：___________
- **测试结果**：□ 通过  □ 失败
- **响应**：___________
- **备注**：___________

#### 1.2 用户登录
- **接口**：`POST /api/user/login`
- **测试时间**：___________
- **测试结果**：□ 通过  □ 失败
- **Token获取**：□ 成功  □ 失败
- **备注**：___________

#### 1.3 邮箱登录
- **发送验证码**：`POST /api/auth/email/send-code`
  - **测试结果**：□ 通过  □ 失败
  - **验证码返回**：□ 是（模拟模式）  □ 否
  
- **邮箱登录**：`POST /api/auth/email/login`
  - **测试结果**：□ 通过  □ 失败
  - **备注**：___________

---

### 二、身份认证模块

#### 2.1 学生身份认证
- **接口**：`POST /api/auth/student/apply`
- **测试数据**：学号=20230001, 姓名=张三, 身份证=110101199001011234
- **测试结果**：□ 通过  □ 失败
- **备注**：___________

#### 2.2 教师身份认证
- **接口**：`POST /api/auth/teacher/apply`
- **测试数据**：工号=T001, 姓名=张教授, 身份证=110101197001011234
- **测试结果**：□ 通过  □ 失败
- **备注**：___________

---

### 三、浏览记录功能

#### 3.1 二手商品浏览记录
- **记录浏览**：`POST /api/secondhand/browse/{id}` - □ 通过  □ 失败
- **获取浏览记录**：`GET /api/secondhand/browse-history` - □ 通过  □ 失败
- **删除浏览记录**：`DELETE /api/secondhand/browse-history/{id}` - □ 通过  □ 失败
- **清空浏览记录**：`DELETE /api/secondhand/browse-history/clear` - □ 通过  □ 失败

#### 3.2 兼职岗位浏览记录
- **记录浏览**：`POST /api/parttime/browse/{id}` - □ 通过  □ 失败
- **获取浏览记录**：`GET /api/parttime/browse-history` - □ 通过  □ 失败
- **删除浏览记录**：`DELETE /api/parttime/browse-history/{id}` - □ 通过  □ 失败
- **清空浏览记录**：`DELETE /api/parttime/browse-history/clear` - □ 通过  □ 失败

---

### 四、收藏功能

#### 4.1 兼职收藏
- **收藏兼职**：`POST /api/parttime/favorite/{id}` - □ 通过  □ 失败
- **获取收藏列表**：`GET /api/parttime/favorites` - □ 通过  □ 失败
- **取消收藏**：`DELETE /api/parttime/favorite/{id}` - □ 通过  □ 失败
- **清空收藏**：`DELETE /api/parttime/favorites/clear` - □ 通过  □ 失败

---

### 五、消息和聊天模块

#### 5.1 消息中心
- **获取消息列表**：`GET /api/messages/list` - □ 通过  □ 失败
- **标记消息已读**：`PUT /api/messages/{id}/read` - □ 通过  □ 失败
- **批量标记已读**：`PUT /api/messages/batch-read` - □ 通过  □ 失败
- **获取未读数量**：`GET /api/messages/unread-count` - □ 通过  □ 失败

#### 5.2 聊天功能
- **获取会话列表**：`GET /api/chat/conversations` - □ 通过  □ 失败
- **获取消息列表**：`GET /api/chat/messages` - □ 通过  □ 失败
- **发送消息**：`POST /api/chat/send` - □ 通过  □ 失败
- **标记会话已读**：`PUT /api/chat/conversations/{conversationId}/read` - □ 通过  □ 失败

---

### 六、行程管理模块

#### 6.1 个人行程
- **创建行程**：`POST /api/schedule/personal/create` - □ 通过  □ 失败
- **获取行程列表**：`GET /api/schedule/personal/list` - □ 通过  □ 失败
- **更新行程**：`PUT /api/schedule/personal/update/{id}` - □ 通过  □ 失败
- **删除行程**：`DELETE /api/schedule/personal/delete/{id}` - □ 通过  □ 失败

#### 6.2 团队管理
- **创建团队**：`POST /api/schedule/team/create` - □ 通过  □ 失败
- **通过邀请码加入**：`POST /api/schedule/team/join-by-code` - □ 通过  □ 失败
- **获取邀请列表**：`GET /api/schedule/team/invitations` - □ 通过  □ 失败
- **处理邀请**：`POST /api/schedule/team/invitation/{id}/process` - □ 通过  □ 失败

#### 6.3 课程表导入
- **手动录入**：`POST /api/schedule/import/manual` - □ 通过  □ 失败
- **链接导入**：`POST /api/schedule/import/from-link` - □ 通过  □ 失败（框架已实现）

#### 6.4 提醒管理
- **更新提醒状态**：`PUT /api/schedule/reminder/{id}/status` - □ 通过  □ 失败
- **删除提醒**：`DELETE /api/schedule/reminder/{id}` - □ 通过  □ 失败

---

## 🐛 发现的问题

### 问题1
- **接口**：___________
- **问题描述**：___________
- **错误信息**：___________
- **解决方案**：___________

### 问题2
- **接口**：___________
- **问题描述**：___________
- **错误信息**：___________
- **解决方案**：___________

---

## 📊 测试统计

- **总测试项**：___ 项
- **通过项**：___ 项
- **失败项**：___ 项
- **通过率**：___ %

---

## ✅ 测试结论

- [ ] 所有核心功能正常
- [ ] 部分功能存在问题
- [ ] 需要进一步优化

**测试人员**：___________  
**测试时间**：___________  
**测试环境**：Windows 10, Spring Boot 3.2.0, MySQL

---

**提示**：建议使用Knife4j（http://localhost:8080/doc.html）进行详细测试。
