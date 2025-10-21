package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "유저 정보 수정 응답 DTO")
@Builder
public record UpdateProfileResponse(
        @Schema(description = "유저의 고유 Id", example = "3")
        Long userId,
        @Schema(description = "인증에 사용된 이메일", example = "example@email.com")
        String email,
        @Schema(description = "유저의 이름", example = "example")
        String name,
        @Schema(description = "유저의 전화번호", example = "011-1234-5678")
        String phoneNumber,
        @Schema(description = "유저가 가진 권한", example = "CUSTOMER/OWNER/MASTER")
        UserRole userRole,
        @Schema(description = "생성일시", example = "2025:01:01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "유저를 생성한 생성자", example = "3")
        Long createdBy,
        @Schema(description = "수정일시", example = "2025:01:01T00:00:00")
        LocalDateTime updatedAt,
        @Schema(description = "유저를 수정한 수정자", example = "3")
        Long updatedBy
) {
    public static UpdateProfileResponse from(User user) {
        return UpdateProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
