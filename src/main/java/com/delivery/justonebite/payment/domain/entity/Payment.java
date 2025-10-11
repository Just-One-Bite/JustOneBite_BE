package com.delivery.justonebite.payment.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "h_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private String paymentId;
    
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    
    @Column(name = "order_name", nullable = false)
    private String orderName;
    
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;
    
    @Column(name = "status", nullable = false)
    private String status;

    @Override
    public String toString() {
        return "paymentId: "+paymentId+" orderId: "+orderId+" orderName: "+orderName+"totalAmount: "+totalAmount+"status: "+status;
    }
}
