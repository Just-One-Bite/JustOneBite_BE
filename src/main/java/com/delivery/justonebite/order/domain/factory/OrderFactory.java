package com.delivery.justonebite.order.domain.factory;

import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderFactory {
    // 주문 도메인 객체 생성 및 비즈니스 로직 계산 등 적용하여 Order 객체 반환하기 위한 클래스
    public Order create(
        User user,
        Shop shop,
        String address,
        CreateOrderRequest request,
        Map<UUID, Item> itemMap
    ) {

        // 주문 총 금액
        Integer totalPrice = calculateTotalPrice(request.orderItems(), itemMap);

        // 주문 이름 ex) 마라탕 외 1건
        String orderName = convertToOrderName(request.orderItems(), itemMap);

        // Order 엔티티 생성
        return Order.create(
            user,
            shop,
            address,
            request.userPhoneNumber(),
            orderName,
            totalPrice,
            request.orderRequest(),
            request.deliveryRequest()
        );
    }

    // OrderItem 리스트 생성 및 여러 주문 아이템 한번에 저장하기 위해 (트랜잭션 최적화)
    public List<OrderItem> getOrderItems(Order order,
        List<OrderItemDto> orderItems,
        Map<UUID, Item> itemMap) {
        return orderItems.stream()
            .map(dto -> {
                Item item = itemMap.get(dto.itemId());
                return OrderItem.create(order, item, dto.count());
            })
            .toList();
    }

    // Item별 가격과 count 곱하여 총 금액 계산
    private Integer calculateTotalPrice(List<OrderItemDto> orderItems, Map<UUID, Item> itemMap) {
        return orderItems.stream()
            .mapToInt(dto -> itemMap.get(dto.itemId()).getPrice() * dto.count())
            .sum();
    }

    private String convertToOrderName(List<OrderItemDto> orderItems, Map<UUID, Item> itemMap) {
        String firstItem = itemMap.get(orderItems.getFirst().itemId()).getName();
        int count = orderItems.size() - 1;
        return count > 0 ? firstItem + " 외 " + count + "건" : firstItem;
    }
}
