package com.delivery.justonebite.review.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "h_review",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_h_review_order", columnNames = "order_id")
        },
        indexes = {
                @Index(name = "IDX_h_review_shop_id", columnList = "shop_id")
        }
)
@Check(constraints = "rating BETWEEN 1 AND 5")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "review_id", nullable = false, updatable = false)
    private UUID reviewId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "rating", nullable = false)
    private int rating;

    public static Review create(Order order,
                                Long userId,
                                UUID shopId,
                                String content,
                                int rating) {
        validateRating(rating);
        return Review.builder()
                .order(order)
                .userId(userId)
                .shopId(order.getShop().getId())
                .content(content)
                .rating(rating)
                .build();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(int rating) {
        validateRating(rating);
        this.rating = rating;
    }

    private static void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new CustomException(ErrorCode.INVALID_RATING_RANGE);
        }
    }

}
