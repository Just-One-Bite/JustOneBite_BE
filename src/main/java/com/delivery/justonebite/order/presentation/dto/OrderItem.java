package com.delivery.justonebite.order.presentation.dto;

import java.util.UUID;

public record OrderItem(
    UUID itemId,
    Integer count
) {

}
