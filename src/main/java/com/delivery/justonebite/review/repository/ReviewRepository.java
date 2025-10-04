package com.delivery.justonebite.review.repository;

import com.delivery.justonebite.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface ReviewRepository extends JpaRepository<Review, UUID>{

    boolean existsByOrder_Id(UUID orderId);
    Optional<Review> findByOrder_Id(UUID orderId);


}
