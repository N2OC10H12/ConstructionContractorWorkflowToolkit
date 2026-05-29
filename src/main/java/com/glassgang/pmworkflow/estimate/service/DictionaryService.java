package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.estimate.dto.dictionary.CostElementResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CostRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateCostRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.ItemTypeResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.TaxRateResponse;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostElementRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateCostRateRequest;
import com.glassgang.pmworkflow.estimate.entity.CostElement;
import com.glassgang.pmworkflow.estimate.entity.CostRate;
import com.glassgang.pmworkflow.estimate.entity.ItemType;
import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import com.glassgang.pmworkflow.estimate.repository.CostElementRepository;
import com.glassgang.pmworkflow.estimate.repository.CostRateRepository;
import com.glassgang.pmworkflow.estimate.repository.ItemTypeRepository;
import com.glassgang.pmworkflow.estimate.repository.TaxRateRepository;
import com.glassgang.pmworkflow.common.exception.BusinessRuleException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.CreateTaxRateRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.dictionary.UpdateTaxRateRequest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class DictionaryService {

    private final ItemTypeRepository itemTypeRepository;
    private final TaxRateRepository taxRateRepository;
    private final CostElementRepository costElementRepository;
    private final CostRateRepository costRateRepository;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    
    public DictionaryService(
            ItemTypeRepository itemTypeRepository,
            TaxRateRepository taxRateRepository,
            CostElementRepository costElementRepository,
            CostRateRepository costRateRepository,
            BidRevisionItemRepository bidRevisionItemRepository) {
        this.itemTypeRepository = itemTypeRepository;
        this.taxRateRepository = taxRateRepository;
        this.costElementRepository = costElementRepository;
        this.costRateRepository = costRateRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
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
        response.setRateUnit(costRate.getRateUnit().name());
        response.setIsActive(costRate.getIsActive());
        return response;
    }

    @Transactional
    public ItemTypeResponse createItemType(CreateItemTypeRequest request) {

        String code = normalizeCode(request.getCode());

        if (itemTypeRepository.existsByCodeAndIsDeletedFalse(code)) {
            throw new BusinessRuleException("Item type code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        ItemType itemType = new ItemType();
        itemType.setItemTypeId(UUID.randomUUID());
        itemType.setCode(code);
        itemType.setName(request.getName().trim());
        itemType.setDescription(request.getDescription());
        itemType.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        itemType.setIsDeleted(false);
        itemType.setCreatedAtUtc(now);
        itemType.setUpdatedAtUtc(now);

        ItemType saved = itemTypeRepository.save(itemType);

        return toItemTypeResponse(saved);
    }

    @Transactional
    public ItemTypeResponse updateItemType(UUID itemTypeId, UpdateItemTypeRequest request) {

        ItemType itemType = itemTypeRepository.findById(itemTypeId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Item type not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            itemTypeRepository.findByCodeAndIsDeletedFalse(code)
                    .filter(existing -> !existing.getItemTypeId().equals(itemTypeId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("Item type code already exists");
                    });

            itemType.setCode(code);
        }

        if (request.getName() != null) {
            itemType.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            itemType.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            itemType.setIsActive(request.getIsActive());
        }

        itemType.setUpdatedAtUtc(LocalDateTime.now());

        ItemType saved = itemTypeRepository.save(itemType);

        return toItemTypeResponse(saved);
    }

    @Transactional
    public void deleteItemType(UUID itemTypeId) {

        ItemType itemType = itemTypeRepository.findById(itemTypeId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Item type not found"));

        if (bidRevisionItemRepository.existsByItemType_ItemTypeIdAndIsDeletedFalse(itemTypeId)) {
            throw new BusinessRuleException("Cannot delete item type because it is used by estimate items");
        }

        LocalDateTime now = LocalDateTime.now();

        itemType.setIsActive(false);
        itemType.setIsDeleted(true);
        itemType.setDeletedAtUtc(now);
        itemType.setUpdatedAtUtc(now);

        itemTypeRepository.save(itemType);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    @Transactional
    public CostElementResponse createCostElement(CreateCostElementRequest request) {

        String code = normalizeCode(request.getCode());

        if (costElementRepository.existsByCodeAndIsDeletedFalse(code)) {
            throw new BusinessRuleException("Cost element code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        CostElement costElement = new CostElement();
        costElement.setCostElementId(UUID.randomUUID());
        costElement.setCode(code);
        costElement.setName(request.getName().trim());
        costElement.setDescription(request.getDescription());
        costElement.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        costElement.setIsDeleted(false);
        costElement.setCreatedAtUtc(now);
        costElement.setUpdatedAtUtc(now);

        CostElement saved = costElementRepository.save(costElement);

        return toCostElementResponse(saved);
    }

    @Transactional
    public CostElementResponse updateCostElement(
            UUID costElementId,
            UpdateCostElementRequest request) {

        CostElement costElement = costElementRepository.findById(costElementId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        if (request.getCode() != null) {

            String code = normalizeCode(request.getCode());

            costElementRepository.findByCodeAndIsDeletedFalse(code)
                    .filter(existing -> !existing.getCostElementId().equals(costElementId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException(
                                "Cost element code already exists");
                    });

            costElement.setCode(code);
        }

        if (request.getName() != null) {
            costElement.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            costElement.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            costElement.setIsActive(request.getIsActive());
        }

        costElement.setUpdatedAtUtc(LocalDateTime.now());

        CostElement saved = costElementRepository.save(costElement);

        return toCostElementResponse(saved);
    }

    @Transactional
    public void deleteCostElement(UUID costElementId) {

        CostElement costElement = costElementRepository.findById(costElementId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        LocalDateTime now = LocalDateTime.now();

        costElement.setIsActive(false);
        costElement.setIsDeleted(true);
        costElement.setDeletedAtUtc(now);
        costElement.setUpdatedAtUtc(now);

        costElementRepository.save(costElement);
    }

    @Transactional
    public CostRateResponse createCostRate(CreateCostRateRequest request) {

        String code = normalizeCode(request.getCode());

        if (costRateRepository.existsByCodeAndIsDeletedFalse(code)) {
            throw new BusinessRuleException("Cost rate code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        CostRate costRate = new CostRate();
        costRate.setCostRateId(UUID.randomUUID());
        costRate.setCode(code);
        costRate.setName(request.getName().trim());
        costRate.setDescription(request.getDescription());
        costRate.setRateAmount(request.getRateAmount());
        costRate.setRateUnit(request.getRateUnit());
        costRate.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        costRate.setIsDeleted(false);
        costRate.setCreatedAtUtc(now);
        costRate.setUpdatedAtUtc(now);

        CostRate saved = costRateRepository.save(costRate);

        return toCostRateResponse(saved);
    }

    @Transactional
    public CostRateResponse updateCostRate(
            UUID costRateId,
            UpdateCostRateRequest request) {

        CostRate costRate = costRateRepository.findById(costRateId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Cost rate not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            costRateRepository.findByCodeAndIsDeletedFalse(code)
                    .filter(existing -> !existing.getCostRateId().equals(costRateId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("Cost rate code already exists");
                    });

            costRate.setCode(code);
        }

        if (request.getName() != null) {
            costRate.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            costRate.setDescription(request.getDescription());
        }

        if (request.getRateAmount() != null) {
            costRate.setRateAmount(request.getRateAmount());
        }

        if (request.getRateUnit() != null) {
            costRate.setRateUnit(request.getRateUnit());
        }

        if (request.getIsActive() != null) {
            costRate.setIsActive(request.getIsActive());
        }

        costRate.setUpdatedAtUtc(LocalDateTime.now());

        CostRate saved = costRateRepository.save(costRate);

        return toCostRateResponse(saved);
    }

    @Transactional
    public void deleteCostRate(UUID costRateId) {

        CostRate costRate = costRateRepository.findById(costRateId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Cost rate not found"));

        LocalDateTime now = LocalDateTime.now();

        costRate.setIsActive(false);
        costRate.setIsDeleted(true);
        costRate.setDeletedAtUtc(now);
        costRate.setUpdatedAtUtc(now);

        costRateRepository.save(costRate);
    }

    @Transactional
    public TaxRateResponse createTaxRate(CreateTaxRateRequest request) {

        String code = normalizeCode(request.getCode());

        if (taxRateRepository.existsByCodeAndIsDeletedFalse(code)) {
            throw new BusinessRuleException("Tax rate code already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        TaxRate taxRate = new TaxRate();
        taxRate.setTaxRateId(UUID.randomUUID());
        taxRate.setCode(code);
        taxRate.setName(request.getName().trim());
        taxRate.setRatePercent(request.getRatePercent());
        taxRate.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        taxRate.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        taxRate.setIsDeleted(false);
        taxRate.setCreatedAtUtc(now);
        taxRate.setUpdatedAtUtc(now);

        if (Boolean.TRUE.equals(taxRate.getIsDefault()) && Boolean.TRUE.equals(taxRate.getIsActive())) {
            unsetOtherDefaultTaxRates(null);
        }

        TaxRate saved = taxRateRepository.save(taxRate);

        return toTaxRateResponse(saved);
    }

    @Transactional
    public TaxRateResponse updateTaxRate(UUID taxRateId, UpdateTaxRateRequest request) {

        TaxRate taxRate = taxRateRepository.findById(taxRateId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Tax rate not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            taxRateRepository.findByCodeAndIsDeletedFalse(code)
                    .filter(existing -> !existing.getTaxRateId().equals(taxRateId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("Tax rate code already exists");
                    });

            taxRate.setCode(code);
        }

        if (request.getName() != null) {
            taxRate.setName(request.getName().trim());
        }

        if (request.getRatePercent() != null) {
            taxRate.setRatePercent(request.getRatePercent());
        }

        if (request.getIsActive() != null) {
            if (Boolean.FALSE.equals(request.getIsActive())
                    && Boolean.TRUE.equals(taxRate.getIsDefault())) {
                throw new BusinessRuleException("Default tax rate cannot be deactivated");
            }

            taxRate.setIsActive(request.getIsActive());
        }

        if (request.getIsDefault() != null) {
            if (Boolean.FALSE.equals(request.getIsDefault())
                    && Boolean.TRUE.equals(taxRate.getIsDefault())) {
                throw new BusinessRuleException("Default tax rate cannot be unset directly");
            }

            if (Boolean.TRUE.equals(request.getIsDefault())) {
                if (Boolean.FALSE.equals(taxRate.getIsActive())) {
                    throw new BusinessRuleException("Inactive tax rate cannot be default");
                }

                unsetOtherDefaultTaxRates(taxRateId);
                taxRate.setIsDefault(true);
            }
        }

        taxRate.setUpdatedAtUtc(LocalDateTime.now());

        TaxRate saved = taxRateRepository.save(taxRate);

        return toTaxRateResponse(saved);
    }

    @Transactional
    public void deleteTaxRate(UUID taxRateId) {

        TaxRate taxRate = taxRateRepository.findById(taxRateId)
                .filter(existing -> !existing.getIsDeleted())
                .orElseThrow(() -> new NotFoundException("Tax rate not found"));

        if (Boolean.TRUE.equals(taxRate.getIsDefault())) {
            throw new BusinessRuleException("Default tax rate cannot be deleted");
        }

        if (bidRevisionItemRepository.existsByTaxRate_TaxRateIdAndIsDeletedFalse(taxRateId)) {
            throw new BusinessRuleException("Cannot delete tax rate because it is used by estimate items");
        }

        LocalDateTime now = LocalDateTime.now();

        taxRate.setIsActive(false);
        taxRate.setIsDeleted(true);
        taxRate.setDeletedAtUtc(now);
        taxRate.setUpdatedAtUtc(now);

        taxRateRepository.save(taxRate);
    }

    private void unsetOtherDefaultTaxRates(UUID keepTaxRateId) {

        taxRateRepository.findByIsDefaultTrueAndIsDeletedFalseAndIsActiveTrue()
                .filter(existing -> keepTaxRateId == null
                        || !existing.getTaxRateId().equals(keepTaxRateId))
                .ifPresent(existing -> {
                    existing.setIsDefault(false);
                    existing.setUpdatedAtUtc(LocalDateTime.now());
                    taxRateRepository.save(existing);
                });
    }
}