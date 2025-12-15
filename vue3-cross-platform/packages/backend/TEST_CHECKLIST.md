# 后端功能测试检查清单

## ✅ 测试前准备

- [ ] MySQL数据库已启动
- [ ] 已执行init.sql初始化脚本
- [ ] 后端服务已启动（端口8080）
- [ ] 可以访问 http://localhost:8080/doc.html

---

## 🔐 用户认证模块

### 基础功能
- [ ] 用户注册（POST /api/user/register）
- [ ] 用户登录（POST /api/user/login）
- [ ] 获取用户信息（GET /api/user/info）
- [ ] 更新用户信息（PUT /api/user/info）
- [ ] 修改密码（POST /api/user/change-password）
- [ ] 退出登录（POST /api/user/logout）

### 邮箱登录
- [ ] 发送邮箱验证码（POST /api/auth/email/send-code）
- [ ] 邮箱登录（POST /api/auth/email/login）
- [ ] 验证码过期测试
- [ ] 验证码错误测试

---

## 🎓 身份认证模块

### 学生认证
- [ ] 学生身份认证申请（POST /api/auth/student/apply）
- [ ] 使用init.sql中的测试数据测试
- [ ] 错误学号/姓名/身份证测试
- [ ] 查询认证申请记录（GET /api/auth/apply/records）

### 教师认证
- [ ] 教师身份认证申请（POST /api/auth/teacher/apply）
- [ ] 上传证书文件测试
- [ ] 错误工号/姓名/身份证测试

### 游客认证
- [ ] 游客活体检测（POST /api/auth/visitor/face-detect）
- [ ] 游客注册（POST /api/auth/visitor/register）

---

## 💳 校园卡模块

- [ ] 查询校园卡信息（GET /api/ecard/info）
- [ ] 校园卡消费（POST /api/ecard/consume）
- [ ] 消费记录查询（GET /api/ecard/consume-records）
- [ ] 校园卡充值（POST /api/ecard/recharge）
- [ ] 校园卡挂失（POST /api/ecard/report-loss）
- [ ] 校园卡解挂（POST /api/ecard/unlock）

---

## 🛒 二手交易模块

### 商品管理
- [ ] 发布商品（POST /api/secondhand/publish）
- [ ] 商品列表查询（GET /api/secondhand/list）
- [ ] 商品详情查询（GET /api/secondhand/detail/{id}）
- [ ] 修改商品信息（PUT /api/secondhand/update/{id}）
- [ ] 下架商品（PUT /api/secondhand/off-shelf/{id}）
- [ ] 删除商品（DELETE /api/secondhand/delete/{id}）

### 收藏功能
- [ ] 收藏商品（POST /api/secondhand/favorite/{id}）
- [ ] 取消收藏（DELETE /api/secondhand/favorite/{id}）
- [ ] 获取收藏列表（GET /api/secondhand/favorites）

### 浏览记录
- [ ] 记录浏览行为（POST /api/secondhand/browse/{id}）
- [ ] 获取浏览记录（GET /api/secondhand/browse-history）
- [ ] 删除浏览记录（DELETE /api/secondhand/browse-history/{id}）
- [ ] 清空浏览记录（DELETE /api/secondhand/browse-history/clear）

---

## 💼 兼职管理模块

### 兼职管理
- [ ] 发布兼职（POST /api/parttime/publish）
- [ ] 兼职列表查询（GET /api/parttime/list）
- [ ] 兼职详情查询（GET /api/parttime/detail/{id}）
- [ ] 报名兼职（POST /api/parttime/apply）
- [ ] 取消报名（DELETE /api/parttime/apply/{id}）

### 收藏功能
- [ ] 收藏兼职（POST /api/parttime/favorite/{id}）
- [ ] 取消收藏（DELETE /api/parttime/favorite/{id}）
- [ ] 获取收藏列表（GET /api/parttime/favorites）
- [ ] 清空收藏（DELETE /api/parttime/favorites/clear）

### 浏览记录
- [ ] 记录浏览行为（POST /api/parttime/browse/{id}）
- [ ] 获取浏览记录（GET /api/parttime/browse-history）
- [ ] 删除浏览记录（DELETE /api/parttime/browse-history/{id}）
- [ ] 清空浏览记录（DELETE /api/parttime/browse-history/clear）

---

## 📅 行程管理模块

