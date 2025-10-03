package com.delivery.justonebite.shop.presentation.controller;

import com.delivery.justonebite.shop.application.service.ShopService;
import com.delivery.justonebite.shop.presentation.dto.request.ShopCreateRequest;
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
    @PostMapping("/")
    public ResponseEntity<Void> createShop(@RequestBody ShopCreateRequest request) {
        shopService.createShop(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
