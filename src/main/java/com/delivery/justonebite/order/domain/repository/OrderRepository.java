package com.delivery.justonebite.order.domain.repository;

import com.delivery.justonebite.order.domain.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByIdAndCustomer_Id(UUID id, Long userId);

    boolean existsByIdAndShop_OwnerId(UUID id, Long userId);

    /**
     * JOIN FETCH o.customer : INNER JOIN h_user u ON o.user_id = u.user_id
     * 조회된 customer 엔티티를 즉시 로딩 대상으로 지정
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :orderId")
    Optional<Order> findByIdWithCustomer(@Param("orderId") UUID orderId);
}
/**
 * // OrderRepository.java
 * // Order를 페이징 처리하여 조회하면서, OrderHistory의 최신 상태를 서브 쿼리 또는 JOIN을 통해 최적화
 * @Query(value = "SELECT o FROM Order o JOIN FETCH o.latestHistory WHERE o.customer.id = :userId",
 *        countQuery = "SELECT COUNT(o) FROM Order o WHERE o.customer.id = :userId")
 * Page<Order> findAllOrdersWithLatestStatusByUserId(UUID userId, Pageable pageable);
 *
 * // (이 방법은 Order 엔티티에 latestHistory 필드가 있다고 가정합니다. 그렇지 않다면
 * // findTopBy... 쿼리를 사용하는 방법이 더 일반적입니다.)
 *
 * // N+1 문제를 근본적으로 해결하려면, DTO Projection을 사용하거나
 * // OrderHistoryRepository의 findTopBy... 쿼리를 효율적인 뷰 쿼리로 대체하는 것이 좋습니다.
 */