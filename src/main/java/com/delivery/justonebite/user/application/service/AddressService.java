package com.delivery.justonebite.user.application.service;

import com.delivery.justonebite.global.common.security.UserDetailsImpl;
import com.delivery.justonebite.user.domain.entity.Address;
import com.delivery.justonebite.user.domain.repository.AddressRepository;
import com.delivery.justonebite.user.presentation.dto.request.RegistAddressRequest;
import com.delivery.justonebite.user.presentation.dto.response.RegistAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    public RegistAddressResponse registAddress(UserDetailsImpl userDetails, RegistAddressRequest request) {
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
}
