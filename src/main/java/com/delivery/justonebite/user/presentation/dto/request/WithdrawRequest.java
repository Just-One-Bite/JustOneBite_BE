package com.delivery.justonebite.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
