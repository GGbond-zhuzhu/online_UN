# 后端待完成工作清单

## 📋 工作分类

### 🔴 高优先级（核心功能）

#### 1. 数据库表结构完善

**缺失的表**：
- ❌ `merchant` - 商户表（存储商户详细信息）
- ❌ `deposit_record` - 保证金记录表
- ❌ `merchant_order` - 商户订单表（可选）
- ❌ `merchant_review` - 商户评价表（可选）
- ❌ `university_config` - 高校功能配置表
- ❌ `notification` - 通知表
- ❌ `user_login_log` - 用户登录日志表（用于活跃度统计）

**影响范围**：
- `MerchantService` - 商户功能无法完整实现
- `UniversityService` - 功能配置、通知推送、活跃度统计无法实现

---

#### 2. Excel 导入/导出功能完善

**待实现功能**：
- ❌ `UniversityService.importUsers()` - Excel 导入用户（使用 EasyExcel）
- ❌ `UniversityService.exportUsers()` - Excel 导出用户（使用 EasyExcel）

**当前状态**：
- ✅ 已预留接口和方法签名
- ✅ `ScheduleService` 的 Excel 导入/导出已实现（可作为参考）
- ❌ 用户导入/导出功能待实现

**参考实现**：
- `ScheduleServiceImpl.importScheduleFromExcel()` - 已实现
- `ScheduleServiceImpl.exportScheduleToExcel()` - 已实现

---

#### 3. 通知推送功能

**待实现功能**：
- ❌ `UniversityService.sendNotification()` - 发送通知（需要通知表）
- ❌ `UniversityService.getNotificationHistory()` - 获取通知历史（需要通知表）

**当前状态**：
- ✅ 已预留接口和方法签名
- ✅ 已实现基本的统计逻辑
- ❌ 缺少通知表，无法持久化存储

**需要创建**：
- `notification` 表
- `Notification` 实体类
- `NotificationMapper` 接口

---

#### 4. 用户活跃度统计

**待实现功能**：
- ❌ `UniversityService.getUserActivityStatistics()` - 用户活跃度统计

**当前状态**：
- ✅ 已预留接口和方法签名
- ❌ 缺少用户登录日志表，无法统计活跃度

**需要创建**：
- `user_login_log` 表
- `UserLoginLog` 实体类
- `UserLoginLogMapper` 接口

---

### 🟡 中优先级（功能增强）

#### 5. 高校功能配置存储

**待实现功能**：
- ❌ `UniversityService.getUniversityConfig()` - 从数据库读取配置
- ❌ `UniversityService.updateUniversityConfig()` - 保存配置到数据库

**当前状态**：
- ✅ 已预留接口和方法签名
- ✅ 已实现基本的配置返回（硬编码）
- ❌ 缺少配置表，无法持久化存储

**需要创建**：
- `university_config` 表
- `UniversityConfig` 实体类
- `UniversityConfigMapper` 接口

---

#### 6. 商户功能完善

**待实现功能**：
- ❌ 创建 `merchant` 表和相关实体类
- ❌ 创建 `deposit_record` 表和相关实体类
- ❌ 完善 `MerchantService` 的所有方法实现

**当前状态**：
- ✅ `MerchantService` 接口已定义
- ✅ `MerchantServiceImpl` 已实现（使用简化数据模型）
- ❌ 缺少专用表，使用 `User` 和 `Parttime` 表作为临时方案

**需要创建**：
- `merchant` 表（商户详细信息）
- `deposit_record` 表（保证金记录）
- `Merchant` 实体类
- `DepositRecord` 实体类
- `MerchantMapper` 接口
- `DepositRecordMapper` 接口

---

### 🟢 低优先级（第三方集成）

#### 7. 第三方 API 对接

**待对接的服务**：
- ❌ **教务系统对接** - 学生学号验证（`AuthService.studentAuthApply()`）
- ❌ **教师认证系统对接** - 教师认证验证（`AuthService.teacherAuthApply()`）
- ❌ **人脸识别 SDK** - 游客人脸识别（`AuthService.visitorFaceDetect()`）
- ❌ **公司验证 API** - 商户公司资格校验（天眼查、企查查等）
- ❌ **支付系统对接** - 保证金支付（支付宝、微信等）

**当前状态**：
- ✅ 已预留接口和方法签名
- ✅ 已实现基本的业务逻辑框架
- ❌ 第三方 API 调用待实现（标记为 TODO）

**注意事项**：
- 这些功能需要实际的第三方服务账号和 API Key
- 可能需要额外的依赖库（如人脸识别 SDK）
- 建议先实现模拟版本，后续再对接真实服务

