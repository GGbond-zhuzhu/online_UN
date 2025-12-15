# 数据库表创建总结

## 创建时间
2024年12月

## ✅ 已创建的数据库表

### 1. merchant（商户表）

**用途**：存储商户详细信息，支持商户入驻、资质审核、信用管理等功能

**主要字段**：
- `user_id` - 用户ID（关联用户表）
- `merchant_name` - 商户名称
- `merchant_type` - 商户类型（COMPANY/INDIVIDUAL）
- `business_scope` - 经营范围
- `credit_code` - 统一社会信用代码
- `contact_name/phone/email` - 联系人信息
- `business_license_url` - 营业执照URL
- `legal_id_card_url` - 法人身份证URL
- `status` - 状态（PENDING/APPROVED/REJECTED/SUSPENDED）
- `deposit_amount` - 保证金金额
- `credit_score` - 信用分（0-100）
- `approved_time` - 审核通过时间
- `approver_id` - 审核人ID
- `reject_reason` - 拒绝原因

**索引**：
- `uk_user_id` - 用户ID唯一索引
- `idx_status` - 状态索引
- `idx_merchant_type` - 商户类型索引

**外键**：
- `fk_merchant_user` - 关联用户表

---

### 2. deposit_record（保证金记录表）

**用途**：存储商户保证金支付记录，支持支付、退款等功能

**主要字段**：
- `merchant_id` - 商户ID（关联商户表）
- `order_id` - 订单号（唯一）
- `amount` - 保证金金额
- `pay_method` - 支付方式（ALIPAY/WECHAT/BANK）
- `pay_status` - 支付状态（PENDING/PAID/FAILED/REFUNDED）
- `pay_time` - 支付时间
- `refund_time` - 退款时间
- `refund_reason` - 退款原因
- `transaction_id` - 第三方交易号
- `remark` - 备注

**索引**：
- `uk_order_id` - 订单号唯一索引
- `idx_merchant_id` - 商户ID索引
- `idx_pay_status` - 支付状态索引

**外键**：
- `fk_deposit_merchant` - 关联商户表

---

### 3. university_config（高校功能配置表）

**用途**：存储高校的功能配置信息，支持定制化功能配置

**主要字段**：
- `university_id` - 高校ID（关联高校表）
- `config_key` - 配置键（如：enableEcard、enableSecondhand等）
- `config_value` - 配置值（JSON格式存储）
- `description` - 配置说明

**索引**：
- `uk_university_key` - 高校ID+配置键唯一索引
- `idx_university_id` - 高校ID索引

**外键**：
- `fk_config_university` - 关联高校表

**设计说明**：
- 使用键值对设计，支持灵活配置
- 配置值使用JSON格式存储，可存储复杂配置对象

---

### 4. notification（通知表）

**用途**：存储系统通知和高校通知，支持群发和定向推送

**主要字段**：
- `university_id` - 高校ID（NULL表示平台通知）
- `title` - 通知标题
- `content` - 通知内容
- `target_type` - 目标类型（ALL/STUDENT/TEACHER/VISITOR）
- `target_user_id` - 目标用户ID（NULL表示群发）
- `is_urgent` - 是否紧急
- `is_read` - 是否已读
- `read_time` - 阅读时间
- `sender_id` - 发送人ID
- `send_time` - 发送时间

**索引**：
- `idx_university_id` - 高校ID索引
- `idx_target_user_id` - 目标用户ID索引
- `idx_send_time` - 发送时间索引
- `idx_is_read` - 是否已读索引

**外键**：
- `fk_notification_university` - 关联高校表
- `fk_notification_user` - 关联用户表

**设计说明**：
- 支持平台通知（university_id为NULL）和高校通知
- 支持群发（target_user_id为NULL）和定向推送
- 支持按角色筛选（target_type）

---

### 5. user_login_log（用户登录日志表）

**用途**：记录用户登录日志，用于统计用户活跃度

**主要字段**：
- `user_id` - 用户ID（关联用户表）
- `login_ip` - 登录IP
- `login_device` - 登录设备
- `login_location` - 登录地点
- `login_time` - 登录时间

