package com.company.ConstructionContractorWorkflowToolkit.estimate.controller;

import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidRevisionItemCostResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidRevisionResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.CreateBidFromRevisionRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.CreateBidRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.CreateBidRevisionItemCostRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.DeleteRevisionGroupCompanyWorkTypeRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.DeleteRevisionGroupRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.UpdateBidRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.UpdateBidRevisionDisplayModesRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.UpdateBidRevisionItemCostRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.UpdateBidRevisionItemRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.BidService;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.BidRevisionItemResponse;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.CreateBidRevisionItemRequest;

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

    @PatchMapping("/{bidId}")
    public BidResponse updateBid(
            @PathVariable UUID bidId,
            @Valid @RequestBody UpdateBidRequest request) {

        return bidService.updateBid(bidId, request);
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
            @PathVariable UUID bidId) {

        return bidService.createRevision(bidId);
    }

    @PatchMapping("/revisions/{bidRevisionId}/display-modes")
    public BidRevisionResponse updateRevisionDisplayModes(
            @PathVariable UUID bidRevisionId,
            @Valid @RequestBody UpdateBidRevisionDisplayModesRequest request) {

        return bidService.updateRevisionDisplayModes(bidRevisionId, request);
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

    @DeleteMapping("/revisions/{bidRevisionId}/items/group")
    public void deleteRevisionGroup(
            @PathVariable UUID bidRevisionId,
            @Valid @RequestBody DeleteRevisionGroupRequest request) {

        bidService.deleteRevisionGroup(bidRevisionId, request);
    }

    @DeleteMapping("/revisions/{bidRevisionId}/items/group-work-type")
    public void deleteRevisionGroupCompanyWorkType(
            @PathVariable UUID bidRevisionId,
            @Valid @RequestBody
            DeleteRevisionGroupCompanyWorkTypeRequest request) {

        bidService.deleteRevisionGroupCompanyWorkType(
                bidRevisionId,
                request);
    }
}