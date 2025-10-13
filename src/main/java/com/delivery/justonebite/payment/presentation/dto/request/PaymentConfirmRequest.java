package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentConfirmRequest(
        UUID paymentId,
        UUID orderId,
        Integer amount
){ }
