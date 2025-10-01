package com.delivery.justonebite.order.presentation.dto.request;

import com.delivery.justonebite.order.presentation.dto.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "가게 ID는 필수입니다")
    UUID shopId,
    @NotNull(message = "배달 주소 ID는 필수입니다")
    UUID deliveryAddressId,
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    String userPhoneNumber,
    String orderRequest,
    String deliveryRequest,
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    @Valid
    List<OrderItem> orderItems
) {

}
