package com.delivery.justonebite.item.presentation.dto.response;

import com.delivery.justonebite.item.domain.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Owner 대상 상품 상세정보 조회 응답 DTO")
@Builder
public record ItemOwnerDetailResponse(
    @Schema(description = "상품 고유 ID", example = "예시: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
    UUID itemId,

    @Schema(description = "상품의 상품명", example = "김치찜")
    String name,

    @Schema(description = "상품의 가격", example = "15000")
    int price,

    @Schema(description = "상품의 이미지", example = "image")
    String image,

    @Schema(description = "상품의 설명", example = "맛있는 김치찜")
    String description,

    @Schema(description = "상품의 숨김 여부", example = "false")
    boolean isHidden,

    @Schema(description = "상품의 생성일시", example = "2025-10-13T10:30:00")
    LocalDateTime createdAt,

    @Schema(description = "상품의 생성자", example = "1")
    Long createdBy,

    @Schema(description = "상품의 수정일시", example = "2025-10-13T10:30:00")
    LocalDateTime updatedAt,

    @Schema(description = "상품의 수정자", example = "1")
    Long updatedBy,

    @Schema(description = "상품의 삭제일시", example = "2025-10-13T10:30:00")
    LocalDateTime deletedAt,

    @Schema(description = "상품의 삭제자", example = "1")
    Long deletedBy
) {
    public static ItemOwnerDetailResponse from(Item item) {
        return ItemOwnerDetailResponse.builder()
            .itemId(item.getItemId())
            .name(item.getName())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .isHidden(item.isHidden())
            .createdAt(item.getCreatedAt())
            .createdBy(item.getCreatedBy())
            .updatedAt(item.getUpdatedAt())
            .updatedBy(item.getUpdatedBy())
            .deletedAt(item.getDeletedAt())
            .deletedBy(item.getDeletedBy())
            .build();
    }
}
