package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import com.delivery.justonebite.payment.presentation.dto.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public PaymentResponse requestPayment(PaymentRequest request) {
        log.info("[결제 요청 시작] orderId={}, amount={}", request.getOrderId(), request.getAmount());

        boolean isValid = validatePayment(request);
        if (!isValid) {
            log.warn("❌ 결제 요청 검증 실패: {}", request);
            return PaymentResponse.fail(request.getOrderId(), "http://localhost:8080/payments/fail?orderId=" + request.getOrderId());
        }

        log.info("✅ 결제 요청 검증 성공");
        return PaymentResponse.success(request.getOrderId(),
                "http://localhost:8080/payments/success?orderId=" + request.getOrderId());
    }

    // 결제 유효 검증
    // TODO: 상황별 오류 코드 출력
    private boolean validatePayment(PaymentRequest request) {
        return request.getAmount() > 0 && request.getOrderName() != null;
    }
}