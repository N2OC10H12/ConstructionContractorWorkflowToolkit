package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstimatePdfCompanyBlock {

    private String companyName;
    private String logoUrl;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String phone;
    private String email;
    private String website;
}