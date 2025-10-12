package com.delivery.justonebite.payment.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String redirectUrl;
    private UUID orderId;
    private String paymentId;
    private Integer amount;

    public static PaymentResponse success(UUID orderId, String paymentId, Integer amount) {
        String successUrl = "http://localhost:8080/payments/success?paymentType=NORMAL&orderId="+orderId
                + "&paymentKey=" + paymentId + "&amount="+amount;
        return new PaymentResponse(successUrl, orderId, paymentId, amount);
    }

    public static String fail(UUID orderId, String failUrl) {
//        return new PaymentResponse(orderId, failUrl);
        return "fail";
    }
}