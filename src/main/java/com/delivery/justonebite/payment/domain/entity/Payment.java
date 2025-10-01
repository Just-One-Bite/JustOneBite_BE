package com.delivery.justonebite.payment.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "order_name", nullable = false)
    private String orderName;
    
    @Column(name = "shop_id", nullable = false)
    private Long shopId;
    
    @Column(name = "method", nullable = false)
    private String method;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "requested_at", nullable = false)
    private Timestamp requestedAt;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

}
