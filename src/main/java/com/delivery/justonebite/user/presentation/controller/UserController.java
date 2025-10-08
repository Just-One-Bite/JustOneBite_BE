package com.delivery.justonebite.user.presentation.controller;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.user.application.service.UserService;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<GetProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetProfileResponse myProfile = userService.findMyProfile(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(myProfile);
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        UpdateProfileResponse myProfile = userService.updateProfile(userDetails, request);
        return ResponseEntity.status(HttpStatus.OK).body(myProfile);
    }

    // Todo: 비밀번호 변경

    // Todo: 회원 탈퇴
}
