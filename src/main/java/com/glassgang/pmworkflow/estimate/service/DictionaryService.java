package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.exception.BusinessRuleException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
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
import com.glassgang.pmworkflow.estimate.entity.CostElement;
import com.glassgang.pmworkflow.estimate.entity.CostRate;
import com.glassgang.pmworkflow.estimate.entity.ItemType;
import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import com.glassgang.pmworkflow.estimate.enums.UnitOfMeasure;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemCostRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import com.glassgang.pmworkflow.estimate.repository.CostElementRepository;
import com.glassgang.pmworkflow.estimate.repository.CostRateRepository;
import com.glassgang.pmworkflow.estimate.repository.ItemTypeRepository;
import com.glassgang.pmworkflow.estimate.repository.TaxRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

@Service
public class DictionaryService {

    private final ItemTypeRepository itemTypeRepository;
    private final TaxRateRepository taxRateRepository;
    private final CostElementRepository costElementRepository;
    private final CostRateRepository costRateRepository;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final EstimateAccessService estimateAccessService;

    public DictionaryService(
            ItemTypeRepository itemTypeRepository,
            TaxRateRepository taxRateRepository,
            CostElementRepository costElementRepository,
            CostRateRepository costRateRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            EstimateAccessService estimateAccessService) {
        this.itemTypeRepository = itemTypeRepository;
        this.taxRateRepository = taxRateRepository;
        this.costElementRepository = costElementRepository;
        this.costRateRepository = costRateRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.estimateAccessService = estimateAccessService;
    }

