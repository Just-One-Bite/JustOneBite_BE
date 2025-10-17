package com.delivery.justonebite.item.domain.repository;

import com.delivery.justonebite.item.domain.entity.Item;
import com.delivery.justonebite.review.repository.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends SoftDeleteRepository<Item, UUID> {
    Optional<Item> findByItemId(UUID item_id);

    List<Item> findAllByItemIdIn(List<UUID> itemIds);

    @Query("SELECT i FROM Item i WHERE i.itemId = :item_id and i.isHidden = false")
    Optional<Item> findByItemIdWithoutHidden(@Param("item_id") UUID itemId);

    @Query("SELECT i FROM Item i WHERE i.shop.id = :shop_id and i.isHidden = false")
    Page<Item> findAllByShopIdWithoutHidden(@Param("shop_id") UUID shopId, Pageable pageable);

    @Query(value = "SELECT * from h_item WHERE item_id = :item_id", nativeQuery = true)
    Optional<Item> findByItemIdWithNativeQuery(@Param("item_id") UUID itemId);

    @Query(value = "SELECT * FROM h_item WHERE shop_id = :shop_id", nativeQuery = true)
    Page<Item> findAllByShopIdWithNativeQuery(@Param("shop_id") UUID shopId, Pageable pageable);
}