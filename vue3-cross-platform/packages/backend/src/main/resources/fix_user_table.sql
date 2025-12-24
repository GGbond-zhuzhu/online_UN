-- 修复user表结构，添加缺失的列
-- 执行前请先检查列是否已存在

USE campus_db;

-- 1. 添加email列（如果列已存在会报错，但可以忽略）
-- 如果报错"Duplicate column name 'email'"，说明列已存在，可以忽略
ALTER TABLE `user` 
ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 2. 添加is_deleted列（如果列已存在会报错，但可以忽略）
-- 如果报错"Duplicate column name 'is_deleted'"，说明列已存在，可以忽略
ALTER TABLE `user` 
ADD COLUMN `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除' AFTER `update_time`;

-- 验证表结构
DESCRIBE `user`;

-- 检查所有列是否存在
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_db' AND TABLE_NAME = 'user'
ORDER BY ORDINAL_POSITION;

