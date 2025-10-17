package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.user.domain.entity.Address;
import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.repository.AddressRepository;
import com.delivery.justonebite.user.presentation.dto.request.RegistAddressRequest;
import com.delivery.justonebite.user.presentation.dto.response.DefaultAddressResponse;
import com.delivery.justonebite.user.presentation.dto.response.RegistAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private static final int MAX_ADDRESS_COUNT = 5;

    @Transactional
    public RegistAddressResponse registAddress(UserDetailsImpl userDetails, RegistAddressRequest request) {
        // 1. 유저별 주소 개수 제한 로직
        checkAddressCountLimit(userDetails.getUser());

        // 2. 새 주소를 대표 주소로 설정하는 경우, 기존 대표 주소 해제
        if (request.isDefault()) {
            addressRepository.findByUserAndIsDefault(userDetails.getUser(), true)
                    .ifPresent(oldDefaultAddress -> oldDefaultAddress.updateIsDefault(false));
        }
        Address address = Address.create(
                userDetails.getUser(),
                request.province(),
                request.city(),
                request.district(),
                request.address(),
                request.isDefault()
        );
        addressRepository.save(address);
        return RegistAddressResponse.toDto(address);
    }

    @Transactional
    public DefaultAddressResponse updateDefaultAddress(UserDetailsImpl userDetails, UUID addressId) {
        Address foundAddress = findAddress(addressId);
        if (foundAddress.isDefault()) {
            return DefaultAddressResponse.toDto(foundAddress);
        }

        addressRepository.findByUserAndIsDefault(userDetails.getUser(), true)
                .ifPresent(oldDefaultAddress -> oldDefaultAddress.updateIsDefault(false));

        foundAddress.updateIsDefault(true);
        return DefaultAddressResponse.toDto(foundAddress);
    }

    private void checkAddressCountLimit(User user) {
        if (addressRepository.countByUser(user) >= MAX_ADDRESS_COUNT) {
            throw new CustomException(ErrorCode.ADDRESS_LIMIT_EXCEEDED); // 예외 처리는 직접 정의하신 것을 사용
        }
    }

    private Address findAddress(UUID addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}
