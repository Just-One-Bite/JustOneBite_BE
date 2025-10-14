package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Schema(description = "주문 취소 요청에 대한 응답 DTO")
@Builder
public record OrderCancelResponse(
    @Schema(description = "취소할 주문의 고유 ID", example = "예시: a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    UUID orderId,
    @Schema(description = "주문 취소 상태", example = "ORDER_CANCELLED")
    String orderStatus,
    @Schema(
        description = "환불될 금액. 현재는 '주문 총 금액'과 동일하게 설정됨 (쿠폰/포인트 미고려)",
        example = "25000"
    )
    Integer refund, // 쿠폰이나 포인트 사용했을 경우, 실제 결제 금액과 다를 수 있으나 현재 시스템에서는 따로 고려하지 않고 있습니다. 참고 바랍니다.
    @Schema(description = "주문이 취소된 시각")
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
