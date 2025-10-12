package com.delivery.justonebite.ai_history.presentation.controller;

import com.delivery.justonebite.ai_history.application.service.AiRequestHistoryService;
import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai")
public class AiRequestHistoryController {

    private final AiRequestHistoryService aiRequestHistoryService;

    @GetMapping("/{id}")
    public ResponseEntity<AiRequestHistoryResponse> getHistory(@PathVariable UUID id,
                                                               @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(aiRequestHistoryService.getHistory(user.getUserId(), id));
    }

    @GetMapping
    public ResponseEntity<Page<AiRequestHistoryResponse>> getHistories(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                                       @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
                                                                       @AuthenticationPrincipal UserDetailsImpl user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(aiRequestHistoryService.getHistories(user.getUserId(), pageable));
    }
}
