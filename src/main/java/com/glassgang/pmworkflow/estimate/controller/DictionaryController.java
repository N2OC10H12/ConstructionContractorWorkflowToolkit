package com.glassgang.pmworkflow.estimate.controller;

import com.glassgang.pmworkflow.estimate.dto.dictionary.CostElementResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CostRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateTaxRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.ItemTypeResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.TaxRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateTaxRateRequest;
import com.glassgang.pmworkflow.estimate.service.DictionaryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

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
    public List<CostRateResponse> getCostRates() {
        return dictionaryService.getCostRates();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/item-types")
    public ItemTypeResponse createItemType(
            @Valid @RequestBody CreateItemTypeRequest request) {
        return dictionaryService.createItemType(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/item-types/{itemTypeId}")
    public ItemTypeResponse updateItemType(
            @PathVariable UUID itemTypeId,
            @Valid @RequestBody UpdateItemTypeRequest request) {
        return dictionaryService.updateItemType(itemTypeId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/item-types/{itemTypeId}")
    public void deleteItemType(
            @PathVariable UUID itemTypeId) {
        dictionaryService.deleteItemType(itemTypeId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cost-elements")
    public CostElementResponse createCostElement(
            @Valid @RequestBody CreateCostElementRequest request) {
        return dictionaryService.createCostElement(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/cost-elements/{costElementId}")
    public CostElementResponse updateCostElement(
            @PathVariable UUID costElementId,
            @Valid @RequestBody UpdateCostElementRequest request) {
        return dictionaryService.updateCostElement(costElementId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cost-elements/{costElementId}")
    public void deleteCostElement(
            @PathVariable UUID costElementId) {
        dictionaryService.deleteCostElement(costElementId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cost-rates")
    public CostRateResponse createCostRate(
            @Valid @RequestBody CreateCostRateRequest request) {
        return dictionaryService.createCostRate(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/cost-rates/{costRateId}")
    public CostRateResponse updateCostRate(
            @PathVariable UUID costRateId,
            @Valid @RequestBody UpdateCostRateRequest request) {
        return dictionaryService.updateCostRate(costRateId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cost-rates/{costRateId}")
    public void deleteCostRate(
            @PathVariable UUID costRateId) {
        dictionaryService.deleteCostRate(costRateId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/tax-rates")
    public TaxRateResponse createTaxRate(
            @Valid @RequestBody CreateTaxRateRequest request) {
        return dictionaryService.createTaxRate(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/tax-rates/{taxRateId}")
    public TaxRateResponse updateTaxRate(
            @PathVariable UUID taxRateId,
            @Valid @RequestBody UpdateTaxRateRequest request) {
        return dictionaryService.updateTaxRate(taxRateId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tax-rates/{taxRateId}")
    public void deleteTaxRate(
            @PathVariable UUID taxRateId) {
        dictionaryService.deleteTaxRate(taxRateId);
    }
}