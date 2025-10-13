package com.delivery.justonebite.order.stub;

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
}
