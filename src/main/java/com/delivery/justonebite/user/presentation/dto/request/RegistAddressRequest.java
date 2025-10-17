package com.delivery.justonebite.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegistAddressRequest(
        @NotBlank(message = "대분류(시/도)를 입력해주세요")
        String province,
        @NotBlank(message = "증분류(시/군/구)를 입력해주세요")
        String city,
        @NotBlank(message = "소분류(읍/면/동)을 입력해주세요")
        String district,
        @NotBlank(message = "주소(도로명주소)를 입력해주세요")
        String address,
        boolean isDefault
) {
}
