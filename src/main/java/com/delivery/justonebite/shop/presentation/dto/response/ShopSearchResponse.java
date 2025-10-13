package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.Shop;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ShopSearchResponse(
        UUID shopId,
        String name,
        List<String> categories,
        double averageRating,
        String address,
        String description,
        String operatingHour
) {
    public static ShopSearchResponse from(Shop shop, double avgRating) {
        return ShopSearchResponse.builder()
                .shopId(shop.getId())
                .name(shop.getName())
                .categories(
                        shop.getCategories().stream()
                                .map(sc -> sc.getCategory().getCategoryName())
                                .toList()
                )
                .averageRating(avgRating)
                .address(shop.getAddress())
                .description(shop.getDescription())
                .operatingHour(shop.getOperatingHour())
                .build();
    }
}
