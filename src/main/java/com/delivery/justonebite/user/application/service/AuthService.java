package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.common.jwt.JwtUtil;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequestDto;
import com.delivery.justonebite.user.presentation.dto.response.SignupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(@Valid SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.toEntity(requestDto, passwordEncoder.encode(requestDto.password()));
        userRepository.save(user);
        String bearerToken = jwtUtil.createToken(user);
        String token = jwtUtil.removePrefix(bearerToken);
        return SignupResponseDto.toDto(token);
    }
}
