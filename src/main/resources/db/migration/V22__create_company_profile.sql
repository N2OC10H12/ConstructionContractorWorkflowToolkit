CREATE SCHEMA IF NOT EXISTS company;

CREATE TABLE company.company_profile (
    company_profile_id UUID PRIMARY KEY,
    profile_code VARCHAR(50) NOT NULL,

    company_name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    employer_id VARCHAR(50),
    country VARCHAR(100),

    company_address_line_1 VARCHAR(255),
    company_address_line_2 VARCHAR(255),
    company_city VARCHAR(100),
    company_state VARCHAR(100),
    company_postal_code VARCHAR(50),
    company_country VARCHAR(100),

    legal_address_line_1 VARCHAR(255),
    legal_address_line_2 VARCHAR(255),
    legal_city VARCHAR(100),
    legal_state VARCHAR(100),
    legal_postal_code VARCHAR(50),
    legal_country VARCHAR(100),

    customer_communication_address_line_1 VARCHAR(255),
    customer_communication_address_line_2 VARCHAR(255),
    customer_communication_city VARCHAR(100),
    customer_communication_state VARCHAR(100),
    customer_communication_postal_code VARCHAR(50),
    customer_communication_country VARCHAR(100),

    primary_phone VARCHAR(50),
    email VARCHAR(255),
    website VARCHAR(255),

    logo_file_id UUID,
    logo_original_filename VARCHAR(255),
    logo_content_type VARCHAR(100),
    logo_size_bytes BIGINT,
    logo_storage_path TEXT,
    logo_url TEXT,

    sync_token INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    updated_by_user_id UUID
);

CREATE UNIQUE INDEX ux_company_profile_code_active
ON company.company_profile (profile_code)
WHERE is_deleted = false;

INSERT INTO company.company_profile (
    company_profile_id,
    profile_code,
    company_name,
    sync_token,
    is_active,
    is_deleted,
    created_at_utc,
    updated_at_utc
)
VALUES (
    gen_random_uuid(),
    'DEFAULT',
    'Company LLC',
    1,
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);