package com.delivery.justonebite.order.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * @Tag: API를 그룹화하는 데 사용 (Controller 레벨)
     * @Operation : 각 API 메서드에 대한 설명 추가하는 어노테이션 (설명, 요약)
     * @Parameter: 메서드의 인자(경로 변수, 쿼리 파라미터)에 대한 설명을 정의
     * @RequestBody: 요청 본문의 DTO 구조를 문서화 (@Schema와 함께 사용)
     * @ApiResponse: HTTP 응답 코드(200, 201, 400 등)별 상세 설명과 반환될 DTO 구조를 정의
     * @PreAuthorize("hasRole('CUSTOMER')") : Spring Security 인증 토큰 및 사용자 역할 검증
     */
    @Operation(
        summary = "주문 생성 요청 API",
        description = "사용자(CUSTOMER)가 주문을 요청합니다. 해당 API 요청 권한은 CUSTOMER만 가능합니다.",
//        parameters = {
//            @Parameter(name = "order-id", description = "조회할 주문의 고유 ID", required = true)
//        }
        security = @SecurityRequirement(name = "Authorization"),
        responses = {
            @ApiResponse(responseCode = "201", description = "주문 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "주문할 가게 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "요청한 총 금액이 서버의 총 금액과 일치하지 않습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (ROLE_CUSTOMER 아님)", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid  @RequestBody CreateOrderRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        orderService.createOrder(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
        summary = "주문 목록 요청 API",
        description = "사용자(CUSTOMER)가 주문 목록을 페이지 단위로 요청합니다. 해당 API 요청 권한은 CUSTOMER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "page", description = "조회할 목록의 페이지 번호", required = true),
            @Parameter(name = "size", description = "페이지 당 조회 개수", required = true),
            @Parameter(name = "sort-by", description = "주문 생성 시점 기준 정렬", required = true),
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "주문 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (ROLE_CUSTOMER 아님)", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasRole('CUSTOMER')")
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

    @Operation(
        summary = "주문 단건 상세정보 조회 요청 API",
        description = "사용자(CUSTOMER)가 주문 상세정보를 요청합니다. 해당 API 요청 권한은 CUSTOMER만 가능합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            // @PathVariable을 @Parameter 배열 내에 포함
            @Parameter(name = "order-id", description = "조회할 주문의 고유 ID", required = true, example = "예시: a1b2c3d4-e5f6-7890-a1b2-c3d4e5f67890"),
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "주문 상세정보 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "주문 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json")),
        }
    )
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
