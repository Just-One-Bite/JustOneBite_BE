package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "주소 등록 요청 DTO")
public record RegistAddressRequest(
        @Schema(description = "주소 대분류(시/도)", example = "서울특별시")
        @NotBlank(message = "대분류(시/도)를 입력해주세요")
        String province,
        @Schema(description = "주소 중분류(시/군/구)", example = "종로구")
        @NotBlank(message = "증분류(시/군/구)를 입력해주세요")
        String city,
        @Schema(description = "주소 소분류(읍/면/동)", example = "광화문")
        @NotBlank(message = "소분류(읍/면/동)을 입력해주세요")
        String district,
        @Schema(description = "주소(도로명주소)", example = "세종대로 172")
        @NotBlank(message = "주소(도로명주소)를 입력해주세요")
        String address,
        @Schema(description = "대표 주소 설정 여부", example = "true/false")
        boolean isDefault
) {
}
