package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "가게 정보 수정 응답 DTO")
@Builder
public record ShopUpdateResponse(

        @Schema(description = "수정 완료 시각", example = "2025-10-15T14:21:00")
        LocalDateTime updatedAt,

        @Schema(description = "수정한 사용자 ID", example = "1")
        Long updatedBy
) {}
