package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "h_payment")
public class Payment extends BaseEntity {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", nullable = false)
    private String status;

    public void updateStatus(String status) {
        this.status = status;
    }
}
