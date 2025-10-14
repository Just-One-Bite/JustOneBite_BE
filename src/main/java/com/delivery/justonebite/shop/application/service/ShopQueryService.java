package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.review.application.service.ReviewAggregationService;
import com.delivery.justonebite.review.presentation.dto.response.RatingAggResponse;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDetailResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopQueryService {

    private final ShopRepository shopRepository;
    private final ReviewAggregationService reviewAggregationService;

    // 전체 가게 목록 조회
    public Page<ShopSearchResponse> searchShops(ShopSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(Sort.Direction.fromString(request.direction()), request.sortBy())
        );

        Page<Shop> shops;

        if (request.q() == null || request.q().isBlank()) {
            shops = shopRepository.findAll(pageable);
        } else {
            shops = shopRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    request.q(), request.q(), pageable
            );
        }

        //조회된 모든 shopId 추출
        List<UUID> shopIds = shops.stream().map(Shop::getId).toList();

        //리뷰 집계 결과 조회 (평균, 리뷰수)
        Map<UUID, RatingAggResponse> aggMap = (Map<UUID, RatingAggResponse>) shopRepository.findAvgByIds(shopIds);

        //각 가게에 평균 평점 주입 후 DTO 변환
        return shops.map(shop -> {
            RatingAggResponse agg = aggMap.get(shop.getId());
            double avgRating = (agg != null) ? agg.avgRating() : 0.0;  // null 방지
            return ShopSearchResponse.from(shop, avgRating);
        });
    }

    // 가게 상세 조회
    public ShopDetailResponse getShopDetail(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        Map<UUID, RatingAggResponse> aggMap =
                (Map<UUID, RatingAggResponse>) shopRepository.findAvgByIds(List.of(shop.getId()));

        RatingAggResponse agg = aggMap.get(shop.getId());
        double avgRating = (agg != null) ? agg.avgRating() : 0.0;

        return ShopDetailResponse.from(shop, avgRating);
    }

}
