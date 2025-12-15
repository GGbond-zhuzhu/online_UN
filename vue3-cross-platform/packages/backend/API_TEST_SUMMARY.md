# API接口测试总结

## 📋 测试准备完成

我已经为您创建了以下测试文档和脚本：

### 1. 测试文档
- ✅ **API_TEST.md** - 详细的API测试指南（包含curl命令和PowerShell脚本示例）
- ✅ **API_TEST_REPORT.md** - API测试报告模板
- ✅ **QUICK_TEST.md** - 快速测试指南（推荐使用Knife4j在线测试）

### 2. 测试脚本
- ✅ **test-api.ps1** - 完整的自动化测试脚本（包含所有主要接口）
- ✅ **test-api-simple.ps1** - 简化版测试脚本（测试关键接口）

---

## 🚀 推荐测试方式

### 方式1：使用Knife4j在线测试（最简单）

1. **启动后端服务**：
   ```bash
   cd vue3-cross-platform/packages/backend
   cmd /c "mvnw.cmd spring-boot:run"
   ```

2. **等待服务启动**（约30-60秒）

3. **打开浏览器访问**：http://localhost:8080/doc.html

4. **测试步骤**：
   - 找到"用户管理" -> "用户登录"接口
   - 点击"调试"，输入用户名和密码
   - 点击"发送请求"，获取token
   - 点击页面右上角"Authorize"按钮，输入：`Bearer {token}`
   - 现在可以测试所有需要认证的接口了

**优势**：
- ✅ 无需编写脚本
- ✅ 可视化界面
- ✅ 自动处理请求头
- ✅ 支持文件上传测试
- ✅ 可以直接查看响应结果

---

### 方式2：使用PowerShell脚本测试

1. **等待后端服务启动完成**

2. **运行测试脚本**：
   ```powershell
   cd vue3-cross-platform/packages/backend
   powershell -ExecutionPolicy Bypass -File test-api-simple.ps1
   ```

---

### 方式3：使用curl命令（Windows PowerShell）

```powershell
# 1. 登录
$body = '{"username":"zhangsan","password":"123456"}'
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/user/login" -Method POST -ContentType "application/json" -Body $body
$token = $response.data.token

# 2. 设置请求头
$headers = @{ Authorization = "Bearer $token" }

# 3. 测试各个接口
Invoke-RestMethod -Uri "http://localhost:8080/api/user/info" -Method GET -Headers $headers
Invoke-RestMethod -Uri "http://localhost:8080/api/ecard/info" -Method GET -Headers $headers
Invoke-RestMethod -Uri "http://localhost:8080/api/secondhand/list?page=1&size=10" -Method GET -Headers $headers
```

---

## 📊 测试接口清单

### ✅ 已实现的接口（共80+个）

#### 用户管理（UserController）
- ✅ 用户注册
- ✅ 用户登录
- ✅ 获取用户信息
- ✅ 更新用户信息
- ✅ 修改密码
- ✅ 退出登录

#### 校园卡管理（EcardController）
- ✅ 获取校园卡信息
- ✅ 校园卡消费
- ✅ 查询消费记录
- ✅ 游客卡申请
- ✅ 挂失/解挂
- ✅ 充值（测试用）
- ✅ 定位校验
- ✅ 统计信息

#### 二手交易（SecondhandController）
- ✅ 发布商品
- ✅ 获取商品列表
- ✅ 获取商品详情
- ✅ 我的商品
- ✅ 修改/下架/删除商品
- ✅ 收藏/取消收藏
- ✅ 分类查询

#### 兼职管理（ParttimeController）
- ✅ 发布兼职
- ✅ 获取兼职列表
- ✅ 获取兼职详情
- ✅ 报名/取消报名
- ✅ 我的报名
- ✅ 我发布的兼职
- ✅ 处理报名申请
- ✅ 修改/更新状态/删除兼职

#### 行程管理（ScheduleController）
- ✅ 创建个人行程
- ✅ 获取个人行程列表
- ✅ 获取行程详情
- ✅ 修改/删除行程
- ✅ 团队管理
- ✅ 团队行程管理
- ✅ Excel导入/导出
- ✅ 获取导入模板

#### 通用功能（CommonController）
- ✅ 帮助中心（分类、文章、搜索）
- ✅ 公告管理（列表、详情）
- ✅ 文件上传（图片、文件）
- ✅ 安全保障信息
- ✅ 隐私政策
- ✅ 服务协议
- ✅ 用户反馈
- ✅ 联系我们
- ✅ 调研问卷
- ✅ 平台介绍

