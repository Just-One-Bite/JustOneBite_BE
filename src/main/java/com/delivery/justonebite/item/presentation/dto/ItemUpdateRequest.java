package com.delivery.justonebite.item.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemUpdateRequest(
    @NotBlank(message = "상품 명은 공백일 수 없습니다.")
    String name,
    @Min(value = 10, message = "상품 금액은 10원 이상이어야 합니다.")
    int price,
    String image,
    String description,
    @JsonProperty("ai_generated")
    boolean aiGenerated
) {

}
