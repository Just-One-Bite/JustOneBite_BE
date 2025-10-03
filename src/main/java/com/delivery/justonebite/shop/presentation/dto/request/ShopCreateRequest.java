package com.delivery.justonebite.shop.presentation.dto.request;

import com.delivery.justonebite.shop.domain.entity.Shop;
import lombok.Builder;

import java.util.List;

@Builder
public record ShopCreateRequest(
        String name,
        String registrationNumber,
        String province,
        String city,
        String district,
        String address,
        String phoneNumber,
        String operatingHour,
        String description,
        List<String> categories
) {
    public Shop toEntity(Long ownerId, Long userId) {
        return Shop.builder()
                .owner(ownerId)
                .name(this.name)
                .registrationNumber(this.registrationNumber)
                .province(this.province)
                .city(this.city)
                .district(this.district)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .operatingHour(this.operatingHour)
                .description(this.description)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }
}
