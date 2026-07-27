package com.glassgang.pmworkflow.estimate.pdf;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EstimatePdfProtectedBlockRenderService {

    private static final Pattern ESTIMATE_ITEMS_TABLE_BLOCK_PATTERN = Pattern.compile(
            "(<([a-zA-Z][a-zA-Z0-9]*)\\b(?=[^>]*\\bdata-pdf-block\\s*=\\s*['\"]ESTIMATE_ITEMS_TABLE['\"])[^>]*>)(.*?)(</\\2>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final String PRINTABLE_ROWS_CONTENT = """
            {{#groupRow}}
                <tr class="pdf-group-row{{#continuationContext}} pdf-continuation-context-row{{/continuationContext}}">
                    <td class="pdf-items-description-cell" colspan="3">
                        {{group.groupName}}{{#continuationContext}} — continued{{/continuationContext}}
                    </td>
                    {{#model.showHierarchyPriceColumn}}
                        <td class="pdf-items-price-cell">
                            {{^continuationContext}}
                                {{#group.showPrice}}
                                    {{#money}}{{group.totalPrice}}{{/money}}
                                {{/group.showPrice}}
                            {{/continuationContext}}
                        </td>
                    {{/model.showHierarchyPriceColumn}}
                </tr>
            {{/groupRow}}

            {{#workTypeRow}}
                <tr class="pdf-item-type-row{{#continuationContext}} pdf-continuation-context-row{{/continuationContext}}">
                    <td class="pdf-items-description-cell" colspan="3">
                        {{workType.workTypeName}}{{#continuationContext}} — continued{{/continuationContext}}
                    </td>
                    {{#model.showHierarchyPriceColumn}}
                        <td class="pdf-items-price-cell">
                            {{^continuationContext}}
                                {{#workType.showPrice}}
                                    {{#money}}{{workType.totalPrice}}{{/money}}
                                {{/workType.showPrice}}
                            {{/continuationContext}}
                        </td>
                    {{/model.showHierarchyPriceColumn}}
                </tr>
            {{/workTypeRow}}

            {{#itemRow}}
                <tr class="pdf-item-row{{#continuationContext}} pdf-continuation-context-row{{/continuationContext}}">
                    <td class="pdf-items-description-cell">
                        <div class="pdf-item-name">
                            {{item.description}}{{#continuationContext}} — continued{{/continuationContext}}
                        </div>

                        {{^continuationContext}}
                            {{#item.customerNote}}
                                <div class="pdf-item-notes">{{item.customerNote}}</div>
                            {{/item.customerNote}}
                        {{/continuationContext}}
                    </td>

                    <td class="pdf-items-qty-cell">
                        {{^continuationContext}}
                            {{#formatQuantity}}{{item.quantity}}{{/formatQuantity}}
                        {{/continuationContext}}
                    </td>

                    <td class="pdf-items-unit-cell">
                        {{^continuationContext}}
                            {{item.unitOfMeasure}}
                        {{/continuationContext}}
                    </td>

                    {{#model.showHierarchyPriceColumn}}
                        <td class="pdf-items-price-cell">
                            {{^continuationContext}}
                                {{#item.showPrice}}
                                    {{#money}}{{item.totalPrice}}{{/money}}
                                {{/item.showPrice}}
                            {{/continuationContext}}
                        </td>
                    {{/model.showHierarchyPriceColumn}}
                </tr>
            {{/itemRow}}

            {{#costRow}}
                <tr class="pdf-cost-row">
                    <td class="pdf-items-description-cell pdf-cost-description-cell">
                        <div class="pdf-cost-element">{{cost.costElementName}}</div>

                        {{#cost.costRateName}}
                            <div class="pdf-cost-rate">{{cost.costRateName}}</div>
                        {{/cost.costRateName}}

                        {{#cost.customerNote}}
                            <div class="pdf-cost-notes">{{cost.customerNote}}</div>
                        {{/cost.customerNote}}
                    </td>

                    <td class="pdf-items-qty-cell">
                        {{#formatQuantity}}{{cost.quantity}}{{/formatQuantity}}
                    </td>

                    <td class="pdf-items-unit-cell">
                        {{cost.unitOfMeasure}}
                    </td>

                    {{#model.showHierarchyPriceColumn}}
                        <td class="pdf-items-price-cell">
                            {{#cost.showPrice}}
                                {{#money}}{{cost.totalPrice}}{{/money}}
                            {{/cost.showPrice}}
                        </td>
                    {{/model.showHierarchyPriceColumn}}
                </tr>
            {{/costRow}}
            """;

    private static final String ESTIMATE_ITEMS_TABLE_CONTENT = """
            {{#hasMainPrintableRows}}
                <table class="pdf-items-table pdf-items-main-table">
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
                        <th class="pdf-items-description-cell pdf-items-description-header-cell">
                            Description
                        </th>
                        <th class="pdf-items-qty-cell pdf-items-qty-header-cell">
                            Qty
                        </th>
                        <th class="pdf-items-unit-cell pdf-items-unit-header-cell">
                            Unit
                        </th>
                        {{#model.showHierarchyPriceColumn}}
                            <th class="pdf-items-price-cell pdf-items-price-header-cell">
                                Price
                            </th>
                        {{/model.showHierarchyPriceColumn}}
                    </tr>
                    </thead>

                    <tbody>
                    {{#mainPrintableRows}}
            %s
                    {{/mainPrintableRows}}
                    </tbody>
                </table>
            {{/hasMainPrintableRows}}

            {{#hasFinalCarryPrintableRows}}
                <section class="pdf-final-carry-block">
                    <table class="pdf-items-table pdf-items-final-carry-table">
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
                            <th class="pdf-items-description-cell pdf-items-description-header-cell">
                                Description
                            </th>
                            <th class="pdf-items-qty-cell pdf-items-qty-header-cell">
                                Qty
                            </th>
                            <th class="pdf-items-unit-cell pdf-items-unit-header-cell">
                                Unit
                            </th>
                            {{#model.showHierarchyPriceColumn}}
                                <th class="pdf-items-price-cell pdf-items-price-header-cell">
                                    Price
                                </th>
                            {{/model.showHierarchyPriceColumn}}
                        </tr>
                        </thead>

                        <tbody>
                        {{#finalCarryPrintableRows}}
            %s
                        {{/finalCarryPrintableRows}}
                        </tbody>
                    </table>

                    <section class="pdf-scope-total-summary">
                        <div>
                            Subtotal:
                            {{#money}}{{model.totals.customerFacingSubtotalPrice}}{{/money}}
                        </div>
                        <div>
                            Tax:
                            {{#money}}{{model.totals.taxAmount}}{{/money}}
                        </div>
                        <div>
                            Total:
                            {{#money}}{{model.totals.totalPrice}}{{/money}}
                        </div>
                    </section>
                </section>
            {{/hasFinalCarryPrintableRows}}

            {{^hasFinalCarryPrintableRows}}
                <section class="pdf-scope-total-summary">
                    <div>
                        Subtotal:
                        {{#money}}{{model.totals.customerFacingSubtotalPrice}}{{/money}}
                    </div>
                    <div>
                        Tax:
                        {{#money}}{{model.totals.taxAmount}}{{/money}}
                    </div>
                    <div>
                        Total:
                        {{#money}}{{model.totals.totalPrice}}{{/money}}
                    </div>
                </section>
            {{/hasFinalCarryPrintableRows}}
            """.formatted(
            PRINTABLE_ROWS_CONTENT,
            PRINTABLE_ROWS_CONTENT);

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