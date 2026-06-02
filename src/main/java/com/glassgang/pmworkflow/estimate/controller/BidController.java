package com.glassgang.pmworkflow.estimate.controller;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemCostResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidFromRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemCostRequest;
import com.glassgang.pmworkflow.estimate.dto.UpdateBidRevisionItemRequest;
import com.glassgang.pmworkflow.estimate.service.BidService;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemResponse;
import com.glassgang.pmworkflow.estimate.dto.CreateBidRevisionItemRequest;

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

    @GetMapping
    public List<BidResponse> getBids(
            @RequestParam(required = false, defaultValue = "all") String scope) {
        return bidService.getBids(scope);
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

    @PostMapping("/revisions/{bidRevisionId}/items")
    public BidRevisionItemResponse createRevisionItem(
            @PathVariable UUID bidRevisionId,
            @Valid @RequestBody CreateBidRevisionItemRequest request) {

        return bidService.createRevisionItem(bidRevisionId, request);
    }

    @GetMapping("/revisions/{bidRevisionId}/items")
    public List<BidRevisionItemResponse> getRevisionItems(
            @PathVariable UUID bidRevisionId) {

        return bidService.getRevisionItems(bidRevisionId);
    }

    @DeleteMapping("/revisions/items/{bidRevisionItemId}")
    public void deleteRevisionItem(@PathVariable UUID bidRevisionItemId) {
        bidService.deleteRevisionItem(bidRevisionItemId);
    }

    @PatchMapping("/revisions/items/{bidRevisionItemId}")
    public BidRevisionItemResponse updateRevisionItem(
            @PathVariable UUID bidRevisionItemId,
            @Valid @RequestBody UpdateBidRevisionItemRequest request) {

        return bidService.updateRevisionItem(bidRevisionItemId, request);
    }

    @PostMapping("/revisions/items/{bidRevisionItemId}/costs")
    public BidRevisionItemCostResponse createItemCost(
            @PathVariable UUID bidRevisionItemId,
            @Valid @RequestBody CreateBidRevisionItemCostRequest request) {

        return bidService.createItemCost(bidRevisionItemId, request);
    }

    @GetMapping("/revisions/items/{bidRevisionItemId}/costs")
    public List<BidRevisionItemCostResponse> getItemCosts(
            @PathVariable UUID bidRevisionItemId) {

        return bidService.getItemCosts(bidRevisionItemId);
    }

    @PatchMapping("/revisions/item-costs/{bidRevisionItemCostId}")
    public BidRevisionItemCostResponse updateItemCost(
            @PathVariable UUID bidRevisionItemCostId,
            @Valid @RequestBody UpdateBidRevisionItemCostRequest request) {

        return bidService.updateItemCost(bidRevisionItemCostId, request);
    }

    @DeleteMapping("/revisions/item-costs/{bidRevisionItemCostId}")
    public void deleteItemCost(@PathVariable UUID bidRevisionItemCostId) {
        bidService.deleteItemCost(bidRevisionItemCostId);
    }

    @PostMapping("/from-revision/{sourceBidRevisionId}")
    public BidResponse createBidFromRevision(
            @PathVariable UUID sourceBidRevisionId,
            @Valid @RequestBody CreateBidFromRevisionRequest request) {
        return bidService.createBidFromRevision(sourceBidRevisionId, request);
    }
}