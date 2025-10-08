package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.UserRepository;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import com.delivery.justonebite.user.presentation.dto.response.GetProfileResponse;
import com.delivery.justonebite.user.presentation.dto.response.UpdateProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GetProfileResponse findMyProfile(UserDetailsImpl userDetails) {
        User myProfile = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return GetProfileResponse.toDto(myProfile);
    }

    @Transactional
    public UpdateProfileResponse updateProfile(
            UserDetailsImpl userDetails,
            UpdateProfileRequest request
    ) {
        User foundUser = findProfile(userDetails.getUsername());
        if (!passwordEncoder.matches(request.password(), foundUser.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        foundUser.updateProfile(request);
        userRepository.save(foundUser);
        return UpdateProfileResponse.toDto(foundUser);
    }

    // Todo: 비밀번호 변경

    // Todo: 회원 탈퇴

    private User findProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
