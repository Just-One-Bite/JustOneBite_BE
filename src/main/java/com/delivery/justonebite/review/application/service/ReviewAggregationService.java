package com.delivery.justonebite.review.application.service;

import com.delivery.justonebite.shop.domain.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewAggregationService {

    private final ShopRepository shopRepository;


    @Transactional
    public void recomputeAllShopAvg() {
        int updated = shopRepository.bulkUpdateAllAvg();
    }

}
