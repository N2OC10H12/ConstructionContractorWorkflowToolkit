package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfGroup;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfItemCostLine;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfItemLine;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfItemTypeGroup;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfModel;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfTotals;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class EstimatePdfHtmlPreviewService {

    private final EstimatePdfModelBuilder estimatePdfModelBuilder;

    public EstimatePdfHtmlPreviewService(EstimatePdfModelBuilder estimatePdfModelBuilder) {
        this.estimatePdfModelBuilder = estimatePdfModelBuilder;
    }

    public String renderHtml(UUID bidRevisionId) {
        EstimatePdfModel model = estimatePdfModelBuilder.build(bidRevisionId);

        StringBuilder html = new StringBuilder();

        html.append("""
                <!doctype html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Estimate Preview</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            font-size: 13px;
                            color: #222;
                            margin: 32px;
                        }

                        .title-page {
                            min-height: 650px;
                            border-bottom: 1px solid #ddd;
                            margin-bottom: 28px;
                            padding-bottom: 28px;
                        }

                        .header {
                            display: flex;
                            justify-content: space-between;
                            gap: 24px;
                            margin-bottom: 32px;
                        }

                        .block {
                            width: 48%;
                        }

                        .label {
                            color: #666;
                            font-size: 11px;
                            text-transform: uppercase;
                            letter-spacing: 0.04em;
                            margin-bottom: 4px;
                        }

                        h1 {
                            font-size: 28px;
                            margin: 0 0 20px 0;
                        }

                        h2 {
                            font-size: 18px;
                            margin: 24px 0 10px 0;
                            border-bottom: 1px solid #ddd;
                            padding-bottom: 6px;
                        }

                        h3 {
                            font-size: 15px;
                            margin: 18px 0 8px 0;
                        }

                        .muted {
                            color: #666;
                        }

                        .total-box {
                            margin-top: 28px;
                            width: 320px;
                            margin-left: auto;
                            border: 1px solid #ddd;
                            padding: 14px;
                        }

                        .total-row {
                            display: flex;
                            justify-content: space-between;
                            margin-bottom: 8px;
                        }

                        .grand-total {
                            font-weight: bold;
                            font-size: 16px;
                            border-top: 1px solid #ddd;
                            padding-top: 8px;
                            margin-top: 8px;
                        }

                        .group {
                            margin-top: 22px;
                        }

                        .item-type {
                            margin-left: 16px;
                            margin-top: 12px;
                        }

                        .item {
                            margin-left: 32px;
                            padding: 6px 0;
                            border-bottom: 1px solid #eee;
                        }

                        .cost {
                            margin-left: 52px;
                            color: #555;
                            font-size: 12px;
                            padding: 3px 0;
                        }

                        .row {
                            display: flex;
                            justify-content: space-between;
                            gap: 16px;
                        }

                        .price {
                            white-space: nowrap;
                            text-align: right;
                        }
                    </style>
                </head>
                <body>
                """);

        appendTitlePage(html, model);
        appendItemsPage(html, model);

        html.append("""
                </body>
                </html>
                """);

        return html.toString();
    }

    private void appendTitlePage(StringBuilder html, EstimatePdfModel model) {
        html.append("<section class=\"title-page\">");

        html.append("<h1>Estimate</h1>");

        html.append("<div class=\"header\">");

        html.append("<div class=\"block\">");
        html.append("<div class=\"label\">Company</div>");
        html.append("<div>").append(escape(model.getCompany().getCompanyName())).append("</div>");
        html.append("</div>");

        html.append("<div class=\"block\">");
        html.append("<div class=\"label\">Customer</div>");
        html.append("<div>").append(escape(model.getCustomer().getDisplayName())).append("</div>");

        if (model.getCustomer().getContact() != null) {
            html.append("<div class=\"muted\">Contact: ")
                    .append(escape(model.getCustomer().getContact().getContactName()))
                    .append("</div>");
        }

        html.append("</div>");

        html.append("</div>");

        html.append("<h2>Job Information</h2>");
        html.append("<div><strong>Job Name:</strong> ").append(escape(model.getJob().getJobName())).append("</div>");
        html.append("<div><strong>Job Number:</strong> ").append(escape(model.getJob().getJobNumber())).append("</div>");
        html.append("<div><strong>Revision:</strong> ").append(escape(model.getRevisionDisplayName())).append("</div>");
        html.append("<div><strong>Construction Type:</strong> ").append(escape(model.getJob().getConstructionType())).append("</div>");
        html.append("<div><strong>Tax Rate:</strong> ")
                .append(escape(model.getJob().getDefaultTaxRateName()))
                .append(" ")
                .append(escape(model.getJob().getDefaultTaxRatePercent()))
                .append("</div>");

        html.append("<h2>Job Address</h2>");
        html.append("<div>").append(escape(model.getJob().getAddressLine1())).append("</div>");
        html.append("<div>").append(escape(model.getJob().getAddressLine2())).append("</div>");
        html.append("<div>")
                .append(escape(model.getJob().getCity()))
                .append(" ")
                .append(escape(model.getJob().getState()))
                .append(" ")
                .append(escape(model.getJob().getPostalCode()))
                .append("</div>");

        if (Boolean.TRUE.equals(model.getShowTitleTotalPrice())) {
            appendTotalsBox(html, model.getTotals());
        }

        html.append("</section>");
    }

    private void appendTotalsBox(StringBuilder html, EstimatePdfTotals totals) {
        html.append("<div class=\"total-box\">");

        html.append("<div class=\"total-row\"><span>Subtotal</span><span>")
                .append(formatMoney(totals.getCustomerFacingSubtotalPrice()))
                .append("</span></div>");

        html.append("<div class=\"total-row\"><span>Tax</span><span>")
                .append(formatMoney(totals.getTaxAmount()))
                .append("</span></div>");

        html.append("<div class=\"total-row grand-total\"><span>Total</span><span>")
                .append(formatMoney(totals.getTotalPrice()))
                .append("</span></div>");

        html.append("</div>");
    }

    private void appendItemsPage(StringBuilder html, EstimatePdfModel model) {
        html.append("<section>");

        html.append("<h2>Items</h2>");
        html.append("<div class=\"muted\">")
                .append(escape(model.getJob().getJobName()))
                .append(" | Revision ")
                .append(escape(model.getRevisionNumber()))
                .append("</div>");

        for (EstimatePdfGroup group : safeList(model.getGroups())) {
            html.append("<div class=\"group\">");
            html.append("<div class=\"row\">");
            html.append("<h3>").append(escape(group.getGroupName())).append("</h3>");

            if (Boolean.TRUE.equals(group.getShowPrice())) {
                html.append("<div class=\"price\">").append(formatMoney(group.getTotalPrice())).append("</div>");
            }

            html.append("</div>");

            for (EstimatePdfItemTypeGroup itemTypeGroup : safeList(group.getItemTypes())) {
                html.append("<div class=\"item-type\">");
                html.append("<div class=\"row\">");
                html.append("<strong>").append(escape(itemTypeGroup.getItemTypeName())).append("</strong>");

                if (Boolean.TRUE.equals(itemTypeGroup.getShowPrice())) {
                    html.append("<div class=\"price\">").append(formatMoney(itemTypeGroup.getTotalPrice())).append("</div>");
                }

                html.append("</div>");

                for (EstimatePdfItemLine item : safeList(itemTypeGroup.getItems())) {
                    html.append("<div class=\"item\">");
                    html.append("<div class=\"row\">");
                    html.append("<div>")
                            .append(escape(item.getLineNumber()))
                            .append(". ")
                            .append(escape(item.getDescription()))
                            .append("</div>");

                    if (Boolean.TRUE.equals(item.getShowPrice())) {
                        html.append("<div class=\"price\">").append(formatMoney(item.getPriceWithTax())).append("</div>");
                    }

                    html.append("</div>");
                    html.append("</div>");

                    if (Boolean.TRUE.equals(item.getShowCostLines())) {
                        for (EstimatePdfItemCostLine cost : safeList(item.getCosts())) {
                            html.append("<div class=\"cost row\">");
                            html.append("<div>")
                                    .append(escape(cost.getLineNumber()))
                                    .append(". ")
                                    .append(escape(cost.getCostElementName()))
                                    .append(" - ")
                                    .append(escape(cost.getCostRateName()))
                                    .append("</div>");

                            if (Boolean.TRUE.equals(cost.getShowPrice())) {
                                html.append("<div class=\"price\">").append(formatMoney(cost.getPriceWithTax())).append("</div>");
                            }

                            html.append("</div>");
                        }
                    }
                }

                html.append("</div>");
            }

            html.append("</div>");
        }

        html.append("</section>");
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "";
        }

        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }

    private String escape(Object value) {
        if (value == null) {
            return "";
        }

        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? List.of() : value;
    }
}