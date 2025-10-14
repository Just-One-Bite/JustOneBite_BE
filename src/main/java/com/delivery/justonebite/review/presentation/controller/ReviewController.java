package com.delivery.justonebite.review.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.request.UpdateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.DeleteReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.RestoreReviewResponse;

import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(
            summary = "리뷰 생성 요청 API",
            description = "사용자(CUSTOMER)가 주문 완료 건에 대해 리뷰를 작성합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "리뷰가 성공적으로 생성되었습니다."),
                    @ApiResponse(responseCode = "400", description = "평점 범위 오류 또는 유효하지 않은 요청 본문", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한 없음(CUSTOMER 아님 또는 주문자 불일치)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "409", description = "이미 해당 주문의 리뷰가 존재함", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping
    public ResponseEntity<CreateReviewResponse> createReview(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        CreateReviewResponse body =
                reviewService.createReview(principal.getUserId(), principal.getUserRole(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(
            summary = "리뷰 단건 조회 API",
            description = "리뷰 ID로 단건 리뷰 정보를 조회합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            parameters = {
                    @Parameter(name = "id", description = "조회할 리뷰의 고유 ID", required = true,
                            example = "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getOne(@PathVariable UUID id) {
        ReviewResponse body = reviewService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(
            summary = "가게별 리뷰 목록 조회 API",
            description = "shopId 기준으로 페이지 단위 리뷰 목록을 조회합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            parameters = {
                    @Parameter(name = "shopId", description = "조회 대상 가게의 고유 ID", required = true,
                            example = "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890"),
                    @Parameter(name = "page", description = "페이지 번호(0부터 시작)"),
                    @Parameter(name = "size", description = "페이지 크기"),
                    @Parameter(name = "sort", description = "정렬 기준(예: createdAt,desc)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getByShopParam(
            @RequestParam("shopId") UUID shopId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ReviewResponse> body = reviewService.getByShop(shopId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(
            summary = "리뷰 수정 요청 API",
            description = "사용자(CUSTOMER)가 본인이 작성한 리뷰를 수정합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            parameters = {
                    @Parameter(name = "id", description = "수정할 리뷰의 고유 ID", required = true,
                            example = "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "평점 범위 오류 또는 유효하지 않은 요청 본문", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한 없음(작성자 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content(mediaType = "application/json"))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable("id") UUID id,
                                                 @AuthenticationPrincipal UserDetailsImpl principal,
                                                 @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse body = reviewService.update(
                id,
                principal.getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @Operation(
            summary = "리뷰 소프트 삭제 요청 API",
            description = "사용자(CUSTOMER)가 본인이 작성한 리뷰를 소프트 삭제합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            parameters = {
                    @Parameter(name = "id", description = "삭제할 리뷰의 고유 ID", required = true,
                            example = "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한 없음(작성자 아님/CUSTOMER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content(mediaType = "application/json"))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteReviewResponse> softDelete(@PathVariable("id") UUID id,
                                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        reviewService.softDelete(id, principal.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(DeleteReviewResponse.ok(id));
    }

    @Operation(
            summary = "리뷰 복구 요청 API",
            description = "소프트 삭제된 리뷰를 복구합니다. (작성자만 가능)",
            security = @SecurityRequirement(name = "Authorization"),
            parameters = {
                    @Parameter(name = "id", description = "복구할 리뷰의 고유 ID", required = true,
                            example = "a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "리뷰 복구 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패(JWT 누락/만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한 없음(작성자 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping("/{id}/restore")
    public ResponseEntity<RestoreReviewResponse> restore(@PathVariable("id") UUID id,
                                                         @AuthenticationPrincipal UserDetailsImpl principal) {
        reviewService.restore(id, principal.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(RestoreReviewResponse.ok(id));
    }
}
