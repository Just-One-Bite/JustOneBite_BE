package com.delivery.justonebite.item.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ItemDetailResponseDto {
    private UUID itemId;
    private String name;
    private int price;
    private String image;
    private String description;
    private boolean isHidden;
}
