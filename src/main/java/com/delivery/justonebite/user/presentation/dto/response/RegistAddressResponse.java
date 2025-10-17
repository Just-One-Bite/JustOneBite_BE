package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.Address;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RegistAddressResponse(
        Long userId,
        UUID addressId,
        String province,
        String city,
        String district,
        String address,
        boolean isDefault,
        LocalDateTime createdAt,
        Long createdBy
) {
    public static RegistAddressResponse toDto(Address address) {
        return RegistAddressResponse.builder()
                .userId(address.getUser().getId())
                .addressId(address.getAddressId())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .address(address.getAddress())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .createdBy(address.getCreatedBy())
                .build();
    }
}
