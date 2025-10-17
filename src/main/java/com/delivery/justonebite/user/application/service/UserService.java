package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.UpdatePasswordRequest;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import com.delivery.justonebite.user.presentation.dto.request.WithdrawRequest;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateUserRoleResponse;
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
        verifyPassword(request.password(), foundUser);
        foundUser.updateProfile(request);
        return UpdateProfileResponse.toDto(foundUser);
    }

    @Transactional
    public void updatePassword(UserDetailsImpl userDetails, UpdatePasswordRequest request) {
        User foundUser = findUser(userDetails.getUserId());
        verifyPassword(request.oldPassword(), foundUser);
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        foundUser.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails, WithdrawRequest request) {
        User foundUser = findUser(userDetails.getUserId());
        verifyPassword(request.password(), foundUser);
        foundUser.softDelete(userDetails.getUserId());
    }

    @Transactional
    public UpdateUserRoleResponse updateUserRole(Long userId) {
        User foundUser = findUser(userId);
        foundUser.updateUserRoleToOwner();
        return UpdateUserRoleResponse.toDto(foundUser);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    private void verifyPassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
