package com.delivery.justonebite.payment.application.service;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import com.delivery.justonebite.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
/**
 *  결제 요청 후 10분 이내로 결제 승인 API를 호출 하지 않으면 자동 만료됨
 */
public class PaymentExpirationScheduler {
    private final PaymentRepository paymentRepository;

    @Transactional
    @Scheduled(fixedRate = 60_000) // 1분마다 실행
    public void expireUnconfirmedPayments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(10);

        // 10분 전에 생성됐고 SUCCESS 상태 결제 찾기
        List<Payment> expiredPayments = paymentRepository.findAllByStatusAndCreatedAtBefore(
                PaymentStatus.SUCCESS,
                threshold
        );

        if (expiredPayments.isEmpty()) return;

        for (Payment payment : expiredPayments) {
            payment.updateStatus(PaymentStatus.EXPIRED);
        }
        paymentRepository.saveAll(expiredPayments);
    }
}
