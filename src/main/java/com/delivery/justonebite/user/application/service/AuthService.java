package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.jwt.JwtUtil;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import com.delivery.justonebite.user.presentation.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        User user = request.toUser(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String bearerToken = jwtUtil.createToken(user);
        String token = jwtUtil.removePrefix(bearerToken);
        return SignupResponse.toDto(token);
    }
}
