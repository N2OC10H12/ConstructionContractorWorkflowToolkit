package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.ForbiddenException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.Bid;
import com.company.ConstructionContractorWorkflowToolkit.user.entity.Role;
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