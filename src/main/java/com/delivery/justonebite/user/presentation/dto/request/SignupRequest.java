package com.delivery.justonebite.user.presentation.dto.request;

import com.delivery.justonebite.user.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        String email,
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        String phoneNumber,
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
