package com.glassgang.pmworkflow.estimate.pdf;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EstimatePdfProtectedBlockRenderService {

    private static final Pattern ESTIMATE_ITEMS_TABLE_BLOCK_PATTERN = Pattern.compile(
            "(<([a-zA-Z][a-zA-Z0-9]*)\\b(?=[^>]*\\bdata-pdf-block\\s*=\\s*['\"]ESTIMATE_ITEMS_TABLE['\"])[^>]*>)(.*?)(</\\2>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final String ESTIMATE_ITEMS_TABLE_CONTENT = """
                        <table class="pdf-items-table">
                            <colgroup>
                                <col class="pdf-items-description-col">
                                <col class="pdf-items-qty-col">
                                <col class="pdf-items-unit-col">
                                {{#model.showHierarchyPriceColumn}}
                                    <col class="pdf-items-price-col">
                                {{/model.showHierarchyPriceColumn}}
                            </colgroup>
                            <thead>
                            <tr class="pdf-items-header-row">
                                <th class="pdf-items-description-cell pdf-items-description-header-cell">Description</th>
                                <th class="pdf-items-qty-cell pdf-items-qty-header-cell">Qty</th>
                                <th class="pdf-items-unit-cell pdf-items-unit-header-cell">Unit</th>
                                {{#model.showHierarchyPriceColumn}}
                                    <th class="pdf-items-price-cell pdf-items-price-header-cell">Price</th>
                                {{/model.showHierarchyPriceColumn}}
                            </tr>
                            </thead>
                            <tbody>
                            {{#model.groups}}
                                <tr class="pdf-group-row">
                                    <td class="pdf-items-description-cell" colspan="3">{{groupName}}</td>
                                    {{#model.showHierarchyPriceColumn}}
                                        <td class="pdf-items-price-cell">
                                            {{#showPrice}}{{#money}}{{totalPrice}}{{/money}}{{/showPrice}}
                                        </td>
                                    {{/model.showHierarchyPriceColumn}}
                                </tr>

                                {{#itemTypes}}
                                    <tr class="pdf-item-type-row">
                                        <td class="pdf-items-description-cell" colspan="3">{{itemTypeName}}</td>
                                        {{#model.showHierarchyPriceColumn}}
                                            <td class="pdf-items-price-cell">
                                                {{#showPrice}}{{#money}}{{totalPrice}}{{/money}}{{/showPrice}}
                                            </td>
                                        {{/model.showHierarchyPriceColumn}}
                                    </tr>

                                    {{#items}}
                                        <tr class="pdf-item-row">
                                            <td class="pdf-items-description-cell">
                                                <div class="pdf-item-name">{{description}}</div>
                                                {{#customerNote}}
                                                    <div class="pdf-item-notes">{{customerNote}}</div>
                                                {{/customerNote}}
                                            </td>
                                            <td class="pdf-items-qty-cell">{{#formatQuantity}}{{quantity}}{{/formatQuantity}}</td>
                                            <td class="pdf-items-unit-cell">{{unitOfMeasure}}</td>
                                            {{#model.showHierarchyPriceColumn}}
                                                <td class="pdf-items-price-cell">
                                                    {{#showPrice}}{{#money}}{{totalPrice}}{{/money}}{{/showPrice}}
                                                </td>
                                            {{/model.showHierarchyPriceColumn}}
                                        </tr>

                                        {{#showCostLines}}
                                            {{#costs}}
                                                <tr class="pdf-cost-row">
                                                    <td class="pdf-items-description-cell pdf-cost-description-cell">
                                                        <div class="pdf-cost-element">{{costElementName}}</div>
                                                        {{#costRateName}}
                                                            <div class="pdf-cost-rate">{{costRateName}}</div>
                                                        {{/costRateName}}
                                                        {{#customerNote}}
                                                            <div class="pdf-cost-notes">{{customerNote}}</div>
                                                        {{/customerNote}}
                                                    </td>
                                                    <td class="pdf-items-qty-cell">{{#formatQuantity}}{{quantity}}{{/formatQuantity}}</td>
                                                    <td class="pdf-items-unit-cell">{{unitOfMeasure}}</td>
                                                    {{#model.showHierarchyPriceColumn}}
                                                        <td class="pdf-items-price-cell">
                                                            {{#showPrice}}{{#money}}{{totalPrice}}{{/money}}{{/showPrice}}
                                                        </td>
                                                    {{/model.showHierarchyPriceColumn}}
                                                </tr>
                                            {{/costs}}
                                        {{/showCostLines}}
                                    {{/items}}
                                {{/itemTypes}}
                            {{/model.groups}}
                            </tbody>
                        </table>
                        """;

    public String applyProtectedBlocks(String htmlTemplate) {
        if (htmlTemplate == null || htmlTemplate.isBlank()) {
            return htmlTemplate;
        }

        Matcher matcher = ESTIMATE_ITEMS_TABLE_BLOCK_PATTERN.matcher(htmlTemplate);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String replacement = matcher.group(1)
                    + ESTIMATE_ITEMS_TABLE_CONTENT
                    + matcher.group(4);

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);

        return result.toString();
    }
}