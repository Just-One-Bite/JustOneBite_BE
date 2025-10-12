package com.delivery.justonebite.payment.presentation.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class PaymentCreateRequest {
    private UUID orderId;
    private String orderName;
    private UUID shopId;
    private String method;
    private BigDecimal totalAmount;

}
