package com.delivery.justonebite.review.repository;

import com.delivery.justonebite.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface ReviewRepository extends SoftDeleteRepository<Review, UUID>{

    boolean existsByOrder_Id(UUID orderId);
   
    Optional<Review> findByOrder_Id(UUID orderId);

    Optional<Review> findById(UUID id);

    Page<Review> findByShopId(UUID shopId, Pageable pageable);

    @Query(value = "SELECT * FROM h_review WHERE review_id = :id", nativeQuery = true)
    Optional<Review> findByIdIncludingDeleted(@Param("id") UUID id);
}
