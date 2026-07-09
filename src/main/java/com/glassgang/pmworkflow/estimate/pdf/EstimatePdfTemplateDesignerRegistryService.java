package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfDesignerBlockResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfDesignerRegistryResponse;
import com.glassgang.pmworkflow.estimate.dto.pdf.EstimatePdfDesignerVariableResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstimatePdfTemplateDesignerRegistryService {

        public EstimatePdfDesignerRegistryResponse getRegistry() {
                return new EstimatePdfDesignerRegistryResponse(
                                variables(),
                                blocks());
        }

        private List<EstimatePdfDesignerVariableResponse> variables() {
                return List.of(
                                variable("COMPANY_NAME", "Company Name", "Company", "Company display name",
                                                "{{model.company.companyName}}", "Glass Gang"),
                                variable("COMPANY_LOGO", "Company Logo", "Company", "Company logo URL",
                                                "{{model.company.logoUrl}}", "https://example.com/logo.png"),

                                variable("CUSTOMER_NAME", "Customer Name", "Customer", "Customer display name",
                                                "{{model.customer.displayName}}", "Test Customer LLC"),
                                variable("CUSTOMER_COMPANY_NAME", "Customer Company Name", "Customer",
                                                "Customer company name", "{{model.customer.companyName}}",
                                                "Test Customer LLC"),
                                variable("CUSTOMER_EMAIL", "Customer Email", "Customer", "Customer email address",
                                                "{{model.customer.email}}", "customer@example.com"),
                                variable("CUSTOMER_PHONE", "Customer Phone", "Customer", "Customer phone number",
                                                "{{model.customer.phone}}", "(555) 555-5555"),
                                variable("CUSTOMER_WEBSITE", "Customer Website", "Customer", "Customer website",
                                                "{{model.customer.website}}", "example.com"),

                                variable("CUSTOMER_ADDRESS_LINE_1", "Customer Address Line 1", "Customer Address",
                                                "Customer address line 1", "{{model.customer.address.line1}}",
                                                "123 Main Street"),
                                variable("CUSTOMER_ADDRESS_LINE_2", "Customer Address Line 2", "Customer Address",
                                                "Customer address line 2", "{{model.customer.address.line2}}",
                                                "Suite 100"),
                                variable("CUSTOMER_CITY", "Customer City", "Customer Address", "Customer city",
                                                "{{model.customer.address.city}}", "Austin"),
                                variable("CUSTOMER_STATE", "Customer State", "Customer Address", "Customer state",
                                                "{{model.customer.address.state}}", "TX"),
                                variable("CUSTOMER_POSTAL_CODE", "Customer Postal Code", "Customer Address",
                                                "Customer postal code", "{{model.customer.address.postalCode}}",
                                                "78701"),
                                variable("CUSTOMER_COUNTRY", "Customer Country", "Customer Address", "Customer country",
                                                "{{model.customer.address.country}}", "USA"),

                                variable("CUSTOMER_CONTACT_NAME", "Customer Contact Name", "Customer Contact",
                                                "Primary customer contact name",
                                                "{{model.customer.contact.contactName}}",
                                                "John Smith"),
                                variable("CUSTOMER_CONTACT_TITLE", "Customer Contact Title", "Customer Contact",
                                                "Primary customer contact title", "{{model.customer.contact.title}}",
                                                "Project Manager"),
                                variable("CUSTOMER_CONTACT_EMAIL", "Customer Contact Email", "Customer Contact",
                                                "Primary customer contact email", "{{model.customer.contact.email}}",
                                                "john@example.com"),
                                variable("CUSTOMER_CONTACT_PHONE", "Customer Contact Phone", "Customer Contact",
                                                "Primary customer contact phone", "{{model.customer.contact.phone}}",
                                                "(555) 555-5555"),

                                variable("JOB_NUMBER", "Job Number", "Job", "Job number", "{{model.job.jobNumber}}",
                                                "J1018"),
                                variable("JOB_NAME", "Job Name", "Job", "Job name", "{{model.job.jobName}}",
                                                "Office Renovation"),
                                variable("JOB_DESCRIPTION", "Job Description", "Job", "Job description",
                                                "{{model.job.description}}", "Interior glass scope"),

                                variable("JOB_ADDRESS_LINE_1", "Job Address Line 1", "Job Address",
                                                "Job address line 1", "{{model.job.addressLine1}}",
                                                "456 Job Site Road"),
                                variable("JOB_ADDRESS_LINE_2", "Job Address Line 2", "Job Address",
                                                "Job address line 2", "{{model.job.addressLine2}}", "Floor 2"),
                                variable("JOB_CITY", "Job City", "Job Address", "Job city", "{{model.job.city}}",
                                                "Austin"),
                                variable("JOB_STATE", "Job State", "Job Address", "Job state", "{{model.job.state}}",
                                                "TX"),
                                variable("JOB_POSTAL_CODE", "Job Postal Code", "Job Address", "Job postal code",
                                                "{{model.job.postalCode}}", "78701"),
                                variable("JOB_COUNTRY", "Job Country", "Job Address", "Job country",
                                                "{{model.job.country}}", "USA"),

                                variable("REVISION_DISPLAY_NAME", "Revision Display Name", "Revision",
                                                "Customer-facing revision name", "{{model.revisionDisplayName}}",
                                                "B1018.C.6.2026.R0"),
                                variable("REVISION_NUMBER", "Revision Number", "Revision", "Revision number",
                                                "{{model.revisionNumber}}", "0"),
                                variable("BID_NUMBER", "Bid Number", "Revision", "Bid number",
                                                "{{model.bidNumber}}", "B1018"),

                                variable("CONSTRUCTION_TYPE", "Construction Type", "Job", "Construction type label",
                                                "{{#label}}{{model.job.constructionType}}{{/label}}",
                                                "New Construction"),

                                variable("CONSTRUCTION_OBJECT_TYPE_NAME", "Construction Object Type", "Job",
                                                "Construction object type name",
                                                "{{model.job.constructionObjectTypeName}}",
                                                "Church"),
                                variable("CONSTRUCTION_OBJECT_TYPE_CODE", "Construction Object Type Code", "Job",
                                                "Construction object type code",
                                                "{{model.job.constructionObjectTypeCode}}",
                                                "CHURCH"),

                                variable("DEPARTMENT", "Department", "Job", "Department code or label",
                                                "{{model.job.departmentCode}}", "C"),

                                variable("TAX_RATE_NAME", "Tax Rate Name", "Tax", "Default tax rate name",
                                                "{{model.job.defaultTaxRateName}}", "Austin Sales Tax"),
                                variable("TAX_RATE_PERCENT", "Tax Rate Percent", "Tax", "Default tax rate percent",
                                                "{{model.job.defaultTaxRatePercent}}", "8.2500"),

                                variable("SUBTOTAL_PRICE", "Subtotal Price", "Totals", "Raw subtotal price",
                                                "{{#money}}{{model.totals.subtotalPrice}}{{/money}}", "$2,000.00"),
                                variable("CUSTOMER_FACING_SUBTOTAL_PRICE", "Customer Facing Subtotal", "Totals",
                                                "Customer-facing subtotal price",
                                                "{{#money}}{{model.totals.customerFacingSubtotalPrice}}{{/money}}",
                                                "$2,000.00"),
                                variable("TOTAL_TAX", "Total Tax", "Totals", "Total tax amount",
                                                "{{#money}}{{model.totals.taxAmount}}{{/money}}", "$320.11"),
                                variable("TOTAL_PRICE", "Total Price", "Totals", "Final total price",
                                                "{{#money}}{{model.totals.totalPrice}}{{/money}}", "$2,320.11"));
        }

        private List<EstimatePdfDesignerBlockResponse> blocks() {
                return List.of(
                                block("HEADER", "Header", "Layout", "Top section for company, logo, and estimate title",
                                                false, false, false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border")),

                                block("LOGO", "Logo", "Media", "Company logo placement", false, false, false,
                                                List.of("width", "height", "margin", "padding", "text-align")),

                                block("CUSTOMER_INFO", "Customer Info", "Estimate", "Customer information section",
                                                false, false, false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border")),

                                block("JOB_INFO", "Job Info", "Estimate", "Job information section", false, false,
                                                false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border")),

                                block("REVISION_INFO", "Revision Info", "Estimate",
                                                "Bid and revision information section", false, false, false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border")),

                                block("TOTAL_SUMMARY", "Total Summary", "Totals", "Summary of subtotal, tax, and total",
                                                false, false, false,
                                                List.of("font-family", "font-size", "font-weight", "color",
                                                                "background-color", "padding", "margin", "border",
                                                                "text-align")),

                                block("ESTIMATE_ITEMS_TABLE", "Estimate Items Table", "Estimate",
                                                "Protected dynamic estimate item hierarchy", true, true, true,
                                                List.of(
                                                                "font-family",
                                                                "font-size",
                                                                "color",
                                                                "background-color",
                                                                "padding",
                                                                "border",
                                                                "border-color",
                                                                "border-width",
                                                                "text-align",
                                                                "line-height"),
                                               List.of(
                                                        "pdf-items-table",
                                                                "pdf-items-description-col",
                                                                "pdf-items-qty-col",
                                                                "pdf-items-unit-col",
                                                                "pdf-items-price-col",
                                                                "pdf-items-header-row",
                                                                "pdf-group-row",
                                                                "pdf-item-type-row",
                                                                "pdf-item-row",
                                                                "pdf-cost-row",
                                                                "pdf-items-description-cell",
                                                                "pdf-items-qty-cell",
                                                                "pdf-items-unit-cell",
                                                                "pdf-items-price-cell",
                                                                "pdf-items-description-header-cell",
                                                                "pdf-items-qty-header-cell",
                                                                "pdf-items-unit-header-cell",
                                                                "pdf-items-price-header-cell",
                                                                "pdf-cost-description-cell",
                                                                "pdf-item-name",
                                                                "pdf-item-notes",
                                                                "pdf-cost-element",
                                                                "pdf-cost-rate",
                                                                "pdf-cost-notes")),

                                block("TERMS", "Terms", "Content", "Terms and conditions section", false, false, false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border")),

                                block("SIGNATURE", "Signature", "Content", "Signature section", false, false, false,
                                                List.of("font-family", "font-size", "color", "padding", "margin",
                                                                "border")),

                                block("FOOTER", "Footer", "Layout", "Footer section", false, false, false,
                                                List.of("font-family", "font-size", "color", "background-color",
                                                                "padding", "margin", "border", "text-align")),

                                block("PAGE_BREAK", "Page Break", "Layout", "PDF page break", false, false, false,
                                                List.of()),

                                block("TWO_COLUMNS", "Two Columns", "Layout", "Two-column layout container", false,
                                                false, false,
                                                List.of("gap", "padding", "margin")),

                                block("SPACER", "Spacer", "Layout", "Vertical spacing block", false, false, false,
                                                List.of("height", "margin")),

                                block("DIVIDER", "Divider", "Layout", "Horizontal divider", false, false, false,
                                                List.of("border-color", "border-width", "margin")),

                                block("STATIC_TEXT", "Static Text", "Content", "Static editable text block", false,
                                                false, false,
                                                List.of("font-family", "font-size", "font-weight", "color",
                                                                "background-color", "padding", "margin",
                                                                "text-align")));
        }

        private EstimatePdfDesignerVariableResponse variable(
                        String key,
                        String label,
                        String category,
                        String description,
                        String mustache,
                        String sampleValue) {
                return new EstimatePdfDesignerVariableResponse(
                                key,
                                label,
                                category,
                                description,
                                mustache,
                                sampleValue);
        }

        private EstimatePdfDesignerBlockResponse block(
                        String key,
                        String label,
                        String category,
                        String description,
                        boolean required,
                        boolean protectedBlock,
                        boolean dynamic,
                        List<String> allowedStyleKeys) {
                return block(
                                key,
                                label,
                                category,
                                description,
                                required,
                                protectedBlock,
                                dynamic,
                                allowedStyleKeys,
                                List.of());
        }

        private EstimatePdfDesignerBlockResponse block(
                        String key,
                        String label,
                        String category,
                        String description,
                        boolean required,
                        boolean protectedBlock,
                        boolean dynamic,
                        List<String> allowedStyleKeys,
                        List<String> cssClasses) {
                return new EstimatePdfDesignerBlockResponse(
                                key,
                                label,
                                category,
                                description,
                                required,
                                protectedBlock,
                                dynamic,
                                allowedStyleKeys,
                                cssClasses);
        }
}