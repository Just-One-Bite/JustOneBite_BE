package com.delivery.justonebite.user.presentation.dto.request;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "최종 관리자(MASTER) 생성 요청 DTO")
@Builder
public record CreatedMasterRequest(
        @Schema(description = "인증에 필요한 이메일(고윳값)", example = "master@email.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email
        String email,
        @Schema(description = "생성할 유저의 이름", example = "master")
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @Schema(description = "생성할 유저의 전화번호", example = "010-9999-9999")
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
    public User toUser(UserRole userRole, String encodedPassword) {
        return User.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .userRole(userRole)
                .build();
    }
}