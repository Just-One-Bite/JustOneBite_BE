package com.delivery.justonebite.review.application.service;


import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.entity.Review;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.presentation.dto.response.ReviewResponse;
import com.delivery.justonebite.review.repository.ReviewRepository;
import com.delivery.justonebite.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import static com.delivery.justonebite.review.entity.Review.create;

@Service
@RequiredArgsConstructor
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;


    @Transactional
    public CreateReviewResponse createReview(Long currentUserId,
                                             UserRole currentUserRole,
                                             CreateReviewRequest request) {
        validateCanWrite(currentUserRole);

        Order order = getOrderOrThrow(request.orderId());
        validateOrderOwner(order, currentUserId);
        validateOrderCompleted(order);
        ensureNoDuplicate(order.getId());

        Review review = buildReview(order, currentUserId, request);
        Review saved = reviewRepository.save(review);
        return CreateReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getByShop(UUID shopId, Pageable pageable) {
        return reviewRepository.findByShopId(shopId, pageable)
                .map(ReviewResponse::from);
    }

    private void validateCanWrite(UserRole role) {
        boolean canWrite = role == UserRole.CUSTOMER || role == UserRole.MANAGER || role == UserRole.MASTER;
        if (!canWrite) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOrderOwner(Order order, Long currentUserId) {
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private void validateOrderCompleted(Order order) {
        OrderStatus latest = orderHistoryRepository
                .findTopByOrder_IdOrderByCreatedAtDesc(order.getId())
                .map(h -> h.getStatus())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_STATUS_NOT_FOUND));

        if (latest != OrderStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ORDER_NOT_COMPLETED);
        }
    }

    private void ensureNoDuplicate(UUID orderId) {
        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }

    private Review buildReview(Order order, Long currentUserId, CreateReviewRequest req) {
        UUID shopId = order.getShop().getId();
        return create(order, currentUserId, shopId, req.content(), req.rating());
    }

}
