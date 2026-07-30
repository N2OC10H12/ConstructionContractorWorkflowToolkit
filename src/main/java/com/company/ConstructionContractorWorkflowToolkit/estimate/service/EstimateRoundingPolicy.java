package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EstimateRoundingPolicy {

    private static final int FRACTIONAL_SCALE = 4;

    public BigDecimal normalizeInput(
            BigDecimal value,
            BidRoundingMode roundingMode) {

        if (value == null) {
            return null;
        }

        if (effectiveMode(roundingMode) == BidRoundingMode.WHOLE) {
            return value.setScale(
                    0,
                    RoundingMode.CEILING);
        }

        return value;
    }

    public BigDecimal roundCalculated(
            BigDecimal value,
            BidRoundingMode roundingMode) {

        BigDecimal safeValue = value != null
                ? value
                : BigDecimal.ZERO;

        if (effectiveMode(roundingMode) == BidRoundingMode.WHOLE) {
            return safeValue.setScale(
                    0,
                    RoundingMode.CEILING);
        }

        return safeValue.setScale(
                FRACTIONAL_SCALE,
                RoundingMode.HALF_UP);
    }

    private BidRoundingMode effectiveMode(
            BidRoundingMode roundingMode) {

        return roundingMode != null
                ? roundingMode
                : BidRoundingMode.WHOLE;
    }
}
