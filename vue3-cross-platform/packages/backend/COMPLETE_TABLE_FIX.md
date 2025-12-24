# 完整修复user表结构

## 🔴 问题

数据库`user`表缺少多个列：
- `email` 列
- `is_deleted` 列

## ✅ 完整修复方案

### 方法1：逐个添加缺失的列（推荐）

连接到MySQL数据库，执行以下SQL：

```sql
USE campus_db;

-- 1. 添加email列
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 2. 添加is_deleted列
ALTER TABLE `user` 
ADD COLUMN `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除' AFTER `update_time`;

-- 验证表结构
DESCRIBE `user`;
```

**注意**：如果列已存在，会报错 `Duplicate column name 'xxx'`，可以忽略该错误继续执行下一个。

### 方法2：检查并添加（更安全）

先检查列是否存在，然后添加：

```sql
USE campus_db;

-- 检查email列是否存在
SELECT COUNT(*) as email_exists
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_db' 
  AND TABLE_NAME = 'user' 
  AND COLUMN_NAME = 'email';

-- 如果email_exists = 0，则添加email列
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 检查is_deleted列是否存在
SELECT COUNT(*) as is_deleted_exists
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_db' 
  AND TABLE_NAME = 'user' 
  AND COLUMN_NAME = 'is_deleted';

-- 如果is_deleted_exists = 0，则添加is_deleted列
ALTER TABLE `user` 
ADD COLUMN `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除' AFTER `update_time`;
```

### 方法3：重新创建表（如果数据不重要）

如果测试数据不重要，可以删除表后重新创建：

```sql
USE campus_db;

-- 备份现有数据（可选）
CREATE TABLE user_backup AS SELECT * FROM user;

-- 删除user表
DROP TABLE IF EXISTS `user`;

-- 重新创建表（执行init.sql中的CREATE TABLE语句）
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `role` varchar(20) NOT NULL COMMENT '角色',
    `school_id` bigint DEFAULT NULL COMMENT '学校ID',
    `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 恢复数据（如果有备份）
-- INSERT INTO user SELECT * FROM user_backup;
```

## 📋 完整的user表结构

修复后，user表应该包含以下列（按顺序）：

1. `id` - bigint, 主键, 自增
2. `username` - varchar(50), 唯一, 非空
3. `password` - varchar(255), 非空
4. `nickname` - varchar(50), 可空
5. `role` - varchar(20), 非空
6. `school_id` - bigint, 可空
7. `avatar_url` - varchar(500), 可空
8. `phone` - varchar(20), 可空
9. `email` - varchar(100), 可空 ← **需要添加**
10. `create_time` - datetime, 默认当前时间
11. `update_time` - datetime, 自动更新
12. `is_deleted` - tinyint, 默认0 ← **需要添加**

## 🚀 一键修复脚本

如果使用MySQL命令行，可以直接执行：

```bash
mysql -u root -p campus_db << EOF
ALTER TABLE user ADD COLUMN email varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER phone;
ALTER TABLE user ADD COLUMN is_deleted tinyint DEFAULT '0' COMMENT '逻辑删除' AFTER update_time;
DESCRIBE user;
EOF
```

或者在MySQL客户端中执行 `fix_user_table.sql` 文件：

```bash
mysql -u root -p campus_db < fix_user_table.sql
```

## ✅ 验证修复

执行以下SQL验证所有列是否存在：

```sql
USE campus_db;

-- 查看表结构
DESCRIBE `user`;

-- 或者查看所有列
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_db' AND TABLE_NAME = 'user'
ORDER BY ORDINAL_POSITION;
```

应该能看到所有12个列。

## 📝 修复后测试

修复后，重新测试登录：

```powershell
$body = @{username='zhangsan';password='123456'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/user/login -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

应该返回200状态码和Token，而不是500错误。

## ⚠️ 注意事项

1. **如果列已存在**：执行ALTER TABLE会报错，但不会影响其他操作
2. **备份数据**：如果担心数据丢失，可以先备份：
   ```sql
   CREATE TABLE user_backup AS SELECT * FROM user;
   ```
3. **现有数据**：添加列后，现有用户的email字段为NULL，is_deleted字段为0，这是正常的
4. **索引**：确保username列有唯一索引，school_id列有普通索引

## 🔍 排查其他缺失的列

如果修复后仍然报错，检查是否还有其他列缺失：

```sql
-- 查看实际表结构
DESCRIBE `user`;

-- 对比init.sql中的表结构，检查是否还有其他列缺失
```

