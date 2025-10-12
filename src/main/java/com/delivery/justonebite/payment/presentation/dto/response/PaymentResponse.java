package com.delivery.justonebite.payment.presentation.dto.response;

import java.util.UUID;

public interface PaymentResponse {
    UUID getOrderId();
    String getRedirectUrl();
}
