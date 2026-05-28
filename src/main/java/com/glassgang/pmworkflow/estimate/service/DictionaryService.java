package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.estimate.dto.dictionary.CostElementResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CostRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.ItemTypeResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.TaxRateResponse;
import com.glassgang.pmworkflow.estimate.entity.CostElement;
import com.glassgang.pmworkflow.estimate.entity.CostRate;
import com.glassgang.pmworkflow.estimate.entity.ItemType;
import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import com.glassgang.pmworkflow.estimate.repository.CostElementRepository;
import com.glassgang.pmworkflow.estimate.repository.CostRateRepository;
import com.glassgang.pmworkflow.estimate.repository.ItemTypeRepository;
import com.glassgang.pmworkflow.estimate.repository.TaxRateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    private final ItemTypeRepository itemTypeRepository;
    private final TaxRateRepository taxRateRepository;
    private final CostElementRepository costElementRepository;
    private final CostRateRepository costRateRepository;

    public DictionaryService(
            ItemTypeRepository itemTypeRepository,
            TaxRateRepository taxRateRepository,
            CostElementRepository costElementRepository,
            CostRateRepository costRateRepository) {
        this.itemTypeRepository = itemTypeRepository;
        this.taxRateRepository = taxRateRepository;
        this.costElementRepository = costElementRepository;
        this.costRateRepository = costRateRepository;
    }

    public List<ItemTypeResponse> getItemTypes() {
        return itemTypeRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toItemTypeResponse)
                .toList();
    }

    public List<TaxRateResponse> getTaxRates() {
        return taxRateRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toTaxRateResponse)
                .toList();
    }

    public List<CostElementResponse> getCostElements() {
        return costElementRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toCostElementResponse)
                .toList();
    }

    public List<CostRateResponse> getCostRates() {
        return costRateRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toCostRateResponse)
                .toList();
    }

    private ItemTypeResponse toItemTypeResponse(ItemType itemType) {
        ItemTypeResponse response = new ItemTypeResponse();
        response.setItemTypeId(itemType.getItemTypeId());
        response.setCode(itemType.getCode());
        response.setName(itemType.getName());
        response.setDescription(itemType.getDescription());
        response.setIsActive(itemType.getIsActive());
        return response;
    }

    private TaxRateResponse toTaxRateResponse(TaxRate taxRate) {
        TaxRateResponse response = new TaxRateResponse();
        response.setTaxRateId(taxRate.getTaxRateId());
        response.setCode(taxRate.getCode());
        response.setName(taxRate.getName());
        response.setRatePercent(taxRate.getRatePercent());
        response.setIsDefault(taxRate.getIsDefault());
        response.setIsActive(taxRate.getIsActive());
        return response;
    }

    private CostElementResponse toCostElementResponse(CostElement costElement) {
        CostElementResponse response = new CostElementResponse();
        response.setCostElementId(costElement.getCostElementId());
        response.setCode(costElement.getCode());
        response.setName(costElement.getName());
        response.setDescription(costElement.getDescription());
        response.setIsActive(costElement.getIsActive());
        return response;
    }

    private CostRateResponse toCostRateResponse(CostRate costRate) {
        CostRateResponse response = new CostRateResponse();
        response.setCostRateId(costRate.getCostRateId());
        response.setCode(costRate.getCode());
        response.setName(costRate.getName());
        response.setRateAmount(costRate.getRateAmount());
        response.setRateUnit(costRate.getRateUnit());
        response.setIsActive(costRate.getIsActive());
        return response;
    }
}