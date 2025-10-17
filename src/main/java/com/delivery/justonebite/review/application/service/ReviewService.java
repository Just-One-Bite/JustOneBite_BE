package com.delivery.justonebite.review.application.service;


import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.enums.OrderStatus;
import com.delivery.justonebite.order.domain.repository.OrderHistoryRepository;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.entity.Review;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.request.UpdateReviewRequest;
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
        reviewAggregationService.updateShopAvgByShopId(order.getShop().getId());
 
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

    @Transactional
    public ReviewResponse update(UUID reviewId,
                                 Long currentUserId,
                                 UpdateReviewRequest req) {

        Review review = loadReviewOrThrow(reviewId);
        assertAuthor(review, currentUserId);

        if (isNoop(req)) return ReviewResponse.from(review);
        boolean ratingChanged = review.applyUpdate(req);
        if (ratingChanged){
            UUID shopId = review.getOrder().getShop().getId();
            reviewAggregationService.updateShopAvgByShopId(shopId);
        }

 
        applyUpdates(review, req);
 
        return ReviewResponse.from(review);
    }

    @Transactional
    public void softDelete(UUID reviewId, Long currentUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        assertDeletable(review, currentUserId);

        if (review.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_REVIEW);
        }
        review.softDelete(currentUserId);
        UUID shopId = review.getOrder().getShop().getId();
        reviewAggregationService.updateShopAvgByShopId(shopId);
 
    }

    @Transactional
    public void restore(UUID reviewId, Long currentUserId) {
        Review review = reviewRepository.findByIdIncludingDeleted(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        assertAuthor(review, currentUserId);

        if (!review.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_ACTIVE_REVIEW);
        }
        review.restore();
        UUID shopId = review.getOrder().getShop().getId();
        reviewAggregationService.updateShopAvgByShopId(shopId);
 
    }

    private void validateCanWrite(UserRole role) {
        if (role != UserRole.CUSTOMER) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
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

    private Review loadReviewOrThrow(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private void assertAuthor(Review review, Long userId) {
        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private boolean isNoop(UpdateReviewRequest req) {
        return req.content() == null && req.rating() == null;
    }

    private void applyUpdates(Review review, UpdateReviewRequest req) {
        if (req.content() != null) review.updateContent(req.content());
        if (req.rating() != null)  review.updateRating(req.rating());
    }

    private void assertDeletable(Review review, Long userId) {
        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
