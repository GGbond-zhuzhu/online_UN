# 后端服务启动和测试指南

## 🚀 服务启动状态

后端服务正在启动中...

### 启动验证

等待约 **10-30秒** 后，服务应该启动完成。您可以通过以下方式验证：

1. **查看控制台输出**
   - 应该看到：`Started BackendApplication in X.XXX seconds`
   - 如果看到错误，请检查错误信息

2. **访问API文档**
   - Knife4j文档：http://localhost:8080/doc.html
   - Swagger UI：http://localhost:8080/swagger-ui.html

3. **测试健康检查**
   ```bash
   curl http://localhost:8080/v3/api-docs
   ```

## ⚠️ 重要：修复登录500错误

在测试登录之前，**必须先更新数据库中的密码**！

### 问题原因
- 数据库中的密码是MD5加密
- 代码使用BCrypt验证
- 两种加密方式不匹配

### 解决方案

#### 方法1：更新现有数据库（如果数据库已存在）

连接到MySQL数据库，执行以下SQL：

```sql
USE campus_db;

-- 更新测试用户的密码为BCrypt加密（原始密码：123456）
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

#### 方法2：重新初始化数据库

如果数据库是新创建的，`init.sql`已经更新为使用BCrypt密码，无需手动更新。

## 🧪 测试步骤

### 步骤1：等待服务启动完成

等待控制台显示：`Started BackendApplication in X.XXX seconds`

### 步骤2：访问API文档

在浏览器中打开：http://localhost:8080/doc.html

### 步骤3：测试用户登录

1. 在Knife4j文档中找到 **"用户管理"** -> **"用户登录"**
2. 点击"调试"按钮
3. 输入测试账号：
   ```json
   {
     "username": "zhangsan",
     "password": "123456"
   }
   ```
4. 点击"发送请求"
5. 如果成功，会返回Token

### 步骤4：设置认证Token

1. 复制返回的 `token` 值
2. 点击页面右上角的 **"Authorize"** 按钮
3. 输入：`Bearer {你的token}`
4. 点击"确定"

### 步骤5：测试其他接口

现在可以测试所有需要登录的接口了！

## 📋 测试检查清单

### 基础功能
- [ ] 服务启动成功
- [ ] API文档可访问
- [ ] 用户登录成功（需要先更新密码）
- [ ] Token验证正常

### 校园卡功能（已完善）
- [ ] `GET /api/ecard/info` - 获取校园卡信息
- [ ] `GET /api/ecard/consume-records` - 查询消费记录
- [ ] `GET /api/ecard/today-statistics` - 获取今日统计
- [ ] `GET /api/ecard/dynamic-code` - 生成动态码

### 二手交易功能
- [ ] `GET /api/secondhand/list` - 查询商品列表
- [ ] `GET /api/secondhand/detail/1` - 获取商品详情

### 兼职功能
- [ ] `GET /api/parttime/list` - 查询兼职列表
- [ ] `GET /api/parttime/detail/1` - 获取兼职详情

## 🐛 常见问题

### 问题1：服务启动失败

**检查项**：
1. MySQL服务是否启动
2. 数据库配置是否正确
3. 端口8080是否被占用

**解决方法**：
```powershell
# 检查端口占用
netstat -ano | findstr :8080

# 如果被占用，结束进程
taskkill /PID {进程ID} /F
```

### 问题2：登录返回500错误

**原因**：数据库密码是MD5，代码使用BCrypt验证

**解决方法**：执行SQL更新密码（见上方"修复登录500错误"部分）

### 问题3：数据库连接失败

**检查项**：
1. MySQL服务是否启动
2. 数据库`campus_db`是否存在
3. `application.properties`中的配置是否正确

**配置示例**：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 问题4：无法访问API文档

**检查项**：
1. 服务是否已完全启动（等待30秒）
2. 防火墙是否阻止了8080端口
3. 浏览器是否能访问 http://localhost:8080

## 📝 测试账号

所有账号的密码都是：**123456**

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| zhangsan | 123456 | STUDENT | 学生 |
| lisi | 123456 | TEACHER | 教师 |
| wangwu | 123456 | STUDENT | 学生 |
| admin | 123456 | ADMIN | 管理员 |

## 🎯 快速测试命令

### 使用curl测试登录

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
```

### 使用Token测试接口

```bash
# 先获取Token（从上面的登录响应中复制）
TOKEN="your_token_here"

# 测试获取校园卡信息
curl -X GET http://localhost:8080/api/ecard/info \
  -H "Authorization: Bearer $TOKEN"
```

## 📊 服务状态

- **编译状态**: ✅ 成功
- **服务状态**: 🚀 启动中...
- **预计启动时间**: 10-30秒

## 🔍 查看日志

如果遇到问题，查看控制台输出的日志，会显示：
- 服务启动过程
- 数据库连接状态
- 详细的错误信息

## ✅ 下一步

测试通过后：
1. 继续完善其他Controller
2. 进行前后端联调
3. 添加更多测试用例
