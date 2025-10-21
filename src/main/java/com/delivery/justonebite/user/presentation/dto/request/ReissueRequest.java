package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 요청 DTO")
public record ReissueRequest(
        @Schema(description = "인증에 사용되는 access token", example = "{accessToken}")
        String accessToken,
        @Schema(description = "access token 재발급에 필요한 refresh token", example = "{refreshToken}")
        String refreshToken
) {
}
