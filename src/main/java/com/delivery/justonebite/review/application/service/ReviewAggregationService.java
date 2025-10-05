package com.delivery.justonebite.review.application.service;

import com.delivery.justonebite.review.presentation.dto.response.RatingAggResponse;
import com.delivery.justonebite.review.repository.ReviewAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewAggregationService {

    private final ReviewAggregationRepository aggRepo;


    @Transactional(readOnly = true)
    public Map<UUID, RatingAggResponse> getAggByShopIds(List<UUID> shopIds) {
        if (shopIds == null || shopIds.isEmpty()) return Collections.emptyMap();

        return aggRepo.findRatingAggByShopIds(shopIds).stream()
                .collect(Collectors.toMap(
                        ReviewAggregationRepository.ShopRatingAggProjection::getShopId,
                        p -> {
                            double avg = p.getAvgRating() == null ? 0.0 : p.getAvgRating();
                            long cnt = p.getReviewCount() == null ? 0L : p.getReviewCount();
                            double rounded = Math.round(avg * 10.0) / 10.0;
                            return new RatingAggResponse(rounded, cnt);
                        }
                ));
    }

}
