package com.company.ConstructionContractorWorkflowToolkit.estimate.controller;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.ConstructionObjectTypeResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CostElementResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CostRateResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateConstructionObjectTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateCostElementRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateCostRateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.CreateTaxRateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.TaxRateResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UnitOfMeasureResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateConstructionObjectTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateCostElementRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateCostRateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary.UpdateTaxRateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.DictionaryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(
            DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/tax-rates")
    public List<TaxRateResponse> getTaxRates() {
        return dictionaryService.getTaxRates();
    }

    @GetMapping("/cost-elements")
    public List<CostElementResponse> getCostElements() {
        return dictionaryService.getCostElements();
    }

    @GetMapping("/cost-rates")
    public List<CostRateResponse> getCostRates(
            @RequestParam(required = false) UUID costElementId) {

        return dictionaryService.getCostRates(costElementId);
    }

    @PostMapping("/cost-elements")
    public CostElementResponse createCostElement(
            @Valid @RequestBody CreateCostElementRequest request) {
        return dictionaryService.createCostElement(request);
    }

    @PatchMapping("/cost-elements/{costElementId}")
    public CostElementResponse updateCostElement(
            @PathVariable UUID costElementId,
            @Valid @RequestBody UpdateCostElementRequest request) {
        return dictionaryService.updateCostElement(costElementId, request);
    }

    @DeleteMapping("/cost-elements/{costElementId}")
    public void deleteCostElement(
            @PathVariable UUID costElementId) {
        dictionaryService.deleteCostElement(costElementId);
    }

    @PostMapping("/cost-rates")
    public CostRateResponse createCostRate(
            @Valid @RequestBody CreateCostRateRequest request) {
        return dictionaryService.createCostRate(request);
    }

    @PatchMapping("/cost-rates/{costRateId}")
    public CostRateResponse updateCostRate(
            @PathVariable UUID costRateId,
            @Valid @RequestBody UpdateCostRateRequest request) {
        return dictionaryService.updateCostRate(costRateId, request);
    }

    @DeleteMapping("/cost-rates/{costRateId}")
    public void deleteCostRate(
            @PathVariable UUID costRateId) {
        dictionaryService.deleteCostRate(costRateId);
    }

    @PostMapping("/tax-rates")
    public TaxRateResponse createTaxRate(
            @Valid @RequestBody CreateTaxRateRequest request) {
        return dictionaryService.createTaxRate(request);
    }

    @PatchMapping("/tax-rates/{taxRateId}")
    public TaxRateResponse updateTaxRate(
            @PathVariable UUID taxRateId,
            @Valid @RequestBody UpdateTaxRateRequest request) {
        return dictionaryService.updateTaxRate(taxRateId, request);
    }

    @DeleteMapping("/tax-rates/{taxRateId}")
    public void deleteTaxRate(
            @PathVariable UUID taxRateId) {
        dictionaryService.deleteTaxRate(taxRateId);
    }

    @GetMapping("/unit-of-measures")
    public List<UnitOfMeasureResponse> getUnitOfMeasures() {
        return dictionaryService.getUnitOfMeasures();
    }

    @GetMapping("/construction-object-types")
    public List<ConstructionObjectTypeResponse> getConstructionObjectTypes() {
        return dictionaryService.getConstructionObjectTypes();
    }

    @PostMapping("/construction-object-types")
    public ConstructionObjectTypeResponse createConstructionObjectType(
            @Valid @RequestBody CreateConstructionObjectTypeRequest request) {
        return dictionaryService.createConstructionObjectType(request);
    }

    @PatchMapping("/construction-object-types/{constructionObjectTypeId}")
    public ConstructionObjectTypeResponse updateConstructionObjectType(
            @PathVariable UUID constructionObjectTypeId,
            @Valid @RequestBody UpdateConstructionObjectTypeRequest request) {
        return dictionaryService.updateConstructionObjectType(constructionObjectTypeId, request);
    }

    @DeleteMapping("/construction-object-types/{constructionObjectTypeId}")
    public void deleteConstructionObjectType(
            @PathVariable UUID constructionObjectTypeId) {
        dictionaryService.deleteConstructionObjectType(constructionObjectTypeId);
    }
}