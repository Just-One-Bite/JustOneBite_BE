package com.delivery.justonebite.order.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record CancelOrderRequest(
    @NotNull(message = "상태값은 필수입니다.")
    String status
) {
}
