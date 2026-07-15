package com.glassgang.pmworkflow.businesspartner.service;

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
import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartner;
import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartnerAddress;
import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartnerContact;
import com.glassgang.pmworkflow.businesspartner.entity.CustomerProfile;
import com.glassgang.pmworkflow.businesspartner.entity.VendorProfile;
import com.glassgang.pmworkflow.businesspartner.mapper.BusinessPartnerMapper;
import com.glassgang.pmworkflow.businesspartner.repository.BusinessPartnerAddressRepository;
import com.glassgang.pmworkflow.businesspartner.repository.BusinessPartnerContactRepository;
import com.glassgang.pmworkflow.businesspartner.repository.BusinessPartnerRepository;
import com.glassgang.pmworkflow.businesspartner.repository.CustomerProfileRepository;
import com.glassgang.pmworkflow.businesspartner.repository.VendorProfileRepository;
import com.glassgang.pmworkflow.common.exception.BusinessRuleException;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BusinessPartnerService {

    private final BusinessPartnerRepository businessPartnerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final VendorProfileRepository vendorProfileRepository;
    private final BusinessPartnerAddressRepository businessPartnerAddressRepository;
    private final BusinessPartnerContactRepository businessPartnerContactRepository;
    private final BusinessPartnerMapper businessPartnerMapper;
    private final CurrentUserUtil currentUserUtil;

    public BusinessPartnerService(
            BusinessPartnerRepository businessPartnerRepository,
            CustomerProfileRepository customerProfileRepository,
            VendorProfileRepository vendorProfileRepository,
            BusinessPartnerAddressRepository businessPartnerAddressRepository,
            BusinessPartnerContactRepository businessPartnerContactRepository,
            BusinessPartnerMapper businessPartnerMapper,
            CurrentUserUtil currentUserUtil) {
        this.businessPartnerRepository = businessPartnerRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.vendorProfileRepository = vendorProfileRepository;
        this.businessPartnerAddressRepository = businessPartnerAddressRepository;
        this.businessPartnerContactRepository = businessPartnerContactRepository;
        this.businessPartnerMapper = businessPartnerMapper;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional(readOnly = true)
    public List<BusinessPartnerSummaryResponse> getBusinessPartners() {
        return businessPartnerRepository.findByIsDeletedFalseOrderByDisplayNameAsc()
                .stream()
                .map(partner -> businessPartnerMapper.toSummaryResponse(
                        partner,
                        findActiveCustomerProfileOrNull(partner.getBusinessPartnerId()),
                        findActiveVendorProfileOrNull(partner.getBusinessPartnerId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BusinessPartnerSummaryResponse> getCustomers() {
        return businessPartnerRepository.findByIsDeletedFalseOrderByDisplayNameAsc()
                .stream()
                .map(partner -> businessPartnerMapper.toSummaryResponse(
                        partner,
                        findActiveCustomerProfileOrNull(partner.getBusinessPartnerId()),
                        null))
                .filter(response -> response.getCustomerCategory() != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BusinessPartnerSummaryResponse> getVendors() {
        return businessPartnerRepository.findByIsDeletedFalseOrderByDisplayNameAsc()
                .stream()
                .map(partner -> businessPartnerMapper.toSummaryResponse(
                        partner,
                        null,
                        findActiveVendorProfileOrNull(partner.getBusinessPartnerId())))
                .filter(response -> response.getVendorCategory() != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public BusinessPartnerResponse getBusinessPartner(UUID businessPartnerId) {
        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        return businessPartnerMapper.toResponse(
                partner,
                findActiveCustomerProfileOrNull(businessPartnerId),
                findActiveVendorProfileOrNull(businessPartnerId),
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse createBusinessPartner(CreateBusinessPartnerRequest request) {
        if (businessPartnerRepository.existsByDisplayNameIgnoreCaseAndIsDeletedFalse(request.getDisplayName())) {
            throw new BusinessRuleException("Business partner display name already exists");
        }

        validateCreateBusinessPartnerChildren(request);

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BusinessPartner partner = new BusinessPartner();
        partner.setBusinessPartnerId(UUID.randomUUID());

        partner.setPartnerType(request.getPartnerType());
        partner.setDisplayName(request.getDisplayName());
        partner.setCompanyName(request.getCompanyName());
        partner.setFirstName(request.getFirstName());
        partner.setLastName(request.getLastName());
        partner.setEmail(request.getEmail());
        partner.setPhone(request.getPhone());
        partner.setWebsite(request.getWebsite());
        partner.setInternalNote(request.getInternalNote());

        partner.setCreatedAtUtc(now);
        partner.setUpdatedAtUtc(now);
        partner.setCreatedByUserId(currentUserId);
        partner.setUpdatedByUserId(currentUserId);
        partner.setIsDeleted(false);

        BusinessPartner saved = businessPartnerRepository.save(partner);

        CustomerProfile savedCustomerProfile = null;
        VendorProfile savedVendorProfile = null;

        if (request.getCustomerProfile() != null) {
            savedCustomerProfile = createCustomerProfileForNewPartner(
                    saved,
                    request.getCustomerProfile(),
                    now,
                    currentUserId);
        }

        if (request.getVendorProfile() != null) {
            savedVendorProfile = createVendorProfileForNewPartner(
                    saved,
                    request.getVendorProfile(),
                    now,
                    currentUserId);
        }

        getCreateAddresses(request).forEach(addressRequest -> {
            BusinessPartnerAddress address = new BusinessPartnerAddress();
            address.setBusinessPartnerAddressId(UUID.randomUUID());
            address.setBusinessPartner(saved);

            address.setAddressType(addressRequest.getAddressType());
            address.setLine1(addressRequest.getLine1());
            address.setLine2(addressRequest.getLine2());
            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setPostalCode(addressRequest.getPostalCode());
            address.setCountry(addressRequest.getCountry());
            address.setIsPrimary(addressRequest.getIsPrimary());

            address.setCreatedAtUtc(now);
            address.setUpdatedAtUtc(now);
            address.setCreatedByUserId(currentUserId);
            address.setUpdatedByUserId(currentUserId);
            address.setIsDeleted(false);

            businessPartnerAddressRepository.save(address);
        });

        getCreateContacts(request).forEach(contactRequest -> {
            BusinessPartnerContact contact = new BusinessPartnerContact();
            contact.setBusinessPartnerContactId(UUID.randomUUID());
            contact.setBusinessPartner(saved);

            contact.setContactName(contactRequest.getContactName());
            contact.setTitle(contactRequest.getTitle());
            contact.setEmail(contactRequest.getEmail());
            contact.setPhone(contactRequest.getPhone());
            contact.setMobilePhone(contactRequest.getMobilePhone());
            contact.setContactRole(contactRequest.getContactRole());
            contact.setIsPrimary(contactRequest.getIsPrimary());

            contact.setCreatedAtUtc(now);
            contact.setUpdatedAtUtc(now);
            contact.setCreatedByUserId(currentUserId);
            contact.setUpdatedByUserId(currentUserId);
            contact.setIsDeleted(false);

            businessPartnerContactRepository.save(contact);
        });

        return businessPartnerMapper.toResponse(
                saved,
                savedCustomerProfile,
                savedVendorProfile,
                findActiveAddresses(saved.getBusinessPartnerId()),
                findActiveContacts(saved.getBusinessPartnerId()));
    }

    @Transactional
    public BusinessPartnerResponse updateBusinessPartner(
            UUID businessPartnerId,
            UpdateBusinessPartnerRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        boolean duplicateDisplayName = businessPartnerRepository.findByIsDeletedFalseOrderByDisplayNameAsc()
                .stream()
                .anyMatch(existing -> !existing.getBusinessPartnerId().equals(businessPartnerId)
                        && existing.getDisplayName() != null
                        && existing.getDisplayName().equalsIgnoreCase(request.getDisplayName()));

        if (duplicateDisplayName) {
            throw new BusinessRuleException("Business partner display name already exists");
        }

        partner.setPartnerType(request.getPartnerType());
        partner.setDisplayName(request.getDisplayName());
        partner.setCompanyName(request.getCompanyName());
        partner.setFirstName(request.getFirstName());
        partner.setLastName(request.getLastName());
        partner.setEmail(request.getEmail());
        partner.setPhone(request.getPhone());
        partner.setWebsite(request.getWebsite());
        partner.setInternalNote(request.getInternalNote());

        touch(partner);

        BusinessPartner saved = businessPartnerRepository.save(partner);

        return businessPartnerMapper.toResponse(
                saved,
                findActiveCustomerProfileOrNull(businessPartnerId),
                findActiveVendorProfileOrNull(businessPartnerId),
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public void deleteBusinessPartner(UUID businessPartnerId) {
        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        partner.setIsDeleted(true);
        partner.setDeletedAtUtc(now);
        partner.setDeletedByUserId(currentUserId);
        partner.setUpdatedAtUtc(now);
        partner.setUpdatedByUserId(currentUserId);

        // findActiveCustomerProfileOrNull(businessPartnerId);
        CustomerProfile customerProfile = findActiveCustomerProfileOrNull(businessPartnerId);
        if (customerProfile != null) {
            customerProfile.setIsDeleted(true);
            customerProfile.setDeletedAtUtc(now);
            customerProfile.setDeletedByUserId(currentUserId);
            customerProfile.setUpdatedAtUtc(now);
            customerProfile.setUpdatedByUserId(currentUserId);
            customerProfileRepository.save(customerProfile);
        }

        VendorProfile vendorProfile = findActiveVendorProfileOrNull(businessPartnerId);
        if (vendorProfile != null) {
            vendorProfile.setIsDeleted(true);
            vendorProfile.setDeletedAtUtc(now);
            vendorProfile.setDeletedByUserId(currentUserId);
            vendorProfile.setUpdatedAtUtc(now);
            vendorProfile.setUpdatedByUserId(currentUserId);
            vendorProfileRepository.save(vendorProfile);
        }

        findActiveAddresses(businessPartnerId).forEach(address -> {
            address.setIsDeleted(true);
            address.setDeletedAtUtc(now);
            address.setDeletedByUserId(currentUserId);
            address.setUpdatedAtUtc(now);
            address.setUpdatedByUserId(currentUserId);
            businessPartnerAddressRepository.save(address);
        });

        findActiveContacts(businessPartnerId).forEach(contact -> {
            contact.setIsDeleted(true);
            contact.setDeletedAtUtc(now);
            contact.setDeletedByUserId(currentUserId);
            contact.setUpdatedAtUtc(now);
            contact.setUpdatedByUserId(currentUserId);
            businessPartnerContactRepository.save(contact);
        });

        businessPartnerRepository.save(partner);
    }

    @Transactional
    public BusinessPartnerResponse createCustomerProfile(
            UUID businessPartnerId,
            CreateCustomerProfileRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        if (customerProfileRepository.existsByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)) {
            throw new BusinessRuleException("Customer profile already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        CustomerProfile profile = new CustomerProfile();
        profile.setCustomerProfileId(UUID.randomUUID());
        profile.setBusinessPartner(partner);

        profile.setCustomerCategory(request.getCustomerCategory());
        profile.setDefaultTaxable(request.getDefaultTaxable());
        profile.setResaleNumber(request.getResaleNumber());
        profile.setInternalNote(request.getInternalNote());

        profile.setCreatedAtUtc(now);
        profile.setUpdatedAtUtc(now);
        profile.setCreatedByUserId(currentUserId);
        profile.setUpdatedByUserId(currentUserId);
        profile.setIsDeleted(false);

        CustomerProfile savedProfile = customerProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                savedProfile,
                findActiveVendorProfileOrNull(businessPartnerId),
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse updateCustomerProfile(
            UUID businessPartnerId,
            UpdateCustomerProfileRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);
        CustomerProfile profile = getActiveCustomerProfile(businessPartnerId);

        profile.setCustomerCategory(request.getCustomerCategory());
        profile.setDefaultTaxable(request.getDefaultTaxable());
        profile.setResaleNumber(request.getResaleNumber());
        profile.setInternalNote(request.getInternalNote());

        touch(profile);

        CustomerProfile savedProfile = customerProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                savedProfile,
                findActiveVendorProfileOrNull(businessPartnerId),
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse deleteCustomerProfile(UUID businessPartnerId) {
        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);
        CustomerProfile profile = getActiveCustomerProfile(businessPartnerId);

        softDelete(profile);
        customerProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                null,
                findActiveVendorProfileOrNull(businessPartnerId),
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse createVendorProfile(
            UUID businessPartnerId,
            CreateVendorProfileRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        if (vendorProfileRepository.existsByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)) {
            throw new BusinessRuleException("Vendor profile already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        VendorProfile profile = new VendorProfile();
        profile.setVendorProfileId(UUID.randomUUID());
        profile.setBusinessPartner(partner);

        profile.setVendorCategory(request.getVendorCategory());
        profile.setVendor1099(request.getVendor1099());
        profile.setTaxIdentifierLast4(request.getTaxIdentifierLast4());
        profile.setAccountNumber(request.getAccountNumber());
        profile.setDefaultPaymentTerms(request.getDefaultPaymentTerms());
        profile.setInsuranceExpirationDate(request.getInsuranceExpirationDate());
        profile.setInternalNote(request.getInternalNote());

        profile.setCreatedAtUtc(now);
        profile.setUpdatedAtUtc(now);
        profile.setCreatedByUserId(currentUserId);
        profile.setUpdatedByUserId(currentUserId);
        profile.setIsDeleted(false);

        VendorProfile savedProfile = vendorProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                findActiveCustomerProfileOrNull(businessPartnerId),
                savedProfile,
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse updateVendorProfile(
            UUID businessPartnerId,
            UpdateVendorProfileRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);
        VendorProfile profile = getActiveVendorProfile(businessPartnerId);

        profile.setVendorCategory(request.getVendorCategory());
        profile.setVendor1099(request.getVendor1099());
        profile.setTaxIdentifierLast4(request.getTaxIdentifierLast4());
        profile.setAccountNumber(request.getAccountNumber());
        profile.setDefaultPaymentTerms(request.getDefaultPaymentTerms());
        profile.setInsuranceExpirationDate(request.getInsuranceExpirationDate());
        profile.setInternalNote(request.getInternalNote());

        touch(profile);

        VendorProfile savedProfile = vendorProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                findActiveCustomerProfileOrNull(businessPartnerId),
                savedProfile,
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse deleteVendorProfile(UUID businessPartnerId) {
        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);
        VendorProfile profile = getActiveVendorProfile(businessPartnerId);

        softDelete(profile);
        vendorProfileRepository.save(profile);

        return businessPartnerMapper.toResponse(
                partner,
                findActiveCustomerProfileOrNull(businessPartnerId),
                null,
                findActiveAddresses(businessPartnerId),
                findActiveContacts(businessPartnerId));
    }

    @Transactional
    public BusinessPartnerResponse createAddress(
            UUID businessPartnerId,
            CreateBusinessPartnerAddressRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BusinessPartnerAddress address = new BusinessPartnerAddress();
        address.setBusinessPartnerAddressId(UUID.randomUUID());
        address.setBusinessPartner(partner);

        address.setAddressType(request.getAddressType());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setIsPrimary(request.getIsPrimary());

        address.setCreatedAtUtc(now);
        address.setUpdatedAtUtc(now);
        address.setCreatedByUserId(currentUserId);
        address.setUpdatedByUserId(currentUserId);
        address.setIsDeleted(false);

        businessPartnerAddressRepository.save(address);

        return getBusinessPartner(businessPartnerId);
    }

    @Transactional
    public BusinessPartnerResponse updateAddress(
            UUID addressId,
            UpdateBusinessPartnerAddressRequest request) {

        BusinessPartnerAddress address = getActiveAddress(addressId);
        UUID businessPartnerId = address.getBusinessPartner().getBusinessPartnerId();

        address.setAddressType(request.getAddressType());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setIsPrimary(request.getIsPrimary());

        touch(address);

        businessPartnerAddressRepository.save(address);

        return getBusinessPartner(businessPartnerId);
    }

    @Transactional
    public BusinessPartnerResponse deleteAddress(UUID addressId) {
        BusinessPartnerAddress address = getActiveAddress(addressId);
        UUID businessPartnerId = address.getBusinessPartner().getBusinessPartnerId();

        softDelete(address);
        businessPartnerAddressRepository.save(address);

        return getBusinessPartner(businessPartnerId);
    }

    @Transactional
    public BusinessPartnerResponse createContact(
            UUID businessPartnerId,
            CreateBusinessPartnerContactRequest request) {

        BusinessPartner partner = getActiveBusinessPartner(businessPartnerId);

        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        BusinessPartnerContact contact = new BusinessPartnerContact();
        contact.setBusinessPartnerContactId(UUID.randomUUID());
        contact.setBusinessPartner(partner);

        contact.setContactName(request.getContactName());
        contact.setTitle(request.getTitle());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setMobilePhone(request.getMobilePhone());
        contact.setContactRole(request.getContactRole());
        contact.setIsPrimary(request.getIsPrimary());

        contact.setCreatedAtUtc(now);
        contact.setUpdatedAtUtc(now);
        contact.setCreatedByUserId(currentUserId);
        contact.setUpdatedByUserId(currentUserId);
        contact.setIsDeleted(false);

        businessPartnerContactRepository.save(contact);

        return getBusinessPartner(businessPartnerId);
    }

    @Transactional
    public BusinessPartnerResponse updateContact(
            UUID contactId,
            UpdateBusinessPartnerContactRequest request) {

        BusinessPartnerContact contact = getActiveContact(contactId);
        UUID businessPartnerId = contact.getBusinessPartner().getBusinessPartnerId();

        contact.setContactName(request.getContactName());
        contact.setTitle(request.getTitle());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setMobilePhone(request.getMobilePhone());
        contact.setContactRole(request.getContactRole());
        contact.setIsPrimary(request.getIsPrimary());

        touch(contact);

        businessPartnerContactRepository.save(contact);

        return getBusinessPartner(businessPartnerId);
    }

    @Transactional
    public BusinessPartnerResponse deleteContact(UUID contactId) {
        BusinessPartnerContact contact = getActiveContact(contactId);
        UUID businessPartnerId = contact.getBusinessPartner().getBusinessPartnerId();

        softDelete(contact);
        businessPartnerContactRepository.save(contact);

        return getBusinessPartner(businessPartnerId);
    }

    private BusinessPartner getActiveBusinessPartner(UUID businessPartnerId) {
        return businessPartnerRepository
                .findByBusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElseThrow(() -> new NotFoundException("Business partner not found"));
    }

    private CustomerProfile getActiveCustomerProfile(UUID businessPartnerId) {
        return customerProfileRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElseThrow(() -> new NotFoundException("Customer profile not found"));
    }

    private CustomerProfile findActiveCustomerProfileOrNull(UUID businessPartnerId) {
        return customerProfileRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElse(null);
    }

    private VendorProfile getActiveVendorProfile(UUID businessPartnerId) {
        return vendorProfileRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElseThrow(() -> new NotFoundException("Vendor profile not found"));
    }

    private VendorProfile findActiveVendorProfileOrNull(UUID businessPartnerId) {
        return vendorProfileRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(businessPartnerId)
                .orElse(null);
    }

    private BusinessPartnerAddress getActiveAddress(UUID addressId) {
        return businessPartnerAddressRepository.findById(addressId)
                .filter(address -> Boolean.FALSE.equals(address.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Business partner address not found"));
    }

    private BusinessPartnerContact getActiveContact(UUID contactId) {
        return businessPartnerContactRepository.findById(contactId)
                .filter(contact -> Boolean.FALSE.equals(contact.getIsDeleted()))
                .orElseThrow(() -> new NotFoundException("Business partner contact not found"));
    }

    private List<BusinessPartnerAddress> findActiveAddresses(UUID businessPartnerId) {
        return businessPartnerAddressRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByCreatedAtUtcAsc(businessPartnerId);
    }

    private List<BusinessPartnerContact> findActiveContacts(UUID businessPartnerId) {
        return businessPartnerContactRepository
                .findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByCreatedAtUtcAsc(businessPartnerId);
    }

    private void touch(BusinessPartner partner) {
        partner.setUpdatedAtUtc(LocalDateTime.now());
        partner.setUpdatedByUserId(currentUserUtil.getCurrentUserId());
    }

    private void touch(CustomerProfile profile) {
        profile.setUpdatedAtUtc(LocalDateTime.now());
        profile.setUpdatedByUserId(currentUserUtil.getCurrentUserId());
    }

    private void touch(VendorProfile profile) {
        profile.setUpdatedAtUtc(LocalDateTime.now());
        profile.setUpdatedByUserId(currentUserUtil.getCurrentUserId());
    }

    private void touch(BusinessPartnerAddress address) {
        address.setUpdatedAtUtc(LocalDateTime.now());
        address.setUpdatedByUserId(currentUserUtil.getCurrentUserId());
    }

    private void touch(BusinessPartnerContact contact) {
        contact.setUpdatedAtUtc(LocalDateTime.now());
        contact.setUpdatedByUserId(currentUserUtil.getCurrentUserId());
    }

    private void softDelete(CustomerProfile profile) {
        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        profile.setIsDeleted(true);
        profile.setDeletedAtUtc(now);
        profile.setDeletedByUserId(currentUserId);
        profile.setUpdatedAtUtc(now);
        profile.setUpdatedByUserId(currentUserId);
    }

    private void softDelete(VendorProfile profile) {
        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        profile.setIsDeleted(true);
        profile.setDeletedAtUtc(now);
        profile.setDeletedByUserId(currentUserId);
        profile.setUpdatedAtUtc(now);
        profile.setUpdatedByUserId(currentUserId);
    }

    private void softDelete(BusinessPartnerAddress address) {
        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        address.setIsDeleted(true);
        address.setDeletedAtUtc(now);
        address.setDeletedByUserId(currentUserId);
        address.setUpdatedAtUtc(now);
        address.setUpdatedByUserId(currentUserId);
    }

    private void softDelete(BusinessPartnerContact contact) {
        LocalDateTime now = LocalDateTime.now();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        contact.setIsDeleted(true);
        contact.setDeletedAtUtc(now);
        contact.setDeletedByUserId(currentUserId);
        contact.setUpdatedAtUtc(now);
        contact.setUpdatedByUserId(currentUserId);
    }

    private List<CreateBusinessPartnerAddressRequest> getCreateAddresses(CreateBusinessPartnerRequest request) {
        return request.getAddresses() == null ? List.of() : request.getAddresses();
    }

    private List<CreateBusinessPartnerContactRequest> getCreateContacts(CreateBusinessPartnerRequest request) {
        return request.getContacts() == null ? List.of() : request.getContacts();
    }

    private void validateCreateBusinessPartnerChildren(CreateBusinessPartnerRequest request) {
        long primaryContactCount = getCreateContacts(request).stream()
                .filter(contact -> Boolean.TRUE.equals(contact.getIsPrimary()))
                .count();

        if (primaryContactCount > 1) {
            throw new BusinessRuleException("Only one primary contact is allowed");
        }

        getCreateAddresses(request).stream()
                .filter(address -> Boolean.TRUE.equals(address.getIsPrimary()))
                .map(CreateBusinessPartnerAddressRequest::getAddressType)
                .distinct()
                .forEach(addressType -> {
                    long primaryAddressCount = getCreateAddresses(request).stream()
                            .filter(address -> Boolean.TRUE.equals(address.getIsPrimary()))
                            .filter(address -> address.getAddressType() == addressType)
                            .count();

                    if (primaryAddressCount > 1) {
                        throw new BusinessRuleException(
                                "Only one primary " + addressType + " address is allowed");
                    }
                });
    }

    private CustomerProfile createCustomerProfileForNewPartner(
            BusinessPartner partner,
            CreateCustomerProfileRequest request,
            LocalDateTime now,
            UUID currentUserId) {

        CustomerProfile profile = new CustomerProfile();
        profile.setCustomerProfileId(UUID.randomUUID());
        profile.setBusinessPartner(partner);

        profile.setCustomerCategory(request.getCustomerCategory());
        profile.setDefaultTaxable(request.getDefaultTaxable());
        profile.setResaleNumber(request.getResaleNumber());
        profile.setInternalNote(request.getInternalNote());

        profile.setCreatedAtUtc(now);
        profile.setUpdatedAtUtc(now);
        profile.setCreatedByUserId(currentUserId);
        profile.setUpdatedByUserId(currentUserId);
        profile.setIsDeleted(false);

        return customerProfileRepository.save(profile);
    }

    private VendorProfile createVendorProfileForNewPartner(
            BusinessPartner partner,
            CreateVendorProfileRequest request,
            LocalDateTime now,
            UUID currentUserId) {

        VendorProfile profile = new VendorProfile();
        profile.setVendorProfileId(UUID.randomUUID());
        profile.setBusinessPartner(partner);

        profile.setVendorCategory(request.getVendorCategory());
        profile.setVendor1099(request.getVendor1099());
        profile.setTaxIdentifierLast4(request.getTaxIdentifierLast4());
        profile.setAccountNumber(request.getAccountNumber());
        profile.setDefaultPaymentTerms(request.getDefaultPaymentTerms());
        profile.setInsuranceExpirationDate(request.getInsuranceExpirationDate());
        profile.setInternalNote(request.getInternalNote());

        profile.setCreatedAtUtc(now);
        profile.setUpdatedAtUtc(now);
        profile.setCreatedByUserId(currentUserId);
        profile.setUpdatedByUserId(currentUserId);
        profile.setIsDeleted(false);

        return vendorProfileRepository.save(profile);
    }
}