package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateMasterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public CreateMasterResponse createMaster(CreatedMasterRequest request) {
        if (userRepository.existsByUserRole(request.userRole())) {
            throw new CustomException(ErrorCode.MASTER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String bearerToken = jwtUtil.createToken(user);
        String token = jwtUtil.removePrefix(bearerToken);
        return CreateMasterResponse.toDto(token, user);
    }
}
