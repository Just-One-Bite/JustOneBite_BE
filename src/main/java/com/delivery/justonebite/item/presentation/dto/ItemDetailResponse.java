package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.Item;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ItemDetailResponse(
    UUID itemId,
    String name,
    int price,
    String image,
    String description,
    boolean isHidden
) {
    public static ItemDetailResponse from(Item item) {
        return ItemDetailResponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .isHidden(item.isHidden())
            .build();
    }
}
