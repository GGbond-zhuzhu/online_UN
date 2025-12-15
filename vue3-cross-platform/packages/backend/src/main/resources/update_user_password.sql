-- ========================================
-- 更新用户密码脚本
-- 功能：将MD5加密的密码更新为BCrypt加密
-- 原始密码：123456
-- ========================================

-- 步骤1：选择数据库
USE campus_db;

-- 步骤2：查看更新前的密码（用于确认）
SELECT 
    id,
    username,
    nickname,
    LEFT(password, 20) as password_preview,
    role
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 步骤3：更新密码为BCrypt加密
-- BCrypt密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO
-- 对应的原始密码：123456
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 步骤4：验证更新结果
-- 更新后，密码应该以 $2a$10$ 开头
SELECT 
    id,
    username,
    nickname,
    LEFT(password, 20) as password_preview,
    CASE 
        WHEN password LIKE '$2a$10$%' THEN '✅ BCrypt格式（正确）'
        ELSE '❌ 格式错误'
    END as password_status,
    role
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- ========================================
-- 说明：
-- 1. 这个脚本会更新4个测试用户的密码
-- 2. 新密码是BCrypt加密，原始密码是：123456
-- 3. 更新后，所有测试账号的密码都是：123456
-- 4. 如果更新失败，可以查看错误信息
-- ========================================
