package com.delivery.justonebite.order.domain.repository;

import com.delivery.justonebite.order.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}
