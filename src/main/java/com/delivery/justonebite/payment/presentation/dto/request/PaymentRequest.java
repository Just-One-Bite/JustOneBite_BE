package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentRequest(
        UUID orderId,
        String orderName,
        Integer amount
) { }
