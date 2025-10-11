package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CustomerOrderResponse(
    UUID orderId,
    String shopName,
    String shopImage,
    String orderStatus,
    LocalDateTime orderedDate,
    Integer totalFee,
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
