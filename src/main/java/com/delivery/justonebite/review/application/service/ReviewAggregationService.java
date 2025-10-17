package com.delivery.justonebite.review.application.service;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.order.domain.repository.OrderRepository;
import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewAggregationService {

    private final ShopRepository shopRepository;

    @Transactional
    public void recomputeAllShopAvg() {
        int updated = shopRepository.bulkUpdateAllAvg();
    }


    @Transactional
    public void updateShopAvgByShopId(UUID shopId) {
        shopRepository.updateAvgForShop(shopId);
    }

}
