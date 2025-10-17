package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "가게별 주문 목록 조회 응답 DTO")
public record ShopOrderResponse(

        @Schema(description = "전체 주문 개수", example = "42")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,

        @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
        int currentPage,

        @Schema(description = "페이지 크기", example = "10")
        int pageSize,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @ArraySchema(arraySchema = @Schema(description = "주문 목록"), schema = @Schema(implementation = OrderSummary.class))
        List<OrderSummary> content
) {
    public static ShopOrderResponse from(Page<OrderSummary> page) {
        return new ShopOrderResponse(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.getSize(),
                page.hasNext(),
                page.getContent()
        );
    }

    @Schema(description = "단일 주문 요약 정보 DTO")
    @Builder
    public static record OrderSummary(
            @Schema(description = "주문 ID", example = "a1b2c3d4-e5f6-7a8b-9c0d-ef1234567890")
            String orderId,

            @Schema(description = "가게 이름", example = "맛있는 치킨집")
            String shopName,

            @Schema(description = "주문 상태", example = "COMPLETED")
            String orderStatus,

            @Schema(description = "주문 일시", example = "2025-10-15T14:21:00")
            LocalDateTime orderedDate,

            @Schema(description = "총 결제 금액", example = "16500")
            Integer totalFee,

            @Schema(description = "주문 항목 (쉼표로 구분)", example = "후라이드 치킨, 콜라, 치즈볼")
            String itemName
    ) {
        public static OrderSummary of(Order order, OrderStatus status, List<String> itemNames) {
            return new OrderSummary(
                    order.getId().toString(),
                    order.getShop().getName(),
                    status.name(),
                    order.getCreatedAt(),
                    order.getTotalPrice(),
                    String.join(", ", itemNames)
            );
        }
    }
}
