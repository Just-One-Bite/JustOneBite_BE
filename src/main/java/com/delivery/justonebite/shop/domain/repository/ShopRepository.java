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

    // 가게이름, 설명으로 검색
    Page<Shop> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable
    );
    // 삭제되지 않은 가게 조회
    Optional<Shop> findByIdAndDeletedAtIsNull(UUID id);


    //리뷰 평점 관련 코드 --


    @Query("select s.id as id, s.averageRating as averageRating from Shop s where s.id in :ids")
    List<ShopAvgProjection> findAvgByIds(@Param("ids") List<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE h_shop AS s
        SET average_rating = COALESCE(sub.avg_rating, 0)
        FROM (
          SELECT s2.shop_id,
                 ROUND(AVG(r.rating)::numeric, 1) AS avg_rating
          FROM h_shop AS s2
          LEFT JOIN h_review AS r
            ON r.shop_id = s2.shop_id
           AND r.deleted_at IS NULL
          GROUP BY s2.shop_id
        ) AS sub
        WHERE s.shop_id = sub.shop_id
        """, nativeQuery = true)
    int bulkUpdateAllAvg();


}