**索引**：
- `idx_user_id` - 用户ID索引
- `idx_login_time` - 登录时间索引

**外键**：
- `fk_login_log_user` - 关联用户表

**设计说明**：
- 记录每次登录的详细信息
- 用于统计用户活跃度和登录行为分析

---

## ✅ 已创建的实体类

### 1. Merchant.java
- 对应 `merchant` 表
- 包含所有商户相关字段
- 使用 `@TableLogic` 支持逻辑删除

### 2. DepositRecord.java
- 对应 `deposit_record` 表
- 包含所有保证金记录相关字段

### 3. UniversityConfig.java
- 对应 `university_config` 表
- 包含配置键值对信息

### 4. Notification.java
- 对应 `notification` 表
- 包含通知相关字段

### 5. UserLoginLog.java
- 对应 `user_login_log` 表
- 包含登录日志相关字段

---

## ✅ 已创建的 Mapper 接口

### 1. MerchantMapper.java
- 继承 `BaseMapper<Merchant>`
- 提供基础的CRUD操作

### 2. DepositRecordMapper.java
- 继承 `BaseMapper<DepositRecord>`
- 提供基础的CRUD操作

### 3. UniversityConfigMapper.java
- 继承 `BaseMapper<UniversityConfig>`
- 提供基础的CRUD操作

### 4. NotificationMapper.java
- 继承 `BaseMapper<Notification>`
- 提供基础的CRUD操作

### 5. UserLoginLogMapper.java
- 继承 `BaseMapper<UserLoginLog>`
- 提供基础的CRUD操作

---

## 📊 创建统计

- **数据库表**：5个
- **实体类**：5个
- **Mapper接口**：5个
- **总计**：15个文件

---

## 🔄 下一步工作

### 1. 更新 Service 实现（高优先级）

需要更新以下 Service 实现，使用新创建的表：

#### MerchantService
- ✅ `applyMerchant()` - 创建商户记录到 `merchant` 表
- ✅ `getMerchantInfo()` - 从 `merchant` 表查询商户信息
- ✅ `payDeposit()` - 创建保证金记录到 `deposit_record` 表
- ✅ `getDepositRecords()` - 从 `deposit_record` 表查询记录
- ✅ `getMerchantStatistics()` - 计算信用分和总收入

#### UniversityService
- ✅ `getUniversityConfig()` - 从 `university_config` 表读取配置
- ✅ `updateUniversityConfig()` - 保存配置到 `university_config` 表
- ✅ `sendNotification()` - 创建通知记录到 `notification` 表
- ✅ `getNotificationHistory()` - 从 `notification` 表查询历史
- ✅ `getUserActivityStatistics()` - 从 `user_login_log` 表统计活跃度

### 2. 添加登录日志记录（中优先级）

需要在用户登录时记录登录日志：
- 在 `UserService.login()` 方法中添加登录日志记录
- 记录登录IP、设备、地点等信息

### 3. Excel 导入/导出功能（中优先级）

- 实现 `UniversityService.importUsers()` - Excel 导入用户
- 实现 `UniversityService.exportUsers()` - Excel 导出用户
- 参考 `ScheduleServiceImpl` 的实现方式

---

## 📝 数据库表设计说明

### 设计原则
1. **遵循现有表设计风格** - 使用相同的命名规范和字段类型
2. **支持逻辑删除** - 重要表使用 `is_deleted` 字段
3. **添加必要索引** - 提高查询性能
4. **外键约束** - 保证数据完整性
5. **时间字段** - 使用 `create_time` 和 `update_time`

### 字段类型选择
- **ID字段**：`bigint` 自增
- **金额字段**：`decimal(10,2)`
- **状态字段**：`varchar(20)`
- **时间字段**：`datetime`
- **逻辑删除**：`tinyint` (0/1)
- **JSON数据**：`text` 或 `json` 类型

---

## ✅ 验证清单

- ✅ 所有表结构已添加到 `init.sql`
- ✅ 所有实体类已创建
- ✅ 所有 Mapper 接口已创建
- ✅ 表结构设计合理
- ✅ 索引和外键已添加
- ✅ 字段类型正确

---

**创建完成时间**：2024年12月  
**创建人员**：AI Assistant
