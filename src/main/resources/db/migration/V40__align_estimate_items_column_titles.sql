DO $migration$
DECLARE
    v_template estimate.estimate_pdf_template%ROWTYPE;
    v_css TEXT;
    v_next_version INTEGER;

    v_css_patch CONSTANT TEXT := $styles$
/* V40: individual estimate items title alignments */
.pdf-items-header-row .pdf-items-description-header-cell {
  text-align: left;
}

.pdf-items-header-row .pdf-items-qty-header-cell {
  text-align: center;
}

.pdf-items-header-row .pdf-items-unit-header-cell {
  text-align: center;
}

.pdf-items-header-row .pdf-items-customer-note-header-cell {
  text-align: center;
}

.pdf-items-header-row .pdf-items-price-header-cell {
  text-align: right;
}
/* /V40: individual estimate items title alignments */
$styles$;

BEGIN
    SELECT *
    INTO v_template
    FROM estimate.estimate_pdf_template
    WHERE code = 'DEFAULT_ESTIMATE_TEMPLATE'
      AND is_deleted = FALSE
    FOR UPDATE;

    /*
     * On a fresh database, the classpath template seeder will use
     * the updated CSS after Flyway finishes.
     */
    IF NOT FOUND THEN
        RETURN;
    END IF;

    v_css := COALESCE(
        v_template.css_template,
        ''
    );

    IF POSITION(
        '/* V40: individual estimate items title alignments */'
        IN v_css
    ) > 0 THEN
        RETURN;
    END IF;

    /*
     * Append higher-specificity rules after V39.
     * These override V39's generic centered-header rule.
     */
    v_css := RTRIM(v_css)
        || E'\n\n'
        || v_css_patch;

    v_next_version :=
        COALESCE(
            v_template.version_number,
            1
        ) + 1;

    UPDATE estimate.estimate_pdf_template
    SET css_template = v_css,
        version_number = v_next_version,
        updated_at_utc = CURRENT_TIMESTAMP
    WHERE estimate_pdf_template_id =
        v_template.estimate_pdf_template_id;

    INSERT INTO estimate.estimate_pdf_template_version (
        estimate_pdf_template_version_id,
        estimate_pdf_template_id,
        version_number,
        name,
        html_template,
        css_template,
        template_definition_json,
        is_active,
        created_at_utc,
        created_by_user_id,
        change_note
    )
    VALUES (
        MD5(
            v_template.estimate_pdf_template_id::TEXT
            || ':V40:'
            || v_next_version::TEXT
        )::UUID,
        v_template.estimate_pdf_template_id,
        v_next_version,
        v_template.name,
        v_template.html_template,
        v_css,
        v_template.template_definition_json,
        v_template.is_active,
        CURRENT_TIMESTAMP,
        NULL,
        'Applied individual alignment to estimate items column titles'
    );
END
$migration$;