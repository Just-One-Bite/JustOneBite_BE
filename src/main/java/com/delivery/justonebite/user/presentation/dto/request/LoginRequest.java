package com.delivery.justonebite.user.presentation.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
