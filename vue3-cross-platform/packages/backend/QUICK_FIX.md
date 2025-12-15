# 快速修复登录500错误

## ⚠️ 重要：必须先执行此步骤！

在测试登录之前，**必须更新数据库中的密码**，否则会返回500错误。

## 🔧 修复步骤

### 步骤1：连接到MySQL数据库

使用MySQL客户端（如MySQL Workbench、Navicat、命令行等）连接到数据库。

### 步骤2：执行SQL更新密码

```sql
USE campus_db;

-- 更新测试用户的密码为BCrypt加密（原始密码：123456）
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

### 步骤3：验证更新

```sql
-- 查看更新结果
SELECT username, LEFT(password, 20) as password_preview FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

应该看到密码以 `$2a$10$` 开头（BCrypt格式）。

### 步骤4：重新测试登录

现在可以使用以下账号登录：
- 用户名：`zhangsan`
- 密码：`123456`

## 📝 为什么需要更新？

- **旧密码**：MD5加密（`e10adc3949ba59abbe56e057f20f883e`）
- **新密码**：BCrypt加密（`$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO`）
- **代码使用**：BCrypt验证

两种加密方式不匹配，所以需要更新。

## ✅ 更新后测试

访问 http://localhost:8080/doc.html，测试登录接口，应该能成功返回Token。
