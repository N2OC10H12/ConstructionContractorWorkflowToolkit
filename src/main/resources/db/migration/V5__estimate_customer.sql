CREATE TABLE estimate.customer (
    customer_id UUID PRIMARY KEY,

    customer_type VARCHAR(50) NOT NULL,

    company_name VARCHAR(255) NULL,
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,

    display_name VARCHAR(255) NOT NULL,

    email VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,

    qbo_customer_id VARCHAR(100) NULL,

    internal_note TEXT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT chk_customer_type
        CHECK (customer_type IN ('COMPANY', 'INDIVIDUAL')),

    CONSTRAINT chk_customer_display_name_not_blank
        CHECK (length(trim(display_name)) > 0),

    CONSTRAINT chk_customer_company_required
        CHECK (
            customer_type <> 'COMPANY'
            OR company_name IS NOT NULL
            AND length(trim(company_name)) > 0
        ),

    CONSTRAINT chk_customer_individual_required
        CHECK (
            customer_type <> 'INDIVIDUAL'
            OR (
                first_name IS NOT NULL
                AND length(trim(first_name)) > 0
                AND last_name IS NOT NULL
                AND length(trim(last_name)) > 0
            )
        )
);

CREATE TABLE estimate.customer_contact (
    customer_contact_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,

    contact_name VARCHAR(255) NOT NULL,
    title VARCHAR(100) NULL,
    email VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,

    is_primary BOOLEAN NOT NULL DEFAULT false,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_customer_contact_customer
        FOREIGN KEY (customer_id)
        REFERENCES estimate.customer (customer_id),

    CONSTRAINT chk_customer_contact_name_not_blank
        CHECK (length(trim(contact_name)) > 0)
);

CREATE TABLE estimate.customer_address (
    customer_address_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,

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

    CONSTRAINT fk_customer_address_customer
        FOREIGN KEY (customer_id)
        REFERENCES estimate.customer (customer_id),

    CONSTRAINT chk_customer_address_type
        CHECK (address_type IN ('BILLING', 'JOB_SITE', 'MAILING', 'OTHER')),

    CONSTRAINT chk_customer_address_line1_not_blank
        CHECK (length(trim(line1)) > 0)
);

CREATE INDEX idx_customer_type
    ON estimate.customer (customer_type);

CREATE INDEX idx_customer_display_name
    ON estimate.customer (display_name);

CREATE INDEX idx_customer_qbo_customer_id
    ON estimate.customer (qbo_customer_id);

CREATE INDEX idx_customer_not_deleted
    ON estimate.customer (is_deleted);

CREATE INDEX idx_customer_contact_customer_id
    ON estimate.customer_contact (customer_id);

CREATE INDEX idx_customer_address_customer_id
    ON estimate.customer_address (customer_id);