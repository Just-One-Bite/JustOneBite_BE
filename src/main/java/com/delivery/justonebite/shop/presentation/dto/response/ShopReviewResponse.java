package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public record ShopReviewResponse(
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        List<ReviewResponse> content
) {

    //응답구조 변환
    public static ShopReviewResponse from(Page<ReviewResponse> page) {
        return new ShopReviewResponse(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.getSize(),
                page.hasNext(),
                page.getContent()
        );
    }
}