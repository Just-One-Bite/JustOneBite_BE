package com.delivery.justonebite.shop.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.shop.presentation.dto.request.ShopUpdateRequest;
import com.delivery.justonebite.user.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
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

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "registration_number", nullable = false, length = 50)
    private String registrationNumber;

    @Column(nullable = false, length = 20)
    private String province;

    @Column(nullable = false, length = 20)
    private String city;

    @Column(nullable = false, length = 20)
    private String district;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 20)
    @Pattern(regexp = "^[0-9\\-]+$", message = "전화번호는 숫자와 하이픈만 허용합니다.")
    private String phoneNumber;

    @Column(name = "operating_hour", nullable = false, length = 100)
    private String operatingHour;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(name = "average_rating", precision = 2, scale = 1, nullable = true)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.valueOf(0.0);

    @Enumerated(EnumType.STRING)
    @Column(name = "create_accept_status", nullable = false)
    @Builder.Default
    private AcceptStatus createAcceptStatus = AcceptStatus.PENDING;

    @Column(name = "create_reject_reason", length = 255)
    private String createRejectReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_accept_status", nullable = false)
    @Builder.Default
    private RejectStatus deleteAcceptStatus = RejectStatus.NONE;

    @Column(name = "delete_reject_reason", length = 255)
    private String deleteRejectReason;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ShopCategory> categories = new HashSet<>();



    //가게 등록
    public void addCategory(Category category) {
        ShopCategory shopCategory = ShopCategory.create(this, category);
        if (!this.categories.contains(shopCategory)) {
            this.categories.add(shopCategory);
        }
    }

    //가게 수정
    public void updateInfo(String name, String phone, String operatingHour, String description) {
        if (name != null) this.name = name;
        if (phone != null) this.phoneNumber = phone;
        if (operatingHour != null) this.operatingHour = operatingHour;
        if (description != null) this.description = description;
    }
}
