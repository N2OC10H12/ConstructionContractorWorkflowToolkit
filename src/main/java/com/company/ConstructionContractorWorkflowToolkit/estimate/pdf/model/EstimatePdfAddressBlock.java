package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstimatePdfAddressBlock {

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}