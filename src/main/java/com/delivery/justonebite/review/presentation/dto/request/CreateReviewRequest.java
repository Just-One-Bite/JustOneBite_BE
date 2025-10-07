package com.delivery.justonebite.review.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record CreateReviewRequest(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        UUID orderId,
        UUID shopId,
        @NotBlank(message = "내용은 필수입니다.")
        String content,
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        int rating
) {

}
