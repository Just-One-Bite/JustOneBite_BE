package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.config.redis.service.RedisService;
import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.request.LoginRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import com.delivery.justonebite.user.presentation.dto.response.TokenResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

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
    public AuthResult createMaster(CreatedMasterRequest request) {
        if (userRepository.existsByUserRole(UserRole.MASTER)) {
            throw new CustomException(ErrorCode.MASTER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(UserRole.MASTER, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        TokenResponse tokenResponse = issueTokensAndSaveRefreshToken(user);
        return AuthResult.toDto(user, tokenResponse);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        return issueTokensAndSaveRefreshToken(user);
    }

    @Transactional
    public TokenResponse reissue(ReissueRequest request) {
        if (!jwtUtil.validateToken(request.refreshToken())) {
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN);
        }
        String email = jwtUtil.getSubjectFromToken(request.refreshToken());
        String redisRefreshToken = redisService.getRefreshToken(email);
        if (redisRefreshToken == null || !redisRefreshToken.equals(request.refreshToken())) {
            throw new CustomException(ErrorCode.RE_LOGIN_REQUIRED);
        }
        redisService.addToDenylist(request.accessToken());
        User user = userRepository.findByEmailIncludeDeleted(email)
                .orElseThrow(() -> new CustomException(ErrorCode.DELETED_ACCOUNT));
        return issueTokensAndSaveRefreshToken(user);
    }

    public void logout(Authentication authentication) {
        if (authentication == null) {
            return;
        }
        String email = authentication.getName();
        String accessToken = (String) authentication.getCredentials();
        invalidateTokens(email, accessToken);
    }

    // 토큰 발급 및 저장
    private TokenResponse issueTokensAndSaveRefreshToken(User user) {
        TokenResponse tokenResponse = jwtUtil.generateToken(user);
        redisService.saveRefreshToken(user.getEmail(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    // 회원 탈퇴 시 토큰 무효화
    public void invalidateTokensForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED);
        }
        String email = authentication.getName();
        String accessToken = (String) authentication.getCredentials();
        invalidateTokens(email, accessToken);
    }

    // 토큰 무효화
    private void invalidateTokens(String email, String accessToken) {
        redisService.deleteRefreshToken(email);
        redisService.addToDenylist(accessToken);
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
