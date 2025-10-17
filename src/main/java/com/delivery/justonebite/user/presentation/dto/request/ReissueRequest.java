package com.delivery.justonebite.user.presentation.dto.request;

public record ReissueRequest(
        String accessToken,
        String refreshToken
) {
}
