package com.delivery.justonebite.user.presentation.dto.response;

import com.delivery.justonebite.user.domain.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "주소 등록 응답 DTO")
@Builder
public record RegistAddressResponse(
        @Schema(description = "주소를 등록한 유저의 고유 Id", example = "3")
        Long userId,
        @Schema(description = "주소 고유 Id", example = "1")
        UUID addressId,
        @Schema(description = "주소 대분류(시/도)", example = "서울특별시")
        String province,
        @Schema(description = "주소 중분류(시/군/구)", example = "종로구")
        String city,
        @Schema(description = "주소 소분류(읍/면/동)", example = "광화문")
        String district,
        @Schema(description = "주소(도로명주소)", example = "세종대로 172")
        String address,
        @Schema(description = "대표 주소 여부", example = "true/false")
        boolean isDefault,
        @Schema(description = "생성일시", example = "2025:01:01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "주소를 생성한 생성자", example = "3")
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
