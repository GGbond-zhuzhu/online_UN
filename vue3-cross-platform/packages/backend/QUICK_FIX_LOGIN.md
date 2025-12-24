# 快速修复登录500错误

## ✅ 已完成的修复

1. **添加了枚举类型处理器** - `UserRoleEnumTypeHandler.java`
   - 用于正确处理数据库字符串到枚举的转换

2. **更新了User实体类** - 添加了`typeHandler`注解

3. **改进了错误处理** - 添加了更详细的错误信息

4. **代码已编译成功** ✅

## 🚀 下一步操作

### 1. 重启后端服务

**如果后端正在运行，需要重启以加载新的代码：**

```powershell
# 停止当前运行的后端（Ctrl+C）
# 然后重新启动
cd vue3-cross-platform\packages\backend
mvn spring-boot:run
```

### 2. 测试登录

重启后，使用以下方式测试：

**方式1：前端页面**
1. 访问 http://localhost:5173/login
2. 输入：
   - 用户名：`zhangsan`
   - 密码：`123456`
3. 点击登录

**方式2：API文档**
1. 访问 http://localhost:8080/doc.html
2. 找到"01-用户管理" → "用户登录"
3. 输入测试数据并发送请求

**方式3：PowerShell测试**
```powershell
$body = @{username='zhangsan';password='123456'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/user/login -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

## 📋 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| zhangsan | 123456 | 学生 |
| lisi | 123456 | 教师 |
| wangwu | 123456 | 学生 |
| admin | 123456 | 管理员 |

## ⚠️ 如果仍然失败

### 检查后端日志

查看后端控制台输出的错误信息，查找：
- "登录过程发生异常"
- 完整的异常堆栈信息

### 检查数据库

确保数据库中的用户数据正确：

```sql
USE campus_db;
SELECT id, username, role, password FROM user WHERE username = 'zhangsan';
```

确认：
- `role`字段的值是 `'STUDENT'`、`'TEACHER'`、`'ADMIN'` 等
- `password`字段是BCrypt加密格式（以`$2a$`开头）

## 📝 预期结果

登录成功后应该返回：

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "zhangsan",
    "role": "STUDENT",
    "expiresIn": 7200
  }
}
```

## 🔍 问题排查

如果重启后仍然出现500错误：

1. **查看后端控制台日志** - 找到具体的错误信息
2. **检查数据库连接** - 确保MySQL服务运行正常
3. **检查数据库数据** - 确保用户数据格式正确
4. **检查密码格式** - 确保密码是BCrypt加密格式

## 📚 相关文档

- [详细修复说明](./FIX_LOGIN_500_ERROR_DETAILED.md)
- [测试账号说明](./TEST_ACCOUNTS.md)

