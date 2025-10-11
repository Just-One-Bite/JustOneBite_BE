package com.delivery.justonebite.payment.presentation.dto;

import com.delivery.justonebite.payment.domain.entity.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentResponse(
    UUID paymentId,
    UUID orderId,
    String orderName,
    UUID shopId,
    String method,
    BigDecimal totalAmount,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
            .paymentId(payment.getPaymentId())
            .orderId(payment.getOrderId())
            .orderName(payment.getOrderName())
            .shopId(payment.getShopId())
            .method(payment.getMethod())
            .totalAmount(payment.getTotalAmount())
            .status(payment.getStatus())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
}