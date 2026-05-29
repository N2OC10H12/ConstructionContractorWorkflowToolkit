package com.glassgang.pmworkflow.user.entity;

import java.util.Arrays;

public enum Role {
    ADMIN,
    PM,
    PROJECT_VIEWER,
    SUPERVISOR,
    ESTIMATOR,
    ESTIMATOR_SUPERVISOR,
    ESTIMATE_VIEWER;
    
    public static Role from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + value));
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isSupervisor() {
        return this == SUPERVISOR;
    }

    public boolean isPm() {
        return this == PM;
    }
}