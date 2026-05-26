package com.glassgang.pmworkflow.estimate.controller;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionRequest;
import com.glassgang.pmworkflow.estimate.service.BidService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/estimates/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/{bidId}")
    public BidResponse getBid(@PathVariable UUID bidId) {
        return bidService.getBid(bidId);
    }

    @PostMapping
    public BidResponse createBid(@Valid @RequestBody CreateBidRequest request) {
        return bidService.createBid(request);
    }

    @GetMapping("/revisions/{bidRevisionId}")
    public BidRevisionResponse getBidRevision(@PathVariable UUID bidRevisionId) {
        return bidService.getBidRevision(bidRevisionId);
    }

    @GetMapping("/{bidId}/revisions")
    public List<BidRevisionResponse> getBidRevisions(@PathVariable UUID bidId) {
        return bidService.getBidRevisions(bidId);
    }

    @PostMapping("/{bidId}/revisions")
    public BidRevisionResponse createRevision(
            @PathVariable UUID bidId,
            @Valid @RequestBody CreateBidRevisionRequest request) {

        return bidService.createRevision(bidId, request);
    }

    @PostMapping("/revisions/{bidRevisionId}/send")
    public BidRevisionResponse sendRevision(
            @PathVariable UUID bidRevisionId) {

        return bidService.sendRevision(bidRevisionId);
    }

    @PostMapping("/revisions/{bidRevisionId}/award")
    public BidRevisionResponse awardRevision(
            @PathVariable UUID bidRevisionId) {

        return bidService.awardRevision(bidRevisionId);
    }

    @PostMapping("/{bidId}/lose")
    public BidResponse loseBid(@PathVariable UUID bidId) {
        return bidService.loseBid(bidId);
    }
}