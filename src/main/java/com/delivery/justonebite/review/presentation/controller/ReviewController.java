package com.delivery.justonebite.review.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.request.UpdateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.DeleteReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.RestoreReviewResponse;

import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<CreateReviewResponse> createReview(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        CreateReviewResponse body =
                reviewService.createReview(principal.getUserId(), principal.getUserRole(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getOne(@PathVariable UUID id) {
        ReviewResponse body = reviewService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getByShopParam(
            @RequestParam("shopId") UUID shopId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ReviewResponse> body = reviewService.getByShop(shopId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable("id") UUID id,
                                                 @AuthenticationPrincipal UserDetailsImpl principal,
                                                 @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse body = reviewService.update(
                id,
                principal.getUserId(),
                principal.getUserRole(),
                request
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteReviewResponse> softDelete(@PathVariable("id") UUID id,
                                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        reviewService.softDelete(id, principal.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(DeleteReviewResponse.ok(id));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<RestoreReviewResponse> restore(@PathVariable("id") UUID id,
                                                         @AuthenticationPrincipal UserDetailsImpl principal) {
        reviewService.restore(id, principal.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(RestoreReviewResponse.ok(id));
    }
}
