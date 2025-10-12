package com.delivery.justonebite.payment.presentation.dto.response;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentCancel;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PaymentConfirmResponse(
        String paymentId,
        String lastTransactionKey,
        String orderId,
        String orderName,
        String status,
        Integer totalAmount,
        Integer balanceAmount,
        List<PaymentCancel> cancels,
        LocalDateTime createdAt,
        LocalDateTime approvedAt
) {
    public static PaymentConfirmResponse from(Payment payment) {
        return PaymentConfirmResponse.builder()
                .paymentId(payment.getPaymentId())
                .lastTransactionKey(payment.getLastTransactionId())
                .orderId(payment.getOrderId().toString())
                .orderName(payment.getOrderName())
                .status(payment.getStatus())
                .totalAmount(payment.getTotalAmount())
                .balanceAmount(payment.getBalanceAmount())
                .cancels(null)
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
