package com.delivery.justonebite.payment.domain.repository;

import com.delivery.justonebite.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}