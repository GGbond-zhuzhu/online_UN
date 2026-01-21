-- 为聊天消息表添加语音消息支持字段
-- 执行时间：2024年

USE campus_db;

-- 添加语音文件URL字段
ALTER TABLE `chat_message` 
ADD COLUMN `voice_url` VARCHAR(500) DEFAULT NULL COMMENT '语音文件URL（VOICE类型消息使用）' AFTER `file_url`;

-- 添加语音时长字段（单位：毫秒）
ALTER TABLE `chat_message` 
ADD COLUMN `duration` INT DEFAULT NULL COMMENT '语音时长（毫秒，VOICE类型消息使用）' AFTER `voice_url`;

-- 添加索引（可选，如果经常按类型查询）
-- ALTER TABLE `chat_message` ADD INDEX `idx_type` (`type`);

-- 验证修改
DESC `chat_message`;

