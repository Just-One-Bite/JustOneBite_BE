package com.delivery.justonebite.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentSuccessResponse implements PaymentResponse{
    private String redirectUrl;
    private UUID orderId;
    private String paymentId;
    private Integer amount;


    public PaymentSuccessResponse (UUID orderId, String paymentId, Integer amount) {
        this.redirectUrl = "http://localhost:8080/payments/success?paymentType=NORMAL&orderId=" + orderId +
                "&paymentKey=" + paymentId + "&amount=" + amount;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
    }
}