package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.Item;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ItemResponse(
    UUID itemId,
    String name,
    int price,
    String image
) {
    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .build();
    }
}
