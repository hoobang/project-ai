package com.wechat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

/**
 * JWT 令牌工具类。
 * <p>
 * 负责生成、解析与校验 JWT：
 * - 使用 HS512 算法与配置文件中的密钥 {@code jwt.secret}；
 * - 令牌过期时间由配置 {@code jwt.expiration} 指定（毫秒）；
 * - subject 为用户名，支持从令牌解析用户名并进行校验。
 * </p>
 * 线程安全说明：
 * - 所有方法不持有状态，依赖的密钥与过期时间通过配置注入，适合单例使用。
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("iat", new Date());
        claims.put("exp", new Date(System.currentTimeMillis() + expiration));
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}
