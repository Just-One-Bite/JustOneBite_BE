package com.delivery.justonebite.ai_history.application.service;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.ai_history.domain.repository.AiRequestHistoryRepository;
import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiRequestHistoryService {

    private final AiRequestHistoryRepository aiRequestHistoryRepository;

    public AiRequestHistoryResponse getHistory(UUID id) {
        AiRequestHistory history = aiRequestHistoryRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return AiRequestHistoryResponse.from(history);
    }

    public Page<AiRequestHistoryResponse> getHistories(Long userId, Pageable pageable) {
        Page<AiRequestHistory> histories = aiRequestHistoryRepository.findAllByUserId(userId, pageable);
        return histories.map(AiRequestHistoryResponse::from);
    }
}
