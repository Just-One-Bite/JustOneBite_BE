package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

// TODO: 빠진 부분 데이터 추가 필요
@Builder
public record OrderDetailsResponse(
    UUID orderId,
    LocalDateTime orderDate,
//    ShopInfoDto shopInfo,
    OrderInfoDto orderInfo,
    List<OrderItemDto> orderItems,
    PaymentDto payment
) {

    @Builder
    public record ShopInfoDto(UUID shopId, String shopName) {
        public static ShopInfoDto toDto(UUID shopId, String shopName) {
            return ShopInfoDto.builder()
                .shopId(shopId)
                .shopName(shopName)
                .build();
        }
    }

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

    @Builder
    public record PaymentDto(
        Integer itemFee,
        Integer deliveryFee,
        Integer totalFee,
        String paymentCard,
        String paymentStatus
    ) {
        // TODO: 추후 연관관계 설정 시 수정 예정
        public static PaymentDto toDto(Integer itemFee,
            Integer deliveryFee,
            Integer totalFee,
            String paymentCard,
            String paymentStatus
        ) {
            return PaymentDto.builder()
                .itemFee(itemFee)
                .deliveryFee(deliveryFee)
                .totalFee(totalFee)
                .paymentCard(paymentCard)
                .paymentStatus(paymentStatus)
                .build();
        }
    }

    public static OrderDetailsResponse toDto(Order order, List<OrderItemDto> orderItems) {
        return OrderDetailsResponse.builder()
            .orderId(order.getId())
            .orderDate(order.getCreatedAt())
//            .shopInfo(ShopInfoDto.toDto())
            .orderInfo(OrderInfoDto.toDto(order))
            .orderItems(orderItems)
//            .payment()
            .build();
    }
}
