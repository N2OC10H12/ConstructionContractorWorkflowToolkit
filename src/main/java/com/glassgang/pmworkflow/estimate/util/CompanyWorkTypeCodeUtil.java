package com.glassgang.pmworkflow.estimate.util;

import java.util.regex.Pattern;

public final class CompanyWorkTypeCodeUtil {

    private static final Pattern ACCEPTED_CODE_PATTERN = Pattern.compile(
            "^(?:\\d{6,8}|\\d{2}[\\s-]?\\d{2}[\\s-]?\\d{2}(?:\\.\\d{1,2})?)$");

    private CompanyWorkTypeCodeUtil() {
    }

    /**
     * Converts supported input into its unique numeric representation.
     *
     * Examples:
     * 08 41 13    -> 084113
     * 084113      -> 084113
     * 08-41-13    -> 084113
     * 08 41 13.1  -> 08411301
     * 08 41 13.01 -> 08411301
     */
    public static String normalize(String rawCode) {
        String trimmedCode = requireValidInput(rawCode);
        String digits = trimmedCode.replaceAll("[^0-9]", "");

        if (digits.length() == 6) {
            return digits;
        }

        if (digits.length() == 7) {
            return digits.substring(0, 6)
                    + "0"
                    + digits.substring(6);
        }

        if (digits.length() == 8) {
            return digits;
        }

        throw invalidCodeException();
    }

    /**
     * Converts supported input into the canonical display format.
     *
     * Examples:
     * 084113   -> 08 41 13
     * 0841131  -> 08 41 13.01
     * 08411301 -> 08 41 13.01
     */
    public static String format(String rawCode) {
        String normalizedCode = normalize(rawCode);

        String formattedBase = normalizedCode.substring(0, 2)
                + " "
                + normalizedCode.substring(2, 4)
                + " "
                + normalizedCode.substring(4, 6);

        if (normalizedCode.length() == 8) {
            return formattedBase + "." + normalizedCode.substring(6);
        }

        return formattedBase;
    }

    public static String deriveDivisionCode(String rawCode) {
        return normalize(rawCode).substring(0, 2);
    }

    /**
     * Company convention:
     * six-digit base code -> level 4
     * decimal extension   -> level 5
     */
    public static int deriveCustomLevel(String rawCode) {
        return normalize(rawCode).length() == 6 ? 4 : 5;
    }

    private static String requireValidInput(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Work type code is required");
        }

        String trimmedCode = rawCode.trim();

        if (!ACCEPTED_CODE_PATTERN.matcher(trimmedCode).matches()) {
            throw invalidCodeException();
        }

        return trimmedCode;
    }

    private static IllegalArgumentException invalidCodeException() {
        return new IllegalArgumentException(
                "Work type code must contain six base digits "
                        + "and an optional one- or two-digit suffix");
    }
}