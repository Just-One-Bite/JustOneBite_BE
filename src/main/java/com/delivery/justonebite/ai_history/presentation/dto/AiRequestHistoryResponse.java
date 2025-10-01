package com.delivery.justonebite.ai_history.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AiRequestHistoryResponse(
    UUID id,
    String model,
    String request,
    String response,
    LocalDateTime createdAt
) {

}


