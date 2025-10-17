package com.delivery.justonebite.payment.presentation.dto.response;

import java.util.UUID;

public record PaymentSuccessResponse(
    String redirectUrl,
    UUID orderId,
    UUID paymentId,
    Integer amount
) implements PaymentResponse {
    
    public PaymentSuccessResponse(UUID orderId, UUID paymentId, Integer amount) {
        this("http://localhost:8080/payments/success?paymentType=NORMAL&orderId=" + orderId +
             "&paymentId=" + paymentId + "&amount=" + amount,
             orderId, paymentId, amount);
    }
}