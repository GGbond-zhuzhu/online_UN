# 快速修复email列缺失问题

## 🔴 问题

登录时出现错误：`Unknown column 'email' in 'field list'`

**原因**：数据库`user`表中缺少`email`列

## ✅ 快速修复

### 步骤1：连接到MySQL数据库

```bash
mysql -u root -p
```

输入密码后，执行：

```sql
USE campus_db;
```

### 步骤2：添加email列

```sql
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;
```

**注意**：如果列已存在，会报错 `Duplicate column name 'email'`，可以忽略。

### 步骤3：验证修复

```sql
DESCRIBE `user`;
```

应该能看到`email`列在`phone`列之后。

### 步骤4：重新测试登录

修复后，重新测试登录接口，应该可以正常工作了。

## 📋 完整的user表列顺序

修复后，user表应该包含以下列（按顺序）：

1. `id`
2. `username`
3. `password`
4. `nickname`
5. `role`
6. `school_id`
7. `avatar_url`
8. `phone`
9. `email` ← **这个列缺失了，需要添加**
10. `create_time`
11. `update_time`
12. `is_deleted`

## 🚀 一键修复脚本

如果使用MySQL命令行，可以直接执行：

```bash
mysql -u root -p campus_db -e "ALTER TABLE user ADD COLUMN email varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER phone;"
```

## ⚠️ 注意事项

1. **如果email列已存在**：执行ALTER TABLE会报错，但不会影响其他操作
2. **备份数据**：如果担心数据丢失，可以先备份：
   ```sql
   CREATE TABLE user_backup AS SELECT * FROM user;
   ```
3. **检查现有数据**：添加列后，现有用户的email字段为NULL，这是正常的

## 📝 修复后测试

使用以下方式测试登录：

```powershell
$body = @{username='zhangsan';password='123456'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/user/login -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

应该返回200状态码和Token，而不是500错误。

