package com.delivery.justonebite.review.repository;


import com.delivery.justonebite.review.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewAggregationRepository extends Repository<Review, UUID> {

    @Query(value = """
        SELECT 
            r.shop_id      AS shopId,
            AVG(r.rating)  AS avgRating,
            COUNT(*)       AS reviewCount
        FROM h_review r
        WHERE r.deleted_at IS NULL
          AND r.shop_id IN (:shopIds)
        GROUP BY r.shop_id
        """, nativeQuery = true)
    List<ShopRatingAggProjection> findRatingAggByShopIds(@Param("shopIds") List<UUID> shopIds);


    interface ShopRatingAggProjection {

        UUID getShopId();
        Double getAvgRating();
        Long getReviewCount();
    }
}


