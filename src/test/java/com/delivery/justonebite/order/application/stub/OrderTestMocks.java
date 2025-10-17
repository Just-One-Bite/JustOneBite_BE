package com.delivery.justonebite.order.application.stub;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import com.delivery.justonebite.order.domain.entity.OrderItem;
import com.delivery.justonebite.order.domain.entity.OrderItemId;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderTestMocks {
    public static Shop mockShop(UUID shopId) {
        Shop shop = mock(Shop.class);
        lenient().doReturn(shopId).when(shop).getId();
        lenient().doReturn("테스트샵").when(shop).getName();
        return shop;
    }

    public static Item mockItem(UUID itemId, int price) {
        Item item = mock(Item.class);
        lenient().doReturn(itemId).when(item).getItemId();
        lenient().doReturn(price).when(item).getPrice();
        lenient().doReturn("테스트상품").when(item).getName();
        return item;
    }

    public static Order mockOrder(UUID orderId, User user, int totalPrice) {
        Shop shop = mock(Shop.class);
        Order order = mock(Order.class);
        lenient().doReturn(orderId).when(order).getId();
        lenient().doReturn(shop).when(order).getShop();
        lenient().doReturn(user).when(order).getCustomer();
        lenient().doReturn(totalPrice).when(order).getTotalPrice();
        lenient().doReturn(OrderStatus.PENDING).when(order).getCurrentStatus();
        return order;
    }

    public static OrderItemId mockOrderItemId(Item item) {
        OrderItemId orderItemId = mock(OrderItemId.class);
        lenient().doReturn(item.getItemId()).when(orderItemId).getItem();
        return orderItemId;
    }

    public static OrderItem mockOrderItem(UUID itemId, int price) {
        Item item = mockItem(itemId, price);
        OrderItemId orderItemId = mockOrderItemId(item);
        OrderItem orderItem = mock(OrderItem.class);
        lenient().doReturn(orderItemId).when(orderItem).getId();
        lenient().doReturn(1).when(orderItem).getCount();
        lenient().doReturn(price).when(orderItem).getPrice();
        return orderItem;
    }

    public static OrderHistory mockOrderHistory(Order order, OrderStatus status) {
        OrderHistory history = mock(OrderHistory.class);
        lenient().doReturn(UUID.randomUUID()).when(history).getId();
        lenient().doReturn(order).when(history).getOrder();
        lenient().doReturn(status).when(history).getStatus();
        lenient().doReturn(LocalDateTime.now()).when(history).getCreatedAt();
        return history;
    }
}
