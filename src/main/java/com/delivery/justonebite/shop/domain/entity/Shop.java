package com.delivery.justonebite.shop.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "h_shop")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shop_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Long owner;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false, length = 5)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    @Pattern(regexp = "^[0-9\\-]+$", message = "전화번호는 숫자와 하이픈만 허용합니다.")
    private String phoneNumber;

    @Column(name = "operating_hour",nullable = false)
    private String operatingHour;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "average_rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "create_accept_status", nullable = false)
    private AcceptStatus createAcceptStatus = AcceptStatus.PENDING;

    @Column(name = "create_reject_reason")
    private String createRejectReason;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "delete_accept_status", nullable = false)
    private RejectStatus deleteAcceptStatus = RejectStatus.NONE;

    @Column(name = "delete_reject_reason")
    private String deleteRejectReason;


    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ShopCategory> categories = new HashSet<>();


    public void addCategory(Category category) {
        ShopCategory shopCategory = ShopCategory.create(this, category);
        if(!this.categories.contains(shopCategory)) this.categories.add(shopCategory);
        this.categories.add(shopCategory);
    }
}
