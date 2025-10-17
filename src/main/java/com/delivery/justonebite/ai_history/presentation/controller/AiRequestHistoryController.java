package com.delivery.justonebite.ai_history.presentation.controller;

import com.delivery.justonebite.ai_history.application.service.AiRequestHistoryService;
import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "AI Request History API", description = "AI 사용 기록의 조회를 담당합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai")
public class AiRequestHistoryController {

    private final AiRequestHistoryService aiRequestHistoryService;

    @Operation(
        summary = "기록 단건 조회 요청 API",
        description = "사용자가 기록을 조회합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        parameters = {
            @Parameter(name = "id", description = "조회할 기록의 id value")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "기록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. 기록의 주인이 아닙니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "기록 정보가 존재하지 않습니다.", content = @Content(mediaType = "application/json"))
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AiRequestHistoryResponse> getHistory(@PathVariable UUID id,
                                                               @AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.status(HttpStatus.OK).body(aiRequestHistoryService.getHistory(user.getUserId(), id));
    }

    @Operation(
        summary = "전체 기록 조회 요청 API",
        description = "사용자의 기록을 전부 조회합니다.",
        security = @SecurityRequirement(name = "Authorization"),
        responses = {
            @ApiResponse(responseCode = "200", description = "기록 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
        }
    )
    @GetMapping
    public ResponseEntity<Page<AiRequestHistoryResponse>> getHistories(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                                       @RequestParam(name = "sort-by", defaultValue = "createdAt") String sortBy,
                                                                       @AuthenticationPrincipal UserDetailsImpl user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(aiRequestHistoryService.getHistories(user.getUser(), pageable));
    }
}
