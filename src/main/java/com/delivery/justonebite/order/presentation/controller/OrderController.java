package com.delivery.justonebite.order.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.order.application.service.OrderService;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.presentation.dto.request.CancelOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.CreateOrderRequest;
import com.delivery.justonebite.order.presentation.dto.request.UpdateOrderStatusRequest;
import com.delivery.justonebite.order.presentation.dto.response.CustomerOrderResponse;
import com.delivery.justonebite.order.presentation.dto.response.GetOrderStatusResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderCancelResponse;
import com.delivery.justonebite.order.presentation.dto.response.OrderDetailsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "주문 생성/조회/취소/상태관리 등을 담당합니다.")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "주문 생성 요청",
        description = "사용자(CUSTOMER)가 주문을 요청합니다. 해당 API 요청 권한은 CUSTOMER만 가능합니다.",
//        parameters = {
//            @Parameter(name = "order-id", description = "조회할 주문의 고유 ID", required = true)
//        }
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "주문 성공"
            )
        }
    )
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid  @RequestBody CreateOrderRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        orderService.createOrder(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<CustomerOrderResponse>> getCustomerOrders(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy
    ) {
        Page<CustomerOrderResponse> response = orderService.getCustomerOrders(
            page - 1,
            size,
            sortBy,
            userDetails.getUser()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable(name="order-id") UUID orderId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderDetailsResponse response = orderService.getOrderDetails(orderId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{order-id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable(name = "order-id") UUID orderId,
        @Valid @RequestBody UpdateOrderStatusRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        orderService.updateOrderStatus(orderId, request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{order-id}/status")
    public ResponseEntity<GetOrderStatusResponse> getOrderStatusHistories(@PathVariable(name = "order-id") UUID orderId,
    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetOrderStatusResponse response = orderService.getOrderStatusHistories(orderId,
            userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{order-id}")
    public ResponseEntity<OrderCancelResponse> cancelOrder(@Valid @RequestBody CancelOrderRequest request,
        @PathVariable(name = "order-id") UUID orderId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderCancelResponse response = orderService.cancelOrder(request, orderId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
