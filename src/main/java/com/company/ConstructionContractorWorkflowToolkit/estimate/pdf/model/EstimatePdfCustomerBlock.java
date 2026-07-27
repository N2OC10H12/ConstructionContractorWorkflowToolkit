package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EstimatePdfCustomerBlock {

    private UUID customerId;

    private String displayName;
    private String companyName;
    private String firstName;
    private String lastName;

    private String email;
    private String phone;
    private String website;

    private EstimatePdfAddressBlock address;
    private EstimatePdfContactBlock contact;
}