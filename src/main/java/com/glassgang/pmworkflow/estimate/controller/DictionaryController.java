package com.glassgang.pmworkflow.estimate.controller;

import com.glassgang.pmworkflow.estimate.dto.dictionary.CostElementResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CostRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.ItemTypeResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.TaxRateResponse;
import com.glassgang.pmworkflow.estimate.service.DictionaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}