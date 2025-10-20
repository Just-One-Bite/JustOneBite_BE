package com.delivery.justonebite.user.domain.repository;

import com.delivery.justonebite.user.domain.entity.User;
import com.delivery.justonebite.user.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT COUNT(*) > 0 FROM h_user WHERE email = :email", nativeQuery = true)
    boolean existsByEmailIncludeDeleted(@Param("email") String email);

    @Query(value = "SELECT * FROM h_user WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailIncludeDeleted(@Param("email") String email);

    boolean existsByUserRole(UserRole userRole);
}
