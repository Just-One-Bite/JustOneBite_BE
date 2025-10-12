package com.delivery.justonebite.payment.domain.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY, // 결제 승인 전 초기 상태
    DONE, // 결제 승인 완료
    ABORTED, // 결제 승인 실패
    CANCELED, // 승인 된 결제 취소
    PARTIAL_CANCELED, // 일부 결제 취소
    EXPIRED; // 결제 만료
}
