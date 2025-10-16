package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "h_payment")
public class Payment extends BaseEntity {

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

    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public static Payment createPayment(UUID orderId, String orderName, Integer amount) {
        return Payment.builder()
                .orderId(orderId)
                .orderName(orderName)
                .totalAmount(amount)
                .balanceAmount(amount)
                .status(PaymentStatus.READY)
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
