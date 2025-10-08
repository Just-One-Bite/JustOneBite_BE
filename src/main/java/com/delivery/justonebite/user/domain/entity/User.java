package com.delivery.justonebite.user.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Entity
@Table(name = "h_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @PrePersist
    public void prePersist() {
        if (this.userRole == null) {
            this.userRole = UserRole.CUSTOMER;
        }
    }

    public void updateProfile(UpdateProfileRequest request) {
        this.name = request.name() != null ? request.name() : this.name;
        this.phoneNumber = request.phoneNumber() != null ? request.phoneNumber() : this.phoneNumber;

    }
}
