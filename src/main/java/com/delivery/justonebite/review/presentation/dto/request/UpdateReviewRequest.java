package com.delivery.justonebite.review.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
@Schema(description = "리뷰 업데이트 요청 DTO")
public record UpdateReviewRequest(
        @Size(max = 300, message = "내용은 최대 300자입니다.")
        String content,

        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        Integer rating
) {

}
