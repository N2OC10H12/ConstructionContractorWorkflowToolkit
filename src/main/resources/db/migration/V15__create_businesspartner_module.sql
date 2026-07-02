CREATE SCHEMA IF NOT EXISTS businesspartner;

CREATE TABLE businesspartner.business_partner (
    business_partner_id UUID PRIMARY KEY,

    partner_type VARCHAR(50) NOT NULL,

    display_name VARCHAR(255) NOT NULL,

    company_name VARCHAR(255) NULL,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,

    email VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,
    website VARCHAR(255) NULL,

    internal_note TEXT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT chk_business_partner_type
        CHECK (partner_type IN ('COMPANY', 'INDIVIDUAL')),

    CONSTRAINT chk_business_partner_display_name_not_blank
        CHECK (length(trim(display_name)) > 0),

    CONSTRAINT chk_business_partner_company_required
        CHECK (
            partner_type <> 'COMPANY'
            OR (
                company_name IS NOT NULL
                AND length(trim(company_name)) > 0
            )
        ),

    CONSTRAINT chk_business_partner_individual_required
        CHECK (
            partner_type <> 'INDIVIDUAL'
            OR (
                first_name IS NOT NULL
                AND length(trim(first_name)) > 0
                AND last_name IS NOT NULL
                AND length(trim(last_name)) > 0
            )
        )
);

CREATE TABLE businesspartner.customer_profile (
    customer_profile_id UUID PRIMARY KEY,
    business_partner_id UUID NOT NULL,

    customer_category VARCHAR(50) NOT NULL,
    default_taxable BOOLEAN NOT NULL DEFAULT true,
    resale_number VARCHAR(100) NULL,

    internal_note TEXT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_customer_profile_business_partner
        FOREIGN KEY (business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id),

    CONSTRAINT chk_customer_profile_category
        CHECK (customer_category IN ('OWNER', 'GENERAL_CONTRACTOR', 'OTHER'))
);

CREATE TABLE businesspartner.vendor_profile (
    vendor_profile_id UUID PRIMARY KEY,
    business_partner_id UUID NOT NULL,

    vendor_category VARCHAR(50) NOT NULL,
    vendor1099 BOOLEAN NOT NULL DEFAULT false,
    tax_identifier_last4 VARCHAR(4) NULL,
    account_number VARCHAR(100) NULL,
    default_payment_terms VARCHAR(100) NULL,
    insurance_expiration_date DATE NULL,

    internal_note TEXT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_vendor_profile_business_partner
        FOREIGN KEY (business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id),

    CONSTRAINT chk_vendor_profile_category
        CHECK (
            vendor_category IN (
                'MATERIAL_VENDOR',
                'EQUIPMENT_VENDOR',
                'SUBCONTRACTOR',
                'SERVICE_VENDOR',
                'OTHER'
            )
        ),

    CONSTRAINT chk_vendor_tax_identifier_last4
        CHECK (
            tax_identifier_last4 IS NULL
            OR tax_identifier_last4 ~ '^[0-9]{4}$'
        )
);

CREATE TABLE businesspartner.business_partner_address (
    business_partner_address_id UUID PRIMARY KEY,
    business_partner_id UUID NOT NULL,

    address_type VARCHAR(50) NOT NULL,

    line1 VARCHAR(255) NOT NULL,
    line2 VARCHAR(255) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    postal_code VARCHAR(50) NULL,
    country VARCHAR(100) NULL,

    is_primary BOOLEAN NOT NULL DEFAULT false,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_business_partner_address_business_partner
        FOREIGN KEY (business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id),

    CONSTRAINT chk_business_partner_address_type
        CHECK (
            address_type IN (
                'BILLING',
                'SHIPPING',
                'JOB_SITE',
                'MAILING',
                'LEGAL',
                'OTHER'
            )
        ),

    CONSTRAINT chk_business_partner_address_line1_not_blank
        CHECK (length(trim(line1)) > 0)
);

