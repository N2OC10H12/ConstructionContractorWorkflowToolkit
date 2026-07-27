package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CustomerDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimatePriceDisplayMode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfModel {

    private UUID bidId;
    private UUID bidRevisionId;

    private String bidNumber;
    private String jobNumber;
    private Integer revisionNumber;
    private String revisionDisplayName;

    private LocalDateTime createdAtUtc;
    private LocalDateTime revisionUpdatedAtUtc;

    private CustomerDisplayMode customerDisplayMode;
    private EstimatePriceDisplayMode priceDisplayMode;

    private Boolean showHierarchyPriceColumn;
    private Boolean showTitleTotalPrice;

    private EstimatePdfCompanyBlock company;
    private EstimatePdfCustomerBlock customer;
    private EstimatePdfJobBlock job;
    private EstimatePdfTotals totals;

    /*
     * Existing hierarchical representation retained during migration.
     */
    private List<EstimatePdfGroup> groups = new ArrayList<>();

    /*
     * Flattened customer-visible hierarchy in final rendering order.
     *
     * This list is populated only after CustomerDisplayMode visibility
     * rules have been applied.
     *
     * It will later be split into:
     * - normal table rows
     * - final carry rows
     * - continuation/context rows
     */
    private List<EstimatePdfPrintableRow> printableRows = new ArrayList<>();
}