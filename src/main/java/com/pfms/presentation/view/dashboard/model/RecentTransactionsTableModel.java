package com.pfms.presentation.view.dashboard.model;

import com.pfms.presentation.viewmodel.dashboard.RecentTransactionViewModel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mô hình dữ liệu bảng hiển thị các giao dịch gần nhất tại Dashboard.
 * Kế thừa AbstractTableModel và đóng gói danh sách RecentTransactionViewModel.
 */
public class RecentTransactionsTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "THỜI GIAN", "VÍ TÀI CHÍNH", "DANH MỤC", "GHI CHÚ", "SỐ TIỀN (VNĐ)"
    };

    private final List<RecentTransactionViewModel> transactions = new ArrayList<>();

    public RecentTransactionsTableModel() {
    }

    public void setTransactions(List<RecentTransactionViewModel> list) {
        this.transactions.clear();
        if (list != null) {
            this.transactions.addAll(list);
        }
        fireTableDataChanged();
    }

    public void clear() {
        this.transactions.clear();
        fireTableDataChanged();
    }

    public List<RecentTransactionViewModel> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }

    public RecentTransactionViewModel getTransactionAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
            return null;
        }
        return this.transactions.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return this.transactions.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < COLUMN_NAMES.length) {
            return COLUMN_NAMES[column];
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
            return null;
        }
        RecentTransactionViewModel txn = this.transactions.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> (txn.dateFormatted() != null) ? txn.dateFormatted() : "-";
            case 1 -> (txn.walletName() != null) ? txn.walletName() : "-";
            case 2 -> (txn.categoryName() != null) ? txn.categoryName() : "-";
            case 3 -> (txn.note() != null) ? txn.note() : "";
            case 4 -> (txn.amountFormatted() != null) ? txn.amountFormatted() : "0";
            default -> null;
        };
    }
}
