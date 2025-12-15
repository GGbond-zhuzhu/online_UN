# 缺失API接口清单

## 检查时间
2024-01-15

## 说明
本文档列出了前端页面需要但后端尚未实现的API接口。根据前端页面的TODO标记和后端已有Controller的对比分析，整理出以下缺失的API接口。

---

## 1. 认证模块 - 邮箱登录相关

### 1.1 发送邮箱验证码（模拟实现）✅ 已实现
**前端需求位置：** `pages/auth/email-login.vue`

**接口信息：**
- **路径：** `POST /api/auth/email/send-code`
- **描述：** 发送邮箱验证码用于邮箱登录（模拟实现，实际不发送邮件）
- **说明：** 由于企业级邮箱服务需要企业级用户才能接入，此接口采用模拟实现。验证码会在响应中返回，开发/测试环境可直接使用。
- **请求参数：**
  ```json
  {
    "email": "string" // 邮箱地址
  }
  ```
- **响应（模拟模式 - 邮件服务未配置时）：**
  ```json
  {
    "code": 200,
    "message": "验证码已发送",
    "data": {
      "codeId": "string", // 验证码ID，用于后续验证
      "code": "123456",   // 验证码（仅模拟模式返回，方便测试）
      "expireTime": 600   // 过期时间（秒），默认10分钟
    }
  }
  ```
- **响应（真实模式 - 邮件服务已配置时）：**
  ```json
  {
    "code": 200,
    "message": "验证码已发送",
    "data": {
      "codeId": "string", // 验证码ID，用于后续验证
      "expireTime": 600   // 过期时间（秒），默认10分钟
      // 注意：不返回验证码，验证码已发送到邮箱
    }
  }
  ```
- **实现说明：**
  - ✅ **已实现**：接口已在 `AuthController` 和 `EmailServiceImpl` 中实现
  - **模拟模式**：当 `spring.mail.enabled=false` 或 `spring.mail.username` 未配置时，验证码直接返回在响应中，方便开发测试
  - **真实模式**：当邮件服务已配置时，验证码发送到邮箱，响应中不返回验证码
  - **验证码格式**：6位数字（100000-999999）
  - **验证码有效期**：10分钟
  - **存储方式**：验证码存储在数据库 `email_verification_code` 表中
  - **验证方式**：通过 `codeId` + `email` + `code` 验证，验证后标记为已使用

### 1.2 邮箱登录 ✅ 已实现
**前端需求位置：** `pages/auth/email-login.vue`

**接口信息：**
- **路径：** `POST /api/auth/email/login`
- **描述：** 使用邮箱和验证码登录
- **请求参数：**
  ```json
  {
    "email": "string",   // 邮箱地址
    "code": "string",    // 验证码
    "codeId": "string"   // 验证码ID（从发送验证码接口获取）
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "userId": 123,
      "username": "user@example.com",
      "email": "user@example.com",
      "role": "TOURIST",
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": 86400000
    }
  }
  ```
- **实现说明：**
  - ✅ **已实现**：接口已在 `AuthController` 和 `AuthServiceImpl` 中实现
  - **验证流程**：验证邮箱验证码 → 查询用户（不存在则自动创建）→ 生成JWT Token → 记录登录日志
  - **自动注册**：如果邮箱对应的用户不存在，系统会自动创建用户（默认为游客角色）
  - **用户角色**：自动创建的用户默认为 `TOURIST`（游客）角色

---

## 2. 浏览记录管理模块

### 2.1 二手商品浏览记录

#### 2.1.1 获取浏览记录列表
**前端需求位置：** `pages/secondhand/history/index.vue`

