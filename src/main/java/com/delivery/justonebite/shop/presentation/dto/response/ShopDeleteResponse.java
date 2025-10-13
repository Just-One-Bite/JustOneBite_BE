package com.delivery.justonebite.shop.presentation.dto.response;

import com.delivery.justonebite.shop.domain.entity.RejectStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ShopDeleteResponse(
        LocalDateTime deletedAt,
        RejectStatus deleteAcceptStatus
) {}
