package com.delivery.justonebite.order.domain.entity;

import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.shop.domain.entity.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "h_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    // 복합 키
    @EmbeddedId
    private OrderItemId id;

    @MapsId("order") // OrderItemId의 order 필드와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @MapsId("item") // OrderItemId의 item 필드와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "item_name", nullable = false, length = 50)
    private String itemName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "count", nullable = false)
    private Integer count;

    public OrderItem(Order order, Item item, Integer count) {
        this.order = order;
        this.item = item;
        this.itemName = item.getName();
        this.price = item.getPrice();
        this.count = count;
    }

    public static OrderItem create(Order order, Item item, Integer count) {
        return new OrderItem(order, item, count);
    }
}