#### 身份认证（AuthController）
- ✅ 学生身份认证申请
- ✅ 教师身份认证申请
- ✅ 游客刷脸检测
- ✅ 游客进校登记
- ✅ 查询认证申请状态
- ✅ 高校接入申请
- ✅ 获取认证申请记录

#### 管理员功能（AdminController）
- ✅ 用户管理（列表、状态更新、密码重置）
- ✅ 认证审核（申请列表、审核）
- ✅ 内容审核（待审核列表、审核）
- ✅ 高校管理（列表、审核）
- ✅ 系统配置（获取/更新）
- ✅ 数据统计

#### 高校管理（UniversityController）
- ✅ 高校信息管理
- ✅ 用户管理（列表、审核、导入/导出）
- ✅ 功能配置
- ✅ 数据统计
- ✅ 内容管理
- ✅ 通知推送

#### 商户管理（MerchantController）
- ✅ 商户入驻申请
- ✅ 公司资格校验
- ✅ 获取商户信息
- ✅ 缴纳保证金
- ✅ 查询保证金记录
- ✅ 获取商户发布的兼职列表
- ✅ 获取商户统计数据

---

## 🎯 测试重点

### 1. 认证流程测试
- [ ] 用户登录获取token
- [ ] 使用token访问需要认证的接口
- [ ] Token过期处理

### 2. 权限验证测试
- [ ] 普通用户无法访问管理员接口
- [ ] 管理员可以访问所有接口
- [ ] 高校角色可以访问高校管理接口

### 3. 数据完整性测试
- [ ] 分页查询返回正确的数据
- [ ] 数据关联正确（如用户和校园卡）
- [ ] 数据状态更新正确

### 4. 异常处理测试
- [ ] 无效token返回401
- [ ] 不存在的资源返回404
- [ ] 参数错误返回400
- [ ] 权限不足返回403

---

## 📝 测试结果记录

### 测试时间
待填写

### 测试环境
- 后端服务：http://localhost:8080
- 数据库：MySQL (campus_db)
- Java版本：17
- Spring Boot版本：3.2.0

### 测试结果

| 模块 | 接口数 | 通过数 | 失败数 | 通过率 |
|------|--------|--------|--------|--------|
| 用户管理 | 6 | - | - | - |
| 校园卡管理 | 8 | - | - | - |
| 二手交易 | 7 | - | - | - |
| 兼职管理 | 8 | - | - | - |
| 行程管理 | 10+ | - | - | - |
| 通用功能 | 15+ | - | - | - |
| 身份认证 | 7 | - | - | - |
| 管理员功能 | 11 | - | - | - |
| 高校管理 | 10+ | - | - | - |
| 商户管理 | 7 | - | - | - |
| **总计** | **90+** | **-** | **-** | **-** |

---

## 🔍 问题排查

### 如果后端服务无法启动

1. **检查端口占用**：
   ```bash
   netstat -ano | findstr :8080
   ```

2. **检查数据库连接**：
   - 确保MySQL服务已启动
   - 检查数据库密码是否正确
   - 检查数据库名称是否为`campus_db`

3. **查看启动日志**：
   - 查看控制台输出的错误信息
   - 检查是否有编译错误

### 如果接口返回500错误

1. **查看后端日志**：通常会有详细的错误堆栈
2. **检查数据库表**：确保所有表都已创建
3. **检查Service实现**：确保Service方法已实现

### 如果接口返回401错误

1. **检查token格式**：应该是 `Bearer {token}`
2. **检查token是否过期**：重新登录获取新token
3. **检查拦截器配置**：确保路径没有被排除

---

## 💡 测试建议

1. **优先使用Knife4j在线测试**：最简单、最直观
2. **先测试基础功能**：登录、获取用户信息等
3. **再测试业务功能**：各个模块的CRUD操作
4. **最后测试管理员功能**：需要管理员账号

---

## 📞 下一步

1. **等待后端服务启动完成**
2. **访问 http://localhost:8080/doc.html**
3. **按照QUICK_TEST.md中的步骤进行测试**
4. **记录测试结果到API_TEST_REPORT.md**

---

**提示**：后端服务启动需要一些时间，请耐心等待。启动成功后，您会看到 `Started BackendApplication` 的日志信息。
