CREATE TABLE estimate.company_work_type_division (
    company_work_type_division_id UUID PRIMARY KEY,

    division_code VARCHAR(2) NOT NULL,
    division_name VARCHAR(200) NOT NULL,

    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,

    enabled_at_utc TIMESTAMP,
    enabled_by_user_id UUID,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP,

    CONSTRAINT chk_company_work_type_division_code
        CHECK (division_code ~ '^[0-9]{2}$'),

    CONSTRAINT chk_company_work_type_division_name_not_blank
        CHECK (length(trim(division_name)) > 0)
);

CREATE UNIQUE INDEX ux_company_work_type_division_code
    ON estimate.company_work_type_division (division_code);


CREATE TABLE estimate.company_work_type (
    company_work_type_id UUID PRIMARY KEY,

    code VARCHAR(30) NOT NULL,
    normalized_code VARCHAR(30) NOT NULL,
    name VARCHAR(200) NOT NULL,

    level INTEGER NOT NULL,
    division_code VARCHAR(2) NOT NULL,

    parent_work_type_id UUID,

    source_type VARCHAR(40) NOT NULL,
    source_edition VARCHAR(20),
    original_name VARCHAR(200),

    search_aliases TEXT,
    display_order INTEGER NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP,

    deleted_at_utc TIMESTAMP,
    deleted_by_user_id UUID,

    CONSTRAINT fk_company_work_type_parent
        FOREIGN KEY (parent_work_type_id)
        REFERENCES estimate.company_work_type (company_work_type_id),

    CONSTRAINT fk_company_work_type_division
        FOREIGN KEY (division_code)
        REFERENCES estimate.company_work_type_division (division_code),

    CONSTRAINT chk_company_work_type_code_not_blank
        CHECK (length(trim(code)) > 0),

    CONSTRAINT chk_company_work_type_normalized_code
        CHECK (normalized_code ~ '^[0-9]+$'),

    CONSTRAINT chk_company_work_type_name_not_blank
        CHECK (length(trim(name)) > 0),

    CONSTRAINT chk_company_work_type_level
        CHECK (level BETWEEN 1 AND 5),

    CONSTRAINT chk_company_work_type_division_code
        CHECK (division_code ~ '^[0-9]{2}$'),

    CONSTRAINT chk_company_work_type_source_type
        CHECK (
            source_type IN (
                'MASTERFORMAT_IMPORT',
                'COMPANY_CUSTOM'
            )
        ),

    CONSTRAINT chk_company_work_type_display_order
        CHECK (display_order >= 0)
);

CREATE UNIQUE INDEX ux_company_work_type_normalized_code_active
    ON estimate.company_work_type (normalized_code)
    WHERE is_deleted = FALSE;

CREATE INDEX ix_company_work_type_division
    ON estimate.company_work_type (division_code)
    WHERE is_deleted = FALSE;

CREATE INDEX ix_company_work_type_parent
    ON estimate.company_work_type (parent_work_type_id)
    WHERE is_deleted = FALSE;

CREATE INDEX ix_company_work_type_active
    ON estimate.company_work_type (is_active, is_deleted);

CREATE INDEX ix_company_work_type_code
    ON estimate.company_work_type (code)
    WHERE is_deleted = FALSE;

CREATE INDEX ix_company_work_type_name_lower
    ON estimate.company_work_type (lower(name))
    WHERE is_deleted = FALSE;

CREATE INDEX ix_company_work_type_display_order
    ON estimate.company_work_type (display_order)
    WHERE is_deleted = FALSE;