package com.delivery.justonebite.review.application.dto.response;

import java.util.UUID;

public record RestoreReviewResponse(
        boolean success,
        String message,
        UUID reviewId
) {
    public static RestoreReviewResponse ok(UUID reviewId) {
        return new RestoreReviewResponse(true, "리뷰가 복구되었습니다.", reviewId);
    }
}