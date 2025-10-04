package com.delivery.justonebite.order.domain.enums;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("준비 중"),
    ORDER_ACCEPTED("주문 접수 완료"),
    ORDER_REJECTED("주문 거절"),
    ORDER_CANCELLED("주문 취소"),
    PREPARING("준비 중"),
    DELIVERING("배달 중"),
    COMPLETED("배달 완료");

    private final String description;

    public static OrderStatus of(String status) {
        return Arrays.stream(OrderStatus.values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
