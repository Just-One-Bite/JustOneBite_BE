package com.delivery.justonebite.order.presentation.controller;

import com.delivery.justonebite.order.application.service.OrderService;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    // TODO: 스프링 시큐리티 처리 완료되면 모든 엔드포인트에 @AuthenticationPrincipal 추가

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid  @RequestBody CreateOrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
