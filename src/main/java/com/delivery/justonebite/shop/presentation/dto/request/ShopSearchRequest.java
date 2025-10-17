package com.delivery.justonebite.shop.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "가게 목록/검색 조회 요청 DTO")
@Builder
public record ShopSearchRequest(

        @Schema(description = "검색어 (가게명, 카테고리명 등)", example = "치킨", nullable = true)
        String q,

        @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
        int page,

        @Schema(description = "한 페이지당 데이터 개수 (10, 30, 50 허용)", example = "10")
        int size,

        @Schema(description = "정렬 기준 (createdAt, averageRating 중 선택)", example = "averageRating")
        String sortBy,

        @Schema(description = "정렬 방향 (ASC 또는 DESC)", example = "DESC")
        String direction
) {
    private static final int DEFAULT_SIZE = 10;

    public static ShopSearchRequest of(String q, Integer page, Integer size, String sortBy, String direction) {
        int validSize = switch (size == null ? DEFAULT_SIZE : size) {
            case 10, 30, 50 -> size;
            default -> DEFAULT_SIZE;
        };

        String validSortBy = switch (sortBy == null ? "createdAt" : sortBy) {
            case "createdAt", "averageRating" -> sortBy;
            default -> "createdAt";
        };

        String validDirection = (direction == null || direction.isBlank())
                ? "DESC"
                : direction.trim().toUpperCase();

        return ShopSearchRequest.builder()
                .q(q)
                .page(page != null ? page : 0)
                .size(validSize)
                .sortBy(validSortBy)
                .direction(validDirection)
                .build();
    }
}
