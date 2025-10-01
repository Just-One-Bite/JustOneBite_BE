package com.delivery.justonebite.item.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemUpdateRequestDto {
    private String name;
    private int price;
    private String image;
    private String description;
    @JsonProperty("ai_generated")
    private boolean aiGenerated;
}

