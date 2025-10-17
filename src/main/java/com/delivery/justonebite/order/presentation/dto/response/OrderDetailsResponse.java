package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import com.delivery.justonebite.payment.domain.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Schema(description = "주문 상세정보 조회 응답 DTO")
@Builder
public record OrderDetailsResponse(
    @Schema(description = "주문 고유 ID", example = "예시: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
    UUID orderId,

    @Schema(description = "주문이 생성된 일시", example = "2025-10-13T10:30:00")
    LocalDateTime orderDate,

    @Schema(description = "가게 정보")
    ShopInfoDto shopInfo,

    @Schema(description = "주문 관련 정보")
    OrderInfoDto orderInfo,

    @Schema(description = "주문된 상품 목록")
    List<OrderItemDto> orderItems,

    @Schema(description = "결제 및 수수료 정보")
    PaymentDto payment
) {

    @Schema(description = "가게 정보 DTO")
    @Builder
    public record ShopInfoDto(UUID shopId, String shopName) {
        public static ShopInfoDto toDto(UUID shopId, String shopName) {
            return ShopInfoDto.builder()
                .shopId(shopId)
                .shopName(shopName)
                .build();
        }
    }

    @Schema(description = "주문 정보 DTO")
    @Builder
    public record OrderInfoDto(
        String address,
        String deliveryRequest,
        String orderRequest
    ) {
        public static OrderInfoDto toDto(Order order) {
            return OrderInfoDto.builder()
                .address(order.getAddress())
                .deliveryRequest(order.getDeliveryRequest())
                .orderRequest(order.getOrderRequest())
                .build();
        }
    }

    @Schema(description = "결제 정보 DTO")
    @Builder
    public record PaymentDto(
        Integer totalPrice,
        String paymentStatus
    ) {
        public static PaymentDto toDto(
            Payment payment
        ) {
            return PaymentDto.builder()
                .totalPrice(payment.getTotalAmount())
                .paymentStatus(payment.getStatus().name())
                .build();
        }
    }

    public static OrderDetailsResponse toDto(Order order, ShopInfoDto shopInfo, List<OrderItemDto> orderItems, PaymentDto payment) {
        return OrderDetailsResponse.builder()
            .orderId(order.getId())
            .orderDate(order.getCreatedAt())
            .shopInfo(shopInfo)
            .orderInfo(OrderInfoDto.toDto(order))
            .orderItems(orderItems)
            .payment(payment)
            .build();
    }
}
