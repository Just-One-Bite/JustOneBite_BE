package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.application.service.AdminService;
import com.delivery.justonebite.user.presentation.dto.request.CreateManagerRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateManagerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "관리자(MANAGER) 생성 등을 담당합니다.")
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "관리자 생성 요청 API",
            description = "최종 관리자가 관리자 계정을 생성합니다. 해당 API 요청 권한은 MASTER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "관리자 계정 생성에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (MASTER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "409", description = "사용중인 이메일입니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<CreateManagerResponse> createManager(@RequestBody @Valid CreateManagerRequest request) {
        CreateManagerResponse manager = adminService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager);
    }
}
