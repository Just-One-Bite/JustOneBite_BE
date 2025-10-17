package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import com.delivery.justonebite.payment.domain.entity.Transaction;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import com.delivery.justonebite.payment.domain.repository.TransactionRepository;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentCancelRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentConfirmRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.*;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
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
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {

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
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 만료 상태 확인
        if (PaymentStatus.EXPIRED.equals(payment.getStatus())) {
            updateOrderStatusToCancel(payment.getOrderId());
            throw new CustomException(ErrorCode.PAYMENT_EXPIRED);
        }
        // 결제 상태 검증
        if (!PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        // 금액 검증
        if (!payment.getTotalAmount().equals(request.amount())) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_NOT_MATCH);
        }
        try {
            Transaction transaction = Transaction.createTransaction(payment, request.amount());
            transactionRepository.save(transaction);

            payment.updateStatus(PaymentStatus.DONE);
            payment.updateApprovedAt(LocalDateTime.now());
            payment.updateLastTransactionId(transaction.getTransactionId());

            return PaymentConfirmResponse.from(payment);
        } catch (Exception e) {
            payment.updateStatus(PaymentStatus.ABORTED);
            updateOrderStatusToCancel(payment.getOrderId());
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    @Transactional
    public PaymentCancelResponse cancelPayment(PaymentCancelRequest request) {
        Payment payment = paymentRepository.findByPaymentId(request.paymentKey())
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        // 취소 상태 검증
        if (PaymentStatus.CANCELED.equals(payment.getStatus())) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        payment.decreaseBalanceAmount(request.cancelAmount());
        if (payment.getBalanceAmount() == 0) {
            payment.updateStatus(PaymentStatus.CANCELED);
        } else {
            payment.updateStatus(PaymentStatus.PARTIAL_CANCELED);
        }
        Transaction transaction = Transaction.createCancelTransaction(payment, request.cancelAmount(), request.cancelReason(), payment.getStatus());
        transactionRepository.save(transaction);
        payment.updateLastTransactionId(transaction.getTransactionId()); // save 안해도 자동 commit
        return PaymentCancelResponse.from(payment, request.cancelReason());
    }

    // orderStatus 변경 및 orderHistory 객체 생성
    private void updateOrderStatusToCancel(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        
        order.updateExpiredStatus(PaymentStatus.EXPIRED);
        orderHistoryRepository.save(OrderHistory.create(order, OrderStatus.ORDER_CANCELLED));
    }
}