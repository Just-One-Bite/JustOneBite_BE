package com.delivery.justonebite.order.presentation.dto.request;

import com.delivery.justonebite.order.presentation.dto.OrderItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderRequest(
    @Schema(description = "주문할 가게의 고유 ID", example = "4a94970e-1a5c-4e89-9a28-661074e50d0a")
    @NotNull(message = "가게 ID는 필수입니다")
    UUID shopId,

    @Schema(description = "배달 받을 주소의 고유 ID", example = "b7c8d9e0-1f2g-3h4i-5j6k-7l8m9n0o1p2q")
    @NotNull(message = "배달 주소 ID는 필수입니다")
    UUID deliveryAddressId,

    @Schema(description = "사용자 전화번호 (하이픈 포함)", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
    String userPhoneNumber,

    @Schema(description = "주문 시 가게에 전달할 요청사항", example = "고수는 빼주세요.", nullable = true)
    @Size(max = 100, message = "주문 요청사항은 최대 100자까지만 입력 가능합니다")
    String orderRequest,

    @Schema(description = "배달 기사님께 전달할 요청사항", example = "문 앞에 놓아주세요.", nullable = true)
    @Size(max = 100, message = "배달 요청사항은 최대 100자까지만 입력 가능합니다")
    String deliveryRequest,

    @Schema(description = "주문 항목 목록 (최소 1개, 최대 100개)")
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    @Size(max = 100, message = "주문 항목은 최대 100개까지만 주문 가능합니다")
    @Valid
    List<OrderItemDto> orderItems,

    @Schema(description = "클라이언트가 계산한 최종 결제 금액 (서버 검증 목적)", example = "25000")
    @NotNull(message = "총 금액은 필수입니다.")
    @Min(value = 1000, message = "총 금액은 1000원 이상이어야 합니다.")
    Integer totalPrice
) {

}
