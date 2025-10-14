package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Schema(description = "고객 주문 목록 조회 응답 DTO")
@Builder
public record CustomerOrderResponse(
    @Schema(description = "주문 고유 ID", example = "예시: a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    UUID orderId,
    @Schema(description = "주문한 가게 이름", example = "마라탕 천국")
    String shopName,
    @Schema(description = "가게 대표 이미지 URL", example = "https://api.domain.com/images/shop_123.jpg", nullable = true)
    String shopImage,
    @Schema(description = "현재 주문 상태", example = "DELIVERING")
    String orderStatus,
    @Schema(description = "주문이 접수된 시각", example = "2025-10-13T16:00:00")
    LocalDateTime orderedDate,
    @Schema(description = "주문 총 결제 금액", example = "35000")
    Integer totalFee,
    @Schema(description = "대표 주문 상품명 (예: '마라탕 외 2건')", example = "마라탕 외 2건")
    String itemName
) {

    public static CustomerOrderResponse toDto(Order order) {
        return CustomerOrderResponse.builder()
            .orderId(order.getId())
            .shopName(order.getShop().getName())
            .orderStatus(order.getCurrentStatus().name())
            .orderedDate(order.getCreatedAt())
            .totalFee(order.getTotalPrice())
            .itemName(order.getOrderName())
            .build();
    }
}
