package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.OrderHistory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Schema(description = "주문 상태 변경 이력 조회 응답 DTO")
@Builder
public record GetOrderStatusResponse(
    @Schema(description = "조회된 주문의 고유 ID", example = "예시: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
    UUID orderId,
    @Schema(description = "주문의 현재 상태", example = "PENDING")
    String currentStatus,

    // 리스트 내부 객체의 타입을 명시
    @ArraySchema(
        schema = @Schema(implementation = OrderHistoryDto.class)
    )
    List<OrderHistoryDto> history
) {
    @Schema(description = "단일 주문 상태 변경 이력 항목")
    @Builder
    public record OrderHistoryDto(
        @Schema(description = "주문 이력의 고유 ID", example = "예시: f8a9b0c1-d2e3-4f5g-6h7i-8j9k0l1m2n3o")
        UUID orderHistoryId,
        @Schema(description = "상태 (예: PENDING, ORDER_CANCELLED)")
        String status,
        @Schema(description = "상태가 변경된 시각")
        LocalDateTime timestamp
    ) {
        public static OrderHistoryDto toDto(OrderHistory orderHistory) {
            return OrderHistoryDto.builder()
                .orderHistoryId(orderHistory.getId())
                .status(orderHistory.getStatus().name())
                .timestamp(orderHistory.getCreatedAt())
                .build();
        }
    }

    public static GetOrderStatusResponse toDto(UUID orderId, List<OrderHistory> histories) {
        OrderHistory currHistory = histories.getFirst();
        String currentStatus = currHistory.getStatus().name();

        // 주문 상태 리스트 DTO 변환
        List<OrderHistoryDto> historyDtos = histories.stream()
            .map(OrderHistoryDto::toDto)
            .toList();

        return GetOrderStatusResponse.builder()
            .orderId(orderId)
            .currentStatus(currentStatus)
            .history(historyDtos)
            .build();
    }
}
