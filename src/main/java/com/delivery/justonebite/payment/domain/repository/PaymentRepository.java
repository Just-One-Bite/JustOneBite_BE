package com.delivery.justonebite.payment.domain.repository;

import com.delivery.justonebite.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByPaymentId(String paymentId);
}