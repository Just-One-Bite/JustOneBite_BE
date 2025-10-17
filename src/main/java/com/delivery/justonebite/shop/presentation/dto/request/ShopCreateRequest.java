package com.delivery.justonebite.shop.presentation.dto.request;

import com.delivery.justonebite.shop.domain.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import java.util.List;

@Schema(description = "가게 등록 요청 DTO")
@Builder
public record ShopCreateRequest(

        @Schema(description = "가게명", example = "맛있는 치킨집")
        @NotBlank(message = "가게 이름은 필수 입력값입니다.")
        @Size(max = 50, message = "가게 이름은 50자를 초과할 수 없습니다.")
        String name,

        @Schema(description = "사업자 등록번호 (숫자와 하이픈만 허용)", example = "123-45-67890")
        @NotBlank(message = "사업자 등록번호는 필수 입력값입니다.")
        @Pattern(regexp = "^[0-9\\-]+$", message = "사업자 등록번호는 숫자와 하이픈만 허용합니다.")
        @Size(max = 50)
        String registrationNumber,

        @Schema(description = "도시 대분류 (시/도)", example = "서울특별시")
        @NotBlank(message = "도시 대분류(시/도)는 필수 입력값입니다.")
        @Size(max = 20)
        String province,

        @Schema(description = "도시 중분류 (시/군/구)", example = "강남구")
        @NotBlank(message = "도시 중분류(시/군/구)는 필수 입력값입니다.")
        @Size(max = 20)
        String city,

        @Schema(description = "도시 소분류 (읍/면/동)", example = "역삼동")
        @NotBlank(message = "도시 소분류(읍/면/동)는 필수 입력값입니다.")
        @Size(max = 20)
        String district,

        @Schema(description = "상세 주소", example = "테헤란로 123, 2층")
        @NotBlank(message = "상세 주소는 필수 입력값입니다.")
        @Size(max = 255)
        String address,

        @Schema(description = "가게 전화번호 (숫자와 하이픈만 허용)", example = "02-123-4567")
        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        @Pattern(regexp = "^[0-9\\-]+$", message = "전화번호는 숫자와 하이픈만 허용합니다.")
        @Size(max = 20)
        String phoneNumber,

        @Schema(description = "영업시간", example = "매일 10:00 ~ 22:00")
        @NotBlank(message = "영업시간은 필수 입력값입니다.")
        @Size(max = 100)
        String operatingHour,

        @Schema(description = "가게 설명", example = "치킨과 맥주의 완벽한 조합!", nullable = true)
        @Size(max = 1000)
        String description,

        @Schema(description = "가게 카테고리 목록 (최소 1개)", example = "[\"치킨\", \"맥주\"]")
        @NotEmpty(message = "최소 한 개의 카테고리를 선택해야 합니다.")
        List<@NotBlank(message = "카테고리 이름은 비어있을 수 없습니다.") @Size(max = 10) String> categories
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
