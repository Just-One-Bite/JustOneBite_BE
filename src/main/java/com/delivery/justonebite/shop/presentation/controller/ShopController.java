package com.delivery.justonebite.shop.presentation.controller;

import com.delivery.justonebite.shop.application.service.ShopService;
import com.delivery.justonebite.shop.application.service.ShopQueryService;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopCreateResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDetailResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopQueryService shopQueryService;

    //가게 등록
    @PostMapping
    public ResponseEntity<ShopCreateResponse> createShop(
            @RequestBody ShopCreateRequest request
    ) {
        Long userId = 1L; // TODO: Security에서 가져올 userId
        var shop = shopService.createShop(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShopCreateResponse.from(shop));
    }

    //전체 가게 조회
    @GetMapping
    public ResponseEntity<Page<ShopSearchResponse>> searchShops(
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
}
