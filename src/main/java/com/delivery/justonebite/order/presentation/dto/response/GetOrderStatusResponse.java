package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.OrderHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GetOrderStatusResponse(
    UUID orderId,
    String currentStatus,
    List<OrderHistoryDto> history
) {
    @Builder
    public record OrderHistoryDto(
        UUID orderHistoryId,
        String status,
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
