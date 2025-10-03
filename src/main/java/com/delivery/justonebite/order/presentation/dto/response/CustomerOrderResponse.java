package com.delivery.justonebite.order.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import java.util.UUID;
import lombok.Builder;

// TODO: 빠진 부분 데이터 추가 필요
@Builder
public record CustomerOrderResponse(
    UUID orderId,
//    String shopName,
    String shopImage,
    String orderStatus,
    String orderedDate,
    Integer totalFee,
    String itemName
) {

    public static CustomerOrderResponse from(Order order, OrderStatus status) {
        return CustomerOrderResponse.builder()
            .orderId(order.getId())
//            .shopName(order.getShop().getShopName())
            .orderStatus(status.name())
            .orderedDate(order.getCreatedAt().toString())
            .totalFee(order.getTotalPrice())
            .itemName(order.getOrderName())
            .build();
    }
}