    @Transactional(readOnly = true)
    public List<UnitOfMeasureResponse> getUnitOfMeasures() {
        return Arrays.stream(UnitOfMeasure.values())
                .map(unit -> new UnitOfMeasureResponse(
                        unit.name(),
                        toUnitOfMeasureName(unit)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemTypeResponse> getItemTypes() {
        return itemTypeRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toItemTypeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaxRateResponse> getTaxRates() {
        return taxRateRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toTaxRateResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CostElementResponse> getCostElements() {
        return costElementRepository.findByIsDeletedFalseOrderByCodeAsc()
                .stream()
                .map(this::toCostElementResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CostRateResponse> getCostRates(UUID costElementId) {
        if (costElementId == null) {
            return costRateRepository.findByIsDeletedFalseOrderByCodeAsc()
                    .stream()
                    .map(this::toCostRateResponse)
                    .toList();
        }

        return costRateRepository
                .findByCostElement_CostElementIdAndIsDeletedFalseAndIsActiveTrueOrderByCodeAsc(
                        costElementId)
                .stream()
                .map(this::toCostRateResponse)
                .toList();
    }

    @Transactional
    public ItemTypeResponse createItemType(CreateItemTypeRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        String code = normalizeCode(request.getCode());

        if (itemTypeRepository.existsByCode(code)) {
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

        return toItemTypeResponse(itemTypeRepository.save(itemType));
    }

    @Transactional
    public ItemTypeResponse updateItemType(UUID itemTypeId, UpdateItemTypeRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        ItemType itemType = itemTypeRepository.findById(itemTypeId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Item type not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            itemTypeRepository.findByCode(code)
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

        return toItemTypeResponse(itemTypeRepository.save(itemType));
    }

    @Transactional
    public void deleteItemType(UUID itemTypeId) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        ItemType itemType = itemTypeRepository.findById(itemTypeId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Item type not found"));

        if (bidRevisionItemRepository.existsByItemType_ItemTypeIdAndIsDeletedFalse(itemTypeId)) {
            throw new BusinessRuleException("Cannot delete item type because it is used by estimate items");
        }

        softDelete(itemType);
        itemTypeRepository.save(itemType);
    }

    @Transactional
    public TaxRateResponse createTaxRate(CreateTaxRateRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        String code = normalizeCode(request.getCode());

        if (taxRateRepository.existsByCode(code)) {
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

        if (Boolean.TRUE.equals(taxRate.getIsDefault())
                && Boolean.TRUE.equals(taxRate.getIsActive())) {
            unsetOtherDefaultTaxRates(null);
        }

        return toTaxRateResponse(taxRateRepository.save(taxRate));
    }

    @Transactional
    public TaxRateResponse updateTaxRate(UUID taxRateId, UpdateTaxRateRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        TaxRate taxRate = taxRateRepository.findById(taxRateId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Tax rate not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            taxRateRepository.findByCode(code)
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

        return toTaxRateResponse(taxRateRepository.save(taxRate));
    }

    @Transactional
    public void deleteTaxRate(UUID taxRateId) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        TaxRate taxRate = taxRateRepository.findById(taxRateId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Tax rate not found"));

        if (Boolean.TRUE.equals(taxRate.getIsDefault())) {
            throw new BusinessRuleException("Default tax rate cannot be deleted");
        }

        if (bidRevisionItemRepository.existsByTaxRate_TaxRateIdAndIsDeletedFalse(taxRateId)) {
            throw new BusinessRuleException("Cannot delete tax rate because it is used by estimate items");
        }

        softDelete(taxRate);
        taxRate.setIsDefault(false);
        taxRateRepository.save(taxRate);
    }

    @Transactional
    public CostElementResponse createCostElement(CreateCostElementRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        String code = normalizeCode(request.getCode());

        if (costElementRepository.existsByCode(code)) {
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

        return toCostElementResponse(costElementRepository.save(costElement));
    }

    @Transactional
    public CostElementResponse updateCostElement(UUID costElementId, UpdateCostElementRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        CostElement costElement = costElementRepository.findById(costElementId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            costElementRepository.findByCode(code)
                    .filter(existing -> !existing.getCostElementId().equals(costElementId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("Cost element code already exists");
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

        return toCostElementResponse(costElementRepository.save(costElement));
    }

    @Transactional
    public void deleteCostElement(UUID costElementId) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        CostElement costElement = costElementRepository.findById(costElementId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        if (bidRevisionItemCostRepository.existsByCostElement_CostElementIdAndIsDeletedFalse(costElementId)) {
            throw new BusinessRuleException("Cannot delete cost element because it is used by estimate costs");
        }

        softDelete(costElement);
        costElementRepository.save(costElement);
    }

    @Transactional
    public CostRateResponse createCostRate(CreateCostRateRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        String code = normalizeCode(request.getCode());

        if (costRateRepository.existsByCode(code)) {
            throw new BusinessRuleException("Cost rate code already exists");
        }

        CostElement costElement = costElementRepository
                .findByCostElementIdAndIsDeletedFalse(request.getCostElementId())
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        if (!Boolean.TRUE.equals(costElement.getIsActive())) {
            throw new BusinessRuleException(
                    "Cannot assign cost rate to inactive cost element");
        }

        LocalDateTime now = LocalDateTime.now();

        CostRate costRate = new CostRate();
        costRate.setCostRateId(UUID.randomUUID());
        costRate.setCode(code);
        costRate.setName(request.getName().trim());
        costRate.setDescription(request.getDescription());
        costRate.setRateAmount(request.getRateAmount());
        costRate.setRateUnit(request.getRateUnit());
        costRate.setCostElement(costElement);
        costRate.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        costRate.setIsDeleted(false);
        costRate.setCreatedAtUtc(now);
        costRate.setUpdatedAtUtc(now);

        return toCostRateResponse(costRateRepository.save(costRate));
    }

    @Transactional
    public CostRateResponse updateCostRate(UUID costRateId, UpdateCostRateRequest request) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        CostRate costRate = costRateRepository.findById(costRateId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Cost rate not found"));

        if (request.getCode() != null) {
            String code = normalizeCode(request.getCode());

            costRateRepository.findByCode(code)
                    .filter(existing -> !existing.getCostRateId().equals(costRateId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException("Cost rate code already exists");
                    });

            costRate.setCode(code);
        }

        if (request.getCostElementId() != null) {
            CostElement costElement = costElementRepository
                    .findByCostElementIdAndIsDeletedFalse(request.getCostElementId())
                    .orElseThrow(() -> new NotFoundException("Cost element not found"));

            if (!Boolean.TRUE.equals(costElement.getIsActive())) {
                throw new BusinessRuleException(
                        "Cannot assign cost rate to inactive cost element");
            }

            costRate.setCostElement(costElement);
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

        return toCostRateResponse(costRateRepository.save(costRate));
    }

    @Transactional
    public void deleteCostRate(UUID costRateId) {
        estimateAccessService.requireEstimateDictionaryManageAccess();

        CostRate costRate = costRateRepository.findById(costRateId)
                .filter(existing -> !Boolean.TRUE.equals(existing.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Cost rate not found"));

        if (bidRevisionItemCostRepository.existsByCostRate_CostRateIdAndIsDeletedFalse(costRateId)) {
            throw new BusinessRuleException("Cannot delete cost rate because it is used by estimate costs");
        }

        softDelete(costRate);
        costRateRepository.save(costRate);
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

    private void softDelete(ItemType itemType) {
        LocalDateTime now = LocalDateTime.now();
        itemType.setIsActive(false);
        itemType.setIsDeleted(true);
        itemType.setDeletedAtUtc(now);
        itemType.setUpdatedAtUtc(now);
    }

    private void softDelete(TaxRate taxRate) {
        LocalDateTime now = LocalDateTime.now();
        taxRate.setIsActive(false);
        taxRate.setIsDeleted(true);
        taxRate.setDeletedAtUtc(now);
        taxRate.setUpdatedAtUtc(now);
    }

    private void softDelete(CostElement costElement) {
        LocalDateTime now = LocalDateTime.now();
        costElement.setIsActive(false);
        costElement.setIsDeleted(true);
        costElement.setDeletedAtUtc(now);
        costElement.setUpdatedAtUtc(now);
    }

    private void softDelete(CostRate costRate) {
        LocalDateTime now = LocalDateTime.now();
        costRate.setIsActive(false);
        costRate.setIsDeleted(true);
        costRate.setDeletedAtUtc(now);
        costRate.setUpdatedAtUtc(now);
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

        if (costRate.getCostElement() != null) {
            response.setCostElementId(costRate.getCostElement().getCostElementId());
            response.setCostElementCode(costRate.getCostElement().getCode());
            response.setCostElementName(costRate.getCostElement().getName());
        }

        return response;
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private String toUnitOfMeasureName(UnitOfMeasure unit) {
    return switch (unit) {
        case HOUR -> "Hour";
        case DAY -> "Day";
        case NIGHT -> "Night";
        case EACH -> "Each";
        case SQFT -> "Square Foot";
        case LF -> "Linear Foot";
        case UNIT -> "Unit";
    };
}
}