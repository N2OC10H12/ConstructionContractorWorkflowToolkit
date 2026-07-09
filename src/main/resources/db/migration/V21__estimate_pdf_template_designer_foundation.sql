ALTER TABLE estimate.estimate_pdf_template
ADD COLUMN IF NOT EXISTS template_definition_json JSONB NULL;

ALTER TABLE estimate.estimate_pdf_template
ADD COLUMN IF NOT EXISTS version_number INTEGER NOT NULL DEFAULT 1;

CREATE TABLE IF NOT EXISTS estimate.estimate_pdf_template_version (
    estimate_pdf_template_version_id UUID PRIMARY KEY,
    estimate_pdf_template_id UUID NOT NULL,

    version_number INTEGER NOT NULL,

    name VARCHAR(255) NOT NULL,
    html_template TEXT NOT NULL,
    css_template TEXT NULL,
    template_definition_json JSONB NULL,

    is_active BOOLEAN NOT NULL,

    created_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    change_note TEXT NULL,

    CONSTRAINT fk_estimate_pdf_template_version_template
        FOREIGN KEY (estimate_pdf_template_id)
        REFERENCES estimate.estimate_pdf_template(estimate_pdf_template_id)
);

CREATE INDEX IF NOT EXISTS idx_estimate_pdf_template_version_template_id
ON estimate.estimate_pdf_template_version (estimate_pdf_template_id);

CREATE INDEX IF NOT EXISTS idx_estimate_pdf_template_version_template_version
ON estimate.estimate_pdf_template_version (estimate_pdf_template_id, version_number);