package com.company.ConstructionContractorWorkflowToolkit.estimate.controller;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CompanyWorkTypeDivisionResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CompanyWorkTypeResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateCompanyWorkTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateCompanyWorkTypeDivisionRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateCompanyWorkTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.CompanyWorkTypeDivisionService;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.CompanyWorkTypeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/dictionaries")
public class CompanyWorkTypeAdminController {

    private final CompanyWorkTypeDivisionService divisionService;
    private final CompanyWorkTypeService companyWorkTypeService;

    public CompanyWorkTypeAdminController(
            CompanyWorkTypeDivisionService divisionService,
            CompanyWorkTypeService companyWorkTypeService) {
        this.divisionService = divisionService;
        this.companyWorkTypeService = companyWorkTypeService;
    }

    @GetMapping("/company-work-type-divisions")
    public List<CompanyWorkTypeDivisionResponse> getDivisions() {
        return divisionService.getDivisions();
    }

    @PatchMapping(
            "/company-work-type-divisions/{divisionCode}")
    public CompanyWorkTypeDivisionResponse updateDivision(
            @PathVariable String divisionCode,
            @Valid @RequestBody
            UpdateCompanyWorkTypeDivisionRequest request) {

        return divisionService.updateDivision(
                divisionCode,
                request);
    }

    @GetMapping("/company-work-types")
    public List<CompanyWorkTypeResponse> getWorkTypes(
            @RequestParam(required = false)
            String divisionCode) {

        return companyWorkTypeService.getWorkTypes(
                divisionCode);
    }

    @GetMapping(
            "/company-work-types/{companyWorkTypeId}")
    public CompanyWorkTypeResponse getWorkType(
            @PathVariable UUID companyWorkTypeId) {

        return companyWorkTypeService.getWorkType(
                companyWorkTypeId);
    }

    @PostMapping("/company-work-types")
    public CompanyWorkTypeResponse createWorkType(
            @Valid @RequestBody
            CreateCompanyWorkTypeRequest request) {

        return companyWorkTypeService.createWorkType(
                request);
    }

    @PatchMapping(
            "/company-work-types/{companyWorkTypeId}")
    public CompanyWorkTypeResponse updateWorkType(
            @PathVariable UUID companyWorkTypeId,
            @Valid @RequestBody
            UpdateCompanyWorkTypeRequest request) {

        return companyWorkTypeService.updateWorkType(
                companyWorkTypeId,
                request);
    }

    @PostMapping(
            "/company-work-types/{companyWorkTypeId}"
                    + "/restore-original-name")
    public CompanyWorkTypeResponse restoreOriginalName(
            @PathVariable UUID companyWorkTypeId) {

        return companyWorkTypeService.restoreOriginalName(
                companyWorkTypeId);
    }

    @DeleteMapping(
            "/company-work-types/{companyWorkTypeId}")
    public void deleteWorkType(
            @PathVariable UUID companyWorkTypeId) {

        companyWorkTypeService.deleteWorkType(
                companyWorkTypeId);
    }
}