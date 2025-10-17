package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.Shop;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Schema(description = "가게 목록 조회 응답 DTO")
@Builder
public record ShopSearchResponse(

        @Schema(description = "가게의 고유 ID", example = "a123b456-c789-012d-ef34-567890ghijkl")
        UUID shopId,

        @Schema(description = "가게 이름", example = "맛있는 치킨집")
        String name,

        @ArraySchema(arraySchema = @Schema(description = "가게의 카테고리 목록"), schema = @Schema(example = "치킨"))
        List<String> categories,

        @Schema(description = "가게 평균 평점", example = "4.7")
        double averageRating,

        @Schema(description = "가게 주소", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "가게 설명", example = "치킨과 맥주의 완벽한 조합!")
        String description,

        @Schema(description = "영업시간", example = "매일 10:00 ~ 22:00")
        String operatingHour
) {
    public static ShopSearchResponse from(Shop shop, double avgRating) {
        return ShopSearchResponse.builder()
                .shopId(shop.getId())
                .name(shop.getName())
                .categories(
                        shop.getCategories().stream()
                                .map(sc -> sc.getCategory().getCategoryName())
                                .toList()
                )
                .averageRating(avgRating)
                .address(shop.getAddress())
                .description(shop.getDescription())
                .operatingHour(shop.getOperatingHour())
                .build();
    }
}
