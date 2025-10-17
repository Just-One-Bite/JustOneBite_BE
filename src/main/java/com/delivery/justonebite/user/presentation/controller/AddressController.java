package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.user.application.service.AddressService;
import com.delivery.justonebite.user.presentation.dto.request.RegistAddressRequest;
import com.delivery.justonebite.user.presentation.dto.response.DefaultAddressResponse;
import com.delivery.justonebite.user.presentation.dto.response.RegistAddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Address API", description = "주소의 생성/대표주소 설정 등을 담당합니다.")
@RestController
@RequestMapping("/v1/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    // 주소 등록
    @Operation(
            summary = "주소 등록 요청 API",
            description = "로그인한 유저가 주소를 등록할 수 있습니다. 주소는 5개까지 등록할 수 있습니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 정보 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "400", description = "주소는 5개를 초과해서 등록할 수 없습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "주소를 찾을 수 없습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PostMapping
    public ResponseEntity<RegistAddressResponse> registAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid RegistAddressRequest request
    ) {
        RegistAddressResponse registedAddress = addressService.registAddress(userDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registedAddress);
    }

    // 대표 주소 설정
    @Operation(
            summary = "대표 주소 설정 요청 API",
            description = "원하는 주소를 대표 주소로 설정할 수 있습니다. 대표 주소는 1개만 설정할 수 있으며 대표 주소를 변경할 시 기존 대표 주소를 해체하고 대표 주소를 설정가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 정보 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PatchMapping("/{addressId}")
    public ResponseEntity<DefaultAddressResponse> updateDefaultAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID addressId
    ) {
        DefaultAddressResponse updatedDefaultAddress = addressService.updateDefaultAddress(userDetails, addressId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDefaultAddress);
    }
}