### 个人行程
- [ ] 创建个人行程（POST /api/schedule/personal/create）
- [ ] 获取个人行程列表（GET /api/schedule/personal/list）
- [ ] 获取个人行程详情（GET /api/schedule/personal/detail/{id}）
- [ ] 修改个人行程（PUT /api/schedule/personal/update/{id}）
- [ ] 删除个人行程（DELETE /api/schedule/personal/delete/{id}）
- [ ] 更新行程状态（PUT /api/schedule/personal/update-status/{id}）

### 团队管理
- [ ] 创建团队（POST /api/schedule/team/create）
- [ ] 获取我的团队列表（GET /api/schedule/team/my-teams）
- [ ] 获取团队详情（GET /api/schedule/team/detail/{id}）
- [ ] 邀请成员（POST /api/schedule/team/invite）
- [ ] 通过邀请码加入团队（POST /api/schedule/team/join-by-code）
- [ ] 获取团队邀请列表（GET /api/schedule/team/invitations）
- [ ] 处理团队邀请（POST /api/schedule/team/invitation/{id}/process）
- [ ] 移除团队成员（DELETE /api/schedule/team/{teamId}/member/{userId}）

### 课程表导入
- [ ] Excel导入课程表（POST /api/schedule/import/schedule-excel）
- [ ] 导出课程表（GET /api/schedule/export/schedule-excel）
- [ ] 获取导入模板（GET /api/schedule/import/template）
- [ ] 手动录入导入课程表（POST /api/schedule/import/manual）
- [ ] 链接导入课程表（POST /api/schedule/import/from-link）

### 提醒管理
- [ ] 获取今日提醒（GET /api/schedule/reminders/today）
- [ ] 标记为已提醒（PUT /api/schedule/reminders/mark-reminded/{id}）
- [ ] 更新提醒状态（PUT /api/schedule/reminder/{id}/status）
- [ ] 删除提醒（DELETE /api/schedule/reminder/{id}）

---

## 💬 消息和聊天模块

### 消息中心
- [ ] 获取消息列表（GET /api/messages/list）
- [ ] 标记消息已读（PUT /api/messages/{id}/read）
- [ ] 批量标记已读（PUT /api/messages/batch-read）
- [ ] 获取未读消息数量（GET /api/messages/unread-count）

### 聊天功能
- [ ] 获取聊天会话列表（GET /api/chat/conversations）
- [ ] 获取聊天消息列表（GET /api/chat/messages）
- [ ] 发送聊天消息（POST /api/chat/send）
- [ ] 标记会话消息为已读（PUT /api/chat/conversations/{conversationId}/read）

---

## 🏢 高校管理模块

### 学生/教师信息管理
- [ ] 获取学生信息列表（GET /api/university/students）
- [ ] 添加学生信息（POST /api/university/students）
- [ ] 导入学生信息（POST /api/university/students/import）
- [ ] 导出学生信息（GET /api/university/students/export）
- [ ] 更新学生信息（PUT /api/university/students/{studentInfoId}）
- [ ] 删除学生信息（DELETE /api/university/students/{studentInfoId}）

- [ ] 获取教师信息列表（GET /api/university/teachers）
- [ ] 添加教师信息（POST /api/university/teachers）
- [ ] 导入教师信息（POST /api/university/teachers/import）
- [ ] 导出教师信息（GET /api/university/teachers/export）
- [ ] 更新教师信息（PUT /api/university/teachers/{teacherInfoId}）
- [ ] 删除教师信息（DELETE /api/university/teachers/{teacherInfoId}）

---

## 🏪 商户管理模块

- [ ] 商户入驻申请（POST /api/merchant/apply）
- [ ] 公司资格校验（POST /api/merchant/verify-company）
- [ ] 支付保证金（POST /api/merchant/deposit/pay）
- [ ] 刷新支付二维码（POST /api/merchant/deposit/pay/{orderId}/refresh）
- [ ] 查询支付状态（GET /api/merchant/deposit/pay/{orderId}/status）

---

## 🔒 权限和安全测试

- [ ] 未登录访问需要认证的接口返回401
- [ ] Token过期后访问返回401
- [ ] 无权限访问返回403
- [ ] 无效参数返回400
- [ ] 资源不存在返回404

---

## 📝 测试记录

### 测试环境
- **测试时间**：___________
- **测试人员**：___________
- **后端版本**：___________
- **数据库版本**：___________

### 测试结果
- **通过数量**：___ / ___
- **失败数量**：___ / ___
- **阻塞问题**：___________

### 问题记录
1. ___________
2. ___________
3. ___________

---

**提示**：建议使用Knife4j进行测试，界面友好，操作简单。
