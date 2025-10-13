package com.delivery.justonebite.shop.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.shop.application.service.ShopOrderService;
import com.delivery.justonebite.shop.application.service.ShopService;
import com.delivery.justonebite.shop.application.service.ShopQueryService;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.*;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/shops")
@RequiredArgsConstructor
public class ShopController {


    private final ShopService shopService;
    private final ShopQueryService shopQueryService;
    private final ShopOrderService shopOrderService;

    // 테스트용으로 userId = 1L로 임시 지정
    private User getSafeUser(UserDetailsImpl userDetails) {
        if (userDetails != null) {
            return userDetails.getUser();
        }
        // 테스트 환경용 더미 유저 생성
        return User.builder()
                .id(1L)
                .userRole(UserRole.OWNER)
                .build();
    }


    //가게 등록
    @PostMapping
    public ResponseEntity<ShopCreateResponse> createShop(
            @RequestBody ShopCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = getSafeUser(userDetails);
        var shop = shopService.createShop(request, user.getId(), user.getUserRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShopCreateResponse.from(shop));
    }


    //전체 가게 조회
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
    @GetMapping("/{shop-id}")
    public ResponseEntity<ShopDetailResponse> getShopDetail(
            @PathVariable("shop-id") UUID shopId
    ) {
        ShopDetailResponse response = shopQueryService.getShopDetail(shopId);
        return ResponseEntity.ok(response);
    }


    //가게 정보 수정 -> 필드 일부 수정 가능(주소 등은 x)
    @PatchMapping("/{shop-id}")
    public ResponseEntity<Shop> updateShop(
            @PathVariable("shop-id") UUID shopId,
            @Valid @RequestBody ShopUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = getSafeUser(userDetails);
        Shop updated = shopService.updateShop(request, shopId, user.getId(), user.getUserRole());
        return ResponseEntity.ok(updated);
    }

    //가게 삭제
    @DeleteMapping("/{shop-id}")
    public ResponseEntity<ShopDeleteResponse> deleteShop(
            @PathVariable("shop-id") UUID shopId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = getSafeUser(userDetails);
        ShopDeleteResponse response = shopService.deleteShop(shopId, user.getId(), user.getUserRole());
        return ResponseEntity.ok(response);
    }


    //가게별 주문 목록 조회
    @GetMapping("/{shopId}/orders")
    public ResponseEntity<ShopOrderResponse> getShopOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        User user = getSafeUser(userDetails);
        ShopOrderResponse response = shopOrderService.getOrdersByShop(shopId, user, page, size, sortBy);
        return ResponseEntity.ok(response);
    }

}
