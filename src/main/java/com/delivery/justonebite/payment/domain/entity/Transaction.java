package com.delivery.justonebite.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "h_transaction")
public class Transaction {

    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", nullable = false)
    private String status;

    @CreatedDate
    @Column(name = "transaction_at", updatable = false, nullable = false)
    private LocalDateTime transactionAt;

    public static Transaction of(Payment payment, String transactionKey) {
        return Transaction.builder()
                .paymentId(payment.getPaymentId())
                .transactionId(transactionKey)
                .orderId(payment.getOrderId())
                .amount(payment.getBalanceAmount())
                .status(payment.getStatus())
                .transactionAt(LocalDateTime.now())
                .build();
    }
}
