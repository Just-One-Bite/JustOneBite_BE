package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 변경 요청 DTO")
public record UpdatePasswordRequest(
        @Schema(description = "기존 비밀번호", example = "!Qwer1234")
        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        String oldPassword,
        @Schema(description = "새로운 비밀번호", example = "!1Q2w3e4r")
        @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
                message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자(@$!%*?&)를 포함해야 합니다."
        )
        String newPassword
) {
}
