package com.delivery.justonebite.global.config.redis.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${spring.data.redis.prefix.access_token}")
    private String accessTokenPrefix;

    @Value("${spring.data.redis.prefix.refresh_token}")
    private String refreshTokenPrefix;

    public void saveRefreshToken(String email, String refreshToken) {
        String key = refreshTokenPrefix + email;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                JwtUtil.REFRESH_TOKEN_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    public String getRefreshToken(String email) {
        String key = refreshTokenPrefix + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String email) {
        String key = refreshTokenPrefix + email;
        redisTemplate.delete(key);
    }

    public void addToDenylist(String accessToken) {
        String key = accessTokenPrefix + accessToken;
        redisTemplate.opsForValue().set(
                key,
                "logout",
                jwtUtil.getRemainingExpiration(accessToken),
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isTokenInDenylist(String accessToken) {
        String key = accessTokenPrefix + accessToken;
        return redisTemplate.hasKey(key);
    }
}
