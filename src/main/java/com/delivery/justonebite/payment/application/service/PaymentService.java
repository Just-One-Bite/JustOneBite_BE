package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import com.delivery.justonebite.payment.presentation.dto.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.PaymentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    // TODO: USER, ORDER 연결
    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
        boolean isValid = validatePayment(request);

        String paymentKey = generateRandomString();
        Payment payment = Payment.builder()
                .paymentId(paymentKey)
                .orderId(request.getOrderId())
                .orderName(request.getOrderName())
                .amount(request.getAmount())
                .status("PENDING")
                .build();
        paymentRepository.save(payment);

        if (!isValid) {
            log.warn("❌ 결제 요청 검증 실패: {}", request);
//            return "Fail";
//            return PaymentResponse.fail(request.getOrderId(), "http://localhost:8080/payments/fail?orderId=" + request.getOrderId());
        }

        log.info("✅ 결제 요청 검증 성공");
        payment.updateStatus("SUCCESS");
        return PaymentResponse.success(request.getOrderId(), paymentKey, request.getAmount());
    }

    // 결제 성공 처리
    public void handlePaymentSuccess(PaymentRequest request) {
        log.info("✅ [결제 성공 처리 완료] ");
        // TODO: DB 상태 변경 (예: 주문 상태를 "PAID"로)
    }

    // 결제 실패 처리
    public void handlePaymentFail(String orderId) {
        log.warn("❌ [결제 실패 처리] 주문 ID: {}", orderId);
        // TODO: 결제 실패 로그 저장, 주문 취소 처리 등
    }

    // 결제 유효 검증
    // TODO: 상황별 오류 코드 출력
    private boolean validatePayment(PaymentRequest request) {
        return request.getAmount() > 0 || request.getOrderName() != null;
    }

    // 랜덤 키 생성 (paymentId)
    public String generateRandomString() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 36; // UUID와 비슷하게 임의로 맞춰봄

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }



}