CREATE TABLE businesspartner.business_partner_contact (
    business_partner_contact_id UUID PRIMARY KEY,
    business_partner_id UUID NOT NULL,

    contact_name VARCHAR(255) NOT NULL,
    title VARCHAR(100) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,
    mobile_phone VARCHAR(50) NULL,
    contact_role VARCHAR(50) NOT NULL DEFAULT 'MAIN',

    is_primary BOOLEAN NOT NULL DEFAULT false,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_business_partner_contact_business_partner
        FOREIGN KEY (business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id),

    CONSTRAINT chk_business_partner_contact_name_not_blank
        CHECK (length(trim(contact_name)) > 0),

    CONSTRAINT chk_business_partner_contact_role
        CHECK (
            contact_role IN (
                'MAIN',
                'ACCOUNTING',
                'ESTIMATOR',
                'PROJECT_MANAGER',
                'SITE_CONTACT',
                'AP',
                'AR',
                'OTHER'
            )
        )
);

CREATE TABLE businesspartner.business_partner_external_ref (
    business_partner_external_ref_id UUID PRIMARY KEY,
    business_partner_id UUID NOT NULL,

    external_system VARCHAR(50) NOT NULL,
    external_entity_type VARCHAR(50) NOT NULL,

    realm_id VARCHAR(100) NULL,
    external_id VARCHAR(100) NOT NULL,
    sync_token VARCHAR(100) NULL,

    display_name_snapshot VARCHAR(255) NULL,
    fully_qualified_name_snapshot VARCHAR(500) NULL,
    active_snapshot BOOLEAN NULL,

    last_synced_at_utc TIMESTAMP NULL,
    last_error TEXT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_business_partner_external_ref_business_partner
        FOREIGN KEY (business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id),

    CONSTRAINT chk_business_partner_external_system
        CHECK (external_system IN ('QBO')),

    CONSTRAINT chk_business_partner_external_entity_type
        CHECK (external_entity_type IN ('CUSTOMER', 'VENDOR')),

    CONSTRAINT chk_business_partner_external_id_not_blank
        CHECK (length(trim(external_id)) > 0)
);

CREATE INDEX idx_business_partner_display_name
    ON businesspartner.business_partner (display_name);

CREATE INDEX idx_business_partner_not_deleted
    ON businesspartner.business_partner (is_deleted);

CREATE INDEX idx_customer_profile_business_partner_id
    ON businesspartner.customer_profile (business_partner_id);

CREATE UNIQUE INDEX uq_customer_profile_business_partner_active
    ON businesspartner.customer_profile (business_partner_id)
    WHERE is_deleted = false;

CREATE INDEX idx_vendor_profile_business_partner_id
    ON businesspartner.vendor_profile (business_partner_id);

CREATE UNIQUE INDEX uq_vendor_profile_business_partner_active
    ON businesspartner.vendor_profile (business_partner_id)
    WHERE is_deleted = false;

CREATE INDEX idx_business_partner_address_business_partner_id
    ON businesspartner.business_partner_address (business_partner_id);

CREATE INDEX idx_business_partner_address_primary
    ON businesspartner.business_partner_address (
        business_partner_id,
        address_type,
        is_primary,
        is_deleted
    );

CREATE INDEX idx_business_partner_contact_business_partner_id
    ON businesspartner.business_partner_contact (business_partner_id);

CREATE INDEX idx_business_partner_contact_primary
    ON businesspartner.business_partner_contact (
        business_partner_id,
        is_primary,
        is_deleted
    );

CREATE INDEX idx_business_partner_external_ref_business_partner_id
    ON businesspartner.business_partner_external_ref (business_partner_id);

CREATE INDEX idx_business_partner_external_ref_external
    ON businesspartner.business_partner_external_ref (
        external_system,
        external_entity_type,
        (COALESCE(realm_id, '')),
        external_id,
        is_deleted
    );

