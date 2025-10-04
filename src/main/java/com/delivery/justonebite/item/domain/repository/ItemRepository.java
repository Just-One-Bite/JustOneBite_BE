package com.delivery.justonebite.item.domain.repository;

import com.delivery.justonebite.item.domain.entity.Item;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByItemId(UUID itemId);
    List<Item> findAllByItemIdIn(List<UUID> itemIds);
    Page<Item> findAllByShopId(UUID shopId, Pageable pageable);
}