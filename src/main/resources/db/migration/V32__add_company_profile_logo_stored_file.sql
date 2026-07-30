ALTER TABLE company.company_profile
    ADD COLUMN logo_stored_file_id UUID;

ALTER TABLE company.company_profile
    ADD CONSTRAINT fk_company_profile_logo_stored_file
        FOREIGN KEY (logo_stored_file_id)
        REFERENCES public.stored_file (stored_file_id)
        ON DELETE RESTRICT;

CREATE INDEX idx_company_profile_logo_stored_file_id
    ON company.company_profile (logo_stored_file_id);