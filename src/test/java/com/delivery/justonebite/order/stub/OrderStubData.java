package com.delivery.justonebite.order.stub;

import static org.mockito.BDDMockito.given;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse.OrderInfoDto;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.mockito.Mockito;

public class OrderStubData {

    public static class MockData {
        private static User mockUser(Long userId) {
            User user = Mockito.mock(User.class);
            given(user.getId()).willReturn(userId);
            return user;
        }

        private static Shop mockShop(UUID shopId, String shopName) {
            Shop shop = Mockito.mock(Shop.class);
            given(shop.getId()).willReturn(shopId);
            given(shop.getName()).willReturn(shopName);
            return shop;
        }

        public static Order getMockOrder(UUID orderId, Long userId, UUID shopId) {
            // 의존성
            User mockUser = mockUser(userId);
            Shop mockShop = mockShop(shopId, "Stub Shop Name");

            return Order.create(
                mockUser,
                mockShop,
                "서울시 종로구 사직로 125길 00빌딩",
                "010-1234-5678",
                "마라탕 외 1건",
                25000,
                OrderStatus.PENDING,
                "단무지 빼주세요",
                "벨 누르지 마세요"
            );
        }

        public static OrderInfoDto getMockOrderInfoDto(Order order) {
            return OrderInfoDto.toDto(getMockOrder(order.getId(), order.getCustomer().getId(), order.getShop().getId()));
        }

        public static List<OrderItemDto> getMockOrderItemsDto() {
            return List.of(
                new OrderItemDto(
                    UUID.randomUUID(),
                    1,
                    20000
                ),
                new OrderItemDto(
                    UUID.randomUUID(),
                    1,
                    15000
                )
            );
        }
    }

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

    public static OrderDetailsResponse getOrderDetailsResponse(UUID orderId, Long userId, UUID shopId) {

        Order mockOrder = MockData.getMockOrder(orderId, userId, shopId);

        return new OrderDetailsResponse(
            orderId,
            LocalDateTime.now().minusMinutes(30),
            MockData.getMockOrderInfoDto(mockOrder),
            MockData.getMockOrderItemsDto(),
            null // TODO: 추후 수정 예정
        );
    }

    public static String getUpdateOrderStatusRequest() {
        return """
            {
            	"newStatus" : "ORDER_ACCEPTED"
            }
            """;
    }
}
