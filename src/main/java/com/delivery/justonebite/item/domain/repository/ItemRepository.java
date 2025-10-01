package com.delivery.justonebite.item.domain.repository;

import com.delivery.justonebite.item.domain.entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    Optional<ItemEntity> findByItemId(UUID itemId);

    Page<ItemEntity> findAllByShopId(UUID shopId, Pageable pageable);
}