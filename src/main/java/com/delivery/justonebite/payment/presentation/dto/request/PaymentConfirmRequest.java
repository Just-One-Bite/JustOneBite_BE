package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String paymentId;
    private String orderId;
    private Integer amount;
}
