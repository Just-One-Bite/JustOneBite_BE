package com.delivery.justonebite.user.presentation.dto.request;

import com.delivery.justonebite.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "회원가입(최초 가입시 CUSTOMER) 요청 DTO")
@Builder
public record SignupRequest(
        @Schema(description = "인증에 필요한 이메일", example = "example@email.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        String email,
        @Schema(description = "생성할 유저의 이름", example = "성이름")
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @Schema(description = "생성할 유저의 전화번호", example = "010-1234-5678")
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        String phoneNumber,
        @Schema(description = "인증에 필요한 비밀번호", example = "!Qwer1234")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
                message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자(@$!%*?&)를 포함해야 합니다."
        )
        String password
) {
        public User toUser(String encodedPassword) {
                return User.builder()
                        .email(email)
                        .name(name)
                        .phoneNumber(phoneNumber)
                        .password(encodedPassword)
                        .build();
        }
}
