package com.delivery.justonebite.order.domain.enums;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum OrderStatus {

    PENDING("준비 중"),
    ORDER_ACCEPTED("주문 접수 완료"),
    ORDER_REJECTED("주문 거절"),
    ORDER_CANCELLED("주문 취소"),
    PREPARING("준비 중"),
    DELIVERING("배달 중"),
    COMPLETED("배달 완료");

    private final String description;
    // 유효한 다음 단계 상태 체크하는 Set
    private Set<OrderStatus> nextValidStatuses;

    OrderStatus(String description) {
        this.description = description;
    }

    static {
        PENDING.nextValidStatuses = EnumSet.of(ORDER_ACCEPTED, ORDER_REJECTED, ORDER_CANCELLED);
        ORDER_ACCEPTED.nextValidStatuses = EnumSet.of(PREPARING, ORDER_REJECTED);
        // ORDER_REJECTED, ORDER_CANCELLED, COMPLETED는 다음 상태가 없으므로 비어있는 Set 할당
        ORDER_REJECTED.nextValidStatuses = Collections.emptySet();
        ORDER_CANCELLED.nextValidStatuses = Collections.emptySet();
        PREPARING.nextValidStatuses = EnumSet.of(DELIVERING, ORDER_REJECTED);
        DELIVERING.nextValidStatuses = EnumSet.of(COMPLETED);
        COMPLETED.nextValidStatuses = Collections.emptySet();
    }

    public static OrderStatus of(String status) {
        return Arrays.stream(OrderStatus.values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    // 유효한 다음 상태인지 확인하는 메서드
    public boolean isValidNextStatus(OrderStatus status) {
        return this.nextValidStatuses.contains(status);
    }
}