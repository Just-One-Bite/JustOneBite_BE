package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.User;
import lombok.Builder;

@Builder
public record AuthResult(User user, TokenResponse tokenResponse) {
    public static AuthResult toDto(User user, TokenResponse tokenResponse) {
        return AuthResult.builder()
                .user(user)
                .tokenResponse(tokenResponse)
                .build();
    }
}
