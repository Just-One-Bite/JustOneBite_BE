package com.delivery.justonebite.payment.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private String redirectUrl;

    public static PaymentResponse success(String orderId, String successUrl) {
        return new PaymentResponse(orderId, successUrl);
    }

    public static PaymentResponse fail(String orderId, String failUrl) {
        return new PaymentResponse(orderId, failUrl);
    }
}