package com.delivery.justonebite.payment.presentation.controller;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.payment.application.service.PaymentService;
import com.delivery.justonebite.payment.domain.entity.Payment;
import com.delivery.justonebite.payment.domain.entity.PaymentStatus;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentCancelRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentConfirmRequest;
import com.delivery.justonebite.payment.presentation.dto.request.PaymentRequest;
import com.delivery.justonebite.payment.presentation.dto.response.*;
import com.delivery.justonebite.review.presentation.controller.ReviewController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = true)
class PaymentControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    PaymentService paymentService;

    private final UUID PAYMENT_ID = UUID.randomUUID();
    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID LAST_TRANSACTION_KEY = UUID.randomUUID();

    @Test
    @DisplayName("GET /v1/payments/{paymentId} - 200 OK + PaymentConfirmResponse 필드 검증")
    void getPaymentById_ok() throws Exception {
        var now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .paymentId(PAYMENT_ID)
                .lastTransactionId(LAST_TRANSACTION_KEY)
                .orderId(ORDER_ID)
                .orderName("페퍼로니 피자")
                .status(PaymentStatus.DONE)
                .totalAmount(28000)
                .balanceAmount(0)
                .createdAt(now.minusMinutes(2))
                .approvedAt(now)
                .build();

        given(paymentService.getPaymentById(eq(PAYMENT_ID))).willReturn(payment);

        mvc.perform(get("/v1/payments/{paymentId}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID.toString()))
                .andExpect(jsonPath("$.lastTransactionKey").value(LAST_TRANSACTION_KEY.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.orderName").value("페퍼로니 피자"))
                .andExpect(jsonPath("$.status").value(PaymentStatus.DONE.name()))
                .andExpect(jsonPath("$.totalAmount").value(28000))
                .andExpect(jsonPath("$.balanceAmount").value(28000))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.approvedAt").isNotEmpty());
    }

    @Test
    @DisplayName("GET /v1/payments/order/{orderId} - 200 OK + PaymentConfirmResponse 필드 검증")
    void getPaymentByOrderId_ok() throws Exception {
        var now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .paymentId(PAYMENT_ID)
                .lastTransactionId(LAST_TRANSACTION_KEY)
                .orderId(ORDER_ID)
                .orderName("페퍼로니 피자")
                .status(PaymentStatus.DONE)
                .totalAmount(28000)
                .balanceAmount(0)
                .createdAt(now.minusMinutes(2))
                .approvedAt(now)
                .build();

        given(paymentService.getPaymentByOrderId(eq(ORDER_ID))).willReturn(payment);

        mvc.perform(get("/v1/payments/order/{orderId}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID.toString()))
                .andExpect(jsonPath("$.lastTransactionKey").value(LAST_TRANSACTION_KEY.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.orderName").value("페퍼로니 피자"))
                .andExpect(jsonPath("$.status").value(PaymentStatus.DONE.name()))
                .andExpect(jsonPath("$.totalAmount").value(28000))
                .andExpect(jsonPath("$.balanceAmount").value(28000))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.approvedAt").isNotEmpty());
    }

    @Test
    @DisplayName("POST /v1/payments/request - 200 OK + PaymentSuccessResponse 필드 검증")
    void requestPayment_ok() throws Exception {

        UUID orderId = ORDER_ID;
        UUID paymentId = PAYMENT_ID;
        int amount = 28000;

        PaymentSuccessResponse resp = new PaymentSuccessResponse(orderId, paymentId, amount);
        given(paymentService.requestPayment(any(PaymentRequest.class))).willReturn(resp);

        String body = """
                {
                  "orderId": "%s",
                  "orderName": "페퍼로니 피자",
                  "amount": 28000,
                  "status": true
                }
                """.formatted(ORDER_ID);

        mvc.perform(post("/v1/payments/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirectUrl").value(
                        "http://localhost:8080/payments/success?paymentType=NORMAL&orderId=" + orderId +
                        "&paymentId=" + paymentId + "&amount=" + amount))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID.toString()))
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    @DisplayName("POST /v1/payments/request - 200 OK + PaymentFailResponse 필드 검증")
    void requestPayment_fail() throws Exception {

        UUID orderId = ORDER_ID;
        String code = "PAY_PROCESS_CANCELED";
        String message = "사용자에 의해 결제가 취소되었습니다.";

        PaymentFailResponse resp = new PaymentFailResponse(orderId, code, message);
        given(paymentService.requestPayment(any(PaymentRequest.class))).willReturn(resp);

        String body = """
            {
              "orderId": "%s",
              "orderName": "페퍼로니 피자",
              "amount": 28000,
              "status": false
            }
            """.formatted(orderId);

        mvc.perform(post("/v1/payments/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirectUrl").value(
                        "http://localhost:8080/payments/fail?code=" + code +
                                "&message=" + message + "&orderId=" + orderId))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    @DisplayName("POST /v1/payments/confirm - 200 OK + PaymentConfirmResponse 필드 검증")
    void confirmPayment_ok() throws Exception {
        var now = LocalDateTime.now();

        PaymentConfirmResponse resp = new PaymentConfirmResponse(
                PAYMENT_ID,
                LAST_TRANSACTION_KEY,
                ORDER_ID,
                "페퍼로니 피자 세트",
                PaymentStatus.DONE,
                28000,
                0,
                now.minusMinutes(2), // 임의로 시간의 차이를 둠
                now
        );

        given(paymentService.confirmPayment(any(PaymentConfirmRequest.class))).willReturn(resp);

        String body = """
                {
                  "paymentId": "%s",
                  "orderId": "%s",
                  "amount": 28000
                }
                """.formatted(PAYMENT_ID,ORDER_ID);

        mvc.perform(post("/v1/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID.toString()))
                .andExpect(jsonPath("$.lastTransactionKey").value(LAST_TRANSACTION_KEY.toString()))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.orderName").value("페퍼로니 피자"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.totalAmount").value(28000))
                .andExpect(jsonPath("$.balanceAmount").value(28000));
    }

    // 결제 승인 요청 시 status 오류
    @Test
    @DisplayName("POST /v1/payments/confirm - 409 Conflict")
    void confirmPayment_invalidStatus() throws Exception {
        given(paymentService.confirmPayment(any(PaymentConfirmRequest.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_PAYMENT_STATUS));

        String body = """
            {
                "paymentId": "%s",
                "orderId": "%s",
                "amount": 28000
            }
            """.formatted(PAYMENT_ID, ORDER_ID);

        mvc.perform(post("/v1/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("승인이 불가한 결제 상태입니다."));
    }

    @Test
    @DisplayName("POST /v1/payments/cancel - 200 OK + PaymentCancelResponse 필드 검증")
    void cancelPayment_ok() throws Exception {
        var now = LocalDateTime.now();

        PaymentCancelResponse resp = new PaymentCancelResponse(
                PAYMENT_ID,
                LAST_TRANSACTION_KEY,
                ORDER_ID,
                "페퍼로니 피자",
                28000,
                0,
                PaymentStatus.CANCELED,
                "배달 상태 불량",
                now
        );

        given(paymentService.cancelPayment(any(PaymentCancelRequest.class))).willReturn(resp);

        String body = """
                {
                  "paymentId": "%s",
                  "cancelReason": "배달 상태 불량"
                  "cancelAmount": 28000
                }
                """.formatted(PAYMENT_ID);

        mvc.perform(post("/v1/payments/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(PAYMENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.reason").value("배달 상태 불량"));
    }
}
