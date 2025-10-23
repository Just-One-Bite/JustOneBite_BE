package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.application.dto.response.ReviewResponse;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.shop.presentation.dto.response.ShopReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopReviewService {

    private final ReviewService reviewService;
    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public ShopReviewResponse getReviewsByShop(UUID shopId, Pageable pageable) {
        // 가게 존재 여부 확인
        if (!shopRepository.existsById(shopId)) {
            throw new CustomException(ErrorCode.SHOP_NOT_FOUND);
        }

        Page<ReviewResponse> reviews = reviewService.getByShop(shopId, pageable);

        //리뷰 응답 변환
        return ShopReviewResponse.from(reviews);
    }
}
