package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartner;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerAddress;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerContact;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository.BusinessPartnerAddressRepository;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository.BusinessPartnerContactRepository;
import com.company.ConstructionContractorWorkflowToolkit.company.dto.CompanyProfileResponse;
import com.company.ConstructionContractorWorkflowToolkit.company.service.CompanyProfileService;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.Bid;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevision;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItem;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItemCost;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimatePriceDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CustomerDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfAddressBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfCompanyBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfContactBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfCustomerBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfGroup;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfItemCostLine;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfItemLine;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfWorkTypeGroup;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfJobBlock;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfModel;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfPrintableRow;
import com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model.EstimatePdfTotals;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemCostRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.service.EstimateAccessService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EstimatePdfModelBuilder {

    private static final String NO_GROUP_KEY = "__NO_GROUP__";
    private static final String NO_WORK_TYPE_KEY = "__NO_WORK_TYPE__";

    private final BidRevisionRepository bidRevisionRepository;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final BusinessPartnerAddressRepository businessPartnerAddressRepository;
    private final BusinessPartnerContactRepository businessPartnerContactRepository;
    private final EstimateAccessService estimateAccessService;
    private final CompanyProfileService companyProfileService;

    public EstimatePdfModelBuilder(
            BidRevisionRepository bidRevisionRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            BusinessPartnerAddressRepository businessPartnerAddressRepository,
            BusinessPartnerContactRepository businessPartnerContactRepository,
            EstimateAccessService estimateAccessService,
            CompanyProfileService companyProfileService) {

        this.bidRevisionRepository = bidRevisionRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.businessPartnerAddressRepository = businessPartnerAddressRepository;
        this.businessPartnerContactRepository = businessPartnerContactRepository;
        this.estimateAccessService = estimateAccessService;
        this.companyProfileService = companyProfileService;
    }

    @Transactional(readOnly = true)
    public EstimatePdfModel build(UUID bidRevisionId) {
        BidRevision revision = bidRevisionRepository.findByBidRevisionIdAndIsDeletedFalse(bidRevisionId)
                .orElseThrow(() -> new EntityNotFoundException("Bid revision not found: " + bidRevisionId));

        Bid bid = revision.getBid();

        estimateAccessService.requireBidViewAccess(bid);

        BusinessPartner customer = bid.getCustomer();

        EstimatePdfModel model = new EstimatePdfModel();
        model.setBidId(bid.getBidId());
        model.setBidRevisionId(revision.getBidRevisionId());
        model.setBidNumber(bid.getBidNumber());
        model.setJobNumber(bid.getJobNumber());
        model.setRevisionNumber(revision.getRevisionNumber());
        model.setRevisionDisplayName(revision.getRevisionDisplayName());
        model.setCreatedAtUtc(revision.getCreatedAtUtc());
        model.setRevisionUpdatedAtUtc(revision.getUpdatedAtUtc());

        model.setCustomerDisplayMode(
                revision.getCustomerDisplayMode() != null
                        ? revision.getCustomerDisplayMode()
                        : CustomerDisplayMode.ITEM_LEVEL);

        model.setPriceDisplayMode(
                revision.getPriceDisplayMode() != null
                        ? revision.getPriceDisplayMode()
                        : EstimatePriceDisplayMode.WORK_TYPE_LEVEL);

        model.setShowHierarchyPriceColumn(
                model.getPriceDisplayMode() != EstimatePriceDisplayMode.TOTALS);

        model.setShowTitleTotalPrice(true);

        model.setCompany(buildCompanyBlock());
        model.setCustomer(buildCustomerBlock(customer));
        model.setJob(buildJobBlock(bid, revision));
        model.setTotals(buildTotals(revision));

        List<BidRevisionItem> items = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        bidRevisionId);

        model.getGroups().addAll(buildGroups(items));
        applyCustomerDisplayMode(model);
        applyPriceDisplayMode(model);
        buildPrintableRows(model);

        return model;
    }

    private EstimatePdfCompanyBlock buildCompanyBlock() {
        CompanyProfileResponse profile = companyProfileService.getDefaultProfile();

        EstimatePdfCompanyBlock block = new EstimatePdfCompanyBlock();

        block.setCompanyName(profile.companyName());
        block.setLogoUrl(companyProfileService.getLogoDataUrl());

        block.setPhone(profile.primaryPhone());
        block.setEmail(profile.email());
        block.setWebsite(profile.website());

        applyBestPdfAddress(profile, block);

        return block;
    }

    private EstimatePdfCustomerBlock buildCustomerBlock(BusinessPartner customer) {
        if (customer == null) {
            return null;
        }

        EstimatePdfCustomerBlock block = new EstimatePdfCustomerBlock();
        block.setCustomerId(customer.getBusinessPartnerId());
        block.setDisplayName(customer.getDisplayName());
        block.setCompanyName(customer.getCompanyName());
        block.setFirstName(customer.getFirstName());
        block.setLastName(customer.getLastName());
        block.setEmail(customer.getEmail());
        block.setPhone(customer.getPhone());
        block.setWebsite(customer.getWebsite());

        BusinessPartnerAddress address = findBestAddress(customer.getBusinessPartnerId());
        block.setAddress(buildAddressBlock(address));

        BusinessPartnerContact contact = findBestContact(customer.getBusinessPartnerId());
        block.setContact(buildContactBlock(contact));

        return block;
    }

    private BusinessPartnerAddress findBestAddress(UUID businessPartnerId) {
        return businessPartnerAddressRepository
                .findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseAndIsPrimaryTrueOrderByUpdatedAtUtcDesc(
                        businessPartnerId)
                .or(() -> businessPartnerAddressRepository
                        .findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByUpdatedAtUtcDesc(
                                businessPartnerId))
                .orElse(null);
    }

    private BusinessPartnerContact findBestContact(UUID businessPartnerId) {
        return businessPartnerContactRepository
                .findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseAndIsPrimaryTrueOrderByUpdatedAtUtcDesc(
                        businessPartnerId)
                .or(() -> businessPartnerContactRepository
                        .findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByUpdatedAtUtcDesc(
                                businessPartnerId))
                .orElse(null);
    }

    private EstimatePdfAddressBlock buildAddressBlock(BusinessPartnerAddress address) {
        if (address == null) {
            return null;
        }

        EstimatePdfAddressBlock block = new EstimatePdfAddressBlock();
        block.setLine1(address.getLine1());
        block.setLine2(address.getLine2());
        block.setCity(address.getCity());
        block.setState(address.getState());
        block.setPostalCode(address.getPostalCode());
        block.setCountry(address.getCountry());

        return block;
    }

    private EstimatePdfContactBlock buildContactBlock(BusinessPartnerContact contact) {
        if (contact == null) {
            return null;
        }

        EstimatePdfContactBlock block = new EstimatePdfContactBlock();
        block.setContactName(contact.getContactName());
        block.setTitle(contact.getTitle());
        block.setEmail(contact.getEmail());
        block.setPhone(contact.getPhone());
        block.setMobilePhone(contact.getMobilePhone());

        return block;
    }

    private EstimatePdfJobBlock buildJobBlock(Bid bid, BidRevision revision) {
        EstimatePdfJobBlock block = new EstimatePdfJobBlock();

        block.setJobName(bid.getJobName());
        block.setJobNumber(bid.getJobNumber());
        block.setDescription(bid.getDescription());

        block.setAddressLine1(bid.getJobAddressLine1());
        block.setAddressLine2(bid.getJobAddressLine2());
        block.setCity(bid.getJobCity());
        block.setState(bid.getJobState());
        block.setPostalCode(bid.getJobPostalCode());
        block.setCountry(bid.getJobCountry());

        block.setDepartmentCode(bid.getDepartmentCode());
        block.setConstructionType(bid.getConstructionType());
        if (bid.getConstructionObjectType() != null) {
            block.setConstructionObjectTypeId(
                    bid.getConstructionObjectType().getConstructionObjectTypeId());
            block.setConstructionObjectTypeCode(
                    bid.getConstructionObjectType().getCode());
            block.setConstructionObjectTypeName(
                    bid.getConstructionObjectType().getName());
        }

        block.setDefaultTaxRateCode(revision.getDefaultTaxRateSnapshotCode());
        block.setDefaultTaxRateName(revision.getDefaultTaxRateSnapshotName());
        block.setDefaultTaxRatePercent(revision.getDefaultTaxRateSnapshotPercent());

        return block;
    }

    private EstimatePdfTotals buildTotals(BidRevision revision) {
        EstimatePdfTotals totals = new EstimatePdfTotals();

        totals.setSubtotalCost(revision.getSubtotalCost());
        totals.setSubtotalPrice(revision.getSubtotalPrice());

        totals.setCustomerFacingSubtotalPrice(
                nvl(revision.getTotalPrice()).subtract(nvl(revision.getTaxAmount())));

        totals.setTaxAmount(revision.getTaxAmount());
        totals.setTotalPrice(revision.getTotalPrice());

        return totals;
    }

    private List<EstimatePdfGroup> buildGroups(List<BidRevisionItem> items) {
        Map<String, EstimatePdfGroup> groupMap = new LinkedHashMap<>();
        Map<String, Map<String, EstimatePdfWorkTypeGroup>> workTypeMapsByGroup = new LinkedHashMap<>();

        for (BidRevisionItem item : items) {
            String groupKey = groupKey(item);
            EstimatePdfGroup group = groupMap.computeIfAbsent(groupKey, key -> {
                EstimatePdfGroup newGroup = new EstimatePdfGroup();
                newGroup.setGroupName(item.getGroupName());
                newGroup.setSubtotalPrice(BigDecimal.ZERO);
                newGroup.setTaxAmount(BigDecimal.ZERO);
                newGroup.setTotalPrice(BigDecimal.ZERO);
                return newGroup;
            });

            Map<String, EstimatePdfWorkTypeGroup> workTypeMap = workTypeMapsByGroup.computeIfAbsent(groupKey,
                    key -> new LinkedHashMap<>());

            String workTypeKey = workTypeKey(item);
            EstimatePdfWorkTypeGroup workTypeGroup = workTypeMap.computeIfAbsent(workTypeKey, key -> {
                EstimatePdfWorkTypeGroup newWorkTypeGroup = new EstimatePdfWorkTypeGroup();

                if (item.getCompanyWorkType() != null) {
                    newWorkTypeGroup.setWorkTypeId(
                            item.getCompanyWorkType().getCompanyWorkTypeId());
                }

                newWorkTypeGroup.setWorkTypeCode(
                        item.getCompanyWorkTypeSnapshotCode());
                newWorkTypeGroup.setWorkTypeName(
                        item.getCompanyWorkTypeSnapshotName());

                newWorkTypeGroup.setSubtotalPrice(BigDecimal.ZERO);
                newWorkTypeGroup.setTaxAmount(BigDecimal.ZERO);
                newWorkTypeGroup.setTotalPrice(BigDecimal.ZERO);

                group.getWorkTypes().add(newWorkTypeGroup);

                return newWorkTypeGroup;
            });

            EstimatePdfItemLine itemLine = buildItemLine(item);
            workTypeGroup.getItems().add(itemLine);

            BigDecimal itemSubtotalForRollup = subtotalForRollup(itemLine);
            BigDecimal itemTax = nvl(itemLine.getTaxAmount());
            BigDecimal itemTotal = nvl(itemLine.getPriceWithTax());

            workTypeGroup.setSubtotalPrice(nvl(workTypeGroup.getSubtotalPrice()).add(itemSubtotalForRollup));
            workTypeGroup.setTaxAmount(nvl(workTypeGroup.getTaxAmount()).add(itemTax));
            workTypeGroup.setTotalPrice(nvl(workTypeGroup.getTotalPrice()).add(itemTotal));

            group.setSubtotalPrice(nvl(group.getSubtotalPrice()).add(itemSubtotalForRollup));
            group.setTaxAmount(nvl(group.getTaxAmount()).add(itemTax));
            group.setTotalPrice(nvl(group.getTotalPrice()).add(itemTotal));
        }

        return List.copyOf(groupMap.values());
    }

    private EstimatePdfItemLine buildItemLine(BidRevisionItem item) {
        EstimatePdfItemLine line = new EstimatePdfItemLine();

        line.setBidRevisionItemId(item.getBidRevisionItemId());
        line.setLineNumber(item.getLineNumber());
        line.setDisplayOrder(item.getDisplayOrder());

        line.setDescription(item.getDescription());
        line.setQuantity(item.getQuantity());
        line.setUnitOfMeasure(toText(item.getUnitOfMeasure()));

        line.setUnitPrice(item.getUnitPrice());
        line.setTotalPrice(item.getTotalPrice());
        line.setTaxAmount(item.getTaxAmount());
        line.setPriceWithTax(item.getPriceWithTax());

        line.setIsOptional(item.getIsOptional());

        line.setCustomerNote(item.getCustomerNote());

        line.setTaxRateCode(item.getTaxRateSnapshotCode());
        line.setTaxRateName(item.getTaxRateSnapshotName());
        line.setTaxRatePercent(item.getTaxRateSnapshotPercent());

        List<BidRevisionItemCost> costs = bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        item.getBidRevisionItemId());

        for (BidRevisionItemCost cost : costs) {
            line.getCosts().add(buildItemCostLine(cost));
        }

        return line;
    }

    private EstimatePdfItemCostLine buildItemCostLine(BidRevisionItemCost cost) {
        EstimatePdfItemCostLine line = new EstimatePdfItemCostLine();

        line.setBidRevisionItemCostId(cost.getBidRevisionItemCostId());
        line.setLineNumber(cost.getLineNumber());
        line.setDisplayOrder(cost.getDisplayOrder());

        if (cost.getCostElement() != null) {
            line.setCostElementCode(cost.getCostElement().getCode());
            line.setCostElementName(cost.getCostElement().getName());
        }

        if (cost.getCostRate() != null) {
            line.setCostRateCode(cost.getCostRate().getCode());
            line.setCostRateName(cost.getCostRate().getName());
        }

        line.setQuantity(cost.getQuantity());
        line.setUnitOfMeasure(toText(cost.getUnitOfMeasure()));

        line.setUnitPrice(cost.getUnitPrice());
        line.setTotalPrice(cost.getTotalPrice());
        line.setTaxAmount(cost.getTaxAmount());
        line.setPriceWithTax(cost.getPriceWithTax());

        line.setIsOptional(cost.getIsOptional());

        line.setCustomerNote(cost.getCustomerNote());

        return line;
    }

    private String groupKey(BidRevisionItem item) {
        return item.getGroupName() == null || item.getGroupName().isBlank()
                ? NO_GROUP_KEY
                : item.getGroupName();
    }

    private String workTypeKey(BidRevisionItem item) {
        if (item.getCompanyWorkType() != null
                && item.getCompanyWorkType().getCompanyWorkTypeId() != null) {
            return item.getCompanyWorkType()
                    .getCompanyWorkTypeId()
                    .toString();
        }

        if (hasText(item.getCompanyWorkTypeSnapshotCode())
                || hasText(item.getCompanyWorkTypeSnapshotName())) {
            return String.valueOf(
                    item.getCompanyWorkTypeSnapshotCode())
                    + "|"
                    + String.valueOf(
                            item.getCompanyWorkTypeSnapshotName());
        }

        return NO_WORK_TYPE_KEY;
    }

    private BigDecimal subtotalForRollup(EstimatePdfItemLine itemLine) {
        if (itemLine.getPriceWithTax() != null) {
            return nvl(itemLine.getPriceWithTax()).subtract(nvl(itemLine.getTaxAmount()));
        }

        return nvl(itemLine.getTotalPrice());
    }

    private void applyPriceDisplayMode(EstimatePdfModel model) {
        EstimatePriceDisplayMode priceMode = model.getPriceDisplayMode() != null
                ? model.getPriceDisplayMode()
                : EstimatePriceDisplayMode.WORK_TYPE_LEVEL;

        CustomerDisplayMode customerMode = model.getCustomerDisplayMode() != null
                ? model.getCustomerDisplayMode()
                : CustomerDisplayMode.ITEM_LEVEL;

        boolean showGroupPrice = priceMode == EstimatePriceDisplayMode.GROUP_LEVEL;

        boolean showWorkTypePrice = priceMode == EstimatePriceDisplayMode.WORK_TYPE_LEVEL;

        boolean showItemPrice = priceMode == EstimatePriceDisplayMode.ITEM_LEVEL
                || priceMode == EstimatePriceDisplayMode.ITEM_COST_LEVEL;

        boolean showCostLines = customerMode == CustomerDisplayMode.ITEM_COST_LEVEL;

        boolean showCostPrice = priceMode == EstimatePriceDisplayMode.ITEM_COST_LEVEL;

        for (EstimatePdfGroup group : model.getGroups()) {
            group.setShowPrice(showGroupPrice);

            for (EstimatePdfWorkTypeGroup workTypeGroup : group.getWorkTypes()) {
                workTypeGroup.setShowPrice(showWorkTypePrice);

                for (EstimatePdfItemLine item : workTypeGroup.getItems()) {
                    item.setShowPrice(showItemPrice);
                    item.setShowCostLines(showCostLines);

                    for (EstimatePdfItemCostLine cost : item.getCosts()) {
                        cost.setShowPrice(showCostPrice);
                    }
                }
            }
        }
    }

    private void applyCustomerDisplayMode(EstimatePdfModel model) {
        CustomerDisplayMode mode = model.getCustomerDisplayMode() != null
                ? model.getCustomerDisplayMode()
                : CustomerDisplayMode.ITEM_LEVEL;

        for (EstimatePdfGroup group : model.getGroups()) {
            if (mode == CustomerDisplayMode.GROUP_LEVEL) {
                group.getWorkTypes().clear();
                continue;
            }

            for (EstimatePdfWorkTypeGroup workTypeGroup : group.getWorkTypes()) {
                if (mode == CustomerDisplayMode.WORK_TYPE_LEVEL) {
                    workTypeGroup.getItems().clear();
                    continue;
                }

                for (EstimatePdfItemLine item : workTypeGroup.getItems()) {
                    if (mode == CustomerDisplayMode.ITEM_LEVEL) {
                        item.getCosts().clear();
                    }
                }
            }
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String toText(Object value) {
        return value != null ? String.valueOf(value) : null;
    }

    private void applyBestPdfAddress(
            CompanyProfileResponse profile,
            EstimatePdfCompanyBlock block) {
        if (hasAnyAddress(
                profile.customerCommunicationAddressLine1(),
                profile.customerCommunicationAddressLine2(),
                profile.customerCommunicationCity(),
                profile.customerCommunicationState(),
                profile.customerCommunicationPostalCode(),
                profile.customerCommunicationCountry())) {
            block.setAddressLine1(profile.customerCommunicationAddressLine1());
            block.setAddressLine2(profile.customerCommunicationAddressLine2());
            block.setCity(profile.customerCommunicationCity());
            block.setState(profile.customerCommunicationState());
            block.setPostalCode(profile.customerCommunicationPostalCode());
            block.setCountry(profile.customerCommunicationCountry());
            return;
        }

        if (hasAnyAddress(
                profile.companyAddressLine1(),
                profile.companyAddressLine2(),
                profile.companyCity(),
                profile.companyState(),
                profile.companyPostalCode(),
                profile.companyCountry())) {
            block.setAddressLine1(profile.companyAddressLine1());
            block.setAddressLine2(profile.companyAddressLine2());
            block.setCity(profile.companyCity());
            block.setState(profile.companyState());
            block.setPostalCode(profile.companyPostalCode());
            block.setCountry(profile.companyCountry());
            return;
        }

        block.setAddressLine1(profile.legalAddressLine1());
        block.setAddressLine2(profile.legalAddressLine2());
        block.setCity(profile.legalCity());
        block.setState(profile.legalState());
        block.setPostalCode(profile.legalPostalCode());
        block.setCountry(profile.legalCountry());
    }

    private boolean hasAnyAddress(
            String line1,
            String line2,
            String city,
            String state,
            String postalCode,
            String country) {
        return hasText(line1)
                || hasText(line2)
                || hasText(city)
                || hasText(state)
                || hasText(postalCode)
                || hasText(country);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void buildPrintableRows(EstimatePdfModel model) {
        model.getPrintableRows().clear();

        for (EstimatePdfGroup group : model.getGroups()) {
            model.getPrintableRows().add(
                    EstimatePdfPrintableRow.forGroup(group));

            for (EstimatePdfWorkTypeGroup workType : group.getWorkTypes()) {
                model.getPrintableRows().add(
                        EstimatePdfPrintableRow.forWorkType(
                                group,
                                workType));

                for (EstimatePdfItemLine item : workType.getItems()) {
                    model.getPrintableRows().add(
                            EstimatePdfPrintableRow.forItem(
                                    group,
                                    workType,
                                    item));

                    if (!Boolean.TRUE.equals(item.getShowCostLines())) {
                        continue;
                    }

                    for (EstimatePdfItemCostLine cost : item.getCosts()) {
                        model.getPrintableRows().add(
                                EstimatePdfPrintableRow.forCost(
                                        group,
                                        workType,
                                        item,
                                        cost));
                    }
                }
            }
        }
    }
}