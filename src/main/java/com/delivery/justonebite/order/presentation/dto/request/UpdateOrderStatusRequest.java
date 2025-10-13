package com.delivery.justonebite.order.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusRequest(
    @Schema(description = "주문 변경할 상태값", example = "ORDER_ACCEPTED")
    @NotNull(message = "상태값은 필수입니다")
    @Size(max = 20, message = "최대 20자까지만 입력 가능합니다")
    String newStatus
) {

}
