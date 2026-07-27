package com.company.ConstructionContractorWorkflowToolkit.estimate.controller;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CompanyWorkTypeSearchResultResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.CompanyWorkTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estimates/company-work-types")
public class CompanyWorkTypeSearchController {

    private final CompanyWorkTypeService companyWorkTypeService;

    public CompanyWorkTypeSearchController(
            CompanyWorkTypeService companyWorkTypeService) {
        this.companyWorkTypeService = companyWorkTypeService;
    }

    @GetMapping("/search")
    public List<CompanyWorkTypeSearchResultResponse>
    searchSelectableWorkTypes(
            @RequestParam(required = false)
            String query,
            @RequestParam(required = false)
            Integer limit) {

        return companyWorkTypeService.searchSelectableWorkTypes(
                query,
                limit);
    }
}