---

#### 8. 游客功能完善

**待实现功能**：
- ❌ `AuthService.visitorFaceDetect()` - 人脸识别活体检测
- ❌ `AuthService.visitorRegister()` - 创建游客记录，生成游客码

**当前状态**：
- ✅ 已预留接口和方法签名
- ❌ 人脸识别功能待集成 SDK
- ❌ 游客记录表待确认（可能使用 `ecard` 表的 `is_visitor_card` 字段）

---

#### 9. 高校接入申请

**待实现功能**：
- ❌ `AuthService.universityAccessApply()` - 创建高校接入申请记录

**当前状态**：
- ✅ 已预留接口和方法签名
- ❌ 高校申请表待创建（或使用 `university` 表的 `status` 字段）

---

## 📊 工作统计

### 数据库表（7个）
- ❌ `merchant` - 商户表
- ❌ `deposit_record` - 保证金记录表
- ❌ `university_config` - 高校配置表
- ❌ `notification` - 通知表
- ❌ `user_login_log` - 用户登录日志表
- ⚠️ `merchant_order` - 商户订单表（可选）
- ⚠️ `merchant_review` - 商户评价表（可选）

### 实体类（5-7个）
- ❌ `Merchant` - 商户实体
- ❌ `DepositRecord` - 保证金记录实体
- ❌ `UniversityConfig` - 高校配置实体
- ❌ `Notification` - 通知实体
- ❌ `UserLoginLog` - 用户登录日志实体
- ⚠️ `MerchantOrder` - 商户订单实体（可选）
- ⚠️ `MerchantReview` - 商户评价实体（可选）

### Mapper 接口（5-7个）
- ❌ `MerchantMapper`
- ❌ `DepositRecordMapper`
- ❌ `UniversityConfigMapper`
- ❌ `NotificationMapper`
- ❌ `UserLoginLogMapper`
- ⚠️ `MerchantOrderMapper`（可选）
- ⚠️ `MerchantReviewMapper`（可选）

### Service 方法完善（约 10+ 个方法）
- ❌ `UniversityService.importUsers()` - Excel 导入
- ❌ `UniversityService.exportUsers()` - Excel 导出
- ❌ `UniversityService.getUniversityConfig()` - 从数据库读取
- ❌ `UniversityService.updateUniversityConfig()` - 保存到数据库
- ❌ `UniversityService.getUserActivityStatistics()` - 活跃度统计
- ❌ `UniversityService.sendNotification()` - 发送通知
- ❌ `UniversityService.getNotificationHistory()` - 通知历史
- ❌ `MerchantService` 相关方法完善（需要表支持）

### 第三方集成（5个）
- ❌ 教务系统对接
- ❌ 教师认证系统对接
- ❌ 人脸识别 SDK 集成
- ❌ 公司验证 API 对接
- ❌ 支付系统对接

---

## 🎯 建议优先级

### 第一阶段：数据库表结构（优先级：高）
1. 创建 `merchant` 表和相关实体类、Mapper
2. 创建 `deposit_record` 表和相关实体类、Mapper
3. 创建 `notification` 表和相关实体类、Mapper
4. 创建 `university_config` 表和相关实体类、Mapper
5. 创建 `user_login_log` 表和相关实体类、Mapper

### 第二阶段：功能完善（优先级：高）
1. 完善 `UniversityService` 的 Excel 导入/导出功能
2. 完善 `UniversityService` 的通知推送功能
3. 完善 `UniversityService` 的用户活跃度统计
4. 完善 `MerchantService` 的所有方法（使用新表）

### 第三阶段：第三方集成（优先级：低）
1. 实现第三方 API 对接（或提供模拟实现）
2. 集成人脸识别 SDK（或提供模拟实现）

---

## 📝 注意事项

1. **数据库表设计**：
   - 需要仔细设计表结构，考虑字段类型、索引、外键等
   - 参考现有表的设计风格

2. **Excel 导入/导出**：
   - 参考 `ScheduleServiceImpl` 的实现方式
   - 使用 EasyExcel 库（已在项目中引入）

3. **第三方集成**：
   - 可以先实现模拟版本，后续再对接真实服务
   - 需要配置 API Key 和密钥（不要硬编码）

4. **代码规范**：
   - 遵循现有的代码风格
   - 添加详细的中文注释
   - 使用统一的异常处理

---

## ✅ 已完成的核心工作

- ✅ 所有 Controller 已完善
- ✅ 所有核心 Service 已实现
- ✅ 权限验证系统已完善
- ✅ 异常处理已统一
- ✅ 代码质量已优化

---

**最后更新**：2024年12月
