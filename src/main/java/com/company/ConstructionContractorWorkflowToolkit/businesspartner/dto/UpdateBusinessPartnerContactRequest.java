package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ContactRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBusinessPartnerContactRequest {

    @NotBlank
    @Size(max = 255)
    private String contactName;

    @Size(max = 100)
    private String title;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 50)
    private String mobilePhone;

    @NotNull
    private ContactRole contactRole;

    @NotNull
    private Boolean isPrimary;
}