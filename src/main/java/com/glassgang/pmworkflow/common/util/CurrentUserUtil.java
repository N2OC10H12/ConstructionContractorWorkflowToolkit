package com.glassgang.pmworkflow.common.util;

import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.entity.Role;
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

    public boolean isCurrentUserSupervisor() {
        return getCurrentRole().isSupervisor();
    }

    public boolean isCurrentUserPm() {
        return getCurrentRole().isPm();
    }
}