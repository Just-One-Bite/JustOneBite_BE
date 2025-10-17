package com.delivery.justonebite.user.domain.repository;

import com.delivery.justonebite.user.domain.entity.Address;
import com.delivery.justonebite.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByUser_IdAndIsDefaultTrue(Long userId);

    long countByUser(User user);

    Optional<Address> findByUserAndIsDefault(User user, boolean isDefault);
}
