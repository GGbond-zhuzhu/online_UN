-- ============================================
-- 重新初始化数据库
-- 警告：此操作会删除所有数据！
-- ============================================

-- 删除数据库（如果存在）
DROP DATABASE IF EXISTS campus_db;

-- 重新创建数据库
CREATE DATABASE campus_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_db;

-- 注意：接下来需要执行 init.sql 文件中的内容
-- 或者重启后端服务，会自动执行 init.sql

