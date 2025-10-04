package com.delivery.justonebite.shop.presentation.controller;

import com.delivery.justonebite.shop.application.service.ShopService;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopCreateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;


    // TODO : user 권한 체크
    @PostMapping
    public ResponseEntity<ShopCreateResponse> createShop(
            @Valid @RequestBody ShopCreateRequest request
    ) {
        Shop shop = shopService.createShop(request,1L);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShopCreateResponse.from(shop));
    }
}
