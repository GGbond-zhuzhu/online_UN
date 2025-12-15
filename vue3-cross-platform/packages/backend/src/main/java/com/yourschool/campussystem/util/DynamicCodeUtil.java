package com.yourschool.campussystem.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

/**
 * 动态码生成工具类
 * 用于生成动态学生码、验证码等
 */
public class DynamicCodeUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 16;
    
    // 动态码有效期（秒）- 5分钟
    public static final long CODE_EXPIRE_SECONDS = 300;

    /**
     * 生成动态学生码
     * 格式：STU{userId}_{timestamp}_{random}
     * 
     * @param userId 用户ID
     * @return 动态码
     */
    public static String generateStudentCode(Long userId) {
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        String randomStr = generateRandomString(8);
        return String.format("STU%d_%d_%s", userId, timestamp, randomStr);
    }

    /**
     * 生成动态码（通用）
     * 
     * @param prefix 前缀
     * @return 动态码
     */
    public static String generateDynamicCode(String prefix) {
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        String randomStr = generateRandomString(CODE_LENGTH);
        return String.format("%s_%d_%s", prefix, timestamp, randomStr);
    }

    /**
     * 生成随机字符串
     * 
     * @param length 长度
     * @return 随机字符串
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成Base64编码的动态码
     * 
     * @param userId 用户ID
     * @return Base64编码的动态码
     */
    public static String generateBase64Code(Long userId) {
        String code = generateStudentCode(userId);
        return Base64.getEncoder().encodeToString(code.getBytes());
    }

    /**
     * 验证动态码是否过期
     * 
     * @param code 动态码
     * @return 是否过期
     */
    public static boolean isCodeExpired(String code) {
        try {
            // 从动态码中提取时间戳
            String[] parts = code.split("_");
            if (parts.length < 2) {
                return true;
            }
            long timestamp = Long.parseLong(parts[1]);
            long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            return (currentTime - timestamp) > CODE_EXPIRE_SECONDS;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取动态码的过期时间
     * 
     * @param code 动态码
     * @return 过期时间戳
     */
    public static long getCodeExpireTime(String code) {
        try {
            String[] parts = code.split("_");
            if (parts.length < 2) {
                return 0;
            }
            long timestamp = Long.parseLong(parts[1]);
            return timestamp + CODE_EXPIRE_SECONDS;
        } catch (Exception e) {
            return 0;
        }
    }
}
