  package com.delivery.justonebite.shop.domain.repository;

import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.projection.ShopAvgProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    //이름, 설명으로 검색
    @Query("select s.id as id, s.averageRating as averageRating from Shop s where s.id in :ids")
    List<ShopAvgProjection> findAvgByIds(@Param("ids") List<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
UPDATE h_shop s
   SET average_rating = COALESCE(sub.avg_rating, 0)
  FROM (
        SELECT r.shop_id, AVG(r.rating) AS avg_rating
          FROM h_review r
         WHERE r.deleted_at IS NULL
      GROUP BY r.shop_id
  ) sub
 WHERE s.shop_id = sub.shop_id
""", nativeQuery = true)
    int bulkUpdateAllAvg();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
UPDATE h_shop s
   SET average_rating = 0
 WHERE NOT EXISTS (
        SELECT 1
          FROM h_review r
         WHERE r.deleted_at IS NULL
           AND r.shop_id = s.shop_id
  )
""", nativeQuery = true)
    int bulkResetAvgForZeroReview();

    Page<Shop> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable
    );

    Optional<Shop> findByIdAndDeletedAtIsNull(UUID id);


}
