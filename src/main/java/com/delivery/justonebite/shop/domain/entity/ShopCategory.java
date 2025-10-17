package com.delivery.justonebite.shop.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;
@Entity
@Table(name = "h_shop_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCategory {

    @EmbeddedId
    private ShopCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shopId")
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public ShopCategory(Shop shop, Category category) {
        this.shop = shop;
        this.category = category;
        this.id = new ShopCategoryId(shop.getId(), category.getId());
    }


    public static ShopCategory create(Shop shop, Category category) {
        return ShopCategory.builder()
                .shop(shop)
                .category(category)
                .build();
    }

    // category  중복 방지 기준
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ShopCategory)) return false;
        ShopCategory shopCategory = (ShopCategory) o;
        return Objects.equals(shop.getId(),shopCategory.shop.getId())&&
                Objects.equals(category.getId(), shopCategory.category.getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(shop.getId(), category.getId());
    }
}

