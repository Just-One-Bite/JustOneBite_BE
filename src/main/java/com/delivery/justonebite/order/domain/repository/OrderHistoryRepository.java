package com.delivery.justonebite.order.domain.repository;

import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.entity.OrderHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.delivery.justonebite.order.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
    Optional<OrderHistory> findByOrderId(UUID orderId);
    // 최신 상태 하나를 반환
    Optional<OrderHistory> findTopByOrder_IdOrderByCreatedAtDesc(UUID orderId);
    // 전체 목록을 최신순으로 반환
    List<OrderHistory> findAllByOrder_IdOrderByCreatedAtDesc(UUID orderId);
    // 가게에서 완료되지 않은 배달 주문이 있는지 확인
    boolean existsByOrder_Shop_IdAndStatusNot(UUID shopId, OrderStatus status);

}
