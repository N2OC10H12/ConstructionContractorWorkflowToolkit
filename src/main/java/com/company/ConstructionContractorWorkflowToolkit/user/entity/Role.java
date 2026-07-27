package com.company.ConstructionContractorWorkflowToolkit.user.entity;

import java.util.Arrays;

public enum Role {
    ADMIN,

    PM,
    PM_MANAGER,
    PM_VIEWER,

    SUPERVISOR, // temporary legacy role, remove later

    ESTIMATOR,
    ESTIMATE_MANAGER,
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

    public boolean isPm() {
        return this == PM;
    }

    public boolean isPmManager() {
        return this == PM_MANAGER;
    }

    public boolean isPmViewer() {
        return this == PM_VIEWER;
    }

    public boolean isSupervisor() {
        return this == SUPERVISOR;
    }

    public boolean isEstimator() {
        return this == ESTIMATOR;
    }

    public boolean isEstimateManager() {
        return this == ESTIMATE_MANAGER;
    }

    public boolean isEstimateViewer() {
        return this == ESTIMATE_VIEWER;
    }

    public boolean canViewAllProjects() {
        return this == ADMIN
                || this == PM_MANAGER
                || this == PM_VIEWER
                || this == SUPERVISOR;
    }

    public boolean canManageProjects() {
        return this == ADMIN
                || this == PM_MANAGER;
    }

    public boolean canWorkOwnProjects() {
        return this == ADMIN
                || this == PM
                || this == PM_MANAGER;
    }

    public boolean canViewAllEstimates() {
        return this == ADMIN
                || this == ESTIMATE_MANAGER
                || this == ESTIMATE_VIEWER;
    }

    public boolean canWorkOwnEstimates() {
        return this == ADMIN
                || this == ESTIMATOR
                || this == ESTIMATE_MANAGER;
    }

    public boolean canManageEstimateDictionaries() {
        return this == ADMIN
                || this == ESTIMATE_MANAGER;
    }

    public boolean canConvertEstimateToProject() {
        return this == ADMIN
                || this == ESTIMATE_MANAGER;
    }
}