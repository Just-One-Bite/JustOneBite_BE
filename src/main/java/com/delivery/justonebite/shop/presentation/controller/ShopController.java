package com.delivery.justonebite.shop.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.shop.application.service.ShopQueryService;
import com.delivery.justonebite.shop.application.service.ShopReviewService;
import com.delivery.justonebite.shop.application.service.ShopService;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.*;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Shop API", description = "가게 조회/등록/수정/삭제 및 검색기능")
@RestController
@RequestMapping("/v1/shops")
@RequiredArgsConstructor
public class ShopController {


    private final ShopService shopService;
    private final ShopQueryService shopQueryService;
    private final ShopReviewService shopReviewService;


    //가게 등록
    @Operation(
            summary = "가게 등록 요청 API",
            description = "OWNER 권한 사용자가 신규 가게 등록을 요청합니다. 동일한 사업자등록번호로는 중복 등록이 불가합니다.",
            security = @SecurityRequirement(name = "Authorization")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가게 등록 요청이 성공적으로 처리되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청 본문에 유효하지 않은 값이 포함되었거나 카테고리 목록이 비어있습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)"),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (OWNER만 등록 가능)"),
            @ApiResponse(responseCode = "409", description = "이미 동일한 사업자등록번호 또는 가게가 존재합니다.")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<ShopCreateResponse> createShop(
            @Valid @RequestBody ShopCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        var shop = shopService.createShop(request, user.getId(), user.getUserRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShopCreateResponse.from(shop));
    }


    //전체 가게 조회
    @Operation(summary = "전체 가게 목록 조회 API", description = "검색어(q) 및 정렬 기준(sortBy, direction)에 따라 전체 가게를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 또는 정렬 기준이 유효하지 않습니다.")
    })
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @GetMapping
    public ResponseEntity<Page<ShopSearchResponse>> searchShops(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        ShopSearchRequest request = ShopSearchRequest.of(q, page, size, sortBy, direction);
        Page<ShopSearchResponse> response = shopQueryService.searchShops(request);
        return ResponseEntity.ok(response);
    }


    //가게 상세 조회
    @Operation(summary = "가게 상세 조회 API", description = "shopId를 기준으로 가게 상세 정보 및 평균 평점을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 상세 정보를 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게 ID입니다.")
    })
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopDetailResponse> getShopDetail(
            @PathVariable("shopId") UUID shopId
    ) {
        ShopDetailResponse response = shopQueryService.getShopDetail(shopId);
        return ResponseEntity.ok(response);
    }


    //가게 정보 수정 -> 필드 일부 수정 가능
    @Operation(summary = "가게 정보 수정 API", description = "OWNER, MANAGER, MASTER 권한 사용자가 가게 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 정보가 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청 본문 값이 유효하지 않거나 존재하지 않는 카테고리입니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)"),
            @ApiResponse(responseCode = "403", description = "수정 권한이 없거나 본인 소유의 가게가 아닙니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게 ID입니다.")
    })
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @PatchMapping("/{shopId}")
    public ResponseEntity<ShopUpdateResponse> updateShop(
            @PathVariable("shopId") UUID shopId,
            @Valid @RequestBody ShopUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        ShopUpdateResponse updated = shopService.updateShop(request, shopId, user.getId(), user.getUserRole());
        return ResponseEntity.ok(updated);
    }

    //가게 삭제 요청
    @Operation(summary = "가게 삭제 요청 API", description = "OWNER, MANAGER, MASTER 권한 사용자가 자신의 가게를 삭제 요청합니다. (Soft Delete 방식)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게가 성공적으로 삭제 요청되었습니다."),
            @ApiResponse(responseCode = "400", description = "삭제 조건이 충족되지 않았습니다. (미완료 주문 존재 등)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다."),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없거나 본인 가게가 아닙니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게 ID입니다."),
            @ApiResponse(responseCode = "409", description = "이미 승인 대기 중인 삭제 요청입니다.")
    })
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{shopId}")
    public ResponseEntity<ShopDeleteResponse> deleteShop(
            @PathVariable("shopId") UUID shopId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        ShopDeleteResponse response = shopService.deleteShop(shopId, user.getId(), user.getUserRole());
        return ResponseEntity.ok(response);
    }



    //가게별 주문 목록 조회
    @Operation(summary = "가게별 주문 목록 조회 API", description = "OWNER, MANAGER, MASTER 권한 사용자가 특정 가게의 주문 목록을 조회합니다. OWNER는 자신의 가게만 조회 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 주문 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다."),
            @ApiResponse(responseCode = "403", description = "조회 권한이 없거나 본인의 가게가 아닙니다."),
            @ApiResponse(responseCode = "404", description = "가게 또는 주문 상태 정보를 찾을 수 없습니다.")
    })
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @GetMapping("/{shopId}/orders")
    public ResponseEntity<ShopOrderResponse> getShopOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("shopId") UUID shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        User user = userDetails.getUser();
        ShopOrderResponse response = shopService.getOrdersByShop(shopId, user, page, size, sortBy);
        return ResponseEntity.ok(response);
    }


    // 가게 리뷰 조회
    @Operation(summary = "가게 리뷰 목록 조회 API", description = "shopId 기준으로 해당 가게의 리뷰 목록을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가게 ID입니다.")
    })
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @GetMapping("/{shopId}/reviews")
    public ResponseEntity<ShopReviewResponse> getShopReviews(
            @PathVariable("shopId") UUID shopId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortDirection, sortBy));

        ShopReviewResponse body = shopReviewService.getReviewsByShop(shopId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

}