**接口信息：**
- **路径：** `GET /api/secondhand/browse-history`
- **描述：** 获取当前用户的二手商品浏览记录
- **请求参数：**
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "records": [
        {
          "id": 1,
          "goodsId": 1001,
          "goodsTitle": "二手笔记本电脑",
          "goodsImage": "string",
          "price": 2000.00,
          "viewTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 50,
      "page": 1,
      "size": 10
    }
  }
  ```

#### 2.1.2 删除单条浏览记录
**前端需求位置：** `pages/secondhand/history/index.vue`

**接口信息：**
- **路径：** `DELETE /api/secondhand/browse-history/{id}`
- **描述：** 删除指定的浏览记录
- **路径参数：**
  - `id` (Long): 浏览记录ID
- **响应：** 标准成功响应

#### 2.1.3 清空浏览记录
**前端需求位置：** `pages/secondhand/history/index.vue`

**接口信息：**
- **路径：** `DELETE /api/secondhand/browse-history/clear`
- **描述：** 清空当前用户的所有浏览记录
- **响应：** 标准成功响应

### 2.2 兼职岗位浏览记录

#### 2.2.1 获取浏览记录列表
**前端需求位置：** `pages/parttime/history/index.vue`

**接口信息：**
- **路径：** `GET /api/parttime/browse-history`
- **描述：** 获取当前用户的兼职岗位浏览记录
- **请求参数：**
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
- **响应：** 类似二手商品浏览记录格式

#### 2.2.2 删除单条浏览记录
**前端需求位置：** `pages/parttime/history/index.vue`

**接口信息：**
- **路径：** `DELETE /api/parttime/browse-history/{id}`
- **描述：** 删除指定的浏览记录

#### 2.2.3 清空浏览记录
**前端需求位置：** `pages/parttime/history/index.vue`

**接口信息：**
- **路径：** `DELETE /api/parttime/browse-history/clear`
- **描述：** 清空当前用户的所有浏览记录

---

## 3. 兼职模块 - 收藏管理

### 3.1 收藏兼职岗位
**前端需求位置：** `pages/parttime/detail/index.vue`, `pages/parttime/favorites/index.vue`

**接口信息：**
- **路径：** `POST /api/parttime/favorite/{id}`
- **描述：** 收藏指定的兼职岗位
- **路径参数：**
  - `id` (Long): 兼职岗位ID
- **响应：** 标准成功响应

### 3.2 取消收藏兼职岗位
**前端需求位置：** `pages/parttime/detail/index.vue`, `pages/parttime/favorites/index.vue`

**接口信息：**
- **路径：** `DELETE /api/parttime/favorite/{id}`
- **描述：** 取消收藏指定的兼职岗位
- **路径参数：**
  - `id` (Long): 兼职岗位ID
- **响应：** 标准成功响应

### 3.3 获取收藏列表
**前端需求位置：** `pages/parttime/favorites/index.vue`

**接口信息：**
- **路径：** `GET /api/parttime/favorites`
- **描述：** 获取当前用户收藏的兼职岗位列表
- **请求参数：**
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "records": [
        {
          "id": 1,
          "parttimeId": 1001,
          "title": "校园推广专员",
          "companyName": "某教育公司",
          "salary": "150-200元/天",
          "location": "校内",
          "favoriteTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 20,
      "page": 1,
      "size": 10
    }
  }
  ```

### 3.4 清空收藏
**前端需求位置：** `pages/parttime/favorites/index.vue`

**接口信息：**
- **路径：** `DELETE /api/parttime/favorites/clear`
- **描述：** 清空当前用户的所有收藏
- **响应：** 标准成功响应

---

## 4. 消息和聊天模块

### 4.1 消息中心

#### 4.1.1 获取消息列表
**前端需求位置：** `pages/messages/index.vue`

**接口信息：**
- **路径：** `GET /api/messages/list`
- **描述：** 获取当前用户的消息列表，支持分类筛选
- **请求参数：**
  - `type` (String, 可选): 消息类型（ALL/SYSTEM/PARTTIME/SECONDHAND）
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "records": [
        {
          "id": 1,
          "type": "SYSTEM",
          "title": "系统通知",
          "content": "您的身份认证已通过审核",
          "read": false,
          "createTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 50,
      "page": 1,
      "size": 10
    }
  }
  ```

#### 4.1.2 标记消息为已读
**前端需求位置：** `pages/messages/index.vue`

**接口信息：**
- **路径：** `PUT /api/messages/{id}/read`
- **描述：** 将消息标记为已读
- **路径参数：**
  - `id` (Long): 消息ID
- **响应：** 标准成功响应

#### 4.1.3 批量标记已读
**接口信息：**
- **路径：** `PUT /api/messages/batch-read`
- **描述：** 批量标记消息为已读
- **请求参数：**
  ```json
  {
    "messageIds": [1, 2, 3] // 消息ID数组
  }
  ```

### 4.2 聊天功能

#### 4.2.1 获取聊天列表
**前端需求位置：** `pages/chat/index.vue`

**接口信息：**
- **路径：** `GET /api/chat/conversations`
- **描述：** 获取当前用户的聊天会话列表
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": [
      {
        "conversationId": "string",
        "targetUserId": 123,
        "targetUserName": "张同学",
        "targetUserAvatar": "string",
        "lastMessage": "你好，请问这个商品还在吗？",
        "lastMessageTime": "2024-01-15 10:30:00",
        "unreadCount": 2
      }
    ]
  }
  ```

