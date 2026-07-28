-- Create provider-neutral metadata for immutable stored objects.
--
-- This table describes physical file content independently from the
-- Project Substep, Estimate Item Quote, Company Profile, or other domain
-- object that references it.
--
-- Storage object keys are logical provider-neutral identifiers.
-- Windows or Linux absolute filesystem paths must never be stored here.

CREATE TABLE stored_file (
    stored_file_id UUID PRIMARY KEY,

    storage_provider VARCHAR(40) NOT NULL,
    storage_container VARCHAR(255),
    storage_object_key VARCHAR(1024) NOT NULL,

    provider_object_id VARCHAR(512),
    provider_version_tag VARCHAR(512),

    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    size_bytes BIGINT NOT NULL,
    sha256 VARCHAR(64),

    uploaded_by UUID NOT NULL,
    uploaded_at_utc TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_stored_file_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES users (id),

    CONSTRAINT chk_stored_file_size_bytes
        CHECK (size_bytes >= 0),

    CONSTRAINT chk_stored_file_storage_provider_not_blank
        CHECK (btrim(storage_provider) <> ''),

    CONSTRAINT chk_stored_file_storage_object_key_not_blank
        CHECK (btrim(storage_object_key) <> ''),

    CONSTRAINT chk_stored_file_original_file_name_not_blank
        CHECK (btrim(original_file_name) <> ''),

    CONSTRAINT chk_stored_file_sha256_format
        CHECK (
            sha256 IS NULL
            OR sha256 ~ '^[0-9a-f]{64}$'
        )
);


-- PostgreSQL normally treats two NULL values as distinct in a unique index.
-- Separate indexes ensure that LOCAL objects with a NULL container still
-- cannot use the same provider/object-key combination twice.

CREATE UNIQUE INDEX uq_stored_file_provider_key_without_container
    ON stored_file (
        storage_provider,
        storage_object_key
    )
    WHERE storage_container IS NULL;

CREATE UNIQUE INDEX uq_stored_file_provider_container_key
    ON stored_file (
        storage_provider,
        storage_container,
        storage_object_key
    )
    WHERE storage_container IS NOT NULL;


CREATE INDEX idx_stored_file_uploaded_by
    ON stored_file (uploaded_by);

CREATE INDEX idx_stored_file_uploaded_at_utc
    ON stored_file (uploaded_at_utc);

CREATE INDEX idx_stored_file_sha256
    ON stored_file (sha256)
    WHERE sha256 IS NOT NULL;