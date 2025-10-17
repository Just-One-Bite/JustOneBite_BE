package com.delivery.justonebite.ai_history.presentation.dto;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "기록의 정보 조회 응답 DTO")
@Builder
public record AiRequestHistoryResponse(
    @Schema(description = "기록 고유 ID", example = "예시: a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d")
    UUID id,

    @Schema(description = "사용 AI 모델", example = "gemini-2.5-flash")
    String model,

    @Schema(description = "AI 요청 프롬프트", example = "집에서 직접 담근 김치를 사용한 김치찜이라는 것을 알려줘")
    String request,

    @Schema(description = "AI 응답", example = "접 담근 김치로 만든 맛있게 매운 김치찜!")
    String response,

    @Schema(description = "생성일자", example = "2025-10-13T10:30:00")
    LocalDateTime createdAt
) {
    public static AiRequestHistoryResponse from(AiRequestHistory aiRequestHistory) {
        return AiRequestHistoryResponse.builder()
            .id(aiRequestHistory.getId())
            .model(aiRequestHistory.getModel())
            .request(aiRequestHistory.getRequest())
            .response(aiRequestHistory.getResponse())
            .createdAt(aiRequestHistory.getCreatedAt())
            .build();
    }
}


