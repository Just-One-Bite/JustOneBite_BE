package com.delivery.justonebite.ai_history.domain.entity;

import com.delivery.justonebite.ai_history.presentation.dto.AiRequestHistoryResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "h_ai_request_history")
public class AiRequestHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Long userId; // fix

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
//    private User user;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    private String response;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiRequestHistoryResponse toDto() {
        return new AiRequestHistoryResponse(
            this.getId(),
            this.getModel(),
            this.getRequest(),
            this.getResponse(),
            this.getCreatedAt()
        );
    }
}
