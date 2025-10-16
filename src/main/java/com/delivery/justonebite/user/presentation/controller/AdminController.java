package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.user.application.service.AdminService;
import com.delivery.justonebite.user.presentation.dto.request.CreatedMasterRequest;
import com.delivery.justonebite.user.presentation.dto.response.CreateMasterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/signup")
    public ResponseEntity<CreateMasterResponse> createMaster(@RequestBody @Valid CreatedMasterRequest request) {
        CreateMasterResponse master = adminService.createMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(master);
    }


}
