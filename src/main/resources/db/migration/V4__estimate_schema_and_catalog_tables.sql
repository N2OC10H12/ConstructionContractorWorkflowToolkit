CREATE SCHEMA IF NOT EXISTS estimate;

CREATE TABLE estimate.cost_element (
    cost_element_id UUID PRIMARY KEY,

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,

    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT uq_cost_element_code UNIQUE (code),
    CONSTRAINT chk_cost_element_code_not_blank CHECK (length(trim(code)) > 0),
    CONSTRAINT chk_cost_element_name_not_blank CHECK (length(trim(name)) > 0)
);

CREATE TABLE estimate.cost_rate (
    cost_rate_id UUID PRIMARY KEY,

    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,

    rate_amount NUMERIC(19,4) NOT NULL,
    rate_unit VARCHAR(50) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT true,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT uq_cost_rate_code UNIQUE (code),
    CONSTRAINT chk_cost_rate_code_not_blank CHECK (length(trim(code)) > 0),
    CONSTRAINT chk_cost_rate_name_not_blank CHECK (length(trim(name)) > 0),
    CONSTRAINT chk_cost_rate_amount_non_negative CHECK (rate_amount >= 0),
    CONSTRAINT chk_cost_rate_unit_not_blank CHECK (length(trim(rate_unit)) > 0)
);

CREATE INDEX idx_cost_element_active_not_deleted
    ON estimate.cost_element (is_active, is_deleted);

CREATE INDEX idx_cost_rate_active_not_deleted
    ON estimate.cost_rate (is_active, is_deleted);