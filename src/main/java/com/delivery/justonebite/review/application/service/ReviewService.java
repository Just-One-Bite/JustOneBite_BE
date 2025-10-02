package com.delivery.justonebite.review.application.service;


import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.entity.Order;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.review.entity.Review;
import com.delivery.justonebite.review.presentation.dto.request.CreateReviewRequest;
import com.delivery.justonebite.review.presentation.dto.response.CreateReviewResponse;
import com.delivery.justonebite.review.repository.ReviewRepository;
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

    //TODO : order, shop, orderhistory 구현되면 주석 해제 예정
    private final ReviewRepository reviewRepository;
//    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
//    private final OrderHistoryRepository orderHistoryRepository;

//
//
//    @Transactional
//    public CreateReviewResponse createReview(CreateReviewRequest request) {
//
//        Order order = orderRepository.findById(request.orderId())
//                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
//
//        var latestStatus = orderHistoryRepository
//                .findTopByOrder_IdOrderByCreatedAtDesc(order.getId())
//                .map(h -> h.getStatus())
//                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_STATUS_NOT_FOUND));
//
//        if (latestStatus != OrderStatus.COMPLETED) {
//            throw new CustomException(ErrorCode.ORDER_NOT_COMPLETED);
//        }
//
//        if (reviewRepository.existsByOrder_Id(order.getId())) {
//            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
//        }
//
//
//        Review review = create(order, orderOwnerId, shopId, request.content(), request.rating());
//
//        refreshShopAverage(order.getShopId());
//
//        return CreateReviewResponse.from(review);
//    }
//
//    private void refreshShopAverage(UUID shopId) {
//        Double avg = reviewRepository.avgRatingByShopId(shopId);
//
//        double safeAvg = avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0;
//        shopRepository.updateAverageRating(shopId, BigDecimal.valueOf(safeAvg));
//    }


}
