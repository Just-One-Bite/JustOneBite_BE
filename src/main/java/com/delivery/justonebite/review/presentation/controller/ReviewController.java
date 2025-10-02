package com.delivery.justonebite.review.presentation.controller;

import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    //TODO : service 완성되면 주석 해제 예정
//    @PostMapping
//    public ResponseEntity<CreateReviewResponse> createReview(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @Valid @RequestBody CreateReviewRequest request
//    ) {
//        CreateReviewResponse review = reviewService.createReview(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(review);
//    }

}
