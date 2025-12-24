-- ============================================
-- 完整修复user表结构 - 一次性执行
-- ============================================

USE campus_db;

-- 检查并添加email列
-- 如果列已存在，会报错但可以忽略
SET @dbname = DATABASE();
SET @tablename = 'user';
SET @columnname = 'email';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',  -- 列已存在，不执行任何操作
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` varchar(100) DEFAULT NULL COMMENT ''邮箱'' AFTER `phone`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查并添加is_deleted列
-- 如果列已存在，会报错但可以忽略
SET @columnname = 'is_deleted';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (TABLE_SCHEMA = @dbname)
      AND (TABLE_NAME = @tablename)
      AND (COLUMN_NAME = @columnname)
  ) > 0,
  'SELECT 1',  -- 列已存在，不执行任何操作
  CONCAT('ALTER TABLE `', @tablename, '` ADD COLUMN `', @columnname, '` tinyint DEFAULT ''0'' COMMENT ''逻辑删除'' AFTER `update_time`;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 验证表结构
DESCRIBE `user`;

-- 显示所有列
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_db' AND TABLE_NAME = 'user'
ORDER BY ORDINAL_POSITION;

