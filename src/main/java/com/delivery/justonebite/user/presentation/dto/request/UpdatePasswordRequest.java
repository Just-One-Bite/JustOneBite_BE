package com.delivery.justonebite.user.presentation.dto.request;

public record UpdatePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
