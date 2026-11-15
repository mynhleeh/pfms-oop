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
        return createCellRenderer(SwingConstants.RIGHT, ViewConstants.COLOR_TEXT_MAIN);
    }

    public static DefaultTableCellRenderer createCenterAlignRenderer() {
        return createCellRenderer(SwingConstants.CENTER, ViewConstants.COLOR_TEXT_MAIN);
    }

    public static void styleTable(JTable table) {
        if (table == null) {
            return;
        }
        table.setRowHeight(30);
        table.setFont(ViewConstants.FONT_TABLE_BODY);
        table.setBackground(ViewConstants.COLOR_CARD_BG);
        table.setForeground(ViewConstants.COLOR_TEXT_MAIN);
        table.setGridColor(ViewConstants.COLOR_BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(ViewConstants.COLOR_SIDEBAR_ACTIVE);
        table.setSelectionForeground(ViewConstants.COLOR_TEXT_ACTIVE);
        if (table.getTableHeader() != null) {
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setFont(ViewConstants.FONT_NAV_ACTIVE);
            table.getTableHeader().setBackground(ViewConstants.COLOR_HEADER_BG);
            table.getTableHeader().setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
            table.getTableHeader().setDefaultRenderer(createHeaderRenderer());
        }
    }

    public static DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(ViewConstants.COLOR_HEADER_BG);
                setForeground(ViewConstants.COLOR_PRIMARY_LIGHT);
                setFont(ViewConstants.FONT_NAV_ACTIVE);
                setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, ViewConstants.COLOR_BORDER),
                        javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                return this;
            }
        };
    }

    public static void styleScrollPane(javax.swing.JScrollPane scrollPane) {
        if (scrollPane == null) {
            return;
        }
        scrollPane.getViewport().setBackground(ViewConstants.COLOR_CARD_BG);
        scrollPane.setBackground(ViewConstants.COLOR_CARD_BG);
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(ViewConstants.COLOR_BORDER));
    }

    public static DefaultTableCellRenderer createCellRenderer(int alignment, java.awt.Color fgColor) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(alignment);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(ViewConstants.COLOR_CARD_BG);
                    setForeground(fgColor != null ? fgColor : ViewConstants.COLOR_TEXT_MAIN);
                }
                return this;
            }
        };
        renderer.setHorizontalAlignment(alignment);
        return renderer;
    }
    // endregion
}
