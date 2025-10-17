package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.global.config.security.UserDetailsImpl;
import com.delivery.justonebite.user.application.service.UserService;
import com.delivery.justonebite.user.presentation.dto.request.UpdatePasswordRequest;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import com.delivery.justonebite.user.presentation.dto.request.WithdrawRequest;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateUserRoleResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "인증된 유저의 정보를 조회/수정/삭제 등을 담당합니다.")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "유저 조회 요청 API",
            description = "로그인한 유저(CUSTOMER, OWNER) 자신의 정보를 조회합니다. 해당 API 요청 권한은 CUSTOMER, OWNER, MASTER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 정보 조회에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (CUSTOMER, OWNER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MASTER')")
    @GetMapping("/me")
    public ResponseEntity<GetProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetProfileResponse myProfile = userService.findMyProfile(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(myProfile);
    }

    @Operation(
            summary = "유저 수정 요청 API",
            description = "로그인한 유저(CUSTOMER, OWNER) 자신의 정보를 수정합니다. 해당 API 요청 권한은 CUSTOMER, OWNER, MASTER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 정보 수정에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (CUSTOMER, OWNER, MASTER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MASTER')")
    @PatchMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        UpdateProfileResponse myProfile = userService.updateProfile(userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).body(myProfile);
    }

    @Operation(
            summary = "비밀번호 수정 요청 API",
            description = "로그인한 유저(CUSTOMER, OWNER) 자신의 비밀번호를 수정합니다. 해당 API 요청 권한은 CUSTOMER, OWNER, MASTER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 정보 수정에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (CUSTOMER, OWNER, MASTER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MASTER')")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdatePasswordRequest request
    ) {
        userService.updatePassword(userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "회원 탈퇴 요청 API",
            description = "회원 탈퇴를 요청합니다. 해당 API 요청 권한은 CUSTOMER, OWNER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "유효한 인증 정보가 없습니다.", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (CUSTOMER, OWNER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid WithdrawRequest request
    ) {
        userService.deleteUser(userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "권한 변경 요청 API",
            description = "관리자가 회원의 권한 변경을 요청합니다. 해당 API 요청 권한은 MANAGER, MASTER만 가능합니다.",
            security = @SecurityRequirement(name = "Authorization"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "권한 변경에 성공하였습니다."),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 요청입니다. (JWT 토큰 누락 또는 만료)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (MANAGER, MASTER 아님)", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "저장된 유저 정보가 없습니다.", content = @Content(mediaType = "application/json"))
            }
    )
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @PatchMapping("/{userId}")
    public ResponseEntity<UpdateUserRoleResponse> updateUserRole(@PathVariable Long userId) {
        UpdateUserRoleResponse updatedUser = userService.updateUserRole(userId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}
