package com.delivery.justonebite.order.stub;

import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class StubData {

    public static String getCreateOrderRequest() {
        return """
                {
                  "shopId": "%s",
                  "deliveryAddressId": "123e4567-e89b-31d3-a456-426614174002",
                  "userPhoneNumber": "010-1254-5678",
                  "orderRequest": "단무지 빼주세요.",
                  "deliveryRequest": "문 앞에 놓아주세요",
                  "orderItems": [
                    {
                      "itemId": "%s",
                      "count": 1
                    },
                    {
                      "itemId": "%s",
                      "count": 1
                    }
                  ],
                  "totalPrice": 37000
                }
            """;
    }

    public static List<CustomerOrderResponse> getCustomerOrderResponse(UUID orderId) {
        return List.of(
            new CustomerOrderResponse(
                orderId,
                "마라탕웨이",
                OrderStatus.ORDER_ACCEPTED.name(),
                LocalDateTime.now(),
                50000,
                "마라탕 외 2건"
            ),
            new CustomerOrderResponse(
                orderId,
                "가마솥순대국밥",
                OrderStatus.ORDER_ACCEPTED.name(),
                LocalDateTime.now(),
                30000,
                "모듬국밥 외 1건"
            )
        );
    }
}
