DO $migration$
DECLARE
    v_template estimate.estimate_pdf_template%ROWTYPE;

    v_html TEXT;
    v_css TEXT;

    v_next_version INTEGER;

    v_has_company_introduction BOOLEAN;
    v_has_bid_scope BOOLEAN;
    v_changed BOOLEAN := FALSE;

    v_title_bottom_marker CONSTANT TEXT :=
        '<section class="pdf-title-bottom-row">';

    v_blocks_html CONSTANT TEXT := $blocks$
    <section
            data-pdf-block="COMPANY_INTRODUCTION"
            class="pdf-company-introduction-block">
        <div class="pdf-company-introduction-title">Introduction</div>
        <div class="pdf-company-introduction-content">
            {{model.company.introductionData}}
        </div>
    </section>

    <section
            data-pdf-block="BID_SCOPE"
            class="pdf-bid-scope-block">
        <div class="pdf-bid-scope-title">Scope</div>
        <div class="pdf-bid-scope-content">
            {{model.estimateScope}}
        </div>
    </section>
$blocks$;

    v_css_patch CONSTANT TEXT := $styles$
/* V36: company introduction and bid scope blocks */
.pdf-company-introduction-block {
  margin-top: 24px;
  font-family: Arial, sans-serif;
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  text-decoration: none;
  line-height: 1.4;
  color: rgb(34, 34, 34);
  text-align: left;
  page-break-inside: avoid;
  break-inside: avoid;
}

.pdf-company-introduction-title {
  margin: 0 0 8px 0;
  font-size: inherit;
  font-weight: bold;
}

.pdf-company-introduction-content {
  margin: 0;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  word-wrap: break-word;
}

.pdf-bid-scope-block {
  margin-top: 24px;
  font-family: Arial, sans-serif;
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  text-decoration: none;
  line-height: 1.4;
  color: rgb(34, 34, 34);
  text-align: left;
  page-break-inside: avoid;
  break-inside: avoid;
}

.pdf-bid-scope-title {
  margin: 0 0 8px 0;
  font-size: inherit;
  font-weight: bold;
}

.pdf-bid-scope-content {
  margin: 0;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  word-wrap: break-word;
}

/*
 * Introduction and Scope now provide the spacing above
 * the signature and total row.
 */
.pdf-title-bottom-row {
  margin-top: 24px;
}
/* /V36: company introduction and bid scope blocks */
$styles$;

BEGIN
    SELECT *
    INTO v_template
    FROM estimate.estimate_pdf_template
    WHERE code = 'DEFAULT_ESTIMATE_TEMPLATE'
      AND is_deleted = FALSE
    FOR UPDATE;

    /*
     * A fresh environment has no persisted template yet.
     * EstimatePdfTemplateSeeder will create it from the updated
     * classpath HTML and CSS after Flyway finishes.
     */
    IF NOT FOUND THEN
        RETURN;
    END IF;

    v_html := v_template.html_template;
    v_css := COALESCE(v_template.css_template, '');

    v_has_company_introduction :=
        v_html ~* 'data-pdf-block[[:space:]]*=[[:space:]]*[''"]COMPANY_INTRODUCTION[''"]';

    v_has_bid_scope :=
        v_html ~* 'data-pdf-block[[:space:]]*=[[:space:]]*[''"]BID_SCOPE[''"]';

    /*
     * Do not guess how to repair a partially modified template.
     * Either both blocks already exist or both must be inserted.
     */
    IF v_has_company_introduction <> v_has_bid_scope THEN
        RAISE EXCEPTION
            'Default estimate PDF template contains only one of COMPANY_INTRODUCTION and BID_SCOPE';
    END IF;

    IF NOT v_has_company_introduction
       AND NOT v_has_bid_scope THEN

        IF POSITION(v_title_bottom_marker IN v_html) = 0 THEN
            RAISE EXCEPTION
                'Default estimate PDF template does not contain the expected pdf-title-bottom-row marker';
        END IF;

        v_html := REPLACE(
            v_html,
            v_title_bottom_marker,
            v_blocks_html
                || E'\n\n    '
                || v_title_bottom_marker
        );

        v_changed := TRUE;
    END IF;

    /*
     * Append a final override rather than replacing existing CSS.
     * This preserves CSS created or changed through the designer.
     */
    IF POSITION(
        '/* V36: company introduction and bid scope blocks */'
        IN v_css
    ) = 0 THEN

        v_css := RTRIM(v_css)
            || E'\n\n'
            || v_css_patch;

        v_changed := TRUE;
    END IF;

    IF NOT v_changed THEN
        RETURN;
    END IF;

    v_next_version :=
        COALESCE(v_template.version_number, 1) + 1;

    UPDATE estimate.estimate_pdf_template
    SET html_template = v_html,
        css_template = NULLIF(v_css, ''),
        version_number = v_next_version,
        updated_at_utc = CURRENT_TIMESTAMP
    WHERE estimate_pdf_template_id =
        v_template.estimate_pdf_template_id;

    /*
     * The normal template service snapshots the newly saved state.
     * This migration follows the same version-history behavior.
     *
     * MD5 produces a deterministic 32-character UUID value without
     * requiring pgcrypto or another UUID extension.
     */
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
            || ':V36:'
            || v_next_version::TEXT
        )::UUID,
        v_template.estimate_pdf_template_id,
        v_next_version,
        v_template.name,
        v_html,
        NULLIF(v_css, ''),
        v_template.template_definition_json,
        v_template.is_active,
        CURRENT_TIMESTAMP,
        NULL,
        'Added protected Company Introduction and Bid Scope title-page blocks'
    );
END
$migration$;