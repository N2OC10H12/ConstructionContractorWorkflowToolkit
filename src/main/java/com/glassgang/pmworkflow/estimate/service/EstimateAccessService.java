package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.exception.ForbiddenException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.user.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class EstimateAccessService {

    private final CurrentUserUtil currentUserUtil;

    public EstimateAccessService(CurrentUserUtil currentUserUtil) {
        this.currentUserUtil = currentUserUtil;
    }

    public void requireBidViewAccess(Bid bid) {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canViewAllEstimates()) {
            return;
        }

        if (role.canWorkOwnEstimates()
                && bid.getCreatedByUserId() != null
                && bid.getCreatedByUserId().equals(currentUserUtil.getCurrentUserId())) {
            return;
        }

        throw new ForbiddenException("No access to estimate");
    }

    public void requireBidEditAccess(Bid bid) {
        Role role = currentUserUtil.getCurrentRole();

        if (role.isAdmin()) {
            return;
        }

        if (role.canWorkOwnEstimates()
                && bid.getCreatedByUserId() != null
                && bid.getCreatedByUserId().equals(currentUserUtil.getCurrentUserId())) {
            return;
        }

        throw new ForbiddenException("No edit access to estimate");
    }

    public void requireEstimateCreateAccess() {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canWorkOwnEstimates()) {
            return;
        }

        throw new ForbiddenException("No access to create estimate");
    }

    public void requireEstimateDictionaryManageAccess() {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canManageEstimateDictionaries()) {
            return;
        }

        throw new ForbiddenException("Estimate dictionary management access required");
    }

    public void requireEstimateToProjectConversionAccess() {
        Role role = currentUserUtil.getCurrentRole();

        if (role.canConvertEstimateToProject()) {
            return;
        }

        throw new ForbiddenException("No access to convert estimate to project");
    }
}