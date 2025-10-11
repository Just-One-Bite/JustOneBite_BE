package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.presentation.dto.PaymentCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentCreateRequest request) {
        Payment payment = paymentService.createPayment(
            request.getOrderId(),
            request.getOrderName(),
            request.getShopId(),
            request.getMethod(),
            request.getTotalAmount()
        );
        return ResponseEntity.ok(payment);
    }
}