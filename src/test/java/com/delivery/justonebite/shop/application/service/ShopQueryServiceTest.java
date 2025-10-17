package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.review.application.service.ReviewAggregationService;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDetailResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopSearchResponse;
import com.delivery.justonebite.shop.projection.ShopAvgProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShopQueryServiceTest {

    private ShopRepository shopRepository;
    private ReviewAggregationService reviewAggregationService;
    private ShopQueryService shopQueryService;

    private UUID shopId;

    @BeforeEach
    void setUp() {
        shopRepository = mock(ShopRepository.class);
        reviewAggregationService = mock(ReviewAggregationService.class);
        shopQueryService = new ShopQueryService(shopRepository, reviewAggregationService);
        shopId = UUID.randomUUID();
    }


    @Test
    @DisplayName("검색어가 있을 때 가게 이름/설명으로 검색")
    void searchShops_withQuery_success() {
        ShopSearchRequest request = ShopSearchRequest.of("치킨", 0, 10, "createdAt", "DESC");
        Pageable pageable = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.fromString(request.direction()), request.sortBy()));

        Shop s1 = Shop.builder()
                .id(UUID.randomUUID())
                .name("맛있는 치킨집")
                .description("바삭하고 고소한 치킨")
                .build();

        Shop s2 = Shop.builder()
                .id(UUID.randomUUID())
                .name("양념치킨 명가")
                .description("매콤달콤 양념 전문점")
                .build();

        Page<Shop> shopPage = new PageImpl<>(List.of(s1, s2), pageable, 2);

        given(shopRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                request.q(), request.q(), pageable
        )).willReturn(shopPage);

        ShopAvgProjection p1 = mock(ShopAvgProjection.class);
        given(p1.getShopId()).willReturn(s1.getId());
        given(p1.getAverageRating()).willReturn(BigDecimal.valueOf(4.5));

        ShopAvgProjection p2 = mock(ShopAvgProjection.class);
        given(p2.getShopId()).willReturn(s2.getId());
        given(p2.getAverageRating()).willReturn(BigDecimal.valueOf(4.0));

        given(shopRepository.findAvgByIds(any())).willReturn(List.of(p1, p2));


        Page<ShopSearchResponse> result = shopQueryService.searchShops(request);


        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).averageRating()).isEqualTo(4.5);
        verify(shopRepository).findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(request.q(), request.q(), pageable);
        verify(shopRepository).findAvgByIds(any());
    }


    @Test
    @DisplayName("검색어가 없을 때 전체 가게 조회")
    void searchShops_noQuery_success() {

        ShopSearchRequest request = ShopSearchRequest.of(null, 0, 10, "averageRating", "ASC");
        Pageable pageable = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.fromString(request.direction()), request.sortBy()));

        Shop s1 = Shop.builder().id(UUID.randomUUID()).name("피자천국").description("치즈 폭탄 피자").build();
        Shop s2 = Shop.builder().id(UUID.randomUUID()).name("분식나라").description("떡볶이, 튀김, 순대").build();

        Page<Shop> shopPage = new PageImpl<>(List.of(s1, s2), pageable, 2);
        given(shopRepository.findAll(pageable)).willReturn(shopPage);


        ShopAvgProjection p1 = mock(ShopAvgProjection.class);
        given(p1.getShopId()).willReturn(s1.getId());
        given(p1.getAverageRating()).willReturn(BigDecimal.valueOf(4.8));

        ShopAvgProjection p2 = mock(ShopAvgProjection.class);
        given(p2.getShopId()).willReturn(s2.getId());
        given(p2.getAverageRating()).willReturn(BigDecimal.valueOf(4.3));

        given(shopRepository.findAvgByIds(any())).willReturn(List.of(p1, p2));


        Page<ShopSearchResponse> result = shopQueryService.searchShops(request);


        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).averageRating()).isEqualTo(4.8);
        verify(shopRepository).findAll(pageable);
        verify(shopRepository).findAvgByIds(any());
    }

    @Test
    @DisplayName("가게 상세조회")
    void getShopDetail_success() {

        Shop shop = Shop.builder()
                .id(shopId)
                .name("치킨스토리")
                .description("국내산 닭 사용")
                .build();

        given(shopRepository.findById(shopId)).willReturn(Optional.of(shop));

        ShopAvgProjection p1 = mock(ShopAvgProjection.class);
        given(p1.getShopId()).willReturn(shopId);
        given(p1.getAverageRating()).willReturn(BigDecimal.valueOf(4.6));

        lenient().when(shopRepository.findAvgByIds(anyList()))
                .thenReturn(List.of(p1));

        ShopDetailResponse response = shopQueryService.getShopDetail(shopId);


        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("치킨스토리");
        assertThat(response.averageRating().doubleValue()).isEqualTo(4.6);
        verify(shopRepository).findById(shopId);
        verify(shopRepository).findAvgByIds(List.of(shopId));
    }


    @Test
    @DisplayName("가게 상세조회 - 존재하지 않는 가게 ID로 요청")
    void getShopDetail_notFound() {
        given(shopRepository.findById(shopId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> shopQueryService.getShopDetail(shopId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("존재하지 않는 가게");


        verify(shopRepository).findById(shopId);
        verify(shopRepository, never()).findAvgByIds(any());
    }
}
