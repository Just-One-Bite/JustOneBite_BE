package com.delivery.justonebite.order.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "주문 취소 요청 DTO")
public record CancelOrderRequest(
    @Schema(description = "주문 변경할 상태값", example = "ORDER_CANCELLED")
    @NotNull(message = "상태값은 필수입니다.")
    String status,

    @NotNull(message = "결제 ID는 필수입니다.")
    UUID paymentKey,

    @NotBlank(message = "결제 취소 이유는 필수입니다.")
    String cancelReason,

    @Min(value = 100, message = "결제 취소 금액은 100원 이상이어야 합니다.")
    Integer cancelAmount
) {
}
