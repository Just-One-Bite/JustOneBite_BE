package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.RejectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.LocalDateTime;

@Schema(description = "가게 삭제 요청 결과 응답 DTO")
@Builder
public record ShopDeleteResponse(

        @Schema(description = "가게 삭제 요청 시각", example = "2025-10-15T14:21:00")
        LocalDateTime deletedAt,

        @Schema(description = "삭제 승인 상태", example = "PENDING")
        RejectStatus deleteAcceptStatus
) {}
