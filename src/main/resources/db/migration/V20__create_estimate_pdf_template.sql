CREATE TABLE estimate.estimate_pdf_template (
    estimate_pdf_template_id UUID PRIMARY KEY,

    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    html_template TEXT NOT NULL,
    css_template TEXT NULL,

    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NULL,
    deleted_at_utc TIMESTAMP NULL,
    updated_by_user_id UUID NULL,
    deleted_by_user_id UUID NULL
);

CREATE INDEX idx_estimate_pdf_template_default_active
ON estimate.estimate_pdf_template (is_default, is_active, is_deleted);