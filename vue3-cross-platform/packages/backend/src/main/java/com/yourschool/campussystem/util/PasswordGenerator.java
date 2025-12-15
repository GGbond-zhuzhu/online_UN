package com.yourschool.campussystem.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具类
 * 用于生成BCrypt加密的密码，用于数据库初始化
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encoded = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt加密: " + encoded);
        System.out.println("验证结果: " + encoder.matches(password, encoded));
    }
}
