package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "인증에 필요한 이메일", example = "email@email.com")
        String email,
        @Schema(description = "인증에 필요한 비밀번호", example = "!Qwer1234")
        String password
) {
}
