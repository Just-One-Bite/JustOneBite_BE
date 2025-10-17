package com.delivery.justonebite.shop.domain.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;


@Entity
@Table(
        name = "h_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = {"category_name"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "category_name", nullable = false, length = 50, unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShopCategory> shopCategories = new HashSet<>();

    @Builder
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
