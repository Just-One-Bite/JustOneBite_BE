package com.delivery.justonebite.payment.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentConfirmRequest(
        @NotNull(message = "결제 ID는 필수입니다.")
        UUID paymentId,
        @NotNull(message = "주문 ID는 필수입니다.")
        UUID orderId,
        @Min(value = 1000, message = "총 금액은 1000원 이상이어야 합니다.")
        Integer amount
){ }