#### 4.2.2 获取聊天消息列表
**前端需求位置：** `pages/chat/index.vue`

**接口信息：**
- **路径：** `GET /api/chat/messages`
- **描述：** 获取指定会话的聊天消息列表
- **请求参数：**
  - `conversationId` (String): 会话ID
  - `targetUserId` (Long, 可选): 目标用户ID（如果没有会话ID）
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认20): 每页大小
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": {
      "records": [
        {
          "id": 1,
          "senderId": 123,
          "senderName": "张同学",
          "content": "你好，请问这个商品还在吗？",
          "type": "TEXT", // TEXT/IMAGE/FILE
          "createTime": "2024-01-15 10:30:00"
        }
      ],
      "total": 50,
      "page": 1,
      "size": 20
    }
  }
  ```

#### 4.2.3 发送消息
**前端需求位置：** `pages/chat/index.vue`

**接口信息：**
- **路径：** `POST /api/chat/send`
- **描述：** 发送聊天消息
- **请求参数：**
  ```json
  {
    "targetUserId": 123, // 目标用户ID
    "content": "string",  // 消息内容
    "type": "TEXT",       // 消息类型（TEXT/IMAGE/FILE）
    "imageUrl": "string"  // 如果是图片消息，图片URL
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "发送成功",
    "data": {
      "messageId": 1,
      "createTime": "2024-01-15 10:30:00"
    }
  }
  ```

---

## 5. 行程管理模块 - 团队相关

### 5.1 通过邀请码加入团队
**前端需求位置：** `pages/schedule/team/index.vue`

**接口信息：**
- **路径：** `POST /api/schedule/team/join-by-code`
- **描述：** 通过团队邀请码加入团队
- **请求参数：**
  ```json
  {
    "inviteCode": "string" // 团队邀请码
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "加入团队成功",
    "data": {
      "teamId": 1,
      "teamName": "项目组A"
    }
  }
  ```

### 5.2 获取团队邀请列表
**前端需求位置：** `pages/schedule/team/index.vue`

**接口信息：**
- **路径：** `GET /api/schedule/team/invitations`
- **描述：** 获取当前用户收到的团队邀请列表
- **响应：**
  ```json
  {
    "code": 200,
    "message": "查询成功",
    "data": [
      {
        "id": 1,
        "teamId": 1,
        "teamName": "项目组A",
        "inviterId": 123,
        "inviterName": "李同学",
        "inviteTime": "2024-01-15 10:30:00",
        "status": "PENDING" // PENDING/ACCEPTED/REJECTED
      }
    ]
  }
  ```

### 5.3 处理团队邀请
**前端需求位置：** `pages/schedule/team/index.vue`

**接口信息：**
- **路径：** `POST /api/schedule/team/invitation/{id}/process`
- **描述：** 处理团队邀请（接受/拒绝）
- **路径参数：**
  - `id` (Long): 邀请ID
- **请求参数：**
  ```json
  {
    "action": "accept" // accept/reject
  }
  ```
- **响应：** 标准成功响应

**注意：** 后端已有类似接口 `/api/schedule/team/invite/{inviteId}/process`，但路径略有不同，需要确认是否一致。

---

## 6. 行程管理模块 - 提醒管理

### 6.1 更新提醒状态
**前端需求位置：** `pages/schedule/index.vue`

**接口信息：**
- **路径：** `PUT /api/schedule/reminder/{id}/status`
- **描述：** 更新行程提醒的启用/禁用状态
- **路径参数：**
  - `id` (Long): 行程ID
- **请求参数：**
  ```json
  {
    "enabled": true // 是否启用提醒
  }
  ```
- **响应：** 标准成功响应

### 6.2 删除提醒
**前端需求位置：** `pages/schedule/index.vue`

**接口信息：**
- **路径：** `DELETE /api/schedule/reminder/{id}`
- **描述：** 删除行程提醒
- **路径参数：**
  - `id` (Long): 提醒ID
- **响应：** 标准成功响应

**注意：** 后端已有删除行程接口 `/api/schedule/personal/delete/{id}`，但可能需要单独的提醒删除接口。

---

## 7. 行程管理模块 - 课程表导入

### 7.1 手动录入导入课程表
**前端需求位置：** `pages/schedule/import/index.vue`

**接口信息：**
- **路径：** `POST /api/schedule/import/manual`
- **描述：** 手动录入课程信息并导入
- **请求参数：**
  ```json
  {
    "semester": "2024-2025-1", // 学期
    "courses": [
      {
        "courseName": "高等数学",
        "dayOfWeek": 1, // 1-7，周一到周日
        "timeSlot": "1-2节",
        "location": "教学楼A101",
        "teacher": "张老师"
      }
    ]
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "导入成功",
    "data": {
      "importedCount": 5,
      "scheduleIds": [1, 2, 3, 4, 5]
    }
  }
  ```

### 7.2 链接导入课程表
**前端需求位置：** `pages/schedule/import/index.vue`

**接口信息：**
- **路径：** `POST /api/schedule/import/from-link`
- **描述：** 通过教务系统链接导入课程表
- **请求参数：**
  ```json
  {
    "url": "string",      // 课程表链接
    "username": "string", // 账号（如果需要登录）
    "password": "string"  // 密码（如果需要登录）
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "导入成功",
    "data": {
      "importedCount": 10,
      "scheduleIds": [1, 2, 3, ...]
    }
  }
  ```

**注意：** 后端已有Excel导入接口 `/api/schedule/import/schedule-excel`，但缺少手动录入和链接导入接口。

---

## 8. 其他补充接口

### 8.1 记录浏览行为
**说明：** 当用户查看商品详情或兼职详情时，需要记录浏览行为

**接口信息：**
- **路径：** `POST /api/secondhand/browse/{id}` - 记录二手商品浏览
- **路径：** `POST /api/parttime/browse/{id}` - 记录兼职岗位浏览
- **描述：** 记录用户浏览行为，用于生成浏览记录
- **路径参数：**
  - `id` (Long): 商品ID或岗位ID
- **响应：** 标准成功响应（可无返回数据）

---

## 总结

### 缺失API统计

| 模块 | 缺失接口数量 | 已实现 | 优先级 |
|------|------------|--------|--------|
| 认证模块 | 2 | ✅ 2 | 高 |
| 浏览记录管理 | 6 | 0 | 中 |
| 兼职收藏管理 | 4 | 0 | 中 |
| 消息和聊天 | 5 | 0 | 高 |
| 团队管理 | 2 | 0 | 中 |
| 行程提醒 | 2 | 0 | 低 |
| 课程表导入 | 2 | 0 | 中 |
| 其他 | 2 | 0 | 低 |
| **总计** | **25** | **2** | - |

### 优先级说明

- **高优先级：** 影响核心功能，需要优先实现
- **中优先级：** 影响用户体验，建议尽快实现
- **低优先级：** 可以后续优化实现

### 实现建议

1. **先实现高优先级接口**：邮箱登录、消息和聊天功能
2. **再实现中优先级接口**：浏览记录、收藏管理、课程表导入
3. **最后实现低优先级接口**：提醒管理、其他补充功能

### 注意事项

1. 所有接口都需要实现JWT认证（除公开接口外）
2. 需要实现权限控制（如：只能操作自己的数据）
3. 需要实现数据校验和异常处理
4. 建议使用统一的响应格式（ApiResponse）
5. 需要添加Swagger文档注解
6. 需要考虑分页、排序等功能

---

## 相关文档

- [API完成报告](./API_COMPLETION_REPORT.md)
- [外部API需求](./EXTERNAL_API_REQUIREMENTS.md)
- [前端API集成文档](../web/API_INTEGRATION.md)
