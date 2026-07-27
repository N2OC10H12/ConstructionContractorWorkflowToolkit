package com.glassgang.pmworkflow.estimate.mapper;

import com.glassgang.pmworkflow.estimate.dto.BidResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemCostResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionItemResponse;
import com.glassgang.pmworkflow.estimate.dto.BidRevisionResponse;
import com.glassgang.pmworkflow.estimate.entity.Bid;
import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import com.glassgang.pmworkflow.estimate.dto.BidOwnerResponse;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;

import org.springframework.stereotype.Component;

@Component
public class BidMapper {

        private final AppUserRepository appUserRepository;

        public BidMapper(AppUserRepository appUserRepository) {
                this.appUserRepository = appUserRepository;
        }

        public BidResponse toBidResponse(Bid bid) {
                BidResponse response = new BidResponse();

                response.setBidId(bid.getBidId());
                response.setCustomerId(
                                bid.getCustomer() != null
                                                ? bid.getCustomer().getBusinessPartnerId()
                                                : null);
                response.setOwner(toBidOwnerResponse(bid));
                response.setBidNumber(bid.getBidNumber());
                response.setJobNumber(bid.getJobNumber());
                response.setJobName(bid.getJobName());
                response.setJobAddressLine1(bid.getJobAddressLine1());
                response.setJobAddressLine2(bid.getJobAddressLine2());
                response.setJobCity(bid.getJobCity());
                response.setJobState(bid.getJobState());
                response.setJobPostalCode(bid.getJobPostalCode());
                response.setJobCountry(bid.getJobCountry());
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
                response.setConstructionType(bid.getConstructionType());
                if (bid.getConstructionObjectType() != null) {
                        response.setConstructionObjectTypeId(
                                        bid.getConstructionObjectType().getConstructionObjectTypeId());
                        response.setConstructionObjectTypeCode(
                                        bid.getConstructionObjectType().getCode());
                        response.setConstructionObjectTypeName(
                                        bid.getConstructionObjectType().getName());
                }

                if (bid.getDefaultTaxRate() != null) {
                        response.setDefaultTaxRateId(bid.getDefaultTaxRate().getTaxRateId());
                        response.setDefaultTaxRateCode(bid.getDefaultTaxRate().getCode());
                        response.setDefaultTaxRateName(bid.getDefaultTaxRate().getName());
                        response.setDefaultTaxRatePercent(bid.getDefaultTaxRate().getRatePercent());
                }

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
                response.setCustomerDisplayMode(bidRevision.getCustomerDisplayMode());
                response.setPriceDisplayMode(bidRevision.getPriceDisplayMode());

                response.setDefaultTaxRateSnapshotCode(bidRevision.getDefaultTaxRateSnapshotCode());
                response.setDefaultTaxRateSnapshotName(bidRevision.getDefaultTaxRateSnapshotName());
                response.setDefaultTaxRateSnapshotPercent(bidRevision.getDefaultTaxRateSnapshotPercent());

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
                response.setCustomerNote(item.getCustomerNote());

                response.setClonedFromItemId(
                                item.getClonedFromItem() != null
                                                ? item.getClonedFromItem().getBidRevisionItemId()
                                                : null);

                response.setCreatedAtUtc(item.getCreatedAtUtc());
                response.setUpdatedAtUtc(item.getUpdatedAtUtc());

                if (item.getCompanyWorkType() != null) {
                        response.setCompanyWorkTypeId(
                                        item.getCompanyWorkType().getCompanyWorkTypeId());
                }

                response.setCompanyWorkTypeCode(
                                item.getCompanyWorkTypeSnapshotCode());

                response.setCompanyWorkTypeName(
                                item.getCompanyWorkTypeSnapshotName());

                if (item.getCompanyWorkTypeSnapshotCode() != null
                                && item.getCompanyWorkTypeSnapshotName() != null) {

                        response.setCompanyWorkTypeDisplayLabel(
                                        item.getCompanyWorkTypeSnapshotCode()
                                                        + " — "
                                                        + item.getCompanyWorkTypeSnapshotName());
                }

                if (item.getTaxRate() != null) {
                        response.setTaxRateId(item.getTaxRate().getTaxRateId());
                }

                response.setTaxRateCode(item.getTaxRateSnapshotCode());
                response.setTaxRateName(item.getTaxRateSnapshotName());
                response.setTaxRatePercent(item.getTaxRateSnapshotPercent());

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

                if (cost.getCostElement() != null) {
                        response.setCostElementCode(cost.getCostElement().getCode());
                        response.setCostElementName(cost.getCostElement().getName());
                }

                response.setCostRateId(
                                cost.getCostRate() != null
                                                ? cost.getCostRate().getCostRateId()
                                                : null);

                if (cost.getCostRate() != null) {
                        response.setCostRateCode(cost.getCostRate().getCode());
                        response.setCostRateName(cost.getCostRate().getName());
                }

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

                response.setIsTaxable(cost.getIsTaxable());
                response.setTaxAmount(cost.getTaxAmount());
                response.setPriceWithTax(cost.getPriceWithTax());

                response.setShowCustomer(cost.getShowCustomer());
                response.setIsOptional(cost.getIsOptional());
                response.setInternalNote(cost.getInternalNote());
                response.setCustomerNote(cost.getCustomerNote());

                response.setClonedFromItemCostId(
                                cost.getClonedFromItemCost() != null
                                                ? cost.getClonedFromItemCost().getBidRevisionItemCostId()
                                                : null);

                response.setCreatedAtUtc(cost.getCreatedAtUtc());
                response.setUpdatedAtUtc(cost.getUpdatedAtUtc());

                return response;
        }

        private BidOwnerResponse toBidOwnerResponse(Bid bid) {
                if (bid.getCreatedByUserId() == null) {
                        return null;
                }

                return appUserRepository.findById(bid.getCreatedByUserId())
                                .map(user -> {
                                        BidOwnerResponse owner = new BidOwnerResponse();
                                        owner.setId(user.getId());
                                        owner.setUsername(user.getUsername());
                                        owner.setDisplayName(user.getDisplayName());
                                        owner.setRole(user.getRole());
                                        return owner;
                                })
                                .orElseGet(() -> {
                                        BidOwnerResponse owner = new BidOwnerResponse();
                                        owner.setId(bid.getCreatedByUserId());
                                        owner.setUsername("Unknown");
                                        owner.setDisplayName("Unknown");
                                        owner.setRole(null);
                                        return owner;
                                });
        }

}