package com.delivery.justonebite.review.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.UUID;

@Schema(description = "리뷰 생성 요청 DTO")
public record CreateReviewRequest(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,
        UUID shopId,
        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 1, max = 300, message = "내용은 300자입니다.")
        String content,
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        Integer rating
) {

}
