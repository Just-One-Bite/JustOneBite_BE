package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateMasterResponse;
import com.delivery.justonebite.user.presentation.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String token = generateToken(user);
        return SignupResponse.toDto(token);
    }

    @Transactional
    public CreateMasterResponse createMaster(CreatedMasterRequest request) {
        if (userRepository.existsByUserRole(UserRole.MASTER)) {
            throw new CustomException(ErrorCode.MASTER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(UserRole.MASTER, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String token = generateToken(user);
        return CreateMasterResponse.toDto(token, user);
    }

    private String generateToken(User user) {
        String bearerToken = jwtUtil.createToken(user);
        return jwtUtil.removePrefix(bearerToken);
    }
}
