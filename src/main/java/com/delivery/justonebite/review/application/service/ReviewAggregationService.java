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
    private final OrderRepository orderRepository;


    @Transactional
    public void recomputeAllShopAvg() {
        int updated = shopRepository.bulkUpdateAllAvg();
        int reset = shopRepository.bulkResetAvgForZeroReview();
    }

    @Transactional
    public void updateShopAvg(UUID orderId) {
        UUID shopId = orderRepository.findShopIdByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        int updated = shopRepository.updateShopAvgById(shopId);
        if (updated == 0) {
            shopRepository.resetShopAvgToZero(shopId);
        }
    }

}
