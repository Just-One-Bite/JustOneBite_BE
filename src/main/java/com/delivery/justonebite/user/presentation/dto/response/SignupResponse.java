package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "회원가입 응답 DTO")
@Builder
public record SignupResponse(
        @Schema(description = "인증에 필요한 Access Token", example = "{accessToken}")
        String accessToken,
        @Schema(description = "Access Token 재발급에 필요한 Refresh Token", example = "{refreshToken}")
        String refreshToken,
        @Schema(description = "유저의 고유 Id", example = "3")
        Long userId,
        @Schema(description = "인증에 사용된 이메일", example = "example@email.com")
        String email,
        @Schema(description = "유저의 이름", example = "성이름")
        String name,
        @Schema(description = "유저의 전화번호", example = "010-1234-5678")
        String phoneNumber,
        @Schema(description = "유저가 가진 권한", example = "CUSTOMER")
        UserRole userRole,
        @Schema(description = "생성일시", example = "2025:01:01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "유저를 생성한 생성자", example = "3")
        Long createdBy
) {
    public static SignupResponse toDto(User user, TokenResponse tokenResponse) {
        return SignupResponse.builder()
                .accessToken(tokenResponse.accessToken())
                .refreshToken(tokenResponse.refreshToken())
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .build();
    }
}
