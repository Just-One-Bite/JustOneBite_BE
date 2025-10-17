package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.Shop;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "가게 상세 조회 응답 DTO")
@Builder
public record ShopDetailResponse(

        @Schema(description = "가게 ID", example = "b1c2d3e4-f5a6-789b-012c-345d6789ef01")
        UUID shopId,

        @Schema(description = "가게 대표(OWNER) ID", example = "1")
        Long ownerId,

        @Schema(description = "가게명", example = "맛있는 치킨집")
        String name,

        @Schema(description = "사업자 등록번호", example = "123-45-67890")
        String registrationNumber,

        @Schema(description = "도시 대분류 (시/도)", example = "서울특별시")
        String province,

        @Schema(description = "도시 중분류 (시/군/구)", example = "강남구")
        String city,

        @Schema(description = "도시 소분류 (읍/면/동)", example = "역삼동")
        String district,

        @Schema(description = "상세 주소", example = "테헤란로 123, 2층")
        String address,

        @Schema(description = "전화번호", example = "02-123-4567")
        String phoneNumber,

        @Schema(description = "영업시간", example = "매일 10:00 ~ 22:00")
        String operatingHour,

        @Schema(description = "가게 설명", example = "치킨과 맥주의 완벽한 조합!")
        String description,

        @Schema(description = "평균 평점", example = "4.5")
        BigDecimal averageRating,

        @Schema(description = "가게 생성 시각")
        LocalDateTime createdAt,

        @Schema(description = "가게 생성자 ID", example = "1")
        Long createdBy,

        @Schema(description = "마지막 수정 시각")
        LocalDateTime updatedAt,

        @Schema(description = "마지막 수정자 ID", example = "1")
        Long updatedBy,

        @Schema(description = "삭제 시각", nullable = true)
        LocalDateTime deletedAt,

        @Schema(description = "삭제자 ID", nullable = true)
        Long deletedBy,

        @ArraySchema(arraySchema = @Schema(description = "가게의 카테고리 목록"), schema = @Schema(example = "치킨"))
        List<String> categories
) {
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
