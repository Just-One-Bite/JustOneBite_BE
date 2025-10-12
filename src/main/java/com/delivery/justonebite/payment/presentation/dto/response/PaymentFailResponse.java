package com.delivery.justonebite.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentFailResponse implements PaymentResponse{
    private String redirectUrl;
    private UUID orderId;
    private String code;
    private String message;

    public PaymentFailResponse (UUID orderId, String code, String message) {
        this.redirectUrl = "http://localhost:8080/payments/fail?code=" + code + "&message=" + message + "&orderId=" + orderId;
        this.orderId = orderId;
        this.code = code;
        this.message = message;
    }

}
