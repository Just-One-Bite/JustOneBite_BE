package com.delivery.justonebite.user.domain.repository;

import com.delivery.justonebite.user.domain.entity.Address;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByUser_IdAndIsDefaultTrue(Long userId);
}
