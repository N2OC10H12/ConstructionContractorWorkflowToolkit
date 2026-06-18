package com.glassgang.pmworkflow.estimate.controller;

import com.glassgang.pmworkflow.estimate.dto.dictionary.CostElementResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CostRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateTaxRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.ItemTypeResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.TaxRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UnitOfMeasureResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateTaxRateRequest;
import com.glassgang.pmworkflow.estimate.service.DictionaryService;

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

    @GetMapping("/item-types")
    public List<ItemTypeResponse> getItemTypes() {
        return dictionaryService.getItemTypes();
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

    @PostMapping("/item-types")
    public ItemTypeResponse createItemType(
            @Valid @RequestBody CreateItemTypeRequest request) {
        return dictionaryService.createItemType(request);
    }

    @PatchMapping("/item-types/{itemTypeId}")
    public ItemTypeResponse updateItemType(
            @PathVariable UUID itemTypeId,
            @Valid @RequestBody UpdateItemTypeRequest request) {
        return dictionaryService.updateItemType(itemTypeId, request);
    }

    @DeleteMapping("/item-types/{itemTypeId}")
    public void deleteItemType(
            @PathVariable UUID itemTypeId) {
        dictionaryService.deleteItemType(itemTypeId);
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
}