package com.delivery.justonebite.user.domain.entity;

import com.delivery.justonebite.global.common.entity.BaseEntity;
import com.delivery.justonebite.user.presentation.dto.request.UpdateProfileRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Getter
@SuperBuilder
@Entity
@Table(name = "h_user")
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLRestriction("deleted_at is NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(
            name = "role",
            nullable = false
    )
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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

    public void updatePassword(String password) {
        this.password = password;
    }

    public void softDelete(Long deleterId) {
        super.markDeleted(deleterId);
    }
}
