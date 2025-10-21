package com.delivery.justonebite.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Schema(description = "유저 정보 수정 요청 DTO")
@Builder
public record UpdateProfileRequest(
        @Schema(description = "본인 인증에 필요한 비밀번호", example = "!Qwer1234")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @Schema(description = "수정할 유저 이름", example = "example")
        String name,
        @Schema(description = "수정할 유저 전화번호", example = "011-1234-5678")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        String phoneNumber
) {
}
