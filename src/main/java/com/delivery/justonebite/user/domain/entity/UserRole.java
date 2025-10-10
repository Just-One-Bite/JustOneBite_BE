package com.delivery.justonebite.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    CUSTOMER(Role.CUSTOMER, "고객"),
    OWNER(Role.OWNER, "점주"),
    MANAGER(Role.MANAGER, "관리자"),
    MASTER(Role.MASTER, "최종 관리자");

    private final String role;
    private final String Description;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(a -> a.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

    private static class Role {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }

    public boolean isCustomer() {
        return this.role.equals(Role.CUSTOMER);
    }

    public boolean isOwner() {
        return this.role.equals(Role.OWNER);
    }

    public boolean isManager() {
        return this.role.equals(Role.MANAGER);
    }

    public boolean isMaster() {
        return this.role.equals(Role.MASTER);
    }

    public boolean isAdmin() {
        return this.role.equals(Role.MANAGER) || this.role.equals(Role.MASTER);
    }
}
