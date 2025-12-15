package com.yourschool.campussystem.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     * @param codeType 验证码类型（LOGIN/RESET_PASSWORD）
     * @return 验证码ID和过期时间
     */
    java.util.Map<String, Object> sendVerificationCode(String email, String codeType);

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param codeId 验证码ID
     * @return 是否验证通过
     */
    boolean verifyCode(String email, String code, String codeId);
}
