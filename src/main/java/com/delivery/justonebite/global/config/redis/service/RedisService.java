package com.delivery.justonebite.global.config.redis.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public void saveRefreshToken(String email, String refreshToken) {
        String key = "RT:" + email;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                JwtUtil.REFRESH_TOKEN_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    public String getRefreshToken(String email) {
        String key = "RT:" + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String email) {
        String key = "RT:" + email;
        redisTemplate.delete(key);
    }

    public void addToDenylist(String accessToken) {
        String key = "ATD:" + accessToken;
        redisTemplate.opsForValue().set(
                key,
                "logout",
                jwtUtil.getRemainingExpiration(accessToken),
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isTokenInDenylist(String accessToken) {
        String key = "ATD:" + accessToken;
        return redisTemplate.hasKey(key);
    }
}
