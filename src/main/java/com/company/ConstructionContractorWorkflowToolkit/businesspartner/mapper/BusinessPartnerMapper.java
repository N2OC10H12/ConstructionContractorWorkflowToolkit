package com.company.ConstructionContractorWorkflowToolkit.businesspartner.mapper;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.BusinessPartnerAddressResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.BusinessPartnerContactResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.BusinessPartnerResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.BusinessPartnerSummaryResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.CustomerProfileResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto.VendorProfileResponse;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartner;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerAddress;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerContact;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.CustomerProfile;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.VendorProfile;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.AddressType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BusinessPartnerMapper {

    public BusinessPartnerResponse toResponse(
            BusinessPartner partner,
            CustomerProfile customerProfile,
            VendorProfile vendorProfile,
            List<BusinessPartnerAddress> addresses,
            List<BusinessPartnerContact> contacts) {

        if (partner == null) {
            return null;
        }

        BusinessPartnerResponse response = new BusinessPartnerResponse();

        response.setBusinessPartnerId(partner.getBusinessPartnerId());
        response.setPartnerType(partner.getPartnerType());

        response.setDisplayName(partner.getDisplayName());
        response.setCompanyName(partner.getCompanyName());
        response.setFirstName(partner.getFirstName());
        response.setLastName(partner.getLastName());

        response.setEmail(partner.getEmail());
        response.setPhone(partner.getPhone());
        response.setWebsite(partner.getWebsite());

        response.setInternalNote(partner.getInternalNote());

        response.setIsCustomer(customerProfile != null);
        response.setCustomerProfile(toCustomerProfileResponse(customerProfile));

        response.setIsVendor(vendorProfile != null);
        response.setVendorProfile(toVendorProfileResponse(vendorProfile));

        List<BusinessPartnerAddressResponse> addressResponses = addresses == null
                ? List.of()
                : addresses.stream()
                .map(this::toAddressResponse)
                .toList();

        List<BusinessPartnerContactResponse> contactResponses = contacts == null
                ? List.of()
                : contacts.stream()
                .map(this::toContactResponse)
                .toList();

        response.setAddresses(addressResponses);
        response.setContacts(contactResponses);

        response.setPrimaryBillingAddress(
                addressResponses.stream()
                        .filter(address -> Boolean.TRUE.equals(address.getIsPrimary()))
                        .filter(address -> AddressType.BILLING.equals(address.getAddressType()))
                        .findFirst()
                        .orElse(null));

        response.setPrimaryShippingAddress(
                addressResponses.stream()
                        .filter(address -> Boolean.TRUE.equals(address.getIsPrimary()))
                        .filter(address -> AddressType.SHIPPING.equals(address.getAddressType()))
                        .findFirst()
                        .orElse(null));

        response.setPrimaryContact(
                contactResponses.stream()
                        .filter(contact -> Boolean.TRUE.equals(contact.getIsPrimary()))
                        .findFirst()
                        .orElse(null));

        response.setCreatedAtUtc(partner.getCreatedAtUtc());
        response.setUpdatedAtUtc(partner.getUpdatedAtUtc());

        return response;
    }

    public BusinessPartnerSummaryResponse toSummaryResponse(
            BusinessPartner partner,
            CustomerProfile customerProfile,
            VendorProfile vendorProfile) {

        if (partner == null) {
            return null;
        }

        BusinessPartnerSummaryResponse response = new BusinessPartnerSummaryResponse();

        response.setBusinessPartnerId(partner.getBusinessPartnerId());
        response.setDisplayName(partner.getDisplayName());
        response.setCompanyName(partner.getCompanyName());
        response.setEmail(partner.getEmail());
        response.setPhone(partner.getPhone());

        response.setCustomerCategory(
                customerProfile != null
                        ? customerProfile.getCustomerCategory()
                        : null);

        response.setVendorCategory(
                vendorProfile != null
                        ? vendorProfile.getVendorCategory()
                        : null);

        return response;
    }

    public CustomerProfileResponse toCustomerProfileResponse(CustomerProfile profile) {
        if (profile == null) {
            return null;
        }

        CustomerProfileResponse response = new CustomerProfileResponse();

        response.setCustomerProfileId(profile.getCustomerProfileId());
        response.setBusinessPartnerId(getBusinessPartnerId(profile.getBusinessPartner()));

        response.setCustomerCategory(profile.getCustomerCategory());
        response.setDefaultTaxable(profile.getDefaultTaxable());
        response.setResaleNumber(profile.getResaleNumber());
        response.setInternalNote(profile.getInternalNote());

        response.setCreatedAtUtc(profile.getCreatedAtUtc());
        response.setUpdatedAtUtc(profile.getUpdatedAtUtc());

        return response;
    }

    public VendorProfileResponse toVendorProfileResponse(VendorProfile profile) {
        if (profile == null) {
            return null;
        }

        VendorProfileResponse response = new VendorProfileResponse();

        response.setVendorProfileId(profile.getVendorProfileId());
        response.setBusinessPartnerId(getBusinessPartnerId(profile.getBusinessPartner()));

        response.setVendorCategory(profile.getVendorCategory());
        response.setVendor1099(profile.getVendor1099());
        response.setTaxIdentifierLast4(profile.getTaxIdentifierLast4());
        response.setAccountNumber(profile.getAccountNumber());
        response.setDefaultPaymentTerms(profile.getDefaultPaymentTerms());
        response.setInsuranceExpirationDate(profile.getInsuranceExpirationDate());
        response.setInternalNote(profile.getInternalNote());

        response.setCreatedAtUtc(profile.getCreatedAtUtc());
        response.setUpdatedAtUtc(profile.getUpdatedAtUtc());

        return response;
    }

    public BusinessPartnerAddressResponse toAddressResponse(BusinessPartnerAddress address) {
        if (address == null) {
            return null;
        }

        BusinessPartnerAddressResponse response = new BusinessPartnerAddressResponse();

        response.setBusinessPartnerAddressId(address.getBusinessPartnerAddressId());
        response.setBusinessPartnerId(getBusinessPartnerId(address.getBusinessPartner()));

        response.setAddressType(address.getAddressType());

        response.setLine1(address.getLine1());
        response.setLine2(address.getLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());
        response.setCountry(address.getCountry());

        response.setIsPrimary(address.getIsPrimary());

        response.setCreatedAtUtc(address.getCreatedAtUtc());
        response.setUpdatedAtUtc(address.getUpdatedAtUtc());

        return response;
    }

    public BusinessPartnerContactResponse toContactResponse(BusinessPartnerContact contact) {
        if (contact == null) {
            return null;
        }

        BusinessPartnerContactResponse response = new BusinessPartnerContactResponse();

        response.setBusinessPartnerContactId(contact.getBusinessPartnerContactId());
        response.setBusinessPartnerId(getBusinessPartnerId(contact.getBusinessPartner()));

        response.setContactName(contact.getContactName());
        response.setTitle(contact.getTitle());
        response.setEmail(contact.getEmail());
        response.setPhone(contact.getPhone());
        response.setMobilePhone(contact.getMobilePhone());

        response.setContactRole(contact.getContactRole());

        response.setIsPrimary(contact.getIsPrimary());

        response.setCreatedAtUtc(contact.getCreatedAtUtc());
        response.setUpdatedAtUtc(contact.getUpdatedAtUtc());

        return response;
    }

    private UUID getBusinessPartnerId(BusinessPartner partner) {
        return partner != null
                ? partner.getBusinessPartnerId()
                : null;
    }
}