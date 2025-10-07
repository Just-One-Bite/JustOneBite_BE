package com.delivery.justonebite.item.presentation.dto;

import com.delivery.justonebite.item.domain.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.val;

import java.util.UUID;

@Builder
public record ItemRequest(
    @JsonProperty("shop_id")
    String shopId,
    @NotBlank(message = "상품 명은 공백일 수 없습니다.")
    String name,
    @Min(value = 10, message = "상품 금액은 10원 이상이어야 합니다.")
    int price,
    String image,
    @Size(max = 100)
    String description,
    @JsonProperty("ai_generated")
    boolean aiGenerated
) {
    public Item toItem() {
        return Item.builder()
            .name(name)
            .price(price)
            .image(image)
            .description(description)
            .aiGenerated(aiGenerated)
            .isHidden(false)
            .build();
    }
}