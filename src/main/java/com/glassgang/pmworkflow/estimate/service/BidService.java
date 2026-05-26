package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionRequest;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.Customer;
import com.glassgang.pmworkflow.estimate.enums.BidStatus;
import com.glassgang.pmworkflow.estimate.enums.RevisionStatus;
import com.glassgang.pmworkflow.estimate.mapper.BidMapper;
import com.glassgang.pmworkflow.estimate.repository.BidRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionRepository;
import com.glassgang.pmworkflow.estimate.repository.CustomerRepository;
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

    public BidService(
            BidRepository bidRepository,
            BidRevisionRepository bidRevisionRepository,
            CustomerRepository customerRepository,
            BidNumberService bidNumberService,
            BidMapper bidMapper) {
        this.bidRepository = bidRepository;
        this.bidRevisionRepository = bidRevisionRepository;
        this.customerRepository = customerRepository;
        this.bidNumberService = bidNumberService;
        this.bidMapper = bidMapper;
    }

    @Transactional
    public BidResponse createBid(CreateBidRequest request) {
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
        revision.setIsDeleted(false);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        savedBid.setCurrentRevision(savedRevision);
        savedBid.setUpdatedAtUtc(now);

        Bid savedBidWithRevision = bidRepository.save(savedBid);

        return bidMapper.toBidResponse(savedBidWithRevision);
    }

    public BidResponse getBid(UUID bidId) {
        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        return bidMapper.toBidResponse(bid);
    }

    public BidRevisionResponse getBidRevision(UUID bidRevisionId) {
        BidRevision bidRevision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        return bidMapper.toBidRevisionResponse(bidRevision);
    }

    public List<BidRevisionResponse> getBidRevisions(UUID bidId) {

        bidRepository.findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

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

        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid cannot be revised");
        }

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Lost bid cannot be revised");
        }

        if (bid.getBidStatus() == BidStatus.ARCHIVED) {
            throw new BusinessRuleException("Archived bid cannot be revised");
        }

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

        revision.setTaxType(request.getTaxType());
        revision.setTaxRatePercent(request.getTaxRatePercent());
        revision.setCustomerNote(request.getCustomerNote());
        revision.setInternalNote(request.getInternalNote());

        revision.setSubtotalCost(currentRevision.getSubtotalCost());
        revision.setSubtotalPrice(currentRevision.getSubtotalPrice());
        revision.setTaxAmount(currentRevision.getTaxAmount());
        revision.setTotalPrice(currentRevision.getTotalPrice());

        revision.setClonedFromBidRevision(currentRevision);

        revision.setCreatedAtUtc(now);
        revision.setUpdatedAtUtc(now);

        revision.setIsDeleted(false);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        bid.setCurrentRevision(savedRevision);
        bid.setBidStatus(BidStatus.DRAFT);
        bid.setUpdatedAtUtc(now);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionResponse(savedRevision);
    }

    @Transactional
    public BidRevisionResponse sendRevision(UUID bidRevisionId) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

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
                || !bid.getCurrentRevision().getBidRevisionId().equals(bidRevisionId)) {
            throw new BusinessRuleException("Only current revision can be sent");
        }

        if (revision.getRevisionStatus() != RevisionStatus.DRAFT) {
            throw new BusinessRuleException(
                    "Only DRAFT revisions can be sent");
        }

        LocalDateTime now = LocalDateTime.now();

        revision.setRevisionStatus(RevisionStatus.SENT);
        revision.setSentAtUtc(now);
        revision.setUpdatedAtUtc(now);

        BidRevision savedRevision = bidRevisionRepository.save(revision);

        bid.setBidStatus(BidStatus.SENT);
        bid.setUpdatedAtUtc(now);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionResponse(savedRevision);
    }

    @Transactional
    public BidRevisionResponse awardRevision(UUID bidRevisionId) {

        BidRevision revision = bidRevisionRepository
                .findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

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
                || !bid.getCurrentRevision().getBidRevisionId().equals(bidRevisionId)) {
            throw new BusinessRuleException("Only current revision can be awarded");
        }

        if (revision.getRevisionStatus() != RevisionStatus.SENT) {
            throw new BusinessRuleException(
                    "Only SENT revisions can be awarded");
        }

        LocalDateTime now = LocalDateTime.now();

        List<BidRevision> bidRevisions = bidRevisionRepository
                .findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(
                        bid.getBidId());

        for (BidRevision bidRevision : bidRevisions) {
            if (bidRevision.getBidRevisionId().equals(bidRevisionId)) {
                bidRevision.setRevisionStatus(RevisionStatus.AWARDED);
                bidRevision.setAwardedAtUtc(now);
                bidRevision.setUpdatedAtUtc(now);
            } else if (bidRevision.getRevisionStatus() != RevisionStatus.ARCHIVED) {
                bidRevision.setRevisionStatus(RevisionStatus.ARCHIVED);
                bidRevision.setArchivedAtUtc(now);
                bidRevision.setUpdatedAtUtc(now);
            }
        }

        bidRevisionRepository.saveAll(bidRevisions);

        bid.setBidStatus(BidStatus.AWARDED);
        bid.setCurrentRevision(revision);
        bid.setUpdatedAtUtc(now);

        bidRepository.save(bid);

        return bidMapper.toBidRevisionResponse(revision);
    }

    @Transactional
    public BidResponse loseBid(UUID bidId) {

        Bid bid = bidRepository
                .findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        if (bid.getBidStatus() == BidStatus.LOST) {
            throw new BusinessRuleException("Bid is already lost");
        }

        if (bid.getBidStatus() == BidStatus.AWARDED) {
            throw new BusinessRuleException("Awarded bid cannot be marked lost");
        }

        LocalDateTime now = LocalDateTime.now();

        List<BidRevision> bidRevisions = bidRevisionRepository
                .findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(bidId);

        for (BidRevision revision : bidRevisions) {
            revision.setRevisionStatus(RevisionStatus.LOST);
            revision.setLostAtUtc(now);
            revision.setUpdatedAtUtc(now);
        }

        bidRevisionRepository.saveAll(bidRevisions);

        bid.setBidStatus(BidStatus.LOST);
        bid.setUpdatedAtUtc(now);

        Bid savedBid = bidRepository.save(bid);

        return bidMapper.toBidResponse(savedBid);
    }
}