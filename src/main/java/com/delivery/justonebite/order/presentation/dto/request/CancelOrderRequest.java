package com.delivery.justonebite.order.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CancelOrderRequest(
    @Schema(description = "주문 변경할 상태값", example = "ORDER_CANCELLED")
    @NotNull(message = "상태값은 필수입니다.")
    String status
) {
}
