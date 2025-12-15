package com.yourschool.campussystem.util;

import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 用于从请求中获取当前登录用户信息
 */
public class UserContextUtils {

    /**
     * 从请求头中获取用户ID
     * @param request HTTP请求
     * @return 用户ID，如果未登录返回null
     */
    public static Long getUserId(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) {
            return null;
        }
        try {
            Claims claims = JWTUtils.parseToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从请求头中获取用户ID（必须登录）
     * @param request HTTP请求
     * @return 用户ID
     * @throws BusinessException 如果未登录
     */
    public static Long getUserIdRequired(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
        }
        return userId;
    }

    /**
     * 从请求头中获取用户名
     * @param request HTTP请求
     * @return 用户名，如果未登录返回null
     */
    public static String getUsername(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) {
            return null;
        }
        try {
            Claims claims = JWTUtils.parseToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从请求头中获取用户角色
     * @param request HTTP请求
     * @return 用户角色，如果未登录返回null
     */
    public static String getUserRole(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null) {
            return null;
        }
        try {
            Claims claims = JWTUtils.parseToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从请求头中提取Token
     * @param request HTTP请求
     * @return Token字符串，如果不存在返回null
     */
    private static String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
