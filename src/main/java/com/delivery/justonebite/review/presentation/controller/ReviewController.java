package com.delivery.justonebite.review.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

}
