package com.glassgang.pmworkflow.audit.controller;

import com.glassgang.pmworkflow.audit.dto.EstimateAuditLogResponse;
import com.glassgang.pmworkflow.audit.service.EstimateAuditService;
import com.glassgang.pmworkflow.common.dto.PagedResponse;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.repository.BidRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionRepository;
import com.glassgang.pmworkflow.estimate.service.EstimateAccessService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/estimates/bids")
public class EstimateAuditController {

    private final EstimateAuditService estimateAuditService;
    private final BidRepository bidRepository;
    private final BidRevisionRepository bidRevisionRepository;
    private final EstimateAccessService estimateAccessService;

    public EstimateAuditController(EstimateAuditService estimateAuditService,
                                   BidRepository bidRepository,
                                   BidRevisionRepository bidRevisionRepository,
                                   EstimateAccessService estimateAccessService) {
        this.estimateAuditService = estimateAuditService;
        this.bidRepository = bidRepository;
        this.bidRevisionRepository = bidRevisionRepository;
        this.estimateAccessService = estimateAccessService;
    }

    @GetMapping("/{bidId}/audit")
    public PagedResponse<EstimateAuditLogResponse> getBidAudit(
            @PathVariable UUID bidId,
            Pageable pageable
    ) {
        Bid bid = bidRepository.findByBidIdAndIsDeletedFalse(bidId)
                .orElseThrow(() -> new NotFoundException("Bid not found"));

        estimateAccessService.requireBidViewAccess(bid);

        return estimateAuditService.getBidAudit(bid.getBidId(), pageable);
    }

    @GetMapping("/revisions/{bidRevisionId}/audit")
    public PagedResponse<EstimateAuditLogResponse> getRevisionAudit(
            @PathVariable UUID bidRevisionId,
            Pageable pageable
    ) {
        BidRevision revision = bidRevisionRepository.findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new NotFoundException("Bid revision not found"));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidViewAccess(bid);

        return estimateAuditService.getRevisionAudit(revision.getBidRevisionId(), pageable);
    }
}