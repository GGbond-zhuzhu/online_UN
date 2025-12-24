# 修复数据库email列缺失问题

## 问题描述

登录时出现SQL错误：`Unknown column 'email' in 'field list'`

这说明数据库`user`表中缺少`email`列。

## 解决方案

### 方法1：手动添加email列（推荐）

连接到MySQL数据库，执行以下SQL：

```sql
USE campus_db;

-- 添加email列
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 验证表结构
DESCRIBE `user`;
```

### 方法2：重新创建表（如果数据不重要）

如果测试数据不重要，可以删除表后重新创建：

```sql
USE campus_db;

-- 删除user表（谨慎操作！会删除所有用户数据）
DROP TABLE IF EXISTS `user`;

-- 重新执行init.sql中的CREATE TABLE语句
-- 或者重启后端服务，会自动执行init.sql
```

### 方法3：检查并修复表结构

执行以下SQL检查表结构，然后根据实际情况修复：

```sql
USE campus_db;

-- 查看当前表结构
DESCRIBE `user`;

-- 查看所有列名
SHOW COLUMNS FROM `user`;

-- 如果email列不存在，添加它
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;
```

## 验证修复

执行以下SQL验证email列是否存在：

```sql
USE campus_db;

-- 查看表结构
DESCRIBE `user`;

-- 或者
SHOW COLUMNS FROM `user` LIKE 'email';
```

应该能看到email列：
```
Field: email
Type: varchar(100)
Null: YES
Key: 
Default: NULL
Extra: 
```

## 修复后测试

修复后，重新测试登录：

```powershell
$body = @{username='zhangsan';password='123456'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/user/login -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

应该返回200状态码和Token。

## 完整的user表结构

根据init.sql，user表应该包含以下列：

```sql
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `role` varchar(20) NOT NULL COMMENT '角色',
    `school_id` bigint DEFAULT NULL COMMENT '学校ID',
    `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',  -- 这个列缺失了
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

## 快速修复命令

如果使用MySQL命令行：

```bash
mysql -u root -p campus_db < fix_user_table.sql
```

或者在MySQL客户端中：

```sql
USE campus_db;
ALTER TABLE `user` ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;
```

