package com.delivery.justonebite.payment.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCancelRequest(
        @NotNull(message = "결제 ID는 필수입니다.")
        UUID paymentKey,
        @NotBlank(message = "결제 취소 이유는 필수입니다.")
        String cancelReason,
        @Min(value = 100, message = "결제 취소 금액은 100원 이상이어야 합니다.")
        Integer cancelAmount
) {}
