package com.delivery.justonebite.user.presentation.dto.response;

public record SignupResponse(
        String token
) {
    public static SignupResponse toDto(String token) {
        return new SignupResponse(token);
    }
}
