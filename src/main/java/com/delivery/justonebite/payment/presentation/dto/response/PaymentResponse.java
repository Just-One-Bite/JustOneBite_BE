package com.delivery.justonebite.payment.presentation.dto.response;

import java.util.UUID;

public interface PaymentResponse {
    UUID orderId();
    String redirectUrl();
}
