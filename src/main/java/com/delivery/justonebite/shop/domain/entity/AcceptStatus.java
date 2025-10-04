package com.delivery.justonebite.shop.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcceptStatus {
    PENDING,
    APPROVED,
    REJECTED
}
