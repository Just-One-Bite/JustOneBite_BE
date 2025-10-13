package com.delivery.justonebite.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentSuccessResponse implements PaymentResponse{
    private String redirectUrl;
    private UUID orderId;
    private UUID paymentId;
    private Integer amount;


    public PaymentSuccessResponse (UUID orderId, UUID paymentId, Integer amount) {
        this.redirectUrl = "http://localhost:8080/payments/success?paymentType=NORMAL&orderId=" + orderId +
                "&paymentId=" + paymentId + "&amount=" + amount;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
    }
}