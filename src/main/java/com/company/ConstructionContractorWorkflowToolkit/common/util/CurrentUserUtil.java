package com.company.ConstructionContractorWorkflowToolkit.common.util;

import com.company.ConstructionContractorWorkflowToolkit.user.entity.AppUser;
import com.company.ConstructionContractorWorkflowToolkit.user.entity.Role;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserUtil {

    public AppUser getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user");
        }

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof AppUser user) {
            return user;
        }

        throw new AuthenticationCredentialsNotFoundException("No authenticated user");
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    public String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public Role getCurrentRole() {
        return Role.from(getCurrentUserRole());
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentRole().isAdmin();
    }

    public boolean isCurrentUserPm() {
        return getCurrentRole().isPm();
    }

    public boolean isCurrentUserPmManager() {
        return getCurrentRole().isPmManager();
    }

    public boolean isCurrentUserPmViewer() {
        return getCurrentRole().isPmViewer();
    }

    public boolean isCurrentUserSupervisor() {
        return getCurrentRole().isSupervisor();
    }

    public boolean isCurrentUserEstimator() {
        return getCurrentRole().isEstimator();
    }

    public boolean isCurrentUserEstimateManager() {
        return getCurrentRole().isEstimateManager();
    }

    public boolean isCurrentUserEstimateViewer() {
        return getCurrentRole().isEstimateViewer();
    }

    public boolean canCurrentUserViewAllProjects() {
        return getCurrentRole().canViewAllProjects();
    }

    public boolean canCurrentUserManageProjects() {
        return getCurrentRole().canManageProjects();
    }

    public boolean canCurrentUserWorkOwnProjects() {
        return getCurrentRole().canWorkOwnProjects();
    }

    public boolean canCurrentUserViewAllEstimates() {
        return getCurrentRole().canViewAllEstimates();
    }

    public boolean canCurrentUserWorkOwnEstimates() {
        return getCurrentRole().canWorkOwnEstimates();
    }

    public boolean canCurrentUserManageEstimateDictionaries() {
        return getCurrentRole().canManageEstimateDictionaries();
    }

    public boolean canCurrentUserConvertEstimateToProject() {
        return getCurrentRole().canConvertEstimateToProject();
    }
}