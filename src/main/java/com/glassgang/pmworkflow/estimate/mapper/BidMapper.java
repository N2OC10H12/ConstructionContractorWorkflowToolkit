package com.glassgang.pmworkflow.estimate.mapper;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
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
                        : null
        );

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
                        : null
        );

        response.setRevisionNumber(bidRevision.getRevisionNumber());
        response.setRevisionDisplayName(bidRevision.getRevisionDisplayName());
        response.setRevisionStatus(bidRevision.getRevisionStatus());

        response.setTaxType(bidRevision.getTaxType());
        response.setTaxRatePercent(bidRevision.getTaxRatePercent());

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
                        : null
        );

        response.setCreatedAtUtc(bidRevision.getCreatedAtUtc());
        response.setUpdatedAtUtc(bidRevision.getUpdatedAtUtc());

        return response;
    }
}