package com.delivery.justonebite.user.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
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
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "province", nullable = false, length = 20)
    private String province;

    @Column(name = "city", nullable = false, length = 20)
    private String city;

    @Column(name = "district", nullable = false, length = 20)
    private String district;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_default", nullable = false, columnDefinition = "boolean default false")
    private boolean isDefault = false; // 대표 주소 여부 (DDL을 생성시 힌트 제공)

    public void updateIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    private Address(User user, String province, String city, String district, String address, boolean isDefault) {
        this.user = user;
        this.province = province;
        this.city = city;
        this.district = district;
        this.address = address;
        this.isDefault = isDefault;
    }

    public static Address create(User user,
                            String province,
                            String city,
                            String district,
                            String address,
                            boolean isDefault) {
        return new Address(user, province, city, district, address, isDefault);
    }
}
