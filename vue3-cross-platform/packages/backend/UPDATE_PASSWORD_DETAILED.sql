-- ============================================
-- 数据库密码更新脚本（详细说明版）
-- ============================================
-- 
-- 问题说明：
-- 数据库中的密码是MD5加密格式，但代码使用BCrypt验证
-- 需要将密码更新为BCrypt格式才能正常登录
--
-- 原始密码：123456
-- BCrypt加密后的密码：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO
--
-- ============================================

-- 步骤1：选择数据库
USE campus_db;

-- 步骤2：查看更新前的密码（可选，用于确认）
SELECT 
    username,
    LEFT(password, 20) as password_preview,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt格式（正确）'
        ELSE 'MD5格式（需要更新）'
    END as password_type
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 步骤3：更新密码为BCrypt格式
-- 注意：这个BCrypt密码对应的原始密码是：123456
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 步骤4：验证更新结果（可选）
SELECT 
    username,
    LEFT(password, 20) as password_preview,
    'BCrypt格式（已更新）' as password_type
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- ============================================
-- 更新完成！
-- 现在可以使用以下账号登录：
-- 用户名：zhangsan, lisi, wangwu, admin
-- 密码：123456
-- ============================================
