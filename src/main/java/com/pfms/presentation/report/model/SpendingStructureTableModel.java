package com.pfms.presentation.report.model;

import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Mô hình bảng dữ liệu Swing cho Báo cáo Phân tích Cơ cấu Thu Chi (UC-REP-02).
 */
public class SpendingStructureTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "Nhóm Nhu cầu", "Số tiền", "Tỷ trọng", "Kỳ trước", "Chênh lệch", "% Thay đổi"
    };

    private final List<NeedGroupShareResponseDTO> groups;
    private final DecimalFormat currencyFormatter;
    private final DecimalFormat percentFormatter;

    public SpendingStructureTableModel() {
        this.groups = new ArrayList<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.percentFormatter = new DecimalFormat("0.0", symbols);
    }

    public void setGroups(List<NeedGroupShareResponseDTO> groups) {
        this.groups.clear();
        if (groups != null) {
            this.groups.addAll(groups);
        }
        fireTableDataChanged();
    }

    public List<NeedGroupShareResponseDTO> getGroups() {
        return Collections.unmodifiableList(this.groups);
    }

    public NeedGroupShareResponseDTO getGroupAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.groups.size()) {
            return null;
        }
        return this.groups.get(rowIndex);
    }

    public void clear() {
        this.groups.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return this.groups.size();
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
        if (rowIndex < 0 || rowIndex >= this.groups.size()) {
            return null;
        }

        NeedGroupShareResponseDTO item = this.groups.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getName();
            case 1 -> formatCurrency(item.getTotalAmount());
            case 2 -> formatPercentage(item.getShare());
            case 3 -> formatCurrency(item.getPreviousTotalAmount());
            case 4 -> formatCurrency(item.getDifferenceAmount());
            case 5 -> item.isNew() ? "Mới" : formatPercentage(item.getPercentageChange());
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
