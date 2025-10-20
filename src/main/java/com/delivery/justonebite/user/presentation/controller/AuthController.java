package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.presentation.dto.request.ReissueRequest;
import com.delivery.justonebite.user.presentation.dto.response.*;
import com.delivery.justonebite.user.application.service.AuthService;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.request.LoginRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
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
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        AuthResult authResult = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignupResponse.toDto(authResult.user(), authResult.tokenResponse()));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoginResponse.toDto(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reisuue(@RequestBody ReissueRequest request) {
        TokenResponse token = authService.reissue(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<CreateMasterResponse> createMaster(@RequestBody @Valid CreatedMasterRequest request) {
        AuthResult master = authService.createMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateMasterResponse.toDto(master.user(), master.tokenResponse()));
    }
}
