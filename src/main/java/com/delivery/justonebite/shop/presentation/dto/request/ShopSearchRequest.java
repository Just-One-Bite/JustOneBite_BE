package com.delivery.justonebite.shop.presentation.dto.request;

import lombok.Builder;

@Builder
public record ShopSearchRequest(
        String q,
        int page,
        int size,
        String sortBy,
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

        //오름차순 / 내림차순 정렬(기본은 내림차순)
        String validDirection = (direction == null || direction.isBlank())
                ? "DESC"
                : direction.trim().toUpperCase(); // ← trim() 추가

        return ShopSearchRequest.builder()
                .q(q)
                .page(page != null ? page : 0)
                .size(validSize)
                .sortBy(validSortBy)
                .direction(validDirection)
                .build();
    }
}
