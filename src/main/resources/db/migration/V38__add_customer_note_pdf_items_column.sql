DO $migration$
DECLARE
    v_template estimate.estimate_pdf_template%ROWTYPE;

    v_css TEXT;
    v_definition JSONB;
    v_updated_definition JSONB;

    v_next_version INTEGER;
    v_changed BOOLEAN := FALSE;

    v_css_patch CONSTANT TEXT := $styles$
/* V38: customer note estimate items column */
.pdf-items-description-col {
  width: 35%;
}

.pdf-items-qty-col {
  width: 7%;
}

.pdf-items-unit-col {
  width: 8%;
}

.pdf-items-customer-note-col {
  width: 35%;
}

.pdf-items-price-col {
  width: 15%;
}

.pdf-items-customer-note-cell {
  padding: 7px 6px;
  border-bottom: 1px solid rgb(221, 221, 221);
  vertical-align: top;
  overflow-wrap: anywhere;
  word-break: break-word;
  text-align: left;
}

.pdf-items-customer-note-header-cell {
  font-weight: bold;
  text-align: left;
}

.pdf-item-notes,
.pdf-cost-notes {
  margin: 0;
  font-size: inherit;
  color: rgb(102, 102, 102);
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}
/* /V38: customer note estimate items column */
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
     * classpath HTML, CSS, and definition after Flyway finishes.
     */
    IF NOT FOUND THEN
        RETURN;
    END IF;

    v_css := COALESCE(
        v_template.css_template,
        ''
    );

    v_definition := COALESCE(
        v_template.template_definition_json,
        '{}'::JSONB
    );

    /*
     * Refuse to rewrite an unexpected definition structure.
     */
    IF JSONB_TYPEOF(v_definition) <> 'object' THEN
        RAISE EXCEPTION
            'Default estimate PDF template definition must be a JSON object';
    END IF;

    IF v_definition ? 'itemsTable'
       AND JSONB_TYPEOF(v_definition -> 'itemsTable') <> 'object' THEN
        RAISE EXCEPTION
            'Default estimate PDF template itemsTable definition must be a JSON object';
    END IF;

    IF v_definition #> '{itemsTable,columns}' IS NOT NULL
       AND JSONB_TYPEOF(
           v_definition #> '{itemsTable,columns}'
       ) <> 'object' THEN
        RAISE EXCEPTION
            'Default estimate PDF template itemsTable.columns definition must be a JSON object';
    END IF;

    /*
     * Append final CSS overrides instead of replacing the full saved
     * stylesheet. This preserves designer changes while enforcing the
     * protected table's new five-column layout.
     */
    IF POSITION(
        '/* V38: customer note estimate items column */'
        IN v_css
    ) = 0 THEN
        v_css := RTRIM(v_css)
            || E'\n\n'
            || v_css_patch;

        v_changed := TRUE;
    END IF;

    /*
     * Preserve unrelated template-definition properties and any extra
     * properties stored against the existing column definitions.
     */
    v_updated_definition := JSONB_SET(
        v_definition,
        '{itemsTable}',
        COALESCE(
            v_definition -> 'itemsTable',
            '{}'::JSONB
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns}',
        COALESCE(
            v_updated_definition #> '{itemsTable,columns}',
            '{}'::JSONB
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns,description}',
        COALESCE(
            v_updated_definition
                #> '{itemsTable,columns,description}',
            '{}'::JSONB
        )
        || JSONB_BUILD_OBJECT(
            'visible',
            TRUE,
            'widthPercent',
            35
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns,quantity}',
        COALESCE(
            v_updated_definition
                #> '{itemsTable,columns,quantity}',
            '{}'::JSONB
        )
        || JSONB_BUILD_OBJECT(
            'visible',
            TRUE,
            'widthPercent',
            7
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns,unit}',
        COALESCE(
            v_updated_definition
                #> '{itemsTable,columns,unit}',
            '{}'::JSONB
        )
        || JSONB_BUILD_OBJECT(
            'visible',
            TRUE,
            'widthPercent',
            8
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns,customerNote}',
        COALESCE(
            v_updated_definition
                #> '{itemsTable,columns,customerNote}',
            '{}'::JSONB
        )
        || JSONB_BUILD_OBJECT(
            'visible',
            TRUE,
            'widthPercent',
            35
        ),
        TRUE
    );

    v_updated_definition := JSONB_SET(
        v_updated_definition,
        '{itemsTable,columns,price}',
        COALESCE(
            v_updated_definition
                #> '{itemsTable,columns,price}',
            '{}'::JSONB
        )
        || JSONB_BUILD_OBJECT(
            'visible',
            TRUE,
            'widthPercent',
            15
        ),
        TRUE
    );

    IF v_updated_definition IS DISTINCT FROM v_definition THEN
        v_changed := TRUE;
    END IF;

    IF NOT v_changed THEN
        RETURN;
    END IF;

    v_next_version :=
        COALESCE(
            v_template.version_number,
            1
        ) + 1;

    UPDATE estimate.estimate_pdf_template
    SET css_template = NULLIF(
            v_css,
            ''
        ),
        template_definition_json = v_updated_definition,
        version_number = v_next_version,
        updated_at_utc = CURRENT_TIMESTAMP
    WHERE estimate_pdf_template_id =
        v_template.estimate_pdf_template_id;

    /*
     * Preserve the same version-history behavior used when an
     * administrator saves the template through the service.
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
            || ':V38:'
            || v_next_version::TEXT
        )::UUID,
        v_template.estimate_pdf_template_id,
        v_next_version,
        v_template.name,
        v_template.html_template,
        NULLIF(
            v_css,
            ''
        ),
        v_updated_definition,
        v_template.is_active,
        CURRENT_TIMESTAMP,
        NULL,
        'Added Customer Note column to the protected estimate items table'
    );
END
$migration$;