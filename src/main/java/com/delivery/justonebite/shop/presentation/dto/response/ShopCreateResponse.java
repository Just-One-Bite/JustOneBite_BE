package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.AcceptStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ShopCreateResponse(
        AcceptStatus status,
        LocalDateTime createdAt
) {
    public static ShopCreateResponse from(Shop shop) {
        return ShopCreateResponse.builder()
                .status(shop.getCreateAcceptStatus())
                .createdAt(shop.getCreatedAt())
                .build();
    }
}