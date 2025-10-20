package com.delivery.justonebite.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "로그인 응답 DTO")
@Builder
public record LoginResponse(
        @Schema(description = "인증에 필요한 Access Token", example = "{accessToken}")
        String accessToken,
        @Schema(description = "Access Token 재발급에 필요한 Refresh Token", example = "{refreshToken}")
        String refreshToken
) {
    public static LoginResponse toDto(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
