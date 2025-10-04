package com.delivery.justonebite.review.application.service;


import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.entity.Review;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.repository.ReviewRepository;
import com.delivery.justonebite.user.domain.entity.UserRole;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
//import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import static com.delivery.justonebite.review.entity.Review.create;

@Service
@RequiredArgsConstructor
public class ReviewService {

    //TODO : order, shop, orderhistory 구현되면 주석 해제
    private final ReviewRepository reviewRepository;
    //TODO : ShopRepository merge되면 주석 해제
//    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
     //TODO: OrderHistoryRepository merge되면 주석 해제
//    private final OrderHistoryRepository orderHistoryRepository;

    // TODO : shop, order 도메인이 merge 되면 주석 해제 예정
//    @Transactional
//    public CreateReviewResponse createReview(Long currentUserId,
//                                             UserRole currentUserRole,
//                                             CreateReviewRequest request) {
//        validateCanWrite(currentUserRole);
//
//        Order order = getOrderOrThrow(request.orderId());
//        validateOrderOwner(order, currentUserId);     // TODO: owner 연동되면 활성화
//        validateOrderCompleted(order);
//        ensureNoDuplicate(order.getId());
//
//        Review review = buildReview(order, currentUserId, request);
//        Review saved = reviewRepository.save(review);
//        return CreateReviewResponse.from(saved);
//    }

    private void validateCanWrite(UserRole role) {
        boolean canWrite = role == UserRole.CUSTOMER || role == UserRole.MANAGER || role == UserRole.MASTER;
        if (!canWrite) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOrderOwner(Order order, Long currentUserId) {
        // TODO: Order에 userId 붙으면 아래 주석 해제
        // if (!order.getUserId().equals(currentUserId)) throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private void validateOrderCompleted(Order order) {
    //TODO: Order에 주문상태 붙으면 아래 주석 해제
        //    if (!order.isCompleted()) throw new CustomException(ErrorCode.ORDER_NOT_COMPLETED);
    }

    private void ensureNoDuplicate(UUID orderId) {
        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }


        // TODO: shop 도메인 merge되면 주석 해제
//    private Review buildReview(Order order, Long currentUserId, CreateReviewRequest req) {
//        UUID shopId = order.getShopId(); // 파생
//        return Review.create(order, currentUserId, shopId, req.content(), req.rating());
//    }

}
