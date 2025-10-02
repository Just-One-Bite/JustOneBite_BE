package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.Item;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ItemReponse(
    UUID itemId,
    String name,
    int price,
    String image,
    boolean isHidden
) {
    public static ItemReponse from(Item item) {
        return ItemReponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .isHidden(item.isHidden())
            .build();
    }
}
