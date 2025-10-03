package com.delivery.justonebite.shop.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Entity
@Table(name = "h_shop_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shop_category_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    private ShopCategory(Shop shop, Category category) {
        this.shop = shop;
        this.category = category;
    }

    public static ShopCategory create(Shop shop, Category category) {
        return ShopCategory.builder()
                .shop(shop)
                .category(category)
                .build();
    }
}

