package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.exception.BusinessRuleException;
import com.glassgang.pmworkflow.estimate.enums.ConstructionType;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
import com.glassgang.pmworkflow.estimate.enums.EstimateTaxModel;
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