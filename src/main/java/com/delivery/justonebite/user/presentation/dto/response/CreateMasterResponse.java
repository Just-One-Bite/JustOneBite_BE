package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "최종 관리자(MASTER) 생성 응답 DTO")
@Builder
public record CreateMasterResponse(
        @Schema(description = "인증에 필요한 Access Token", example = "{accessToken}")
        String accessToken,
        @Schema(description = "Access Token 재발급에 필요한 Refresh Token", example = "{refreshToken}")
        String refreshToken,
        @Schema(description = "최종 관리자 고유 Id", example = "1")
        Long userId,
        @Schema(description = "인증에 사용된 이메일", example = "master@email.com")
        String email,
        @Schema(description = "최종 관리자 이름", example = "master")
        String name,
        @Schema(description = "최종 관리자 전화번호", example = "010-9999-9999")
        String phoneNumber,
        @Schema(description = "최종 관리자가 가진 권한", example = "MASTER")
        UserRole userRole,
        @Schema(description = "생성일시", example = "1")
        LocalDateTime createdAt,
        @Schema(description = "최종 관리자 계정을 생성한 생성자", example = "2025:01:01T00:00:00")
        Long createdBy
) {
    public static CreateMasterResponse of(User user, TokenResponse tokenResponse) {
        return CreateMasterResponse.builder()
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
