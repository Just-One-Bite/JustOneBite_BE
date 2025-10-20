package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "h_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id",nullable = false)
    private UUID paymentId;

    @Column(name = "last_transaction_id")
    private UUID lastTransactionId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    // 초기 결제 금액
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    // 취소 가능 금액
    @Column(name = "balance_amount", nullable = false)
    private Integer balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_status", nullable = false)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public static Payment createPayment(UUID orderId, String orderName, Integer amount) {
        return Payment.builder()
                .orderId(orderId)
                .orderName(orderName)
                .totalAmount(amount)
                .balanceAmount(amount)
                .status(PaymentStatus.READY)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void decreaseBalanceAmount(int amount) {
        if (amount > this.balanceAmount) {
            throw new CustomException(ErrorCode.CANCEL_AMOUNT_EXCEEDED);
        }
        this.balanceAmount -= amount;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updateApprovedAt(LocalDateTime now) {
        this.approvedAt = now;
    }

    public void updateLastTransactionId(UUID transactionKey) {
        this.lastTransactionId = transactionKey;
    }
}
