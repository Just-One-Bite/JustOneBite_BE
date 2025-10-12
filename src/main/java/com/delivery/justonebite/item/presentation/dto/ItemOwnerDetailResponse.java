package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.Item;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ItemOwnerDetailResponse(UUID itemId,
                                      String name,
                                      int price,
                                      String image,
                                      String description,
                                      boolean isHidden,
                                      LocalDateTime createdAt,
                                      Long createdBy,
                                      LocalDateTime updatedAt,
                                      Long updatedBy,
                                      LocalDateTime deletedAt,
                                      Long deletedBy
) {
    public static ItemOwnerDetailResponse from(Item item) {
        return ItemOwnerDetailResponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .isHidden(item.isHidden())
            .createdAt(item.getCreatedAt())
            .createdBy(item.getCreatedBy())
            .updatedAt(item.getUpdatedAt())
            .updatedBy(item.getUpdatedBy())
            .deletedAt(item.getDeletedAt())
            .deletedBy(item.getDeletedBy())
            .build();
    }
}
