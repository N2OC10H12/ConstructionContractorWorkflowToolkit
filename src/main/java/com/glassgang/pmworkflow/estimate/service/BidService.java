package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.audit.service.EstimateAuditService;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartner;
import com.glassgang.pmworkflow.businesspartner.repository.BusinessPartnerRepository;
import com.glassgang.pmworkflow.businesspartner.repository.CustomerProfileRepository;
import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemCostResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidFromRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.DeleteRevisionGroupItemTypeRequest;
import com.glassgang.pmworkflow.estimate.dto.DeleteRevisionGroupRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionDisplayModesRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemRequest;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import com.glassgang.pmworkflow.estimate.entity.ConstructionObjectType;
import com.glassgang.pmworkflow.estimate.entity.CostElement;
import com.glassgang.pmworkflow.estimate.entity.CostRate;
import com.glassgang.pmworkflow.estimate.entity.ItemType;
import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import com.glassgang.pmworkflow.estimate.enums.BidStatus;
import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import com.glassgang.pmworkflow.estimate.enums.EstimatePriceDisplayMode;
import com.glassgang.pmworkflow.estimate.enums.RevisionStatus;
import com.glassgang.pmworkflow.estimate.mapper.BidMapper;
import com.glassgang.pmworkflow.estimate.repository.BidRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionRepository;
import com.glassgang.pmworkflow.estimate.repository.ConstructionObjectTypeRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import com.glassgang.pmworkflow.estimate.repository.ItemTypeRepository;
import com.glassgang.pmworkflow.estimate.repository.TaxRateRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemCostRepository;
import com.glassgang.pmworkflow.estimate.repository.CostElementRepository;
import com.glassgang.pmworkflow.estimate.repository.CostRateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glassgang.pmworkflow.common.exception.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final BidRevisionRepository bidRevisionRepository;
    private final BidNumberService bidNumberService;
    private final BidMapper bidMapper;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CostElementRepository costElementRepository;
    private final CostRateRepository costRateRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final TaxRateRepository taxRateRepository;
    private final PricingService pricingService;
    private final CurrentUserUtil currentUserUtil;
    private final EstimateAccessService estimateAccessService;
    private final EstimateAuditService estimateAuditService;
    private final ConstructionObjectTypeRepository constructionObjectTypeRepository;

    public BidService(
            BidRepository bidRepository,
            BidRevisionRepository bidRevisionRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            CostElementRepository costElementRepository,
            CostRateRepository costRateRepository,
            BusinessPartnerRepository businessPartnerRepository,
            CustomerProfileRepository customerProfileRepository,
            BidNumberService bidNumberService,
            BidMapper bidMapper,
            ItemTypeRepository itemTypeRepository,
            TaxRateRepository taxRateRepository,
            PricingService pricingService,
            CurrentUserUtil currentUserUtil,
            EstimateAccessService estimateAccessService,
            EstimateAuditService estimateAuditService,
            ConstructionObjectTypeRepository constructionObjectTypeRepository) {
        this.bidRepository = bidRepository;
        this.bidRevisionRepository = bidRevisionRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.costElementRepository = costElementRepository;
        this.costRateRepository = costRateRepository;
        this.businessPartnerRepository = businessPartnerRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.bidNumberService = bidNumberService;
        this.bidMapper = bidMapper;
        this.itemTypeRepository = itemTypeRepository;
        this.taxRateRepository = taxRateRepository;
        this.pricingService = pricingService;
        this.currentUserUtil = currentUserUtil;
        this.estimateAccessService = estimateAccessService;
        this.estimateAuditService = estimateAuditService;
        this.constructionObjectTypeRepository = constructionObjectTypeRepository;
    }

    @Transactional
    public BidResponse createBid(CreateBidRequest request) {

        estimateAccessService.requireEstimateCreateAccess();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BusinessPartner customer = getActiveCustomerBusinessPartner(request.getCustomerId());

        TaxRate defaultTaxRate = null;

        if (request.getDefaultTaxRateId() != null) {
            defaultTaxRate = getActiveTaxRate(request.getDefaultTaxRateId());
        }

        ConstructionObjectType constructionObjectType = null;

        UUID constructionObjectTypeId = request.getConstructionObjectTypeId();

        if (constructionObjectTypeId != null) {
            constructionObjectType = getActiveConstructionObjectType(constructionObjectTypeId);
        }

        LocalDateTime now = LocalDateTime.now();

        Bid bid = new Bid();
        bid.setBidId(UUID.randomUUID());
        bid.setCustomer(customer);
        bid.setBidNumber(bidNumberService.generateNextBidNumber());
        bid.setJobNumber(bidNumberService.generateNextJobNumber());
        bid.setJobName(request.getJobName());
        bid.setJobAddressLine1(request.getJobAddressLine1());
        bid.setJobAddressLine2(request.getJobAddressLine2());
        bid.setJobCity(request.getJobCity());
        bid.setJobState(request.getJobState());
        bid.setJobPostalCode(request.getJobPostalCode());
        bid.setJobCountry(request.getJobCountry());
        bid.setDescription(request.getDescription());
        bid.setDepartmentCode(request.getDepartmentCode());
        bid.setBidStatus(BidStatus.DRAFT);
        bid.setCreatedAtUtc(now);
        bid.setUpdatedAtUtc(now);
        bid.setCreatedByUserId(currentUserId);
        bid.setUpdatedByUserId(currentUserId);
        bid.setConstructionType(request.getConstructionType());
        bid.setConstructionObjectType(constructionObjectType);
        bid.setDefaultTaxRate(defaultTaxRate);
        bid.setIsDeleted(false);

        Bid savedBid = bidRepository.save(bid);

        BidRevision revision = new BidRevision();
        revision.setBidRevisionId(UUID.randomUUID());
        revision.setBid(savedBid);
        revision.setRevisionNumber(0);
        revision.setRevisionDisplayName(
                bidNumberService.buildRevisionDisplayName(
                        savedBid.getBidNumber(),
                        savedBid.getDepartmentCode().name(),
                        0));
        revision.setRevisionStatus(RevisionStatus.DRAFT);
        revision.setSubtotalCost(BigDecimal.ZERO);
        revision.setSubtotalPrice(BigDecimal.ZERO);
        revision.setTaxAmount(BigDecimal.ZERO);
        revision.setTotalPrice(BigDecimal.ZERO);

        applyDefaultTaxRateSnapshot(revision, savedBid.getDefaultTaxRate());

        revision.setCreatedAtUtc(now);
        revision.setUpdatedAtUtc(now);
        revision.setCreatedByUserId(currentUserId);
        revision.setUpdatedByUserId(currentUserId);
        revision.setIsDeleted(false);
        revision.setCustomerDisplayMode(CustomerDisplayMode.ITEM_LEVEL);
        revision.setPriceDisplayMode(EstimatePriceDisplayMode.ITEM_TYPE_LEVEL);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        savedBid.setCurrentRevision(savedRevision);
        savedBid.setUpdatedAtUtc(now);

        Bid savedBidWithRevision = bidRepository.save(savedBid);

        estimateAuditService.log(
                savedBidWithRevision.getBidId(),
                savedRevision.getBidRevisionId(),
                "CREATED",
                "BID",
                savedBidWithRevision.getBidId(),
                null,
                savedBidWithRevision.getBidNumber(),
                "Bid created: " + savedBidWithRevision.getBidNumber());

        return bidMapper.toBidResponse(savedBidWithRevision);
    }

    @Transactional
    public BidResponse updateBid(UUID bidId, UpdateBidRequest request) {

        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);

        BidRevision currentRevision = bid.getCurrentRevision();

        if (currentRevision == null) {
            throw new BusinessRuleException("Bid current revision is missing");
        }

        ensureCurrentRevisionCanBeChanged(bid, currentRevision);

        String oldValue = buildBidAuditValue(bid);

        if (request.getCustomerId() != null) {
            BusinessPartner customer = getActiveCustomerBusinessPartner(request.getCustomerId());
            bid.setCustomer(customer);
        }

        if (request.getJobName() != null) {
            if (request.getJobName().isBlank()) {
                throw new BadRequestException("Job name cannot be blank");
            }

            bid.setJobName(request.getJobName().trim());
        }

        if (request.getJobAddressLine1() != null) {
            bid.setJobAddressLine1(request.getJobAddressLine1());
        }

        if (request.getJobAddressLine2() != null) {
            bid.setJobAddressLine2(request.getJobAddressLine2());
        }

        if (request.getJobCity() != null) {
            bid.setJobCity(request.getJobCity());
        }

        if (request.getJobState() != null) {
            bid.setJobState(request.getJobState());
        }

        if (request.getJobPostalCode() != null) {
            bid.setJobPostalCode(request.getJobPostalCode());
        }

        if (request.getJobCountry() != null) {
            bid.setJobCountry(request.getJobCountry());
        }

        if (request.getDescription() != null) {
            bid.setDescription(request.getDescription());
        }

        if (request.getDepartmentCode() != null) {
            bid.setDepartmentCode(request.getDepartmentCode());
        }

        if (request.getConstructionType() != null) {
            bid.setConstructionType(request.getConstructionType());
        }

        UUID constructionObjectTypeId = request.getConstructionObjectTypeId();

        if (constructionObjectTypeId != null) {
            ConstructionObjectType constructionObjectType = getActiveConstructionObjectType(constructionObjectTypeId);
            bid.setConstructionObjectType(constructionObjectType);
        }

        if (request.getDefaultTaxRateId() != null) {
            TaxRate defaultTaxRate = getActiveTaxRate(request.getDefaultTaxRateId());
            bid.setDefaultTaxRate(defaultTaxRate);
            applyDefaultTaxRateSnapshot(currentRevision, defaultTaxRate);
        }

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        Bid savedBid = bidRepository.save(bid);

        String newValue = buildBidAuditValue(savedBid);

        if (!oldValue.equals(newValue)) {
            estimateAuditService.log(
                    savedBid.getBidId(),
                    currentRevision.getBidRevisionId(),
                    "UPDATED",
                    "BID",
                    savedBid.getBidId(),
                    oldValue,
                    newValue,
                    "Bid updated: " + savedBid.getBidNumber());
        }

        return bidMapper.toBidResponse(savedBid);
    }

    @Transactional
    public BidResponse createBidFromRevision(
            UUID sourceBidRevisionId,
            CreateBidFromRevisionRequest request) {

        estimateAccessService.requireEstimateCreateAccess();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BidRevision sourceRevision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(sourceBidRevisionId)
                .orElseThrow(() -> new NotFoundException("Source bid revision not found"));

        Bid sourceBid = sourceRevision.getBid();

        estimateAccessService.requireBidViewAccess(sourceBid);

        BusinessPartner customer = request.getCustomerId() != null
                ? getActiveCustomerBusinessPartner(request.getCustomerId())
                : sourceBid.getCustomer();

        LocalDateTime now = LocalDateTime.now();

        Bid newBid = new Bid();
        newBid.setBidId(UUID.randomUUID());
        newBid.setCustomer(customer);
        newBid.setBidNumber(bidNumberService.generateNextBidNumber());
        newBid.setJobNumber(bidNumberService.generateNextJobNumber());

        newBid.setJobName(
                request.getJobName() != null && !request.getJobName().isBlank()
                        ? request.getJobName().trim()
                        : sourceBid.getJobName());

        newBid.setJobAddressLine1(sourceBid.getJobAddressLine1());
        newBid.setJobAddressLine2(sourceBid.getJobAddressLine2());
        newBid.setJobCity(sourceBid.getJobCity());
        newBid.setJobState(sourceBid.getJobState());
        newBid.setJobPostalCode(sourceBid.getJobPostalCode());
        newBid.setJobCountry(sourceBid.getJobCountry());

        newBid.setDescription(
                request.getDescription() != null
                        ? request.getDescription()
                        : sourceBid.getDescription());

        newBid.setDepartmentCode(
                request.getDepartmentCode() != null
                        ? request.getDepartmentCode()
                        : sourceBid.getDepartmentCode());

        newBid.setConstructionType(
                request.getConstructionType() != null
                        ? request.getConstructionType()
                        : sourceBid.getConstructionType());

        UUID constructionObjectTypeId = request.getConstructionObjectTypeId();

        newBid.setConstructionObjectType(
                constructionObjectTypeId != null
                        ? getActiveConstructionObjectType(constructionObjectTypeId)
                        : sourceBid.getConstructionObjectType());

        newBid.setDefaultTaxRate(sourceBid.getDefaultTaxRate());

        newBid.setBidStatus(BidStatus.DRAFT);
        newBid.setCreatedAtUtc(now);
        newBid.setUpdatedAtUtc(now);
        newBid.setCreatedByUserId(currentUserId);
        newBid.setUpdatedByUserId(currentUserId);
        newBid.setIsDeleted(false);

        Bid savedBid = bidRepository.save(newBid);

        BidRevision newRevision = new BidRevision();
        newRevision.setBidRevisionId(UUID.randomUUID());
        newRevision.setBid(savedBid);
        newRevision.setRevisionNumber(0);
        newRevision.setRevisionDisplayName(
                bidNumberService.buildRevisionDisplayName(
                        savedBid.getBidNumber(),
                        savedBid.getDepartmentCode().name(),
                        0));

        newRevision.setRevisionStatus(RevisionStatus.DRAFT);

        newRevision.setTaxType(null);
        newRevision.setTaxRatePercent(null);

        copyDefaultTaxRateSnapshot(newRevision, sourceRevision, savedBid.getDefaultTaxRate());

        newRevision.setCustomerNote(sourceRevision.getCustomerNote());
        newRevision.setInternalNote(sourceRevision.getInternalNote());

        newRevision.setSubtotalCost(sourceRevision.getSubtotalCost());
        newRevision.setSubtotalPrice(sourceRevision.getSubtotalPrice());
        newRevision.setTaxAmount(sourceRevision.getTaxAmount());
        newRevision.setTotalPrice(sourceRevision.getTotalPrice());

        newRevision.setClonedFromBidRevision(sourceRevision);

        newRevision.setCreatedAtUtc(now);
        newRevision.setUpdatedAtUtc(now);
        newRevision.setCreatedByUserId(currentUserId);
        newRevision.setUpdatedByUserId(currentUserId);
        newRevision.setIsDeleted(false);

        newRevision.setCustomerDisplayMode(
                sourceRevision.getCustomerDisplayMode() != null
                        ? sourceRevision.getCustomerDisplayMode()
                        : CustomerDisplayMode.ITEM_LEVEL);

        newRevision.setPriceDisplayMode(
                sourceRevision.getPriceDisplayMode() != null
                        ? sourceRevision.getPriceDisplayMode()
                        : EstimatePriceDisplayMode.ITEM_TYPE_LEVEL);

        BidRevision savedRevision = bidRevisionRepository.save(newRevision);

        cloneRevisionItems(sourceRevision, savedRevision, now, currentUserId);

        pricingService.recalculateRevisionTotals(savedRevision);
        savedRevision.setUpdatedAtUtc(now);
        bidRevisionRepository.save(savedRevision);

        savedBid.setCurrentRevision(savedRevision);
        savedBid.setUpdatedAtUtc(now);
        savedBid.setUpdatedByUserId(currentUserId);

        Bid savedBidWithRevision = bidRepository.save(savedBid);

        estimateAuditService.log(
                savedBidWithRevision.getBidId(),
                savedRevision.getBidRevisionId(),
                "COPIED_FROM_REVISION",
                "BID",
                savedBidWithRevision.getBidId(),
                sourceRevision.getRevisionDisplayName(),
                savedBidWithRevision.getBidNumber(),
                "Bid created from revision: "
                        + savedBidWithRevision.getBidNumber()
                        + " from "
                        + sourceRevision.getRevisionDisplayName());

        return bidMapper.toBidResponse(savedBidWithRevision);
    }

    @Transactional(readOnly = true)
    public BidResponse getBid(UUID bidId) {
        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidViewAccess(bid);

        return bidMapper.toBidResponse(bid);
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getBids(String scope) {

        boolean canAccessEstimates = currentUserUtil.isCurrentUserAdmin()
                || currentUserUtil.isCurrentUserEstimator()
                || currentUserUtil.isCurrentUserEstimateManager()
                || currentUserUtil.isCurrentUserEstimateViewer();

        if (!canAccessEstimates) {
            throw new ForbiddenException("No access to estimates");
        }

        if (scope == null || scope.isBlank() || scope.equalsIgnoreCase("all")) {
            return bidRepository.findByIsDeletedFalseOrderByCreatedAtUtcDesc()
                    .stream()
                    .map(bidMapper::toBidResponse)
                    .toList();
        }

        if (scope.equalsIgnoreCase("mine")) {
            UUID currentUserId = currentUserUtil.getCurrentUserId();

            return bidRepository
                    .findByCreatedByUserIdAndIsDeletedFalseOrderByCreatedAtUtcDesc(currentUserId)
                    .stream()
                    .map(bidMapper::toBidResponse)
                    .toList();
        }

        throw new BadRequestException("Invalid bid scope");
    }

    @Transactional(readOnly = true)
    public BidRevisionResponse getBidRevision(UUID bidRevisionId) {
        BidRevision bidRevision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        estimateAccessService.requireBidViewAccess(bidRevision.getBid());

        return bidMapper.toBidRevisionResponse(bidRevision);
    }

    @Transactional(readOnly = true)
    public void requireBidRevisionPdfPreviewAccess(UUID bidRevisionId) {
        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        estimateAccessService.requireBidViewAccess(revision.getBid());
    }

    @Transactional(readOnly = true)
    public void requireBidRevisionPdfDownloadAccess(UUID bidRevisionId) {
        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        estimateAccessService.requireBidViewAccess(revision.getBid());

        if (revision.getRevisionStatus() == RevisionStatus.DRAFT) {
            throw new BusinessRuleException(
                    "DRAFT revision PDF cannot be downloaded. Use PDF preview instead.");
        }
    }

    @Transactional(readOnly = true)
    public List<BidRevisionResponse> getBidRevisions(UUID bidId) {

        Bid bid = bidRepository.findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidViewAccess(bid);

        return bidRevisionRepository
                .findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(bidId)
                .stream()
                .map(bidMapper::toBidRevisionResponse)
                .toList();
    }

    @Transactional
    public BidRevisionResponse createRevision(
            UUID bidId,
            CreateBidRevisionRequest request) {

        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeRevised(bid);

        BidRevision currentRevision = bid.getCurrentRevision();

        if (currentRevision == null) {
            throw new IllegalStateException("Bid current revision is missing");
        }

        if (currentRevision.getRevisionStatus() == RevisionStatus.AWARDED) {
            throw new BusinessRuleException("Awarded revision cannot be cloned");
        }

        if (currentRevision.getRevisionStatus() == RevisionStatus.LOST) {
            throw new BusinessRuleException("Lost revision cannot be cloned");
        }

        if (currentRevision.getRevisionStatus() == RevisionStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived revision cannot be cloned");
        }

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        Integer nextRevisionNumber = currentRevision.getRevisionNumber() + 1;

        BidRevision revision = new BidRevision();

        revision.setBidRevisionId(UUID.randomUUID());
        revision.setBid(bid);

        revision.setRevisionNumber(nextRevisionNumber);

        revision.setRevisionDisplayName(
                bidNumberService.buildRevisionDisplayName(
                        bid.getBidNumber(),
                        bid.getDepartmentCode().name(),
                        nextRevisionNumber));

        revision.setRevisionStatus(RevisionStatus.DRAFT);

        revision.setTaxType(null);
        revision.setTaxRatePercent(null);
        revision.setCustomerNote(request.getCustomerNote());
        revision.setInternalNote(request.getInternalNote());

        revision.setSubtotalCost(currentRevision.getSubtotalCost());
        revision.setSubtotalPrice(currentRevision.getSubtotalPrice());
        revision.setTaxAmount(currentRevision.getTaxAmount());
        revision.setTotalPrice(currentRevision.getTotalPrice());

        copyDefaultTaxRateSnapshot(revision, currentRevision, bid.getDefaultTaxRate());

        revision.setClonedFromBidRevision(currentRevision);

        revision.setCreatedAtUtc(now);
        revision.setUpdatedAtUtc(now);
        revision.setCreatedByUserId(currentUserId);
        revision.setUpdatedByUserId(currentUserId);

        revision.setIsDeleted(false);

        revision.setCustomerDisplayMode(
                currentRevision.getCustomerDisplayMode() != null
                        ? currentRevision.getCustomerDisplayMode()
                        : CustomerDisplayMode.ITEM_LEVEL);

        revision.setPriceDisplayMode(
                currentRevision.getPriceDisplayMode() != null
                        ? currentRevision.getPriceDisplayMode()
                        : EstimatePriceDisplayMode.ITEM_TYPE_LEVEL);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        cloneRevisionItems(currentRevision, savedRevision, now, currentUserId);

        pricingService.recalculateRevisionTotals(savedRevision);
        savedRevision.setUpdatedAtUtc(now);
        bidRevisionRepository.save(savedRevision);

        bid.setCurrentRevision(savedRevision);
        bid.setBidStatus(BidStatus.DRAFT);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        estimateAuditService.log(
                bid.getBidId(),
                savedRevision.getBidRevisionId(),
                "CLONED",
                "BID_REVISION",
                savedRevision.getBidRevisionId(),
                currentRevision.getRevisionDisplayName(),
                savedRevision.getRevisionDisplayName(),
                "Revision created: " + savedRevision.getRevisionDisplayName()
                        + " from " + currentRevision.getRevisionDisplayName());

        return bidMapper.toBidRevisionResponse(savedRevision);
    }

    @Transactional
    public BidRevisionResponse updateRevisionDisplayModes(
            UUID bidRevisionId,
            UpdateBidRevisionDisplayModesRequest request) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        ensureDisplayModesCompatible(
                request.getCustomerDisplayMode(),
                request.getPriceDisplayMode());

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        revision.setCustomerDisplayMode(request.getCustomerDisplayMode());
        revision.setPriceDisplayMode(request.getPriceDisplayMode());
        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);
        bidRepository.save(bid);

        return bidMapper.toBidRevisionResponse(savedRevision);
    }

    @Transactional
    public BidRevisionResponse sendRevision(UUID bidRevisionId) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureCurrentRevisionCanBeSent(bid, revision);

        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision().getBidRevisionId().equals(bidRevisionId)) {
            throw new BusinessRuleException("Only current revision can be sent");
        }

        if (revision.getRevisionStatus() != RevisionStatus.DRAFT) {
            throw new BusinessRuleException(
                    "Only DRAFT revisions can be sent");
        }

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        revision.setRevisionStatus(RevisionStatus.SENT);
        revision.setSentAtUtc(now);
        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        bid.setBidStatus(BidStatus.SENT);
        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        estimateAuditService.log(
                bid.getBidId(),
                savedRevision.getBidRevisionId(),
                "SENT",
                "BID_REVISION",
                savedRevision.getBidRevisionId(),
                RevisionStatus.DRAFT.name(),
                RevisionStatus.SENT.name(),
                "Revision sent: " + savedRevision.getRevisionDisplayName());

        return bidMapper.toBidRevisionResponse(savedRevision);
    }

    @Transactional
    public BidRevisionResponse awardRevision(UUID bidRevisionId) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureCurrentRevisionCanBeAwarded(bid, revision);

        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision().getBidRevisionId().equals(bidRevisionId)) {
            throw new BusinessRuleException("Only current revision can be awarded");
        }

        if (revision.getRevisionStatus() != RevisionStatus.SENT) {
            throw new BusinessRuleException(
                    "Only SENT revisions can be awarded");
        }

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        List<BidRevision> bidRevisions = bidRevisionRepository
                .findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(
                        bid.getBidId());

        for (BidRevision bidRevision : bidRevisions) {
            if (bidRevision.getBidRevisionId().equals(bidRevisionId)) {
                bidRevision.setRevisionStatus(RevisionStatus.AWARDED);
                bidRevision.setAwardedAtUtc(now);
                bidRevision.setUpdatedAtUtc(now);
                bidRevision.setUpdatedByUserId(currentUserId);
            } else if (bidRevision.getRevisionStatus() != RevisionStatus.ARCHIVED) {
                bidRevision.setRevisionStatus(RevisionStatus.ARCHIVED);
                bidRevision.setArchivedAtUtc(now);
                bidRevision.setUpdatedAtUtc(now);
                bidRevision.setUpdatedByUserId(currentUserId);
            }
        }

        bidRevisionRepository.saveAll(bidRevisions);

        bid.setBidStatus(BidStatus.AWARDED);
        bid.setCurrentRevision(revision);
        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        estimateAuditService.log(
                bid.getBidId(),
                revision.getBidRevisionId(),
                "AWARDED",
                "BID_REVISION",
                revision.getBidRevisionId(),
                RevisionStatus.SENT.name(),
                RevisionStatus.AWARDED.name(),
                "Revision awarded: " + revision.getRevisionDisplayName());

        return bidMapper.toBidRevisionResponse(revision);
    }

    @Transactional
    public BidResponse loseBid(UUID bidId) {

        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidEditAccess(bid);

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Bid is already lost");
        }

        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid cannot be marked lost");
        }

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BidStatus oldBidStatus = bid.getBidStatus();

        UUID currentRevisionId = bid.getCurrentRevision() != null
                ? bid.getCurrentRevision().getBidRevisionId()
                : null;

        List<BidRevision> bidRevisions = bidRevisionRepository
                .findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(bidId);

        for (BidRevision revision : bidRevisions) {
            revision.setRevisionStatus(RevisionStatus.LOST);
            revision.setLostAtUtc(now);
            revision.setUpdatedAtUtc(now);
            revision.setUpdatedByUserId(currentUserId);
        }

        bidRevisionRepository.saveAll(bidRevisions);

        bid.setBidStatus(BidStatus.LOST);
        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        Bid savedBid = bidRepository.save(bid);

        estimateAuditService.log(
                savedBid.getBidId(),
                currentRevisionId,
                "LOST",
                "BID",
                savedBid.getBidId(),
                oldBidStatus.name(),
                BidStatus.LOST.name(),
                "Bid marked lost: " + savedBid.getBidNumber());

        return bidMapper.toBidResponse(savedBid);
    }

    @Transactional
    public BidRevisionItemResponse createRevisionItem(
            UUID bidRevisionId,
            CreateBidRevisionItemRequest request) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        int nextLineNumber = bidRevisionItemRepository
                .findTopLineNumberByBidRevisionId(bidRevisionId)
                .orElse(0) + 1;

        int nextDisplayOrder = bidRevisionItemRepository
                .findTopDisplayOrderByBidRevisionId(bidRevisionId)
                .orElse(0) + 1;

        ItemType itemType = getActiveItemType(request.getItemTypeId());
        TaxRate taxRate = getActiveTaxRate(request.getTaxRateId());

        BidRevisionItem item = new BidRevisionItem();

        item.setItemType(itemType);
        applyTaxRateSnapshot(item, taxRate);

        item.setIsTaxable(true);
        item.setShowCustomerRow(true);
        item.setShowCustomerPrice(true);

        item.setBidRevisionItemId(UUID.randomUUID());
        item.setBidRevision(revision);

        item.setLineNumber(nextLineNumber);
        item.setDisplayOrder(nextDisplayOrder);

        item.setGroupName(request.getGroupName());
        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitOfMeasure(request.getUnitOfMeasure());

        item.setUnitCost(request.getUnitCost());
        item.setMarkupPercent(request.getMarkupPercent());

        item.setUnitPrice(BigDecimal.ZERO);
        item.setTotalCost(BigDecimal.ZERO);
        item.setTotalPrice(BigDecimal.ZERO);
        item.setGpmPercent(null);

        pricingService.recalculateItemMaterialTotals(item);
        pricingService.recalculateItemTotals(item);

        // item.setTaxAmount(BigDecimal.ZERO);
        // item.setPriceWithTax(BigDecimal.ZERO);

        item.setIsOptional(request.getIsOptional());

        item.setInternalNote(request.getInternalNote());
        item.setCustomerNote(request.getCustomerNote());

        item.setCreatedAtUtc(now);
        item.setUpdatedAtUtc(now);
        item.setCreatedByUserId(currentUserId);
        item.setUpdatedByUserId(currentUserId);

        item.setIsDeleted(false);

        BidRevisionItem savedItem = bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);

        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionItemResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public List<BidRevisionItemResponse> getRevisionItems(UUID bidRevisionId) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        estimateAccessService.requireBidViewAccess(revision.getBid());

        return bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndIsDeletedFalseOrderByDisplayOrderAsc(bidRevisionId)
                .stream()
                .map(bidMapper::toBidRevisionItemResponse)
                .toList();
    }

    @Transactional
    public void deleteRevisionItem(UUID bidRevisionItemId) {

        BidRevisionItem item = bidRevisionItemRepository
                .findByBidRevisionItemIdAndIsDeletedFalse(bidRevisionItemId)
                .orElseThrow(() -> new NotFoundException("Bid revision item not found"));

        BidRevision revision = item.getBidRevision();
        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        List<BidRevisionItemCost> activeCosts = bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalse(
                        item.getBidRevisionItemId());

        for (BidRevisionItemCost cost : activeCosts) {
            cost.setIsDeleted(true);
            cost.setDeletedAtUtc(now);
            cost.setDeletedByUserId(currentUserId);
            cost.setUpdatedAtUtc(now);
            cost.setUpdatedByUserId(currentUserId);
        }

        bidRevisionItemCostRepository.saveAll(activeCosts);

        item.setIsDeleted(true);
        item.setDeletedAtUtc(now);
        item.setDeletedByUserId(currentUserId);
        item.setUpdatedAtUtc(now);
        item.setUpdatedByUserId(currentUserId);

        bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);

        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);
    }

    private void softDeleteItemsWithCosts(
            List<BidRevisionItem> items,
            LocalDateTime now,
            UUID currentUserId) {

        for (BidRevisionItem item : items) {

            List<BidRevisionItemCost> activeCosts = bidRevisionItemCostRepository
                    .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalse(
                            item.getBidRevisionItemId());

            for (BidRevisionItemCost cost : activeCosts) {
                cost.setIsDeleted(true);
                cost.setDeletedAtUtc(now);
                cost.setDeletedByUserId(currentUserId);
                cost.setUpdatedAtUtc(now);
                cost.setUpdatedByUserId(currentUserId);
            }

            bidRevisionItemCostRepository.saveAll(activeCosts);

            item.setIsDeleted(true);
            item.setDeletedAtUtc(now);
            item.setDeletedByUserId(currentUserId);
            item.setUpdatedAtUtc(now);
            item.setUpdatedByUserId(currentUserId);
        }

        bidRevisionItemRepository.saveAll(items);
    }

    @Transactional
    public void deleteRevisionGroup(
            UUID bidRevisionId,
            DeleteRevisionGroupRequest request) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        List<BidRevisionItem> items = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndGroupNameAndIsDeletedFalse(
                        bidRevisionId,
                        request.getGroupName());

        if (items.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        softDeleteItemsWithCosts(items, now, currentUserId);

        pricingService.recalculateRevisionTotals(revision);

        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);
        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);
        bidRepository.save(bid);
    }

    @Transactional
    public void deleteRevisionGroupItemType(
            UUID bidRevisionId,
            DeleteRevisionGroupItemTypeRequest request) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        List<BidRevisionItem> items = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndGroupNameAndItemType_ItemTypeIdAndIsDeletedFalse(
                        bidRevisionId,
                        request.getGroupName(),
                        request.getItemTypeId());

        if (items.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        softDeleteItemsWithCosts(items, now, currentUserId);

        pricingService.recalculateRevisionTotals(revision);

        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);
        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);
        bidRepository.save(bid);
    }

    @Transactional
    public BidRevisionItemResponse updateRevisionItem(
            UUID bidRevisionItemId,
            UpdateBidRevisionItemRequest request) {

        BidRevisionItem item = bidRevisionItemRepository
                .findByBidRevisionItemIdAndIsDeletedFalse(bidRevisionItemId)
                .orElseThrow(() -> new NotFoundException("Bid revision item not found"));

        BidRevision revision = item.getBidRevision();
        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        if (request.getGroupName() != null) {
            item.setGroupName(request.getGroupName());
        }

        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }

        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
        }

        if (request.getUnitOfMeasure() != null) {
            item.setUnitOfMeasure(request.getUnitOfMeasure());
        }

        if (request.getIsOptional() != null) {
            item.setIsOptional(request.getIsOptional());
        }

        if (request.getInternalNote() != null) {
            item.setInternalNote(request.getInternalNote());
        }

        if (request.getCustomerNote() != null) {
            item.setCustomerNote(request.getCustomerNote());
        }

        if (request.getItemTypeId() != null) {
            ItemType itemType = getActiveItemType(request.getItemTypeId());
            item.setItemType(itemType);
        }

        if (request.getTaxRateId() != null) {
            TaxRate taxRate = getActiveTaxRate(request.getTaxRateId());
            applyTaxRateSnapshot(item, taxRate);
        }

        if (request.getUnitCost() != null) {
            item.setUnitCost(request.getUnitCost());
        }

        if (request.getMarkupPercent() != null) {
            item.setMarkupPercent(request.getMarkupPercent());
        }

        pricingService.recalculateItemMaterialTotals(item);
        pricingService.recalculateItemTotals(item);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        item.setUpdatedAtUtc(now);
        item.setUpdatedByUserId(currentUserId);

        BidRevisionItem savedItem = bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);

        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionItemResponse(savedItem);
    }

    @Transactional
    public BidRevisionItemCostResponse createItemCost(
            UUID bidRevisionItemId,
            CreateBidRevisionItemCostRequest request) {

        BidRevisionItem item = bidRevisionItemRepository
                .findByBidRevisionItemIdAndIsDeletedFalse(bidRevisionItemId)
                .orElseThrow(() -> new NotFoundException("Bid revision item not found"));

        BidRevision revision = item.getBidRevision();
        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        CostElement costElement = costElementRepository
                .findByCostElementIdAndIsDeletedFalse(request.getCostElementId())
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        if (!Boolean.TRUE.equals(costElement.getIsActive())) {
            throw new BusinessRuleException(
                    "Cannot assign cost rate to inactive cost element");
        }

        CostRate costRate = null;

        if (request.getCostRateId() != null) {
            costRate = costRateRepository
                    .findByCostRateIdAndIsDeletedFalseAndIsActiveTrue(request.getCostRateId())
                    .orElseThrow(() -> new NotFoundException("Cost rate not found"));
        }

        ensureCostRateBelongsToCostElement(costElement, costRate);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        int nextLineNumber = bidRevisionItemCostRepository
                .findTopLineNumberByBidRevisionItemId(bidRevisionItemId)
                .orElse(0) + 1;

        int nextDisplayOrder = bidRevisionItemCostRepository
                .findTopDisplayOrderByBidRevisionItemId(bidRevisionItemId)
                .orElse(0) + 1;

        BidRevisionItemCost cost = new BidRevisionItemCost();

        cost.setBidRevisionItemCostId(UUID.randomUUID());
        cost.setBidRevisionItem(item);
        cost.setCostElement(costElement);
        cost.setCostRate(costRate);

        cost.setLineNumber(nextLineNumber);
        cost.setDisplayOrder(nextDisplayOrder);

        cost.setGroupName(request.getGroupName());
        cost.setQuantity(request.getQuantity());
        cost.setUnitOfMeasure(costRate.getRateUnit().name());

        cost.setRateSnapshot(costRate.getRateAmount());
        cost.setRateUnitSnapshot(costRate.getRateUnit().name());

        cost.setUnitCost(request.getUnitCost());

        cost.setMarkupPercent(request.getMarkupPercent());

        if (request.getUnitPrice() != null) {
            cost.setUnitPrice(request.getUnitPrice());
        }

        pricingService.recalculateItemCostTotals(cost);

        cost.setIsTaxable(true);

        cost.setShowCustomer(request.getShowCustomer());
        cost.setIsOptional(request.getIsOptional());
        cost.setInternalNote(request.getInternalNote());
        cost.setCustomerNote(request.getCustomerNote());

        cost.setCreatedAtUtc(now);
        cost.setUpdatedAtUtc(now);
        cost.setCreatedByUserId(currentUserId);
        cost.setUpdatedByUserId(currentUserId);

        cost.setIsDeleted(false);

        BidRevisionItemCost savedCost = bidRevisionItemCostRepository.save(cost);

        pricingService.recalculateItemTotals(item);
        item.setUpdatedAtUtc(now);
        item.setUpdatedByUserId(currentUserId);

        bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);
        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionItemCostResponse(savedCost);
    }

    @Transactional(readOnly = true)
    public List<BidRevisionItemCostResponse> getItemCosts(UUID bidRevisionItemId) {

        BidRevisionItem item = bidRevisionItemRepository
                .findByBidRevisionItemIdAndIsDeletedFalse(bidRevisionItemId)
                .orElseThrow(() -> new NotFoundException("Bid revision item not found"));

        estimateAccessService.requireBidViewAccess(item.getBidRevision().getBid());

        return bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        bidRevisionItemId)
                .stream()
                .map(bidMapper::toBidRevisionItemCostResponse)
                .toList();
    }

    @Transactional
    public BidRevisionItemCostResponse updateItemCost(
            UUID bidRevisionItemCostId,
            UpdateBidRevisionItemCostRequest request) {

        BidRevisionItemCost cost = bidRevisionItemCostRepository
                .findByBidRevisionItemCostIdAndIsDeletedFalse(bidRevisionItemCostId)
                .orElseThrow(() -> new NotFoundException("Bid revision item cost not found"));

        BidRevisionItem item = cost.getBidRevisionItem();
        BidRevision revision = item.getBidRevision();
        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        if (request.getCostElementId() != null) {

            CostElement costElement = costElementRepository
                    .findByCostElementIdAndIsDeletedFalse(
                            request.getCostElementId())
                    .orElseThrow(() -> new NotFoundException("Cost element not found"));

            if (!Boolean.TRUE.equals(costElement.getIsActive())) {
                throw new BusinessRuleException(
                        "Cannot assign cost rate to inactive cost element");
            }

            cost.setCostElement(costElement);
        }

        if (request.getCostRateId() != null) {

            CostRate costRate = costRateRepository
                    .findByCostRateIdAndIsDeletedFalseAndIsActiveTrue(
                            request.getCostRateId())
                    .orElseThrow(() -> new NotFoundException("Cost rate not found"));

            cost.setCostRate(costRate);
            cost.setRateSnapshot(costRate.getRateAmount());
            cost.setRateUnitSnapshot(costRate.getRateUnit().name());
        }

        if (request.getGroupName() != null) {
            cost.setGroupName(request.getGroupName());
        }

        if (request.getQuantity() != null) {
            cost.setQuantity(request.getQuantity());
        }

        if (request.getUnitOfMeasure() != null) {
            cost.setUnitOfMeasure(request.getUnitOfMeasure());
        }

        if (request.getUnitCost() != null) {
            cost.setUnitCost(request.getUnitCost());
        }

        if (request.getUnitPrice() != null) {
            cost.setUnitPrice(request.getUnitPrice());
        }

        if (request.getMarkupPercent() != null) {
            cost.setMarkupPercent(request.getMarkupPercent());
        }

        if (request.getShowCustomer() != null) {
            cost.setShowCustomer(request.getShowCustomer());
        }

        if (request.getIsOptional() != null) {
            cost.setIsOptional(request.getIsOptional());
        }

        if (request.getInternalNote() != null) {
            cost.setInternalNote(request.getInternalNote());
        }

        if (request.getCustomerNote() != null) {
            cost.setCustomerNote(request.getCustomerNote());
        }

        ensureCostRateBelongsToCostElement(cost.getCostElement(), cost.getCostRate());
        cost.setUnitOfMeasure(cost.getCostRate().getRateUnit().name());

        pricingService.recalculateItemCostTotals(cost);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        cost.setUpdatedAtUtc(now);
        cost.setUpdatedByUserId(currentUserId);

        BidRevisionItemCost savedCost = bidRevisionItemCostRepository.save(cost);

        pricingService.recalculateItemTotals(item);
        item.setUpdatedAtUtc(now);
        item.setUpdatedByUserId(currentUserId);

        bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);
        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionItemCostResponse(savedCost);
    }

    @Transactional
    public void deleteItemCost(UUID bidRevisionItemCostId) {

        BidRevisionItemCost cost = bidRevisionItemCostRepository
                .findByBidRevisionItemCostIdAndIsDeletedFalse(bidRevisionItemCostId)
                .orElseThrow(() -> new NotFoundException("Bid revision item cost not found"));

        BidRevisionItem item = cost.getBidRevisionItem();
        BidRevision revision = item.getBidRevision();
        Bid bid = revision.getBid();

        estimateAccessService.requireBidEditAccess(bid);

        ensureBidCanBeChanged(bid);
        ensureCurrentRevisionCanBeChanged(bid, revision);

        LocalDateTime now = LocalDateTime.now();

        UUID currentUserId = currentUserUtil.getCurrentUserId();

        cost.setIsDeleted(true);
        cost.setDeletedAtUtc(now);
        cost.setDeletedByUserId(currentUserId);

        cost.setUpdatedAtUtc(now);
        cost.setUpdatedByUserId(currentUserId);

        bidRevisionItemCostRepository.save(cost);

        pricingService.recalculateItemTotals(item);
        item.setUpdatedAtUtc(now);
        item.setUpdatedByUserId(currentUserId);

        bidRevisionItemRepository.save(item);

        pricingService.recalculateRevisionTotals(revision);
        revision.setUpdatedAtUtc(now);
        revision.setUpdatedByUserId(currentUserId);

        bidRevisionRepository.save(revision);

        bid.setUpdatedAtUtc(now);
        bid.setUpdatedByUserId(currentUserId);

        bidRepository.save(bid);
    }

    private ItemType getActiveItemType(UUID itemTypeId) {
        return itemTypeRepository.findByItemTypeIdAndIsDeletedFalseAndIsActiveTrue(itemTypeId)
                .orElseThrow(() -> new BusinessRuleException("Item type not found or inactive"));
    }

    private TaxRate getActiveTaxRate(UUID taxRateId) {
        return taxRateRepository.findByTaxRateIdAndIsDeletedFalseAndIsActiveTrue(taxRateId)
                .orElseThrow(() -> new BusinessRuleException("Tax rate not found or inactive"));
    }

    private ConstructionObjectType getActiveConstructionObjectType(UUID constructionObjectTypeId) {
        return constructionObjectTypeRepository
                .findByConstructionObjectTypeIdAndIsDeletedFalseAndIsActiveTrue(
                        constructionObjectTypeId)
                .orElseThrow(() -> new BusinessRuleException(
                        "Construction object type not found or inactive"));
    }

    private void applyTaxRateSnapshot(BidRevisionItem item, TaxRate taxRate) {
        item.setTaxRate(taxRate);
        item.setTaxRateSnapshotCode(taxRate.getCode());
        item.setTaxRateSnapshotName(taxRate.getName());
        item.setTaxRateSnapshotPercent(taxRate.getRatePercent());
    }

    private void ensureBidCanBeChanged(Bid bid) {
        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid revision cannot be changed");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Lost bid revision cannot be changed");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived bid revision cannot be changed");
        }
    }

    private void ensureCurrentRevisionCanBeChanged(Bid bid, BidRevision revision) {
        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision().getBidRevisionId().equals(revision.getBidRevisionId())) {
            throw new BusinessRuleException("Only current revision can be changed");
        }

        if (revision.getRevisionStatus() != RevisionStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT revisions can be changed");
        }
    }

    private void ensureBidCanBeRevised(Bid bid) {
        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid cannot be revised");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Lost bid cannot be revised");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived bid cannot be revised");
        }
    }

    private void ensureCurrentRevisionCanBeSent(Bid bid, BidRevision revision) {
        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid revision cannot be sent");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Lost bid revision cannot be sent");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived bid revision cannot be sent");
        }

        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision().getBidRevisionId().equals(revision.getBidRevisionId())) {
            throw new BusinessRuleException("Only current revision can be sent");
        }

        if (revision.getRevisionStatus() != RevisionStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT revisions can be sent");
        }
    }

    private void ensureCurrentRevisionCanBeAwarded(Bid bid, BidRevision revision) {
        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid revision cannot be awarded");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Lost bid revision cannot be awarded");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived bid revision cannot be awarded");
        }

        if (bid.getCurrentRevision() == null
                || !bid.getCurrentRevision().getBidRevisionId().equals(revision.getBidRevisionId())) {
            throw new BusinessRuleException("Only current revision can be awarded");
        }

        if (revision.getRevisionStatus() != RevisionStatus.SENT) {
            throw new BusinessRuleException("Only SENT revisions can be awarded");
        }
    }

    private void cloneRevisionItems(
            BidRevision sourceRevision,
            BidRevision targetRevision,
            LocalDateTime now,
            UUID currentUserId) {

        List<BidRevisionItem> sourceItems = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        sourceRevision.getBidRevisionId());

        for (BidRevisionItem sourceItem : sourceItems) {
            BidRevisionItem savedClonedItem = cloneRevisionItem(sourceItem, targetRevision, now, currentUserId);

            cloneItemCosts(sourceItem, savedClonedItem, now, currentUserId);

            pricingService.recalculateItemTotals(savedClonedItem);
            savedClonedItem.setUpdatedAtUtc(now);
            bidRevisionItemRepository.save(savedClonedItem);
        }
    }

    private BidRevisionItem cloneRevisionItem(
            BidRevisionItem sourceItem,
            BidRevision targetRevision,
            LocalDateTime now,
            UUID currentUserId) {

        BidRevisionItem clonedItem = new BidRevisionItem();

        clonedItem.setBidRevisionItemId(UUID.randomUUID());
        clonedItem.setBidRevision(targetRevision);

        clonedItem.setLineNumber(sourceItem.getLineNumber());
        clonedItem.setDisplayOrder(sourceItem.getDisplayOrder());
        clonedItem.setGroupName(sourceItem.getGroupName());
        clonedItem.setDescription(sourceItem.getDescription());
        clonedItem.setQuantity(sourceItem.getQuantity());
        clonedItem.setUnitOfMeasure(sourceItem.getUnitOfMeasure());

        clonedItem.setUnitCost(sourceItem.getUnitCost());
        clonedItem.setUnitPrice(sourceItem.getUnitPrice());
        clonedItem.setTotalCost(sourceItem.getTotalCost());
        clonedItem.setTotalPrice(sourceItem.getTotalPrice());
        clonedItem.setMarkupPercent(sourceItem.getMarkupPercent());
        clonedItem.setGpmPercent(sourceItem.getGpmPercent());

        clonedItem.setIsTaxable(sourceItem.getIsTaxable());
        clonedItem.setTaxAmount(sourceItem.getTaxAmount());
        clonedItem.setPriceWithTax(sourceItem.getPriceWithTax());

        clonedItem.setItemType(sourceItem.getItemType());
        clonedItem.setTaxRate(sourceItem.getTaxRate());
        clonedItem.setTaxRateSnapshotCode(sourceItem.getTaxRateSnapshotCode());
        clonedItem.setTaxRateSnapshotName(sourceItem.getTaxRateSnapshotName());
        clonedItem.setTaxRateSnapshotPercent(sourceItem.getTaxRateSnapshotPercent());

        clonedItem.setIsOptional(sourceItem.getIsOptional());
        clonedItem.setShowCustomerRow(sourceItem.getShowCustomerRow());
        clonedItem.setShowCustomerPrice(sourceItem.getShowCustomerPrice());
        clonedItem.setInternalNote(sourceItem.getInternalNote());
        clonedItem.setCustomerNote(sourceItem.getCustomerNote());

        clonedItem.setClonedFromItem(sourceItem);

        clonedItem.setCreatedAtUtc(now);
        clonedItem.setUpdatedAtUtc(now);
        clonedItem.setCreatedByUserId(currentUserId);
        clonedItem.setUpdatedByUserId(currentUserId);

        clonedItem.setIsDeleted(false);

        return bidRevisionItemRepository.save(clonedItem);
    }

    private void cloneItemCosts(
            BidRevisionItem sourceItem,
            BidRevisionItem targetItem,
            LocalDateTime now,
            UUID currentUserId) {

        List<BidRevisionItemCost> sourceCosts = bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        sourceItem.getBidRevisionItemId());

        for (BidRevisionItemCost sourceCost : sourceCosts) {
            BidRevisionItemCost clonedCost = new BidRevisionItemCost();

            clonedCost.setBidRevisionItemCostId(UUID.randomUUID());
            clonedCost.setBidRevisionItem(targetItem);

            clonedCost.setCostElement(sourceCost.getCostElement());
            clonedCost.setCostRate(sourceCost.getCostRate());

            clonedCost.setLineNumber(sourceCost.getLineNumber());
            clonedCost.setDisplayOrder(sourceCost.getDisplayOrder());

            clonedCost.setQuantity(sourceCost.getQuantity());
            clonedCost.setUnitOfMeasure(sourceCost.getUnitOfMeasure());

            clonedCost.setRateSnapshot(sourceCost.getRateSnapshot());
            clonedCost.setRateUnitSnapshot(sourceCost.getRateUnitSnapshot());

            clonedCost.setUnitCost(sourceCost.getUnitCost());
            clonedCost.setUnitPrice(sourceCost.getUnitPrice());

            clonedCost.setTotalCost(sourceCost.getTotalCost());
            clonedCost.setTotalPrice(sourceCost.getTotalPrice());

            // Legacy compatibility only.
            clonedCost.setTaxAmount(BigDecimal.ZERO);
            clonedCost.setPriceWithTax(sourceCost.getTotalPrice());

            clonedCost.setMarkupPercent(sourceCost.getMarkupPercent());
            clonedCost.setGpmPercent(sourceCost.getGpmPercent());

            clonedCost.setIsTaxable(sourceCost.getIsTaxable());
            clonedCost.setShowCustomer(sourceCost.getShowCustomer());
            clonedCost.setIsOptional(sourceCost.getIsOptional());
            clonedCost.setGroupName(sourceCost.getGroupName());
            clonedCost.setInternalNote(sourceCost.getInternalNote());
            clonedCost.setCustomerNote(sourceCost.getCustomerNote());

            clonedCost.setClonedFromItemCost(sourceCost);

            clonedCost.setCreatedAtUtc(now);
            clonedCost.setUpdatedAtUtc(now);
            clonedCost.setCreatedByUserId(currentUserId);
            clonedCost.setUpdatedByUserId(currentUserId);
            clonedCost.setIsDeleted(false);

            bidRevisionItemCostRepository.save(clonedCost);
        }
    }

    private void ensureCostRateBelongsToCostElement(
            CostElement costElement,
            CostRate costRate) {

        if (costRate == null) {
            throw new BusinessRuleException("Cost rate is required");
        }

        if (costRate.getCostElement() == null
                || !costRate.getCostElement().getCostElementId()
                        .equals(costElement.getCostElementId())) {
            throw new BusinessRuleException(
                    "Cost rate does not belong to selected cost element");
        }
    }

    private String buildBidAuditValue(Bid bid) {
        UUID customerId = bid.getCustomer() != null
                ? bid.getCustomer().getBusinessPartnerId()
                : null;

        UUID defaultTaxRateId = bid.getDefaultTaxRate() != null
                ? bid.getDefaultTaxRate().getTaxRateId()
                : null;

        UUID constructionObjectTypeId = bid.getConstructionObjectType() != null
                ? bid.getConstructionObjectType().getConstructionObjectTypeId()
                : null;

        return "customerId=" + customerId
                + "; jobName=" + bid.getJobName()
                + "; jobAddressLine1=" + bid.getJobAddressLine1()
                + "; jobAddressLine2=" + bid.getJobAddressLine2()
                + "; jobCity=" + bid.getJobCity()
                + "; jobState=" + bid.getJobState()
                + "; jobPostalCode=" + bid.getJobPostalCode()
                + "; jobCountry=" + bid.getJobCountry()
                + "; description=" + bid.getDescription()
                + "; departmentCode=" + bid.getDepartmentCode()
                + "; constructionType=" + bid.getConstructionType()
                + "; constructionObjectTypeId=" + constructionObjectTypeId
                + "; defaultTaxRateId=" + defaultTaxRateId;
    }

    private BusinessPartner getActiveCustomerBusinessPartner(UUID businessPartnerId) {
        BusinessPartner partner = businessPartnerRepository
                .findByBusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        boolean hasCustomerProfile = customerProfileRepository
                .existsByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId);

        if (!hasCustomerProfile) {
            throw new BusinessRuleException("Business partner is not a customer");
        }

        return partner;
    }

    private void applyDefaultTaxRateSnapshot(BidRevision revision, TaxRate taxRate) {
        if (taxRate == null) {
            revision.setDefaultTaxRateSnapshotCode(null);
            revision.setDefaultTaxRateSnapshotName(null);
            revision.setDefaultTaxRateSnapshotPercent(null);
            return;
        }

        revision.setDefaultTaxRateSnapshotCode(taxRate.getCode());
        revision.setDefaultTaxRateSnapshotName(taxRate.getName());
        revision.setDefaultTaxRateSnapshotPercent(taxRate.getRatePercent());
    }

    private void copyDefaultTaxRateSnapshot(
            BidRevision targetRevision,
            BidRevision sourceRevision,
            TaxRate fallbackTaxRate) {

        if (sourceRevision != null
                && (sourceRevision.getDefaultTaxRateSnapshotCode() != null
                        || sourceRevision.getDefaultTaxRateSnapshotName() != null
                        || sourceRevision.getDefaultTaxRateSnapshotPercent() != null)) {

            targetRevision.setDefaultTaxRateSnapshotCode(sourceRevision.getDefaultTaxRateSnapshotCode());
            targetRevision.setDefaultTaxRateSnapshotName(sourceRevision.getDefaultTaxRateSnapshotName());
            targetRevision.setDefaultTaxRateSnapshotPercent(sourceRevision.getDefaultTaxRateSnapshotPercent());
            return;
        }

        applyDefaultTaxRateSnapshot(targetRevision, fallbackTaxRate);
    }

    private void ensureDisplayModesCompatible(
            CustomerDisplayMode customerDisplayMode,
            EstimatePriceDisplayMode priceDisplayMode) {

        if (customerDisplayMode == null) {
            throw new BadRequestException("Customer display mode is required");
        }

        if (priceDisplayMode == null) {
            throw new BadRequestException("Price display mode is required");
        }

        if (priceDisplayMode == EstimatePriceDisplayMode.TOTALS) {
            return;
        }

        int customerLevel = customerDisplayLevel(customerDisplayMode);
        int priceLevel = priceDisplayLevel(priceDisplayMode);

        if (priceLevel > customerLevel) {
            throw new BusinessRuleException(
                    "Price display mode cannot be more detailed than customer display mode");
        }
    }

    private int customerDisplayLevel(CustomerDisplayMode mode) {
        return switch (mode) {
            case GROUP_LEVEL -> 1;
            case ITEM_TYPE_LEVEL -> 2;
            case ITEM_LEVEL -> 3;
            case ITEM_COST_LEVEL -> 4;
        };
    }

    private int priceDisplayLevel(EstimatePriceDisplayMode mode) {
        return switch (mode) {
            case TOTALS -> 0;
            case GROUP_LEVEL -> 1;
            case ITEM_TYPE_LEVEL -> 2;
            case ITEM_LEVEL -> 3;
            case ITEM_COST_LEVEL -> 4;
        };
    }
}