package com.delivery.justonebite.payment.domain.repository;

import com.delivery.justonebite.payment.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
