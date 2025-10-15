package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.user.application.service.AddressService;
import com.delivery.justonebite.user.presentation.dto.request.DefaultAddressRequest;
import com.delivery.justonebite.user.presentation.dto.request.RegistAddressRequest;
import com.delivery.justonebite.user.presentation.dto.response.DefaultAddressResponse;
import com.delivery.justonebite.user.presentation.dto.response.RegistAddressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    // 주소 등록
    @PostMapping
    public ResponseEntity<RegistAddressResponse> registAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid RegistAddressRequest request
    ) {
        RegistAddressResponse registedAddress = addressService.registAddress(userDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registedAddress);
    }

    // 대표 주소 설정
    @PatchMapping("/{addressId}")
    public ResponseEntity<DefaultAddressResponse> updateDefaultAddress(
            @PathVariable UUID addressId,
            @RequestBody @Valid DefaultAddressRequest request
    ) {
        DefaultAddressResponse updatedDefaultAddress = addressService.updateDefaultAddress(addressId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDefaultAddress);
    }
}
