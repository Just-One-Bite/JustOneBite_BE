package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateMasterResponse(
        String token,
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
    public static CreateMasterResponse toDto(String token, User user) {
        return CreateMasterResponse.builder()
                .token(token)
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
