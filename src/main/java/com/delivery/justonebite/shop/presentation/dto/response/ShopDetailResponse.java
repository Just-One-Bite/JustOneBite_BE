package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.Shop;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record ShopDetailResponse(
        UUID shopId,
        Long ownerId,
        String name,
        String registrationNumber,
        String province,
        String city,
        String district,
        String address,
        String phoneNumber,
        String operatingHour,
        String description,
        BigDecimal averageRating,
        LocalDateTime createdAt,
        Long createdBy,
        LocalDateTime updatedAt,
        Long updatedBy,
        LocalDateTime deletedAt,
        Long deletedBy,
        List<String> categories
) {

    @Builder
    public record CategoryInfo(
            UUID categoryId,
            String categoryName
    ) {
        public static CategoryInfo from(com.delivery.justonebite.shop.domain.entity.Category category) {
            return new CategoryInfo(category.getId(), category.getCategoryName());
        }
    }

    public static ShopDetailResponse from(Shop shop, double avgRating) {
        return ShopDetailResponse.builder()
                .shopId(shop.getId())
                .ownerId(shop.getOwnerId())
                .name(shop.getName())
                .registrationNumber(shop.getRegistrationNumber())
                .province(shop.getProvince())
                .city(shop.getCity())
                .district(shop.getDistrict())
                .address(shop.getAddress())
                .phoneNumber(shop.getPhoneNumber())
                .operatingHour(shop.getOperatingHour())
                .description(shop.getDescription())
                .averageRating(BigDecimal.valueOf(avgRating))
                .createdAt(shop.getCreatedAt())
                .createdBy(shop.getCreatedBy())
                .updatedAt(shop.getUpdatedAt())
                .updatedBy(shop.getUpdatedBy())
                .deletedAt(shop.getDeletedAt())
                .deletedBy(shop.getDeletedBy())
                .categories(
                        shop.getCategories().stream()
                                .map(sc -> sc.getCategory().getCategoryName())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
