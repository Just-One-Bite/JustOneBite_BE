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
        User myProfile = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        return GetProfileResponse.toDto(myProfile);
    }

    @Transactional
    public UpdateProfileResponse updateProfile(
            UserDetailsImpl userDetails,
            UpdateProfileRequest request
    ) {
        User foundUser = findProfile(userDetails.getUsername());
        varifyPassword(request.password(), foundUser);
        foundUser.updateProfile(request);
        userRepository.save(foundUser);
        return UpdateProfileResponse.toDto(foundUser);
    }

    @Transactional
    public void updatePassword(UserDetailsImpl userDetails, UpdatePasswordRequest request) {
        User foundUser = findProfile(userDetails.getUsername());
        varifyPassword(request.oldPassword(), foundUser);
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        foundUser.updatePassword(encodedPassword);
        userRepository.save(foundUser);
    }

    // Todo: 회원 탈퇴

    private User findProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    private void varifyPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }
}
