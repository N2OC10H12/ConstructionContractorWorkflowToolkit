package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.common.exception.ForbiddenException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.user.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {

    private final CurrentUserUtil currentUserUtil;

    public ProjectAccessService(CurrentUserUtil currentUserUtil) {
        this.currentUserUtil = currentUserUtil;
    }

    public void requireProjectViewAccess(Project project) {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canViewAllProjects()) {
            return;
        }

        if (role.canWorkOwnProjects()
                && project.getOwner() != null
                && project.getOwner().getId().equals(currentUserUtil.getCurrentUserId())) {
            return;
        }

        throw new ForbiddenException("No access to project");
    }

    public void requireProjectEditAccess(Project project) {
        Role role = currentUserUtil.getCurrentRole();

        if (role.isAdmin()) {
            return;
        }

        if (role.canWorkOwnProjects()
                && project.getOwner() != null
                && project.getOwner().getId().equals(currentUserUtil.getCurrentUserId())) {
            return;
        }

        throw new ForbiddenException("No edit access to project");
    }

    public void requireProjectManagementAccess() {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canManageProjects()) {
            return;
        }

        throw new ForbiddenException("Project management access required");
    }
}