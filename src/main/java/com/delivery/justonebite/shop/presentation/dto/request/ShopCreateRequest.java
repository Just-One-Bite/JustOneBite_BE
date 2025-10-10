package com.delivery.justonebite.shop.presentation.dto.request;

import com.delivery.justonebite.shop.domain.entity.Shop;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record ShopCreateRequest(

        @NotBlank(message = "가게 이름은 필수 입력값입니다.")
        @Size(max = 50, message = "가게 이름은 50자를 초과할 수 없습니다.")
        String name,

        @NotBlank(message = "사업자 등록번호는 필수 입력값입니다.")
        @Pattern(regexp = "^[0-9\\-]+$", message = "사업자 등록번호는 숫자와 하이픈만 허용합니다.")
        String registrationNumber,

        @NotBlank(message = "도시 정보는 필수입니다.")
        String province,

        @NotBlank(message = "도시 정보는 필수입니다.")
        String city,

        @NotBlank(message = "도시 정보는 필수입니다.")
        String district,

        @NotBlank(message = "주소는 필수 입력값입니다.")
        String address,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^[0-9\\-]+$", message = "전화번호는 숫자와 하이픈만 허용합니다.")
        String phoneNumber,

        @NotBlank(message = "영업시간은 필수입니다.")
        @Size(max = 100, message = "영업시간은 최대 100자까지 입력 가능합니다.")
        String operatingHour,

        @Size(max = 1000, message = "가게 설명은 1000자를 초과할 수 없습니다.")
        String description,

        @NotEmpty(message = "최소 한 개의 카테고리를 선택해야 합니다.")
        List<@NotBlank(message = "카테고리 이름은 비어있을 수 없습니다.") String> categories
) {
        public Shop toEntity(Long userId) {
                return Shop.builder()
                        .ownerId(userId)
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
