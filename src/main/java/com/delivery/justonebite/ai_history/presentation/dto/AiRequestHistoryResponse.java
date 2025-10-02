package com.delivery.justonebite.ai_history.presentation.dto;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AiRequestHistoryResponse(
    UUID id,
    String model,
    String request,
    String response,
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


