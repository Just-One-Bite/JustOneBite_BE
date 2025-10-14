package com.delivery.justonebite.shop.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record ShopUpdateRequest(
        @NotBlank(message = "가게 이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phone_number,

        @NotBlank(message = "운영 시간은 필수 입력값입니다.")
        String operating_hour,

        String description,
        List<String> categories
) {}
