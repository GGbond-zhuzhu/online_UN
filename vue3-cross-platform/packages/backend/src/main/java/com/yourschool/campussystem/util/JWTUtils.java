package com.yourschool.campussystem.util;

import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtils {

    // 密钥（实际项目中应从配置文件中读取）
    private static final String SECRET = "ThisIsASecretKeyForJWTTokenGenerationWithSufficientLength";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 改为public常量，确保可以在其他类中访问
    public static final long EXPIRATION = 7200L; // 2小时，单位秒

    // 生成JWT Token
    public static String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(KEY)
                .compact();
    }

    // 验证并解析Token
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
    }
}