package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "관리자(MANAGER) 생성 응답 DTO")
@Builder
public record CreateManagerResponse(
        @Schema(description = "관리자 고유 Id", example = "2")
        Long userId,
        @Schema(description = "인증에 사용된 이메일", example = "manager1@email.com")
        String email,
        @Schema(description = "관리자 이름", example = "111")
        String name,
        @Schema(description = "관리자 전화번호", example = "010-1111-1111")
        String phoneNumber,
        @Schema(description = "관리자가 가진 권한", example = "MANAGER")
        UserRole userRole,
        @Schema(description = "생성일시", example = "2025:01:01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "관리자 계정을 생성한 생성자", example = "1")
        Long createdBy
) {
    public static CreateManagerResponse from(User user) {
        return CreateManagerResponse.builder()
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
