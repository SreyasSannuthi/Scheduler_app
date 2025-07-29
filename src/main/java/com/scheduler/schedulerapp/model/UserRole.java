package com.scheduler.schedulerapp.model;

public enum UserRole {
    ADMIN("admin"),
    DOCTOR("doctor"),
    RECEPTIONIST("receptionist"),
    CUSTOMER_CARE("customer_care");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}