package com.glassgang.pmworkflow.estimate.pdf.model;

import com.glassgang.pmworkflow.estimate.enums.EstimatePriceDisplayMode;
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

    private EstimatePriceDisplayMode priceDisplayMode;
    private Boolean showTitleTotalPrice;

    private EstimatePdfCompanyBlock company;
    private EstimatePdfCustomerBlock customer;
    private EstimatePdfJobBlock job;
    private EstimatePdfTotals totals;

    private List<EstimatePdfGroup> groups = new ArrayList<>();
}