package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.ForbiddenException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import org.springframework.stereotype.Service;

@Service
public class EstimatePdfTemplateAccessService {

    private final CurrentUserUtil currentUserUtil;

    public EstimatePdfTemplateAccessService(CurrentUserUtil currentUserUtil) {
        this.currentUserUtil = currentUserUtil;
    }

    public void requireAdminAccess() {
        if (!currentUserUtil.isCurrentUserAdmin()) {
            throw new ForbiddenException("Only admins can manage estimate PDF templates");
        }
    }
}