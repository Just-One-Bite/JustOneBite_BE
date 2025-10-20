package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 요청 DTO")
public record ReissueRequest(
        @Schema(description = "인증에 사용되는 access token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBnbWFpbC5jb20iLCJyb2xlIjoiQ1VTVE9NRVIiLCJleHAiOjE3NjA2Nzg0NTEsImlhdCI6MTc2MDY3NDg1MX0.eiEFfMsVHmiC5r1X-zJYKMEhi053WxSXfIGCHFUsIeU")
        String accessToken,
        @Schema(description = "access token 재발급에 필요한 refresh token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBnbWFpbC5jb20iLCJleHAiOjE3NjMyNjY4NTEsImlhdCI6MTc2MDY3NDg1MX0.mu2Tgn60cNXDvS1iC-W__ixcgGGYi-A3QtEQqIEWj-o")
        String refreshToken
) {
}
