package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.application.service.AdminService;
import com.delivery.justonebite.user.presentation.dto.request.CreateManagerRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateManagerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<CreateManagerResponse> createManager(@RequestBody @Valid CreateManagerRequest request) {
        CreateManagerResponse manager = adminService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager);
    }
}
