package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.AcceptStatus;
import com.delivery.justonebite.shop.domain.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.LocalDateTime;

@Schema(description = "가게 등록 요청 결과 응답 DTO")
@Builder
public record ShopCreateResponse(

        @Schema(description = "가게 등록 승인 상태", example = "PENDING")
        AcceptStatus status,

        @Schema(description = "가게 등록 요청 시각", example = "2025-10-15T13:45:30")
        LocalDateTime createdAt
) {
    public static ShopCreateResponse from(Shop shop) {
        return ShopCreateResponse.builder()
                .status(shop.getCreateAcceptStatus())
                .createdAt(shop.getCreatedAt())
                .build();
    }
}
