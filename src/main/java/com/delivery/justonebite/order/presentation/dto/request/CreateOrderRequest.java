package com.delivery.justonebite.order.presentation.dto.request;

import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100, message = "주문 요청사항은 최대 100자까지만 입력 가능합니다")
    String orderRequest,
    @Size(max = 100, message = "배달 요청사항은 최대 100자까지만 입력 가능합니다")
    String deliveryRequest,
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    @Size(max = 100, message = "주문 항목은 최대 100개까지만 주문 가능합니다")
    @Valid
    List<OrderItemDto> orderItems
) {

}
