# 后端开发计划

## 当前状态总结

### ✅ 已完成
1. **数据库表结构** - 所有表已创建
2. **实体类** - 所有实体类已创建
3. **Mapper接口** - 所有Mapper接口已创建
4. **Service层（部分）**：
   - ✅ EcardService - 完整实现
   - ✅ SecondhandService - 完整实现
   - ✅ ParttimeService - 完整实现
   - ⚠️ ScheduleService - 接口已定义，实现类存在但需检查完整性
   - ⚠️ CommonService - 接口已定义，实现类存在但需检查完整性
5. **Controller层（部分）**：
   - ✅ EcardController - 已部分使用Service，但仍有模拟数据

### ⏳ 待完成
1. **Service层实现**：
   - ScheduleService实现类完善
   - CommonService实现类完善
   - AdminService（可选，管理员功能）
2. **Controller层完善**：
   - EcardController - 替换剩余模拟数据
   - SecondhandController - 替换为Service调用
   - ParttimeController - 替换为Service调用
   - ScheduleController - 替换为Service调用
   - CommonController - 替换为Service调用
   - AdminController - 替换为Service调用
   - AuthController - 完善认证逻辑
3. **工具类和配置**：
   - 文件上传工具类
   - JWT工具类完善（从请求头获取用户ID的统一方法）
   - 定位校验工具类
4. **权限验证**：
   - 统一权限验证拦截器/注解
   - 从JWT Token获取用户信息的统一方法

## 开发计划（按优先级）

### 阶段一：完善Service层（优先级：高）

#### 1.1 检查并完善ScheduleService实现
- [ ] 检查ScheduleServiceImpl的完整性
- [ ] 实现个人行程管理功能
- [ ] 实现团队管理功能
- [ ] 实现团队行程管理功能
- [ ] 实现行程同步功能
- [ ] 实现提醒和日历视图功能

#### 1.2 检查并完善CommonService实现
- [ ] 检查CommonServiceImpl的完整性
- [ ] 实现帮助中心功能
- [ ] 实现公告管理功能
- [ ] 实现文件上传功能
- [ ] 实现反馈功能
- [ ] 实现其他通用功能

### 阶段二：完善Controller层（优先级：高）

#### 2.1 完善EcardController
- [ ] 替换所有模拟数据为Service调用
- [ ] 统一用户ID获取方式（使用拦截器或工具类）
- [ ] 添加权限验证

#### 2.2 完善SecondhandController
- [ ] 注入SecondhandService
- [ ] 替换所有模拟数据为Service调用
- [ ] 添加权限验证

#### 2.3 完善ParttimeController
- [ ] 注入ParttimeService
- [ ] 替换所有模拟数据为Service调用
- [ ] 添加权限验证

#### 2.4 完善ScheduleController
- [ ] 注入ScheduleService
- [ ] 替换所有模拟数据为Service调用
- [ ] 添加权限验证

#### 2.5 完善CommonController
- [ ] 注入CommonService
- [ ] 替换所有模拟数据为Service调用

#### 2.6 完善AdminController
- [ ] 创建AdminService（可选）
- [ ] 替换所有模拟数据为Service调用
- [ ] 添加管理员权限验证

#### 2.7 完善AuthController
- [ ] 完善认证逻辑
- [ ] 添加文件上传处理

### 阶段三：工具类和配置（优先级：中）

#### 3.1 JWT工具类完善
- [ ] 创建统一的用户信息获取工具类
- [ ] 创建权限验证注解
- [ ] 创建权限验证拦截器

#### 3.2 文件上传工具类
- [ ] 创建文件上传Service
- [ ] 实现图片上传功能
- [ ] 实现文件上传功能
- [ ] 添加文件大小和类型验证

#### 3.3 其他工具类
- [ ] 定位校验工具类（已部分实现，需完善）
- [ ] 动态码生成工具类
- [ ] 日期时间工具类

### 阶段四：异常处理和验证（优先级：中）

#### 4.1 统一异常处理
- [ ] 检查GlobalExceptionHandler
- [ ] 完善异常处理逻辑
- [ ] 添加业务异常处理

#### 4.2 参数验证
- [ ] 检查DTO中的验证注解
- [ ] 添加自定义验证器（如需要）

#### 4.3 权限验证
- [ ] 实现统一的权限验证机制
- [ ] 添加角色权限验证
- [ ] 添加资源权限验证（如同校验证）

## 详细实施步骤

### 第一步：检查现有Service实现
1. 检查ScheduleServiceImpl的完整性
2. 检查CommonServiceImpl的完整性
3. 修复发现的bug和缺失功能

### 第二步：完善Controller层（按模块）
1. **EcardController** - 优先处理，因为Service已完整实现
2. **SecondhandController** - Service已完整实现
3. **ParttimeController** - Service已完整实现
4. **ScheduleController** - 待Service完善后处理
5. **CommonController** - 待Service完善后处理

### 第三步：创建工具类
1. 创建JWT工具类（统一获取用户信息）
2. 创建文件上传工具类
3. 创建权限验证工具类

### 第四步：测试和优化
1. 测试各个Controller接口
2. 修复发现的问题
3. 优化代码结构

## 技术要点

### Controller层改造要点
1. **统一用户ID获取**：
   ```java
   // 创建工具类或拦截器统一处理
   private Long getCurrentUserId(HttpServletRequest request) {
       // 从JWT Token中获取用户ID
   }
   ```

2. **Service注入**：
   ```java
   @RequiredArgsConstructor
   public class XxxController {
       private final XxxService xxxService;
   }
   ```

3. **替换模拟数据**：
   ```java
   // 之前：返回模拟数据
   return ApiResponse.success(new Object() { ... });
   
   // 之后：调用Service
   return ApiResponse.success(xxxService.method(params));
   ```

### Service层完善要点
1. **事务管理**：使用@Transactional注解
2. **异常处理**：使用BusinessException抛出业务异常
3. **权限验证**：在Service层进行权限检查
4. **数据转换**：Entity -> VO的转换

### 工具类创建要点
1. **JWT工具类**：统一处理Token解析和用户信息获取
2. **文件上传**：支持多种文件类型，添加大小限制
3. **权限验证**：使用注解和拦截器实现

## 预计工作量

- **阶段一**：2-3小时（完善Service层）
- **阶段二**：3-4小时（完善Controller层）
- **阶段三**：1-2小时（工具类）
- **阶段四**：1小时（异常处理和验证）

**总计**：约7-10小时

## 注意事项

1. 保持代码风格一致
2. 所有数据库操作使用事务
3. 所有异常使用BusinessException
4. 所有返回数据使用VO对象
5. 添加必要的注释
6. 遵循RESTful API设计规范
