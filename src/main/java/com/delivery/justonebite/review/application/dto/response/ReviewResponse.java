package com.delivery.justonebite.review.application.dto.response;

import com.delivery.justonebite.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID reviewId,
        UUID orderId,
        Long userId,
        UUID shopId,
        String content,
        int rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getReviewId(),
                r.getOrder().getId(),
                r.getUserId(),
                r.getShopId(),
                r.getContent(),
                r.getRating(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getCreatedBy()
        );
    }
}
