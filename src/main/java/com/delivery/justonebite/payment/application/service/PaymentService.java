package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment createPayment(UUID orderId, String orderName, UUID shopId, String method, BigDecimal totalAmount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setOrderName(orderName);
        payment.setShopId(shopId);
        payment.setMethod(method);
        payment.setTotalAmount(totalAmount);
        payment.setStatus("PENDING");
        
        return paymentRepository.save(payment);
    }
}