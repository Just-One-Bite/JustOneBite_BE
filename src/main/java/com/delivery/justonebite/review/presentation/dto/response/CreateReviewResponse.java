package com.delivery.justonebite.review.presentation.dto.response;

import com.delivery.justonebite.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateReviewResponse(
        UUID reviewId,
        UUID orderId,
        UUID shopId,
        String content,
        int rating,
        LocalDateTime createdAt,
        Long createdBy
) {
    public static CreateReviewResponse from(Review r) {
        return new CreateReviewResponse(
                r.getReviewId(),
                r.getOrder().getId(),
                r.getShopId(),
                r.getContent(),
                r.getRating(),
                r.getCreatedAt(),
                r.getCreatedBy()
        );
    }

}
