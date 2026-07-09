package com.glassgang.pmworkflow.estimate.pdf.model;

import com.glassgang.pmworkflow.estimate.enums.ConstructionType;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfJobBlock {

    private String jobName;
    private String jobNumber;
    private String description;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private DepartmentCode departmentCode;
    private ConstructionType constructionType;
    private UUID constructionObjectTypeId;
    private String constructionObjectTypeCode;
    private String constructionObjectTypeName;

    private String defaultTaxRateCode;
    private String defaultTaxRateName;
    private java.math.BigDecimal defaultTaxRatePercent;
}