CREATE UNIQUE INDEX uq_business_partner_external_ref_partner_active
    ON businesspartner.business_partner_external_ref (
        business_partner_id,
        external_system,
        external_entity_type,
        (COALESCE(realm_id, ''))
    )
    WHERE is_deleted = false;

INSERT INTO businesspartner.business_partner (
    business_partner_id,
    partner_type,
    display_name,
    company_name,
    first_name,
    last_name,
    email,
    phone,
    website,
    internal_note,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
)
SELECT
    customer_id,
    customer_type,
    display_name,
    company_name,
    first_name,
    last_name,
    email,
    phone,
    NULL,
    internal_note,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
FROM estimate.customer;

INSERT INTO businesspartner.customer_profile (
    customer_profile_id,
    business_partner_id,
    customer_category,
    default_taxable,
    resale_number,
    internal_note,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
)
SELECT
    customer_id,
    customer_id,
    'OTHER',
    true,
    NULL,
    NULL,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
FROM estimate.customer;

INSERT INTO businesspartner.business_partner_address (
    business_partner_address_id,
    business_partner_id,
    address_type,
    line1,
    line2,
    city,
    state,
    postal_code,
    country,
    is_primary,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
)
SELECT
    customer_address_id,
    customer_id,
    address_type,
    line1,
    line2,
    city,
    state,
    postal_code,
    country,
    is_primary,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
FROM estimate.customer_address;

INSERT INTO businesspartner.business_partner_contact (
    business_partner_contact_id,
    business_partner_id,
    contact_name,
    title,
    email,
    phone,
    mobile_phone,
    contact_role,
    is_primary,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
)
SELECT
    customer_contact_id,
    customer_id,
    contact_name,
    title,
    email,
    phone,
    NULL,
    'MAIN',
    is_primary,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
FROM estimate.customer_contact;

WITH qbo_refs AS (
    SELECT
        c.*,
        md5(c.customer_id::text || ':QBO:CUSTOMER:' || c.qbo_customer_id) AS ref_hash
    FROM estimate.customer c
    WHERE c.qbo_customer_id IS NOT NULL
      AND length(trim(c.qbo_customer_id)) > 0
)
INSERT INTO businesspartner.business_partner_external_ref (
    business_partner_external_ref_id,
    business_partner_id,
    external_system,
    external_entity_type,
    realm_id,
    external_id,
    sync_token,
    display_name_snapshot,
    fully_qualified_name_snapshot,
    active_snapshot,
    last_synced_at_utc,
    last_error,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
)
SELECT
    (
        substr(ref_hash, 1, 8) || '-' ||
        substr(ref_hash, 9, 4) || '-' ||
        substr(ref_hash, 13, 4) || '-' ||
        substr(ref_hash, 17, 4) || '-' ||
        substr(ref_hash, 21, 12)
    )::uuid,
    customer_id,
    'QBO',
    'CUSTOMER',
    NULL,
    qbo_customer_id,
    NULL,
    display_name,
    display_name,
    CASE
        WHEN is_deleted = true THEN false
        ELSE true
    END,
    NULL,
    NULL,
    created_at_utc,
    updated_at_utc,
    created_by_user_id,
    updated_by_user_id,
    is_deleted,
    deleted_at_utc,
    deleted_by_user_id
FROM qbo_refs;

ALTER TABLE estimate.bid
    ADD COLUMN customer_business_partner_id UUID;

UPDATE estimate.bid
SET customer_business_partner_id = customer_id;

ALTER TABLE estimate.bid
    ADD CONSTRAINT fk_bid_customer_business_partner
        FOREIGN KEY (customer_business_partner_id)
        REFERENCES businesspartner.business_partner (business_partner_id);

CREATE INDEX idx_bid_customer_business_partner_id
    ON estimate.bid (customer_business_partner_id);

ALTER TABLE estimate.bid
    ALTER COLUMN customer_id DROP NOT NULL;