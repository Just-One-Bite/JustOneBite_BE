package com.delivery.justonebite.payment.presentation.dto.response;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentCancelResponse(
        UUID paymentKey,
        UUID lastTransactionKey,
        UUID orderId,
        String orderName,
        Integer totalAmount,
        Integer balanceAmount,
        PaymentStatus status,
        String cancelReason,
        LocalDateTime canceledAt
) {
    public static PaymentCancelResponse from(Payment payment, String reason) {
        return PaymentCancelResponse.builder()
                .paymentKey(payment.getPaymentId())
                .lastTransactionKey(payment.getLastTransactionId())
                .orderId(payment.getOrderId())
                .orderName(payment.getOrderName())
                .totalAmount(payment.getTotalAmount())
                .balanceAmount(payment.getBalanceAmount())
                .status(payment.getStatus())
                .cancelReason(reason)
                .canceledAt(LocalDateTime.now())
                .build();
    }
}
