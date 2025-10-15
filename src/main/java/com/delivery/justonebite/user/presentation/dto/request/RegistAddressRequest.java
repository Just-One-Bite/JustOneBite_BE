package com.delivery.justonebite.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegistAddressRequest(
        @NotBlank()
        String province,
        @NotBlank
        String city,
        @NotBlank
        String district,
        @NotBlank
        String address,
        boolean isDefault
) {
}
