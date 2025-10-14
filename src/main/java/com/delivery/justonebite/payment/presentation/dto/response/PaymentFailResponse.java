package com.delivery.justonebite.payment.presentation.dto.response;

import java.util.UUID;

public record PaymentFailResponse(
    String redirectUrl,
    UUID orderId,
    String code,
    String message
) implements PaymentResponse {
    public PaymentFailResponse(UUID orderId, String code, String message) {
        this("http://localhost:8080/payments/fail?code=" + code + "&message=" + message + "&orderId=" + orderId,
             orderId, code, message);
    }
}
