package com.delivery.justonebite.shop.domain.repository;

import com.delivery.justonebite.shop.domain.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    //이름, 설명으로 검색
    Page<Shop> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable
    );

    Optional<Shop> findByIdAndDeletedAtIsNull(UUID id);


}
