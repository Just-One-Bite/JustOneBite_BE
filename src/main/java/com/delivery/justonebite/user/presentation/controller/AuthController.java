package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.application.service.AuthService;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequestDto;
import com.delivery.justonebite.user.presentation.dto.response.SignupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        SignupResponseDto token = authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }
}
