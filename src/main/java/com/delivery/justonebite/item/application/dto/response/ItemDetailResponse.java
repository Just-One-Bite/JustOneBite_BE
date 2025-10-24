package com.delivery.justonebite.item.application.dto.response;

import com.delivery.justonebite.item.domain.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Schema(description = "상품 상세정보 조회 응답 DTO")
@Builder
public record ItemDetailResponse(
    @Schema(description = "상품 고유 ID", example = "예시: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
    UUID itemId,

    @Schema(description = "상품의 상품명", example = "김치찜")
    String name,

    @Schema(description = "상품의 가격", example = "15000")
    int price,

    @Schema(description = "상품의 image")
    String image,

    @Schema(description = "상품의 설명")
    String description
) {
    public static ItemDetailResponse from(Item item) {
        return ItemDetailResponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .build();
    }
}
