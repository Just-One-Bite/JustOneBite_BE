package com.delivery.justonebite.review.application.dto.response;

public record RatingAggResponse(

        double avgRating,
        long reviewCount
) {

}