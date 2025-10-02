package com.delivery.justonebite.order.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record OrderItem(
    UUID itemId,
    @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다")
    @Max(value = 100, message = "주문 수량은 최대 100개까지만 주문 가능합니다")
    Integer count
) {

}
