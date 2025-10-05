package com.delivery.justonebite.order.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusRequest(
    @NotNull(message = "상태값은 필수입니다")
    @Size(max = 20, message = "최대 20자까지만 입력 가능합니다")
    String newStatus
) {

}
