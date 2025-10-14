package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import com.delivery.justonebite.payment.domain.entity.Transaction;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import com.delivery.justonebite.payment.domain.repository.TransactionRepository;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentConfirmRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentConfirmResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentFailResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    // TODO: ORDER 연결
    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
//        boolean isValid = validatePayment(request);

        Payment payment = Payment.createPayment(request.orderId(), request.orderName(), request.amount());
        paymentRepository.save(payment);

        if (!request.status()) {
            payment.updateStatus(PaymentStatus.FAIL);
            return new PaymentFailResponse(request.orderId(), "PAY_PROCESS_CANCELED","사용자에 의해 결제가 취소되었습니다.");
        }
        payment.updateStatus(PaymentStatus.SUCCESS);
        return new PaymentSuccessResponse(request.orderId(), payment.getPaymentId(), request.amount());
    }


    @Transactional
    public Object confirmPayment(PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByPaymentId(request.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 요청 내역을 찾을 수 없습니다.")); //TODO: 오류 처리

        try {
            // 금액 검증
            if (!payment.getTotalAmount().equals(request.amount())) {
                throw new IllegalArgumentException("결제 금액이 일치하지 않습니다."); //TODO: 오류 처리
            }

            Transaction transaction = Transaction.createTransaction(payment);
            transactionRepository.save(transaction);

            payment.updateStatus(PaymentStatus.DONE);
            payment.updateApprovedAt(LocalDateTime.now());
            payment.updateLastTransactionId(transaction.getTransactionId());
            paymentRepository.save(payment);

            return PaymentConfirmResponse.from(payment);

        } catch (Exception e) {
            payment.updateStatus(PaymentStatus.ABORTED);
            paymentRepository.save(payment);
            throw new RuntimeException("결제 승인 실패: " + e.getMessage()); //TODO: 실패 방식 변경
        }

    }

    //TODO: 부분 거래 취소 api

}