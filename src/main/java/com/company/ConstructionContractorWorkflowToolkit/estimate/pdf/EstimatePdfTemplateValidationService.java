package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.PreviewEstimatePdfTemplateRequest;
import com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf.UpdateEstimatePdfTemplateRequest;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EstimatePdfTemplateValidationService {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_HTML_TEMPLATE_LENGTH = 1_000_000;
    private static final int MAX_CSS_TEMPLATE_LENGTH = 500_000;
    private static final int MAX_TEMPLATE_DEFINITION_JSON_LENGTH = 2_000_000;
    private static final int MAX_CHANGE_NOTE_LENGTH = 2_000;

    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<\\s*script\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern JAVASCRIPT_URL_PATTERN = Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE);

    private static final Pattern INLINE_EVENT_HANDLER_PATTERN = Pattern.compile("\\s+on[a-zA-Z]+\\s*=",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern DANGEROUS_EMBED_TAG_PATTERN = Pattern.compile("<\\s*(iframe|embed|object)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FORM_TAG_PATTERN = Pattern.compile("<\\s*form\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern ESTIMATE_ITEMS_TABLE_BLOCK_PATTERN = Pattern.compile(
            "data-pdf-block\\s*=\\s*['\"]ESTIMATE_ITEMS_TABLE['\"]",
            Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper;

    public EstimatePdfTemplateValidationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void validateUpdate(UpdateEstimatePdfTemplateRequest request) {
        if (request == null) {
            throw new BadRequestException("Template update request is required");
        }

        validateName(request.getName());
        validateHtmlTemplate(request.getHtmlTemplate());
        validateCssTemplate(request.getCssTemplate());
        validateTemplateDefinitionJson(request.getTemplateDefinitionJson());
        validateChangeNote(request.getChangeNote());
        validateRequiredProtectedBlocks(request.getHtmlTemplate());

        if (request.getIsActive() == null) {
            throw new BadRequestException("Template active flag is required");
        }
    }

    public void validatePreview(PreviewEstimatePdfTemplateRequest request) {
        if (request == null) {
            throw new BadRequestException("Template preview request is required");
        }

        if (request.getBidRevisionId() == null) {
            throw new BadRequestException("Bid revision id is required");
        }

        validateHtmlTemplate(request.getHtmlTemplate());
        validateCssTemplate(request.getCssTemplate());
        validateTemplateDefinitionJson(request.getTemplateDefinitionJson());
        validateRequiredProtectedBlocks(request.getHtmlTemplate());
    }

    private void validateName(String name) {
        if (!hasText(name)) {
            throw new BadRequestException("Template name is required");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new BadRequestException("Template name is too long");
        }
    }

    private void validateHtmlTemplate(String htmlTemplate) {
        if (!hasText(htmlTemplate)) {
            throw new BadRequestException("HTML template is required");
        }

        if (htmlTemplate.length() > MAX_HTML_TEMPLATE_LENGTH) {
            throw new BadRequestException("HTML template is too large");
        }

        rejectDangerousHtml(htmlTemplate);
    }

    private void validateCssTemplate(String cssTemplate) {
        if (!hasText(cssTemplate)) {
            return;
        }

        if (cssTemplate.length() > MAX_CSS_TEMPLATE_LENGTH) {
            throw new BadRequestException("CSS template is too large");
        }

        if (JAVASCRIPT_URL_PATTERN.matcher(cssTemplate).find()) {
            throw new BadRequestException("CSS template cannot contain javascript URLs");
        }
    }

    private void validateTemplateDefinitionJson(String templateDefinitionJson) {
        if (!hasText(templateDefinitionJson)) {
            return;
        }

        if (templateDefinitionJson.length() > MAX_TEMPLATE_DEFINITION_JSON_LENGTH) {
            throw new BadRequestException("Template definition JSON is too large");
        }

        try {
            objectMapper.readTree(templateDefinitionJson);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Template definition JSON is invalid");
        }
    }

    private void validateChangeNote(String changeNote) {
        if (!hasText(changeNote)) {
            return;
        }

        if (changeNote.length() > MAX_CHANGE_NOTE_LENGTH) {
            throw new BadRequestException("Change note is too long");
        }
    }

    private void validateRequiredProtectedBlocks(String htmlTemplate) {
        if (htmlTemplate == null || htmlTemplate.isBlank()) {
            return;
        }

        if (!ESTIMATE_ITEMS_TABLE_BLOCK_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template must contain protected block: ESTIMATE_ITEMS_TABLE");
        }
    }

    private void rejectDangerousHtml(String htmlTemplate) {
        if (SCRIPT_TAG_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template cannot contain script tags");
        }

        if (JAVASCRIPT_URL_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template cannot contain javascript URLs");
        }

        if (INLINE_EVENT_HANDLER_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template cannot contain inline event handlers");
        }

        if (DANGEROUS_EMBED_TAG_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template cannot contain iframe, embed, or object tags");
        }

        if (FORM_TAG_PATTERN.matcher(htmlTemplate).find()) {
            throw new BadRequestException("HTML template cannot contain form tags");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}