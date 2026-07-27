package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.BusinessRuleException;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.ConstructionType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.DepartmentCode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimateTaxModel;
import org.springframework.stereotype.Service;

@Service
public class EstimateTaxModelResolver {

    public EstimateTaxModel resolveTaxModel(
            DepartmentCode departmentCode,
            ConstructionType constructionType) {

        if (departmentCode == DepartmentCode.C) {
            if (constructionType == ConstructionType.NEW_CONSTRUCTION) {
                return EstimateTaxModel.MATERIAL_COST_ONLY;
            }

            if (constructionType == ConstructionType.REMODELING) {
                return EstimateTaxModel.ALL_SELL_PRICE;
            }
        }

        if (departmentCode == DepartmentCode.R) {
            if (constructionType == ConstructionType.NEW_CONSTRUCTION
                    || constructionType == ConstructionType.REMODELING) {
                return EstimateTaxModel.MATERIAL_COST_ONLY;
            }
        }

        if (departmentCode == DepartmentCode.S) {
            if (constructionType == ConstructionType.REPAIR_MAINTENANCE) {
                return EstimateTaxModel.ALL_SELL_PRICE;
            }
        }

        throw new BusinessRuleException("Unsupported department and construction type tax rule");
    }
}