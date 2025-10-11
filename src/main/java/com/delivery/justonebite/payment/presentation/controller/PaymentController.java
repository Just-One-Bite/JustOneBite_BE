package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.presentation.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentResponse.from(payment));
    }
}