package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
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
}