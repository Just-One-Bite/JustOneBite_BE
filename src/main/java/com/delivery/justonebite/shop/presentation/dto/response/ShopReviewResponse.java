package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.data.domain.Page;
import java.util.List;

@Schema(description = "가게 리뷰 목록 조회 응답 DTO")
public record ShopReviewResponse(

        @Schema(description = "전체 리뷰 개수", example = "128")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "13")
        int totalPages,

        @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
        int currentPage,

        @Schema(description = "페이지 크기", example = "10")
        int pageSize,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @ArraySchema(arraySchema = @Schema(description = "리뷰 목록"), schema = @Schema(implementation = ReviewResponse.class))
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

}
