package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.application.service.AuthService;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateMasterResponse;
import com.delivery.justonebite.user.presentation.dto.response.SignupResponse;
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
        SignupResponse token = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<CreateMasterResponse> createMaster(@RequestBody @Valid CreatedMasterRequest request) {
        CreateMasterResponse master = authService.createMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(master);
    }
}
