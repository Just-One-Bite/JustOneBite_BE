package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.payment.application.service.TossPaymentService;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentHistoryResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentResponse;
import com.delivery.justonebite.payment.presentation.dto.request.TossPaymentConfirmRequest;
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
    private final TossPaymentService tossPaymentService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentHistoryResponse> getPaymentById(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentHistoryResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentHistoryResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentHistoryResponse.from(payment));
    }

    // 결제 요청 (프론트에서 requestPayment를 대신해서 작동)
    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 결제 승인 요청 (Toss API 테스트)
    @PostMapping("/confirm")
    public String confirmPayment(@RequestBody TossPaymentConfirmRequest request) {
        return tossPaymentService.confirmPayment(request);
    }
}