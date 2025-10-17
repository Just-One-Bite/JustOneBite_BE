package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignupResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String name,
        String phoneNumber,
        UserRole userRole,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy
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
