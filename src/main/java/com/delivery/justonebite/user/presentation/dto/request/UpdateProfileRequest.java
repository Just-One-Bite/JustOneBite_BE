package com.delivery.justonebite.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdateProfileRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        String name,
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        String phoneNumber
) {
}
