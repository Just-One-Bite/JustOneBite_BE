package com.delivery.justonebite.user.domain.entity;

import com.delivery.justonebite.common.entity.BaseEntity;
import com.delivery.justonebite.user.presentation.dto.request.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "h_users")
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

    public User(
            String email,
            String name,
            String phoneNumber,
            String password
    ) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public static User toEntity(SignupRequestDto requestDto, String password) {
        return new User(
                requestDto.email(),
                requestDto.name(),
                requestDto.phoneNumber(),
                password
        );
    }

    @PrePersist
    public void prePersist() {
        if (this.userRole == null) {
            this.userRole = UserRole.CUSTOMER;
        }
    }
}
