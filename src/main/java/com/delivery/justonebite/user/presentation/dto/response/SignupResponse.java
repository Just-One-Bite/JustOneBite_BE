package com.delivery.justonebite.user.presentation.dto.response;

import lombok.Builder;

@Builder
public record SignupResponse(
        String token
) {
    public static SignupResponse toDto(String token) {
        return SignupResponse.builder()
                .token(token)
                .build();
    }
}
