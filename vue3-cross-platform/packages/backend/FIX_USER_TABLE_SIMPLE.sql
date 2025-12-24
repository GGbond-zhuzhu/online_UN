-- ============================================
-- 简单修复user表 - 直接执行（推荐）
-- 如果列已存在会报错，但可以忽略
-- ============================================

USE campus_db;

-- 添加email列
ALTER TABLE `user` ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `phone`;

-- 添加is_deleted列
ALTER TABLE `user` ADD COLUMN `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除' AFTER `update_time`;

-- 验证
DESCRIBE `user`;

