package com.delivery.justonebite.payment.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private UUID orderId;
    private String paymentId;
    private String redirectUrl;

//    public static PaymentResponse success(UUID orderId, String successUrl) {
//        return new PaymentResponse(orderId, successUrl);
//    }
//
//    public static PaymentResponse fail(UUID orderId, String failUrl) {
//        return new PaymentResponse(orderId, failUrl);
//    }
}