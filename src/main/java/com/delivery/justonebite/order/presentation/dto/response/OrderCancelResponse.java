package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderCancelResponse(
    UUID orderId,
    String orderStatus,
    Integer refund, // 쿠폰이나 포인트 사용했을 경우, 실제 결제 금액과 다를 수 있으나 현재 시스템에서는 따로 고려하지 않고 있습니다. 참고 바랍니다.
    LocalDateTime cancelledAt
) {
    public static OrderCancelResponse toDto(Order order, LocalDateTime cancelledAt) {
        return OrderCancelResponse.builder()
            .orderId(order.getId())
            .orderStatus(order.getCurrentStatus().name())
            .refund(order.getTotalPrice())
            .cancelledAt(cancelledAt)
            .build();
    }
}
