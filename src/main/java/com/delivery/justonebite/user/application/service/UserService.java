package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.UpdatePasswordRequest;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public GetProfileResponse findMyProfile(UserDetailsImpl userDetails) {
        User foundUser = findUser(userDetails.getUserId());
        return GetProfileResponse.toDto(foundUser);
    }

    @Transactional
    public UpdateProfileResponse updateProfile(
            UserDetailsImpl userDetails,
            UpdateProfileRequest request
    ) {
        User foundUser = findUser(userDetails.getUserId());
        varifyPassword(request.password(), foundUser);
        foundUser.updateProfile(request);
        userRepository.save(foundUser);
        return UpdateProfileResponse.toDto(foundUser);
    }

    @Transactional
    public void updatePassword(UserDetailsImpl userDetails, UpdatePasswordRequest request) {
        User foundUser = findUser(userDetails.getUserId());
        varifyPassword(request.oldPassword(), foundUser);
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        foundUser.updatePassword(encodedPassword);
        userRepository.save(foundUser);
    }

    // Todo: 회원 탈퇴

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    private void varifyPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
