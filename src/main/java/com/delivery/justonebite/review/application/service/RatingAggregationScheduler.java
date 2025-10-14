package com.delivery.justonebite.review.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingAggregationScheduler {

    private final ReviewAggregationService service;

    @Scheduled(cron = " 0 0 6 * * *", zone = "Asia/Seoul")
    public void recomputeNightly() {
        long t0 = System.currentTimeMillis();
        try {
            service.recomputeAllShopAvg();
            log.info("[RatingAggregationScheduler] 완료 — 소요 시간: {} ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            log.error("[RatingAggregationScheduler] 실패 ", e);
        }
    }
}
