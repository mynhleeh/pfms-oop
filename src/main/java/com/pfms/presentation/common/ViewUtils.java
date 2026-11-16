package com.pfms.presentation.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Lớp tiện ích dùng chung cho tầng giao diện đồ họa PFMS (100% thuần Java).
 */
public final class ViewUtils {

    // region Fields
    private static final DecimalFormat CURRENCY_FORMAT;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("#,##0", symbols);
    }
    // endregion

    // region Constructors
    private ViewUtils() {
        // Utility class private constructor
    }
    // endregion

    // region Helpers
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return CURRENCY_FORMAT.format(amount) + " VNĐ";
    }

    public static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return CURRENCY_FORMAT.format(amount);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }
    // endregion
}
