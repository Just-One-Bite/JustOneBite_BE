package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "lastTransactio_key")
    private String lastTransactionKey;

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
    private String approvedAt;

    // 결제 취소 내역
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "payment_cancels",
            joinColumns = @JoinColumn(name = "payment_id")
    )
    private List<PaymentCancel> cancels = new ArrayList<>();


    public void updateStatus(String status) {
        this.status = status;
    }

}
