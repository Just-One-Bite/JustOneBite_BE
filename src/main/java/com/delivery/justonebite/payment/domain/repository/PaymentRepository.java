package com.delivery.justonebite.payment.domain.repository;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByPaymentId(UUID paymentId);
    List<Payment> findAllByStatusAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime createdAt
    );
}