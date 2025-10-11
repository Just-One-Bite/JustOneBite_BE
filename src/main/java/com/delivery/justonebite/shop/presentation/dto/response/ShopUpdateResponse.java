package com.delivery.justonebite.shop.presentation.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ShopUpdateResponse(
        LocalDateTime updatedAt,
        Long updatedBy
) {}
