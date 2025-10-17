package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.CreateManagerRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateManagerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateManagerResponse createManager(CreateManagerRequest request) {
        if (userRepository.existsByEmailIncludeDeleted(request.email())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = request.toUser(UserRole.MANAGER, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return CreateManagerResponse.toDto(user);
    }
}
