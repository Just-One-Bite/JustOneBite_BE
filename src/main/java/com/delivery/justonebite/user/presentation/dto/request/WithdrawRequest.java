package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 탈퇴 요청 DTO")
public record WithdrawRequest(
        @Schema(description = "본인 인증에 필요한 비밀번호", example = "!Qwer1234")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
