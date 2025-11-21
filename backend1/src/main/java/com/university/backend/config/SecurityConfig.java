package com.university.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（开发环境）
                .csrf(csrf -> csrf.disable())

                // 配置请求授权（使用方法引用优化）
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()  // 允许所有请求
                )

                // 启用CORS支持
                .cors(cors -> {})

                // 修正：使用新的headers配置方式（避免弃用方法）
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())  // 新的API
                );

        return http.build();
    }
}