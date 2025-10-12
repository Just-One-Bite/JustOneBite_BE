package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "h_payment")
public class Payment extends BaseEntity {

    @Id
    @Column(name = "payment_id",nullable = false)
    private String paymentId;

    @Column(name = "last_transaction_id")
    private String lastTransactionId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "balance_amount", nullable = false)
    private Integer balanceAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 결제 취소 내역
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "payment_cancels",
            joinColumns = @JoinColumn(name = "payment_id")
    )
    private List<PaymentCancel> cancels = new ArrayList<>();

    public static Payment createPayment(String paymentId, UUID orderId, String orderName, Integer amount) {
        return Payment.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .orderName(orderName)
                .totalAmount(amount)
                .balanceAmount(amount)
                .status("PaymentStatus.READY.name()")
                .build();
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateApprovedAt(LocalDateTime now) {
        this.approvedAt = now;
    }

    public void updateLastTransactionId(String transactionKey) {
        this.lastTransactionId = transactionKey;
    }
}
