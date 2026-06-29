package com.glassgang.pmworkflow.businesspartner.controller;

import com.glassgang.pmworkflow.businesspartner.dto.BusinessPartnerResponse;
import com.glassgang.pmworkflow.businesspartner.dto.BusinessPartnerSummaryResponse;
import com.glassgang.pmworkflow.businesspartner.dto.CreateBusinessPartnerAddressRequest;
import com.glassgang.pmworkflow.businesspartner.dto.CreateBusinessPartnerContactRequest;
import com.glassgang.pmworkflow.businesspartner.dto.CreateBusinessPartnerRequest;
import com.glassgang.pmworkflow.businesspartner.dto.CreateCustomerProfileRequest;
import com.glassgang.pmworkflow.businesspartner.dto.CreateVendorProfileRequest;
import com.glassgang.pmworkflow.businesspartner.dto.UpdateBusinessPartnerAddressRequest;
import com.glassgang.pmworkflow.businesspartner.dto.UpdateBusinessPartnerContactRequest;
import com.glassgang.pmworkflow.businesspartner.dto.UpdateBusinessPartnerRequest;
import com.glassgang.pmworkflow.businesspartner.dto.UpdateCustomerProfileRequest;
import com.glassgang.pmworkflow.businesspartner.dto.UpdateVendorProfileRequest;
import com.glassgang.pmworkflow.businesspartner.service.BusinessPartnerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-partners")
public class BusinessPartnerController {

    private final BusinessPartnerService businessPartnerService;

    public BusinessPartnerController(BusinessPartnerService businessPartnerService) {
        this.businessPartnerService = businessPartnerService;
    }

    @GetMapping
    public List<BusinessPartnerSummaryResponse> getBusinessPartners() {
        return businessPartnerService.getBusinessPartners();
    }

    @GetMapping("/customers")
    public List<BusinessPartnerSummaryResponse> getCustomers() {
        return businessPartnerService.getCustomers();
    }

    @GetMapping("/vendors")
    public List<BusinessPartnerSummaryResponse> getVendors() {
        return businessPartnerService.getVendors();
    }

    @GetMapping("/{businessPartnerId}")
    public BusinessPartnerResponse getBusinessPartner(
            @PathVariable UUID businessPartnerId) {

        return businessPartnerService.getBusinessPartner(businessPartnerId);
    }

    @PostMapping
    public BusinessPartnerResponse createBusinessPartner(
            @Valid @RequestBody CreateBusinessPartnerRequest request) {

        return businessPartnerService.createBusinessPartner(request);
    }

    @PatchMapping("/{businessPartnerId}")
    public BusinessPartnerResponse updateBusinessPartner(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody UpdateBusinessPartnerRequest request) {

        return businessPartnerService.updateBusinessPartner(businessPartnerId, request);
    }

    @DeleteMapping("/{businessPartnerId}")
    public void deleteBusinessPartner(
            @PathVariable UUID businessPartnerId) {

        businessPartnerService.deleteBusinessPartner(businessPartnerId);
    }

    @PostMapping("/{businessPartnerId}/customer-profile")
    public BusinessPartnerResponse createCustomerProfile(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody CreateCustomerProfileRequest request) {

        return businessPartnerService.createCustomerProfile(businessPartnerId, request);
    }

    @PatchMapping("/{businessPartnerId}/customer-profile")
    public BusinessPartnerResponse updateCustomerProfile(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody UpdateCustomerProfileRequest request) {

        return businessPartnerService.updateCustomerProfile(businessPartnerId, request);
    }

    @DeleteMapping("/{businessPartnerId}/customer-profile")
    public BusinessPartnerResponse deleteCustomerProfile(
            @PathVariable UUID businessPartnerId) {

        return businessPartnerService.deleteCustomerProfile(businessPartnerId);
    }

    @PostMapping("/{businessPartnerId}/vendor-profile")
    public BusinessPartnerResponse createVendorProfile(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody CreateVendorProfileRequest request) {

        return businessPartnerService.createVendorProfile(businessPartnerId, request);
    }

    @PatchMapping("/{businessPartnerId}/vendor-profile")
    public BusinessPartnerResponse updateVendorProfile(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody UpdateVendorProfileRequest request) {

        return businessPartnerService.updateVendorProfile(businessPartnerId, request);
    }

    @DeleteMapping("/{businessPartnerId}/vendor-profile")
    public BusinessPartnerResponse deleteVendorProfile(
            @PathVariable UUID businessPartnerId) {

        return businessPartnerService.deleteVendorProfile(businessPartnerId);
    }

    @PostMapping("/{businessPartnerId}/addresses")
    public BusinessPartnerResponse createAddress(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody CreateBusinessPartnerAddressRequest request) {

        return businessPartnerService.createAddress(businessPartnerId, request);
    }

    @PatchMapping("/addresses/{addressId}")
    public BusinessPartnerResponse updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateBusinessPartnerAddressRequest request) {

        return businessPartnerService.updateAddress(addressId, request);
    }

    @DeleteMapping("/addresses/{addressId}")
    public BusinessPartnerResponse deleteAddress(
            @PathVariable UUID addressId) {

        return businessPartnerService.deleteAddress(addressId);
    }

    @PostMapping("/{businessPartnerId}/contacts")
    public BusinessPartnerResponse createContact(
            @PathVariable UUID businessPartnerId,
            @Valid @RequestBody CreateBusinessPartnerContactRequest request) {

        return businessPartnerService.createContact(businessPartnerId, request);
    }

    @PatchMapping("/contacts/{contactId}")
    public BusinessPartnerResponse updateContact(
            @PathVariable UUID contactId,
            @Valid @RequestBody UpdateBusinessPartnerContactRequest request) {

        return businessPartnerService.updateContact(contactId, request);
    }

    @DeleteMapping("/contacts/{contactId}")
    public BusinessPartnerResponse deleteContact(
            @PathVariable UUID contactId) {

        return businessPartnerService.deleteContact(contactId);
    }
}