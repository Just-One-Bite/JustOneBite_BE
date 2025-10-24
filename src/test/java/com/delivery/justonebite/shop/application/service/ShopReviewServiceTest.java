package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.review.application.service.ReviewService;
import com.delivery.justonebite.review.application.dto.response.ReviewResponse;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.response.ShopReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ShopReviewServiceTest {

    private ReviewService reviewService;
    private ShopRepository shopRepository;
    private ShopReviewService shopReviewService;
    private UUID shopId;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        shopRepository = mock(ShopRepository.class);
        shopReviewService = new ShopReviewService(reviewService, shopRepository);
        shopId = UUID.randomUUID();
    }


    @Test
    @DisplayName("가게에 대한 리뷰 목록을 정상적으로 조회한다.")
    void getReviewsByShop_success() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        ReviewResponse r1 = new ReviewResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                shopId,
                "후라이드 치킨 정말 맛있어요!",
                5,
                LocalDateTime.parse("2025-10-14T12:00:00"),
                LocalDateTime.parse("2025-10-14T12:10:00"),
                1L
        );

        ReviewResponse r2 = new ReviewResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2L,
                shopId,
                "양이 많아요! 만족합니다.",
                4,
                LocalDateTime.parse("2025-10-14T13:00:00"),
                LocalDateTime.parse("2025-10-14T13:10:00"),
                2L
        );

        given(shopRepository.existsById(shopId)).willReturn(true);
        given(reviewService.getByShop(shopId, pageable))
                .willReturn(new PageImpl<>(List.of(r1, r2), pageable, 2L));

        ShopReviewResponse response = shopReviewService.getReviewsByShop(shopId, pageable);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).content()).contains("맛있어요");
        verify(shopRepository).existsById(shopId);
        verify(reviewService).getByShop(shopId, pageable);
    }



    @Test
    @DisplayName("존재하지 않는 가게 ID로 리뷰 조회 시 예외 발생")
    void getReviewsByShop_shopNotFound() {
        Pageable pageable = PageRequest.of(0, 5);
        given(shopRepository.existsById(shopId)).willReturn(false);

        assertThatThrownBy(() -> shopReviewService.getReviewsByShop(shopId, pageable))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SHOP_NOT_FOUND);

        verify(shopRepository).existsById(shopId);
        verify(reviewService, never()).getByShop(any(), any());
    }
}
