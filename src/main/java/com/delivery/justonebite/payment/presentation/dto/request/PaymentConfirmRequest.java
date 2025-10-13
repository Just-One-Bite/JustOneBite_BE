package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    private UUID paymentId;
    private UUID orderId;
    private Integer amount;
}
