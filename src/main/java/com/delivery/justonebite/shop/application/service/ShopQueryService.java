package com.delivery.justonebite.shop.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.review.application.service.ReviewAggregationService;
import com.delivery.justonebite.review.presentation.dto.response.RatingAggResponse;
import com.delivery.justonebite.shop.domain.entity.Shop;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import com.delivery.justonebite.shop.presentation.dto.request.ShopSearchRequest;
import com.delivery.justonebite.shop.presentation.dto.response.ShopDetailResponse;
import com.delivery.justonebite.shop.presentation.dto.response.ShopSearchResponse;
import com.delivery.justonebite.shop.projection.ShopAvgProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

//        //리뷰 집계 결과 조회 (평균, 리뷰수)
//        Map<UUID, RatingAggResponse> aggMap = (Map<UUID, RatingAggResponse>) shopRepository.findAvgByIds(shopIds);


//        //각 가게에 평균 평점 주입 후 DTO 변환
//        return shops.map(shop -> {
//            RatingAggResponse agg = aggMap.get(shop.getId());
//            double avgRating = (agg != null) ? agg.avgRating() : 0.0;  // null 방지
//            return ShopSearchResponse.from(shop, avgRating);
//        });
        // ShopAvgProjection 조회
        List<ShopAvgProjection> avgList = shopRepository.findAvgByIds(shopIds);

        // Projection -> Map 변환
        Map<UUID, BigDecimal> avgMap = avgList.stream()
                .collect(Collectors.toMap(
                        ShopAvgProjection::getShopId,
                        ShopAvgProjection::getAverageRating
                ));


        // DTO 변환
        return shops.map(shop -> {
            BigDecimal avgRating = avgMap.getOrDefault(shop.getId(), BigDecimal.valueOf(0.0));
            return ShopSearchResponse.from(shop, avgRating.doubleValue());
        });

    }

    // 가게 상세 조회
    public ShopDetailResponse getShopDetail(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.SHOP_NOT_FOUND));
        List<ShopAvgProjection> avgList = shopRepository.findAvgByIds(List.of(shopId));

        BigDecimal avgRating = avgList.isEmpty()
                ? BigDecimal.ZERO
                : avgList.get(0).getAverageRating();

        return ShopDetailResponse.from(shop, avgRating.doubleValue());
    }

}
