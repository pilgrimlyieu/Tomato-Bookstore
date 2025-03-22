package com.tomato.bookstore.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret:tomatobookstoresecretkeytomatobookstoresecretkey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 默认 24 小时
    private long jwtExpiration;

    /**
     * 从 token 中提取用户名
     *
     * @param token
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从 token 中提取过期时间
     *
     * @param token
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从 token 中提取指定的 claim
     *
     * @param token
     * @param claimsResolver
     * @return 用户指定的 claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从 token 中提取所有的 claim
     *
     * @param token
     * @return 所有的 claim
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 token 是否过期
     *
     * @param token
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 生成 token
     *
     * @param username 用户名
     * @param userId   用户 ID
     * @return token
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, username);
    }

    /**
     * 创建 token
     *
     * @param claims  claims
     * @param subject subject
     * @return token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 验证 token
     *
     * @param token       token
     * @param userDetails 用户信息
     * @return 是否验证通过
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从 token 中提取用户 ID
     *
     * @param token token
     * @return 用户 ID
     */
    public Long extractUserId(String token) {
        return Long.valueOf(extractClaim(token, claims -> claims.get("userId", Long.class)).longValue());
    }
}
