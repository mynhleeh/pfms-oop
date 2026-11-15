package com.pfms.presentation.view.data.model;

import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mô hình bảng dữ liệu Swing hiển thị danh sách sự cố đối soát toàn vẹn hệ thống (FR-DAT-03).
 */
public class ReconciliationIssueTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "Mức độ", "Mã quy tắc", "Loại vấn đề", "Chi tiết sai lệch", "Khuyến nghị"
    };

    private final List<ReconciliationIssueResponseDTO> issues;

    public ReconciliationIssueTableModel() {
        this.issues = new ArrayList<>();
    }

    public void setIssues(List<ReconciliationIssueResponseDTO> issues) {
        this.issues.clear();
        if (issues != null) {
            this.issues.addAll(issues);
        }
        fireTableDataChanged();
    }

    public List<ReconciliationIssueResponseDTO> getIssues() {
        return Collections.unmodifiableList(this.issues);
    }

    public ReconciliationIssueResponseDTO getIssueAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.issues.size()) {
            return null;
        }
        return this.issues.get(rowIndex);
    }

    public void clear() {
        this.issues.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return this.issues.size();
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
        if (rowIndex < 0 || rowIndex >= this.issues.size()) {
            return null;
        }

        ReconciliationIssueResponseDTO item = this.issues.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getSeverity();
            case 1 -> item.getRuleCode();
            case 2 -> item.getIssueType();
            case 3 -> item.getMessage();
            case 4 -> item.getRecommendation();
            default -> null;
        };
    }
}
