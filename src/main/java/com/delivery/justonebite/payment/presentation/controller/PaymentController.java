package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentCancelRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentConfirmRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentCancelResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentConfirmResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<PaymentConfirmResponse> getPaymentById(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentConfirmResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentConfirmResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentConfirmResponse.from(payment));
    }

    // 결제 요청 (프론트에서 requestPayment를 대신해서 작동)
    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
        Object response = paymentService.confirmPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = paymentService.cancelPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}