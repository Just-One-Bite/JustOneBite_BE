package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.presentation.dto.request.ReissueRequest;
import com.delivery.justonebite.user.presentation.dto.response.*;
import com.delivery.justonebite.user.application.service.AuthService;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.request.LoginRequest;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "회원 가입/로그인/최종관리자 생성/토큰 재발급을 담당합니다.")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원 가입 요청 API",
            description = "회원 가입을 통해 user 객체를 생성합니다. 최초 생성 시 권한은 CUSTOMER입니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원 가입에 성공하였습니다."),
                    @ApiResponse(responseCode = "409", description = "사용중인 이메일입니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        AuthResult authResult = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignupResponse.of(authResult.user(), authResult.tokenResponse()));
    }

    @Operation(
            summary = "로그인 요청 API",
            description = "로그인을 통해 인증을 진행합니다. 인증에 성공하면 access token, refresh token을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "로그인에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "로그인에 실패했습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LoginResponse.of(token.accessToken(), token.refreshToken()));
    }

    @Operation(
            summary = "토큰 재발급 요청 API",
            description = "access token 만료 시 access token과 refresh token을 재발급합니다. 기존 access token과 refresh token은 무효처리됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "로그인 정보가 유효하지 않습니다. 다시 로그인해주세요.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "탈퇴 처리된 유저입니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody ReissueRequest request) {
        TokenResponse token = authService.reissue(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @Operation(
            summary = "최종 관리자 생성 요청 API",
            description = "MASTER권한의 최종 관리자를 생성합니다. 최종 관리자 계정은 1개만 존재 가능합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "유저 정보 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "409", description = "최종관리자가 이미 존재합니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "409", description = "사용중인 이메일입니다.", content = @Content(mediaType = "application/json"))

            }
    )
    @PostMapping("/admin/signup")
    public ResponseEntity<CreateMasterResponse> createMaster(@RequestBody @Valid CreatedMasterRequest request) {
        AuthResult master = authService.createMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateMasterResponse.of(master.user(), master.tokenResponse()));
    }
}
