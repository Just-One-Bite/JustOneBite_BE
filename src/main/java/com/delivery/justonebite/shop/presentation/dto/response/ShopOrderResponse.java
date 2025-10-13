package com.delivery.justonebite.shop.presentation.dto.response;

import java.util.List;

public record ShopOrderResponse(
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        List<OrderSummary> content
) {
    public static ShopOrderResponse from(org.springframework.data.domain.Page<OrderSummary> page) {
        return new ShopOrderResponse(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.getSize(),
                page.hasNext(),
                page.getContent()
        );
    }

    // 내부 static record: 주문 하나하나의 정보 --> 응답 api 구조 편의성
    public static record OrderSummary(
            String orderId,
            String shopName,
            String orderStatus,
            java.time.LocalDateTime orderedDate,
            Integer totalFee,
            String itemName
    ) {
        public static OrderSummary of(
                com.delivery.justonebite.order.domain.entity.Order order,
                com.delivery.justonebite.order.domain.enums.OrderStatus status,
                List<String> itemNames
        ) {
            return new OrderSummary(
                    order.getId().toString(),
                    order.getShop().getName(),
                    status.name(),
                    order.getCreatedAt(),
                    order.getTotalPrice(),
                    String.join(", ", itemNames)
            );
        }
    }
}
