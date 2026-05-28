package com.glassgang.pmworkflow.estimate.mapper;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemCostResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;

import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidResponse toBidResponse(Bid bid) {
        BidResponse response = new BidResponse();

        response.setBidId(bid.getBidId());
        response.setCustomerId(bid.getCustomer().getCustomerId());
        response.setBidNumber(bid.getBidNumber());
        response.setJobNumber(bid.getJobNumber());
        response.setJobName(bid.getJobName());
        response.setDescription(bid.getDescription());
        response.setDepartmentCode(bid.getDepartmentCode());
        response.setBidStatus(bid.getBidStatus());

        response.setCurrentRevisionId(
                bid.getCurrentRevision() != null
                        ? bid.getCurrentRevision().getBidRevisionId()
                        : null);

        response.setConvertedProjectId(bid.getConvertedProjectId());
        response.setCreatedAtUtc(bid.getCreatedAtUtc());
        response.setUpdatedAtUtc(bid.getUpdatedAtUtc());

        return response;
    }

    public BidRevisionResponse toBidRevisionResponse(BidRevision bidRevision) {
        BidRevisionResponse response = new BidRevisionResponse();

        response.setBidRevisionId(bidRevision.getBidRevisionId());

        response.setBidId(
                bidRevision.getBid() != null
                        ? bidRevision.getBid().getBidId()
                        : null);

        response.setRevisionNumber(bidRevision.getRevisionNumber());
        response.setRevisionDisplayName(bidRevision.getRevisionDisplayName());
        response.setRevisionStatus(bidRevision.getRevisionStatus());

        // response.setTaxType(bidRevision.getTaxType());
        // response.setTaxRatePercent(bidRevision.getTaxRatePercent());

        response.setSubtotalCost(bidRevision.getSubtotalCost());
        response.setSubtotalPrice(bidRevision.getSubtotalPrice());
        response.setTaxAmount(bidRevision.getTaxAmount());
        response.setTotalPrice(bidRevision.getTotalPrice());

        response.setCustomerNote(bidRevision.getCustomerNote());

        response.setSentAtUtc(bidRevision.getSentAtUtc());
        response.setAwardedAtUtc(bidRevision.getAwardedAtUtc());
        response.setLostAtUtc(bidRevision.getLostAtUtc());
        response.setArchivedAtUtc(bidRevision.getArchivedAtUtc());

        response.setClonedFromBidRevisionId(
                bidRevision.getClonedFromBidRevision() != null
                        ? bidRevision.getClonedFromBidRevision().getBidRevisionId()
                        : null);

        response.setCreatedAtUtc(bidRevision.getCreatedAtUtc());
        response.setUpdatedAtUtc(bidRevision.getUpdatedAtUtc());

        return response;
    }

    public BidRevisionItemResponse toBidRevisionItemResponse(BidRevisionItem item) {
        BidRevisionItemResponse response = new BidRevisionItemResponse();

        response.setBidRevisionItemId(item.getBidRevisionItemId());

        response.setBidRevisionId(
                item.getBidRevision() != null
                        ? item.getBidRevision().getBidRevisionId()
                        : null);

        response.setLineNumber(item.getLineNumber());
        response.setDisplayOrder(item.getDisplayOrder());
        response.setGroupName(item.getGroupName());
        response.setDescription(item.getDescription());
        response.setQuantity(item.getQuantity());
        response.setUnitOfMeasure(item.getUnitOfMeasure());

        response.setUnitCost(item.getUnitCost());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalCost(item.getTotalCost());
        response.setTotalPrice(item.getTotalPrice());
        response.setMarkupPercent(item.getMarkupPercent());
        response.setGpmPercent(item.getGpmPercent());

        response.setIsTaxable(item.getIsTaxable());
        response.setTaxAmount(item.getTaxAmount());
        response.setPriceWithTax(item.getPriceWithTax());

        response.setIsOptional(item.getIsOptional());
        response.setShowCustomerRow(item.getShowCustomerRow());
        response.setShowCustomerPrice(item.getShowCustomerPrice());
        response.setInternalNote(item.getInternalNote());

        response.setClonedFromItemId(
                item.getClonedFromItem() != null
                        ? item.getClonedFromItem().getBidRevisionItemId()
                        : null);

        response.setCreatedAtUtc(item.getCreatedAtUtc());
        response.setUpdatedAtUtc(item.getUpdatedAtUtc());

        if (item.getItemType() != null) {
            response.setItemTypeId(item.getItemType().getItemTypeId());
            response.setItemTypeCode(item.getItemType().getCode());
            response.setItemTypeName(item.getItemType().getName());
        }

        if (item.getTaxRate() != null) {
            response.setTaxRateId(item.getTaxRate().getTaxRateId());
        }

        response.setTaxRateCode(item.getTaxRateSnapshotCode());
        response.setTaxRateName(item.getTaxRateSnapshotName());
        response.setTaxRatePercent(item.getTaxRateSnapshotPercent());

        response.setCustomerDisplayMode(item.getCustomerDisplayMode());

        return response;
    }

    public BidRevisionItemCostResponse toBidRevisionItemCostResponse(BidRevisionItemCost cost) {
        BidRevisionItemCostResponse response = new BidRevisionItemCostResponse();

        response.setBidRevisionItemCostId(cost.getBidRevisionItemCostId());

        response.setBidRevisionItemId(
                cost.getBidRevisionItem() != null
                        ? cost.getBidRevisionItem().getBidRevisionItemId()
                        : null);

        response.setCostElementId(
                cost.getCostElement() != null
                        ? cost.getCostElement().getCostElementId()
                        : null);

        response.setCostRateId(
                cost.getCostRate() != null
                        ? cost.getCostRate().getCostRateId()
                        : null);

        response.setLineNumber(cost.getLineNumber());
        response.setDisplayOrder(cost.getDisplayOrder());
        response.setGroupName(cost.getGroupName());

        response.setQuantity(cost.getQuantity());
        response.setUnitOfMeasure(cost.getUnitOfMeasure());

        response.setRateSnapshot(cost.getRateSnapshot());
        response.setRateUnitSnapshot(cost.getRateUnitSnapshot());

        response.setUnitCost(cost.getUnitCost());
        response.setUnitPrice(cost.getUnitPrice());
        response.setTotalCost(cost.getTotalCost());
        response.setTotalPrice(cost.getTotalPrice());

        response.setMarkupPercent(cost.getMarkupPercent());
        response.setGpmPercent(cost.getGpmPercent());

        // response.setIsTaxable(cost.getIsTaxable());
        // response.setTaxAmount(cost.getTaxAmount());
        // response.setPriceWithTax(cost.getPriceWithTax());

        response.setShowCustomer(cost.getShowCustomer());
        response.setIsOptional(cost.getIsOptional());
        response.setInternalNote(cost.getInternalNote());

        response.setClonedFromItemCostId(
                cost.getClonedFromItemCost() != null
                        ? cost.getClonedFromItemCost().getBidRevisionItemCostId()
                        : null);

        response.setCreatedAtUtc(cost.getCreatedAtUtc());
        response.setUpdatedAtUtc(cost.getUpdatedAtUtc());

        return response;
    }

}