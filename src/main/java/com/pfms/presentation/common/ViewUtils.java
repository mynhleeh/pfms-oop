package com.pfms.presentation.common;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Lớp tiện ích dùng chung cho tầng giao diện đồ họa Swing.
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

    public static DefaultTableCellRenderer createRightAlignRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
    }

    public static DefaultTableCellRenderer createCenterAlignRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    public static void styleTable(JTable table) {
        if (table == null) {
            return;
        }
        table.setRowHeight(32);
        table.setFont(ViewConstants.FONT_TABLE_BODY);
        table.setBackground(ViewConstants.COLOR_CARD_BG);
        table.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        table.setGridColor(ViewConstants.COLOR_BORDER);
        table.setShowGrid(true);
        if (table.getTableHeader() != null) {
            table.getTableHeader().setFont(ViewConstants.FONT_TABLE_HEADER);
            table.getTableHeader().setBackground(ViewConstants.COLOR_HEADER_BG);
            table.getTableHeader().setForeground(ViewConstants.COLOR_TEXT_MAIN);
        }
    }
    // endregion
}
