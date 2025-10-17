package com.delivery.justonebite.payment.presentation.dto.response;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentConfirmResponse(
        UUID paymentId,
        UUID lastTransactionKey,
        UUID orderId,
        String orderName,
        PaymentStatus status,
        Integer totalAmount,
        Integer balanceAmount,
        LocalDateTime createdAt,
        LocalDateTime approvedAt
) {
    public static PaymentConfirmResponse from(Payment payment) {
        return PaymentConfirmResponse.builder()
                .paymentId(payment.getPaymentId())
                .lastTransactionKey(payment.getLastTransactionId())
                .orderId(payment.getOrderId())
                .orderName(payment.getOrderName())
                .status(payment.getStatus())
                .totalAmount(payment.getTotalAmount())
                .balanceAmount(payment.getBalanceAmount())
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
