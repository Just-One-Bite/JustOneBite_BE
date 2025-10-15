package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentCancelRequest(
        UUID paymentKey,
        String cancelReason,
        Integer cancelAmount
) {}
