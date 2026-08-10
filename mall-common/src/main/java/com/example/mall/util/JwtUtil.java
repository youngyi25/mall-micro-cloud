package com.example.mall.util;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    // 从配置文件读取密钥和过期时间
    @Value("${jwt.secret-key:MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY")
    private String secretKey;
    @Value("${jwt.expire-time:1800000}") // 默认30分钟，单位毫秒
    private Long expireTime;
    @Value("${jwt.redis.token-prefix:user:token}")
    private String tokenPrefix;
    @Value("${jwt.redis.blacklist-prefix:blacklist:token}")
    private String blacklistPrefix;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成签名密钥
     */
    private SecretKey getSignKey() {
        // 注意：HS256算法要求密钥长度至少32个字符
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     * @param userId 用户ID（作为Subject）
     * @param claims 自定义载荷数据（如username、role等）
     * @return JWT Token字符串
     */
    public String createToken(Long userId, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        // 签发时间
        Date issuedAt = new Date(now);
        // 过期时间
        Date expiration = new Date(now + expireTime);

        return Jwts.builder()
                .setClaims(claims)        // 自定义载荷
                .setSubject(String.valueOf(userId)) // 主题（一般放用户ID）
                .setIssuedAt(issuedAt)     // 签发时间
                .setExpiration(expiration) // 过期时间
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();
    }

    /**
     * 简化版生成Token（不带额外claims）
     */
    public String createToken(Long userId) {
        return createToken(userId, new HashMap<>());
    }

    /**
     * 解析Token，获取所有Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 验证Token是否有效（未过期、签名正确）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token已过期");
        } catch (SignatureException e) {
            System.err.println("Token签名无效");
        } catch (MalformedJwtException e) {
            System.err.println("Token格式错误");
        } catch (IllegalArgumentException e) {
            System.err.println("Token为空");
        }
        return false;
    }
    /**
     * 生成 Token 并存入 Redis（登录时使用）
     */
    public String createTokenAndStore(Long userId, Map<String, Object> claims) {
        String token = createToken(userId, claims);
        String redisKey = tokenPrefix + ":" + userId;
        // 存入 Redis，过期时间与 JWT 一致（毫秒转秒）
        long expireSeconds = expireTime / 1000;
        redisTemplate.opsForValue().set(redisKey, token, expireSeconds, TimeUnit.SECONDS);
        return token;
    }
    /**
     * 完整验证：JWT 有效性 + Redis 存在性 + 黑名单检查
     *
     * @param token  JWT Token
     * @param userId 用户 ID（用于 Redis 查询）
     * @return true 有效，false 无效
     */
    public boolean validateTokenWithRedis(String token, Long userId) {
        // 1. 检查黑名单
        String blacklistKey = blacklistPrefix + ":" + token;
        Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID);
        }
        // 2. 检查 JWT 签名和过期
        if (!validateToken(token)) {
            throw new BusinessException(ResultCodeEnum.TOKEN_EXPIRED);
        }
        // 3. 检查 Redis 中是否存在
        String redisKey = tokenPrefix + ":" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        if (storedToken == null) {
            throw new BusinessException(ResultCodeEnum.TOKEN_EXPIRED);
        }
        // 4. 校验 Token 是否匹配
        if (!token.equals(storedToken)) {
            throw new BusinessException(4010, "Token 不匹配，可能在其他设备登录");
        }
        return true;
    }
    /**
     * 通过 Claims 中的 userId 自动提取进行校验
     */
    public boolean validateTokenWithRedis(String token) {
        Claims claims = parseToken(token);
        Long userId = Long.valueOf(claims.getSubject());
        return validateTokenWithRedis(token, userId);
    }
    /**
     * 登录：生成 Token 并存入 Redis
     */
    public String login(Long userId, Map<String, Object> extraClaims) {
        if (extraClaims == null) {
            extraClaims = new HashMap<>();
        }
        extraClaims.put("userId", userId);
        return createTokenAndStore(userId, extraClaims);
    }
    /**
     * 退出登录：删除 Redis 中的 Token
     */
    public void logout(Long userId) {
        String redisKey = tokenPrefix + ":" + userId;
        // 删除 Redis 中的 Token
        redisTemplate.delete(redisKey);
    }
    /**
     * 退出登录（通过 Token 注销）
     */
    public void logoutByToken(String token) {
        Long userId = getUserId(token);
        // 加入黑名单
        String blacklistKey = blacklistPrefix + ":" + token;
        // 获取Token有效期
        long remainingTtl = getRemainingTtl(token);
        if (remainingTtl > 0) {
            redisTemplate.opsForValue().set(blacklistKey, "1", remainingTtl, TimeUnit.MILLISECONDS);
        }
        // 删除 Redis
        String redisKey = tokenPrefix + ":" + userId;
        redisTemplate.delete(redisKey);
    }
    /**
     * 强制踢人（管理员操作）
     */
    public void forceLogout(Long userId) {
        String redisKey = tokenPrefix + ":" + userId;
        String token = (String) redisTemplate.opsForValue().get(redisKey);
        if (token != null) {
            // 加入黑名单（永久或长期）
            String blacklistKey = blacklistPrefix + ":" + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", 7, TimeUnit.DAYS);
        }
        redisTemplate.delete(redisKey);
    }
    /**
     * 获取 Token 剩余有效期（毫秒）
     */
    public long getRemainingTtl(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
    /**
     * 检查 Token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        String blacklistKey = blacklistPrefix + ":" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }
    /**
     * 获取 Redis 中存储的 Token
     */
    public String getStoredToken(Long userId) {
        String redisKey = tokenPrefix + ":" + userId;
        return (String) redisTemplate.opsForValue().get(redisKey);
    }
    /**
     * 判断用户是否在线
     */
    public boolean isOnline(Long userId) {
        String redisKey = tokenPrefix + ":" + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}