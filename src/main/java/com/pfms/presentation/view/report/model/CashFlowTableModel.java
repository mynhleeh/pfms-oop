package com.pfms.presentation.view.report.model;

import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Mô hình bảng dữ liệu Swing cho Báo cáo Dòng tiền 12 tháng liên tục (UC-REP-01).
 */
public class CashFlowTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "Tháng", "Thu nhập", "Chi tiêu", "Dòng tiền thuần", "Tỷ lệ tiết kiệm"
    };

    private final List<MonthlyCashFlowResponseDTO> months;
    private final DecimalFormat currencyFormatter;
    private final DecimalFormat percentFormatter;

    public CashFlowTableModel() {
        this.months = new ArrayList<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.percentFormatter = new DecimalFormat("0.0", symbols);
    }

    public void setMonths(List<MonthlyCashFlowResponseDTO> months) {
        this.months.clear();
        if (months != null) {
            this.months.addAll(months);
        }
        fireTableDataChanged();
    }

    public List<MonthlyCashFlowResponseDTO> getMonths() {
        return Collections.unmodifiableList(this.months);
    }

    public MonthlyCashFlowResponseDTO getMonthAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.months.size()) {
            return null;
        }
        return this.months.get(rowIndex);
    }

    public void clear() {
        this.months.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return this.months.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < COLUMN_NAMES.length) {
            return COLUMN_NAMES[columnIndex];
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= this.months.size()) {
            return null;
        }

        MonthlyCashFlowResponseDTO item = this.months.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getYearMonth();
            case 1 -> formatCurrency(item.getTotalIncome());
            case 2 -> formatCurrency(item.getTotalExpense());
            case 3 -> formatCurrency(item.getNetCashFlow());
            case 4 -> formatPercentage(item.getSavingsRate());
            default -> null;
        };
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe) + " VNĐ";
    }

    private String formatPercentage(BigDecimal percentage) {
        BigDecimal safe = (percentage != null) ? percentage : BigDecimal.ZERO;
        return this.percentFormatter.format(safe) + "%";
    }
}
