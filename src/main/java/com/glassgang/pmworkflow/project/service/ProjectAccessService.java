package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.common.exception.ForbiddenException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.user.entity.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {

    private final CurrentUserUtil currentUserUtil;

    public ProjectAccessService(CurrentUserUtil currentUserUtil) {
        this.currentUserUtil = currentUserUtil;
    }

    private AppUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof AppUser user) {
            return user;
        }

        throw new ForbiddenException("Unauthenticated");
    }

    public void requireProjectViewAccess(Project project) {
        AppUser user = getCurrentUser();

        if (isAdmin(user) || isSupervisor(user)) {
            return;
        }

        if (isPm(user) && project.getOwner().getId().equals(user.getId())) {
            return;
        }

        throw new ForbiddenException("No access to project");
    }

    public void requireProjectEditAccess(Project project) {
        AppUser user = getCurrentUser();

        if (isAdmin(user)) {
            return;
        }

        if (isPm(user) && project.getOwner().getId().equals(user.getId())) {
            return;
        }

        throw new ForbiddenException("No edit access to project");
    }

    private boolean isAdmin(AppUser user) {
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    private boolean isSupervisor(AppUser user) {
        return "SUPERVISOR".equalsIgnoreCase(user.getRole());
    }

    private boolean isPm(AppUser user) {
        return "PM".equalsIgnoreCase(user.getRole());
    }
}