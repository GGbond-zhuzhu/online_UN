# 数据库密码更新 - 详细步骤说明

## 📋 问题说明

**为什么需要更新密码？**
- 数据库中的密码是 **MD5加密**（旧格式）
- 代码使用 **BCrypt加密** 验证密码（新格式）
- 两种格式不匹配，导致登录失败（返回500错误）

## 🔧 更新步骤（详细版）

### 方法1：使用MySQL命令行（推荐）

1. **打开命令提示符（CMD）或PowerShell**

2. **连接到MySQL数据库**
   ```bash
   mysql -u root -p
   ```
   输入MySQL密码后回车

3. **选择数据库**
   ```sql
   USE campus_db;
   ```

4. **查看当前密码格式（可选，用于确认）**
   ```sql
   SELECT username, LEFT(password, 20) as password_preview 
   FROM `user` 
   WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
   ```
   如果看到 `e10adc3949ba59abbe56` 开头，说明是MD5格式，需要更新

5. **执行更新SQL**
   ```sql
   UPDATE `user` 
   SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
   WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
   ```

6. **验证更新结果**
   ```sql
   SELECT username, LEFT(password, 20) as password_preview 
   FROM `user` 
   WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
   ```
   应该看到 `$2a$10$N.zmdr9k7uOCQb376No` 开头，说明更新成功

### 方法2：使用MySQL Workbench（图形界面）

1. **打开MySQL Workbench**
2. **连接到数据库服务器**
3. **打开SQL编辑器**
4. **复制并执行以下SQL**：

```sql
USE campus_db;

UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

5. **点击执行按钮（⚡）**
6. **查看执行结果**，应该显示 "1 row(s) affected" 或类似信息

### 方法3：使用Navicat或其他数据库工具

1. **连接到MySQL数据库**
2. **选择 `campus_db` 数据库**
3. **打开SQL查询窗口**
4. **执行更新SQL**（同上）
5. **执行完成**

## ✅ 更新后的验证

更新完成后，可以使用以下账号登录：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| zhangsan | 123456 | 学生 |
| lisi | 123456 | 教师 |
| wangwu | 123456 | 学生 |
| admin | 123456 | 管理员 |

## 🔍 如何确认更新成功？

执行以下SQL查看：

```sql
SELECT username, 
       LEFT(password, 30) as password_preview,
       CASE 
           WHEN password LIKE '$2a$%' THEN '✅ BCrypt格式（正确）'
           ELSE '❌ MD5格式（需要更新）'
       END as status
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

如果所有用户的status都显示"✅ BCrypt格式（正确）"，说明更新成功！

## ⚠️ 注意事项

1. **只更新测试用户**：SQL中的WHERE条件确保只更新指定的4个测试用户
2. **不影响其他用户**：如果数据库中有其他用户，不会被影响
3. **密码格式**：BCrypt密码以 `$2a$10$` 开头，共60个字符
4. **原始密码不变**：所有测试账号的原始密码仍然是 `123456`

## 🎯 更新完成后

更新密码后，重新测试登录应该就能成功了！
