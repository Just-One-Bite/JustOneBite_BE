package com.delivery.justonebite.user.domain.entity;

import com.delivery.justonebite.shop.domain.entity.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "h_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @Column(name = "province", nullable = false, length = 20)
    private String province;

    @Column(name = "city", nullable = false, length = 20)
    private String city;

    @Column(name = "district", nullable = false, length = 20)
    private String district;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false; // 대표 주소 여부

    public void updateIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
