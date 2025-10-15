package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import com.delivery.justonebite.user.presentation.dto.request.ReissueRequest;
import com.delivery.justonebite.user.presentation.dto.response.TokenResponse;
import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.LoginRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public AuthResult signup(SignupRequest request) {
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        TokenResponse tokenResponse = issueTokensAndSaveRefreshToken(user);

        return AuthResult.toDto(user, tokenResponse);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

        return issueTokensAndSaveRefreshToken(user);
    }

    @Transactional
    public TokenResponse reissue(ReissueRequest request) {
        if (!jwtUtil.validateToken(request.refreshToken())) {
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN);
        }

        String email = jwtUtil.getSubjectFromRefreshToken(request.refreshToken());

        String redisRefreshToken = redisTemplate.opsForValue().get("Refresh Token: " + email);
        if (redisRefreshToken == null || !redisRefreshToken.equals(request.refreshToken())) {
            throw new CustomException(ErrorCode.RE_LOGIN_REQUIRED);
        }

        User user = userRepository.findByEmailIncludeDeleted(email)
                .orElseThrow(() -> new CustomException(ErrorCode.DELETED_ACCOUNT));

        return issueTokensAndSaveRefreshToken(user);
    }

    private TokenResponse issueTokensAndSaveRefreshToken(User user) {
        TokenResponse tokenResponse = jwtUtil.generateToken(user);

        redisTemplate.opsForValue().set(
                "Refresh Token: " + user.getEmail(),
                tokenResponse.refreshToken(),
                JwtUtil.REFRESH_TOKEN_EXPIRATION,
                TimeUnit.MINUTES);

        return tokenResponse;
    }

    @Builder
    public record AuthResult(User user, TokenResponse tokenResponse) {
        public static AuthResult toDto(User user, TokenResponse tokenResponse) {
            return AuthResult.builder()
                    .user(user)
                    .tokenResponse(tokenResponse)
                    .build();
        }
    }
}
