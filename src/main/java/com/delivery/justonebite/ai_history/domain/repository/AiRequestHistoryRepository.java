package com.delivery.justonebite.ai_history.domain.repository;

import com.delivery.justonebite.ai_history.domain.entity.AiRequestHistory;
import com.delivery.justonebite.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRequestHistoryRepository extends JpaRepository<AiRequestHistory, UUID> {
    Page<AiRequestHistory> findAllByUser(User user, Pageable pageable);
}
