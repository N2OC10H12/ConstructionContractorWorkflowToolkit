-- ============================================================
-- Migrate Project Substep attachments to reusable file storage
--
-- Development-stage decision:
-- Existing legacy substep-file records are intentionally reset.
-- Their absolute filesystem paths are not migrated.
--
-- The attachment ID remains substep_file.id so existing URLs such
-- as /api/files/{attachmentId}/preview remain compatible.
-- ============================================================

-- Current development rows reference the obsolete local root:
-- C:/apps/pmworkflow/uploads
--
-- Do not copy those environment-specific paths into stored_file.
DELETE FROM substep_file;

-- The attachment timestamp is retained conceptually but renamed
-- to make its UTC meaning explicit.
ALTER TABLE substep_file
    RENAME COLUMN uploaded_at TO created_at_utc;

-- Technical file metadata now belongs to stored_file.
ALTER TABLE substep_file
    DROP COLUMN file_name,
    DROP COLUMN file_url,
    DROP COLUMN uploaded_by;

-- Link the typed Project Substep attachment to immutable,
-- provider-neutral stored content.
ALTER TABLE substep_file
    ADD COLUMN stored_file_id UUID NOT NULL;

ALTER TABLE substep_file
    ADD CONSTRAINT fk_substep_file_stored_file
        FOREIGN KEY (stored_file_id)
        REFERENCES stored_file(stored_file_id)
        ON DELETE RESTRICT;

CREATE INDEX idx_substep_file_stored_file
    ON substep_file(stored_file_id);