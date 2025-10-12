package com.delivery.justonebite.payment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancel {
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "cancel_reason", nullable = false)
    private String cancelReason;

    @Column(name = "cancel_amount", nullable = false)
    private Integer cancelAmount;

    @Column(name = "cancel_status", nullable = false)
    private String cancelStatus;

    @CreatedDate
    @Column(name = "canceled_at", nullable = false)
    private LocalDateTime canceledAt;
}
