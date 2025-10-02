package com.delivery.justonebite.user.presentation.dto.response;

public record SignupResponseDto(
        String token
) {
    public static SignupResponseDto toDto(String token) {
        return new SignupResponseDto(token);
    }
}
