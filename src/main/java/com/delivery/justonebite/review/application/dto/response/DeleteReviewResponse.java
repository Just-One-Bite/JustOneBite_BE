package com.delivery.justonebite.review.application.dto.response;

import java.util.UUID;

public record DeleteReviewResponse(
        boolean success,
        String message,
        UUID reviewId
) {
    public static DeleteReviewResponse ok(UUID reviewId) {
        return new DeleteReviewResponse(true, "리뷰가 삭제되었습니다.", reviewId);
    }
}