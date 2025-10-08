package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetProfileResponse findMyProfile(UserDetailsImpl userDetails) {
        User myProfile = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        return GetProfileResponse.toDto(myProfile);
    }

    // Todo: 유저 프로필 수정

    // Todo: 비밀번호 변경

    // Todo: 회원 탈퇴
}
