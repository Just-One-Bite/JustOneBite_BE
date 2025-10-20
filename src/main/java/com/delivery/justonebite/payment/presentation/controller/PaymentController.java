package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentCancelRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentConfirmRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentCancelResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentConfirmResponse;
import com.delivery.justonebite.payment.presentation.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Payment API", description = "결제 요청, 승인, 취소 및 조회 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 ID로 조회",
            description = "결제 ID를 통해 결제 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PaymentConfirmResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 결제 내역을 찾을 수 없음")
            }
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentConfirmResponse> getPaymentById(
            @Parameter(description = "결제 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentConfirmResponse.from(payment));
    }

    @Operation(
            summary = "주문 ID로 조회",
            description = "주문 ID를 통해 결제 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PaymentConfirmResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 주문의 결제 내역을 찾을 수 없음")
            }
    )
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentConfirmResponse> getPaymentByOrderId(
            @Parameter(description = "주문 ID", example = "3f7087b1-45a7-4df1-8cc1-385eebf4eac1")
            @PathVariable UUID orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(PaymentConfirmResponse.from(payment));
    }

    // 결제 요청 (프론트에서 requestPayment를 대신해서 작동)
    @Operation(
            summary = "결제 요청 생성",
            description = "새로운 결제 요청을 생성하고 결제 리디렉션 URL을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 요청 성공",
                            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
            }
    )
    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "결제 승인",
            description = "결제 완료를 승인하고 상태를 DONE으로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 승인 성공",
                            content = @Content(schema = @Schema(implementation = PaymentConfirmResponse.class))),
                    @ApiResponse(responseCode = "403", description = "결제 승인 불가 (이미 승인됨 또는 유효시간 초과)"),
                    @ApiResponse(responseCode = "404", description = "결제 정보 없음")
            }
    )
    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
        Object response = paymentService.confirmPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "결제 취소",
            description = "기존 결제를 전액 또는 부분 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 취소 성공",
                            content = @Content(schema = @Schema(implementation = PaymentCancelResponse.class))),
                    @ApiResponse(responseCode = "404", description = "결제 정보 없음"),
                    @ApiResponse(responseCode = "400", description = "취소 금액 또는 상태 오류")
            }
    )
    @PostMapping("/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@Valid @RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = paymentService.cancelPayment(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}