package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemCostResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidFromRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemRequest;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import com.glassgang.pmworkflow.estimate.entity.CostElement;
import com.glassgang.pmworkflow.estimate.entity.CostRate;
import com.glassgang.pmworkflow.estimate.entity.Customer;
import com.glassgang.pmworkflow.estimate.entity.ItemType;
import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import com.glassgang.pmworkflow.estimate.enums.BidStatus;
import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import com.glassgang.pmworkflow.estimate.enums.RevisionStatus;
import com.glassgang.pmworkflow.estimate.mapper.BidMapper;
import com.glassgang.pmworkflow.estimate.repository.BidRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import com.glassgang.pmworkflow.estimate.repository.CustomerRepository;
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
    private final CustomerRepository customerRepository;
    private final BidNumberService bidNumberService;
    private final BidMapper bidMapper;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final CostElementRepository costElementRepository;
    private final CostRateRepository costRateRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final TaxRateRepository taxRateRepository;
    private final PricingService pricingService;
    private final CurrentUserUtil currentUserUtil;
    private final EstimateAccessService estimateAccessService;

    public BidService(
            BidRepository bidRepository,
            BidRevisionRepository bidRevisionRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            CostElementRepository costElementRepository,
            CostRateRepository costRateRepository,
            CustomerRepository customerRepository,
            BidNumberService bidNumberService,
            BidMapper bidMapper,
            ItemTypeRepository itemTypeRepository,
            TaxRateRepository taxRateRepository,
            PricingService pricingService,
            CurrentUserUtil currentUserUtil,
            EstimateAccessService estimateAccessService) {
        this.bidRepository = bidRepository;
        this.bidRevisionRepository = bidRevisionRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.costElementRepository = costElementRepository;
        this.costRateRepository = costRateRepository;
        this.customerRepository = customerRepository;
        this.bidNumberService = bidNumberService;
        this.bidMapper = bidMapper;
        this.itemTypeRepository = itemTypeRepository;
        this.taxRateRepository = taxRateRepository;
        this.pricingService = pricingService;
        this.currentUserUtil = currentUserUtil;
        this.estimateAccessService = estimateAccessService;
    }

    @Transactional
    public BidResponse createBid(CreateBidRequest request) {

        estimateAccessService.requireEstimateCreateAccess();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        Customer customer = customerRepository
                .findByCustomerIdAndIsDeletedFalse(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        LocalDateTime now = LocalDateTime.now();

        Bid bid = new Bid();
        bid.setBidId(UUID.randomUUID());
        bid.setCustomer(customer);
        bid.setBidNumber(bidNumberService.generateNextBidNumber());
        bid.setJobNumber(bidNumberService.generateNextJobNumber());
        bid.setJobName(request.getJobName());
        bid.setDescription(request.getDescription());
        bid.setDepartmentCode(request.getDepartmentCode());
        bid.setBidStatus(BidStatus.DRAFT);
        bid.setCreatedAtUtc(now);
        bid.setUpdatedAtUtc(now);
        bid.setCreatedByUserId(currentUserId);
        bid.setUpdatedByUserId(currentUserId);
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
        revision.setCreatedAtUtc(now);
        revision.setUpdatedAtUtc(now);
        revision.setCreatedByUserId(currentUserId);
        revision.setUpdatedByUserId(currentUserId);
        revision.setIsDeleted(false);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        savedBid.setCurrentRevision(savedRevision);
        savedBid.setUpdatedAtUtc(now);

        Bid savedBidWithRevision = bidRepository.save(savedBid);

        return bidMapper.toBidResponse(savedBidWithRevision);
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

        estimateAccessService.requireBidViewAccess(sourceRevision.getBid());

        Customer customer = customerRepository
                .findByCustomerIdAndIsDeletedFalse(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        LocalDateTime now = LocalDateTime.now();

        Bid newBid = new Bid();
        newBid.setBidId(UUID.randomUUID());
        newBid.setCustomer(customer);
        newBid.setBidNumber(bidNumberService.generateNextBidNumber());
        newBid.setJobNumber(bidNumberService.generateNextJobNumber());
        newBid.setJobName(request.getJobName());
        newBid.setDescription(request.getDescription());
        newBid.setDepartmentCode(request.getDepartmentCode());
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

        BidRevision savedRevision = bidRevisionRepository.save(newRevision);

        cloneRevisionItems(sourceRevision, savedRevision, now, currentUserId);

        pricingService.recalculateRevisionTotals(savedRevision);
        savedRevision.setUpdatedAtUtc(now);
        bidRevisionRepository.save(savedRevision);

        savedBid.setCurrentRevision(savedRevision);
        savedBid.setUpdatedAtUtc(now);
        bidRepository.save(savedBid);

        return bidMapper.toBidResponse(savedBid);
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

        revision.setClonedFromBidRevision(currentRevision);

        revision.setCreatedAtUtc(now);
        revision.setUpdatedAtUtc(now);
        revision.setCreatedByUserId(currentUserId);
        revision.setUpdatedByUserId(currentUserId);

        revision.setIsDeleted(false);

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

        item.setCustomerDisplayMode(
                request.getCustomerDisplayMode() != null
                        ? request.getCustomerDisplayMode()
                        : CustomerDisplayMode.ITEM_TOTAL_ONLY);

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

        item.setUnitCost(BigDecimal.ZERO);
        item.setUnitPrice(BigDecimal.ZERO);
        item.setTotalCost(BigDecimal.ZERO);
        item.setTotalPrice(BigDecimal.ZERO);

        item.setMarkupPercent(null);
        item.setGpmPercent(null);

        item.setTaxAmount(BigDecimal.ZERO);
        item.setPriceWithTax(BigDecimal.ZERO);

        item.setIsOptional(request.getIsOptional());

        item.setInternalNote(request.getInternalNote());

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

        if (request.getItemTypeId() != null) {
            ItemType itemType = getActiveItemType(request.getItemTypeId());
            item.setItemType(itemType);
        }

        if (request.getTaxRateId() != null) {
            TaxRate taxRate = getActiveTaxRate(request.getTaxRateId());
            applyTaxRateSnapshot(item, taxRate);
        }

        if (request.getCustomerDisplayMode() != null) {
            item.setCustomerDisplayMode(request.getCustomerDisplayMode());
        }

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
                .findByCostElementIdAndIsDeletedFalseAndIsActiveTrue(request.getCostElementId())
                .orElseThrow(() -> new NotFoundException("Cost element not found"));

        CostRate costRate = null;

        if (request.getCostRateId() != null) {
            costRate = costRateRepository
                    .findByCostRateIdAndIsDeletedFalseAndIsActiveTrue(request.getCostRateId())
                    .orElseThrow(() -> new NotFoundException("Cost rate not found"));
        }

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
        cost.setUnitOfMeasure(request.getUnitOfMeasure());

        cost.setRateSnapshot(costRate != null ? costRate.getRateAmount() : null);
        cost.setRateUnitSnapshot(costRate != null ? costRate.getRateUnit().name() : null);

        cost.setUnitCost(request.getUnitCost());
        cost.setUnitPrice(request.getUnitPrice());

        pricingService.recalculateItemCostTotals(cost);

        cost.setMarkupPercent(null);
        cost.setGpmPercent(null);

        cost.setIsTaxable(true);

        cost.setShowCustomer(request.getShowCustomer());
        cost.setIsOptional(request.getIsOptional());
        cost.setInternalNote(request.getInternalNote());

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
                    .findByCostElementIdAndIsDeletedFalseAndIsActiveTrue(
                            request.getCostElementId())
                    .orElseThrow(() -> new NotFoundException("Cost element not found"));

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

        if (request.getShowCustomer() != null) {
            cost.setShowCustomer(request.getShowCustomer());
        }

        if (request.getIsOptional() != null) {
            cost.setIsOptional(request.getIsOptional());
        }

        if (request.getInternalNote() != null) {
            cost.setInternalNote(request.getInternalNote());
        }

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

        clonedItem.setCustomerDisplayMode(
                sourceItem.getCustomerDisplayMode() != null
                        ? sourceItem.getCustomerDisplayMode()
                        : CustomerDisplayMode.ITEM_TOTAL_ONLY);

        clonedItem.setIsOptional(sourceItem.getIsOptional());
        clonedItem.setShowCustomerRow(sourceItem.getShowCustomerRow());
        clonedItem.setShowCustomerPrice(sourceItem.getShowCustomerPrice());
        clonedItem.setInternalNote(sourceItem.getInternalNote());

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

            clonedCost.setClonedFromItemCost(sourceCost);

            clonedCost.setCreatedAtUtc(now);
            clonedCost.setUpdatedAtUtc(now);
            clonedCost.setCreatedByUserId(currentUserId);
            clonedCost.setUpdatedByUserId(currentUserId);
            clonedCost.setIsDeleted(false);

            bidRevisionItemCostRepository.save(clonedCost);
        }
    }
}