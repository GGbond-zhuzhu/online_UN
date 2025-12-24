-- 更新用户密码为正确的BCrypt加密密码（原始密码：123456）
-- 注意：BCrypt密码每次生成都不同，但都能验证同一个原始密码
-- 如果这个密码仍然不工作，请使用在线BCrypt生成器生成新的密码：https://bcrypt-generator.com/

USE campus_db;

-- 更新所有测试用户的密码
-- 这个BCrypt密码对应原始密码：123456
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 验证更新结果
SELECT username, LEFT(password, 20) as password_preview, 
       CASE 
           WHEN password LIKE '$2a$10$%' THEN 'BCrypt格式正确'
           ELSE '格式错误'
       END as format_check
FROM `user` 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

