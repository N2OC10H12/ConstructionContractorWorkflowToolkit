CREATE TABLE estimate.bid (
    bid_id UUID PRIMARY KEY,

    customer_id UUID NOT NULL,

    bid_number VARCHAR(50) NOT NULL,
    job_number VARCHAR(50) NOT NULL,

    job_name VARCHAR(255) NULL,
    description TEXT NULL,

    department_code VARCHAR(10) NOT NULL,

    bid_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',

    current_revision_id UUID NULL,
    converted_project_id UUID NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_bid_customer
        FOREIGN KEY (customer_id)
        REFERENCES estimate.customer (customer_id),

    CONSTRAINT uq_bid_number UNIQUE (bid_number),
    CONSTRAINT uq_bid_job_number UNIQUE (job_number),

    CONSTRAINT chk_bid_number_not_blank
        CHECK (length(trim(bid_number)) > 0),

    CONSTRAINT chk_bid_job_number_not_blank
        CHECK (length(trim(job_number)) > 0),

    CONSTRAINT chk_bid_department_code
        CHECK (department_code IN ('C', 'R', 'S')),

    CONSTRAINT chk_bid_status
        CHECK (bid_status IN ('DRAFT', 'SENT', 'AWARDED', 'LOST', 'ARCHIVED'))
);

CREATE TABLE estimate.bid_revision (
    bid_revision_id UUID PRIMARY KEY,

    bid_id UUID NOT NULL,

    revision_number INTEGER NOT NULL,
    revision_display_name VARCHAR(100) NOT NULL,

    revision_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',

    tax_type VARCHAR(50) NULL,
    tax_rate_percent NUMERIC(9,4) NULL,

    subtotal_cost NUMERIC(19,4) NOT NULL DEFAULT 0,
    subtotal_price NUMERIC(19,4) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_price NUMERIC(19,4) NOT NULL DEFAULT 0,

    customer_note TEXT NULL,
    internal_note TEXT NULL,

    sent_at_utc TIMESTAMP NULL,
    awarded_at_utc TIMESTAMP NULL,
    lost_at_utc TIMESTAMP NULL,
    archived_at_utc TIMESTAMP NULL,

    cloned_from_bid_revision_id UUID NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_bid_revision_bid
        FOREIGN KEY (bid_id)
        REFERENCES estimate.bid (bid_id),

    CONSTRAINT fk_bid_revision_cloned_from
        FOREIGN KEY (cloned_from_bid_revision_id)
        REFERENCES estimate.bid_revision (bid_revision_id),

    CONSTRAINT uq_bid_revision_number
        UNIQUE (bid_id, revision_number),

    CONSTRAINT uq_bid_revision_display_name
        UNIQUE (revision_display_name),

    CONSTRAINT chk_bid_revision_number_non_negative
        CHECK (revision_number >= 0),

    CONSTRAINT chk_bid_revision_display_name_not_blank
        CHECK (length(trim(revision_display_name)) > 0),

    CONSTRAINT chk_bid_revision_status
        CHECK (revision_status IN ('DRAFT', 'SENT', 'AWARDED', 'LOST', 'ARCHIVED')),

    CONSTRAINT chk_bid_revision_tax_rate_non_negative
        CHECK (tax_rate_percent IS NULL OR tax_rate_percent >= 0),

    CONSTRAINT chk_bid_revision_totals_non_negative
        CHECK (
            subtotal_cost >= 0
            AND subtotal_price >= 0
            AND tax_amount >= 0
            AND total_price >= 0
        )
);

ALTER TABLE estimate.bid
ADD CONSTRAINT fk_bid_current_revision
FOREIGN KEY (current_revision_id)
REFERENCES estimate.bid_revision (bid_revision_id);

CREATE INDEX idx_bid_customer_id
    ON estimate.bid (customer_id);

CREATE INDEX idx_bid_status
    ON estimate.bid (bid_status);

CREATE INDEX idx_bid_department_code
    ON estimate.bid (department_code);

CREATE INDEX idx_bid_not_deleted
    ON estimate.bid (is_deleted);

CREATE INDEX idx_bid_revision_bid_id
    ON estimate.bid_revision (bid_id);

CREATE INDEX idx_bid_revision_status
    ON estimate.bid_revision (revision_status);

CREATE INDEX idx_bid_revision_not_deleted
    ON estimate.bid_revision (is_deleted);