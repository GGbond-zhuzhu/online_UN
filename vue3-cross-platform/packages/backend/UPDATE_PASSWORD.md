# 更新数据库密码说明

## 问题原因

登录返回500错误的主要原因是：**数据库中的密码是MD5加密，但代码使用BCrypt验证**。

## 解决方案

### 方案1：更新现有数据库（推荐）

如果数据库已经存在，执行以下SQL更新密码：

```sql
USE campus_db;

-- 更新测试用户的密码为BCrypt加密（原始密码：123456）
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

### 方案2：重新初始化数据库

1. 删除现有数据库：
   ```sql
   DROP DATABASE IF EXISTS campus_db;
   ```

2. 重新启动服务，会自动执行`init.sql`创建数据库和表

## 验证

更新密码后，使用以下账号测试登录：
- 用户名：`zhangsan`
- 密码：`123456`

应该能成功登录并返回Token。

## BCrypt密码说明

- BCrypt密码每次生成都不同，但都能验证同一个原始密码
- 格式：`$2a$10$...`（60个字符）
- 已更新的密码：`$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO`
- 对应的原始密码：`123456`
