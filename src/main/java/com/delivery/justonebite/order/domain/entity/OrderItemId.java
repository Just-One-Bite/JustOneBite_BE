package com.delivery.justonebite.order.domain.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// 복합 키 클래스 (Serializable 구현, 생성자, hashCode(), equals())
@Getter
@EqualsAndHashCode
public class OrderItemId implements Serializable {

    private UUID order;
    private UUID item;

    public OrderItemId() {}

    public OrderItemId(UUID order, UUID item) {
        this.order = order;
        this.item = item;
    }
}
