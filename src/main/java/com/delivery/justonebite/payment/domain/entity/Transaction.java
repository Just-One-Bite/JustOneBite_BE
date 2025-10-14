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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_status")
    private PaymentStatus cancelStatus;

    @CreatedDate
    @Column(name = "transaction_at", updatable = false, nullable = false)
    private LocalDateTime transactionAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public static Transaction createTransaction(Payment payment) {
        return Transaction.builder()
                .payment(payment)
                .amount(payment.getBalanceAmount())
                .status(PaymentStatus.DONE)
                .transactionAt(LocalDateTime.now())
                .build();
    }
}
