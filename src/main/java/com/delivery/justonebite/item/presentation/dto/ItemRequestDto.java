package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.ItemEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ItemRequestDto {
    @JsonProperty("shop_id")
    private String shopId;
    private String name;
    private int price;
    private String image;
    private String description;
    @JsonProperty("ai_generated")
    private boolean aiGenerated;

    // required fix : 당장은 created_by와 같은 컬럼이 참조가 되지 않아 1L로 주입
    public ItemEntity toEntity() {
        return ItemEntity.builder()
            .shopId(UUID.fromString(shopId))
            .name(name)
            .price(price)
            .image(image)
            .description(description)
            .aiGenerated(aiGenerated)
            .isHidden(false)
            .createdBy(1L)
            .updatedBy(1L)
            .build();
    }
}
