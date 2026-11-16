package com.pfms.presentation.transaction.model;

import com.pfms.presentation.dto.transaction.TransactionResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Mô hình bảng dữ liệu hiển thị danh sách giao dịch thu chi (UC-TRN-03).
 * Cấu trúc 9 cột chuẩn hóa: Ngày | Loại | Nhóm | Danh mục | Số tiền | Ví | Phụ phí | Ghi chú | Thao tác.
 */
public class TransactionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = {
            "Ngày", "Loại", "Nhóm", "Danh mục", "Số tiền", "Ví", "Phụ phí", "Ghi chú", "Thao tác"
    };

    private final List<TransactionResponseDTO> transactions;
    private final Map<UUID, String> categoryGroupMap;
    private final DecimalFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter;

    public TransactionTableModel() {
        this.transactions = new ArrayList<>();
        this.categoryGroupMap = new HashMap<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions.clear();
        if (transactions != null) {
            this.transactions.addAll(transactions);
        }
        fireTableDataChanged();
    }

    public void setCategoryGroupMap(Map<UUID, String> groupMap) {
        this.categoryGroupMap.clear();
        if (groupMap != null) {
            this.categoryGroupMap.putAll(groupMap);
        }
        fireTableDataChanged();
    }

    public List<TransactionResponseDTO> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }

    public TransactionResponseDTO getTransactionAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
            return null;
        }
        return this.transactions.get(rowIndex);
    }

    public void clear() {
        this.transactions.clear();
        fireTableDataChanged();
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
    public String getColumnName(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < COLUMN_NAMES.length) {
            return COLUMN_NAMES[columnIndex];
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= this.transactions.size()) {
            return null;
        }

        TransactionResponseDTO txn = this.transactions.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> (txn.getTransactionDate() != null) ? this.dateFormatter.format(txn.getTransactionDate()) : "-";
            case 1 -> mapTransactionType(txn.getTransactionType());
            case 2 -> resolveGroupName(txn.getCategoryId());
            case 3 -> (txn.getCategoryName() != null) ? txn.getCategoryName() : "-";
            case 4 -> formatSignedAmount(txn.getAmount(), txn.getTransactionType());
            case 5 -> (txn.getWalletName() != null) ? txn.getWalletName() : "-";
            case 6 -> (txn.hasFee() && txn.getFeeAmount() != null && txn.getFeeAmount().compareTo(BigDecimal.ZERO) > 0)
                    ? "-" + formatCurrency(txn.getFeeAmount())
                    : "-";
            case 7 -> (txn.getNote() != null) ? txn.getNote() : "";
            case 8 -> txn;
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 8;
    }

    private String resolveGroupName(UUID categoryId) {
        if (categoryId == null) {
            return "-";
        }
        String group = this.categoryGroupMap.get(categoryId);
        return (group != null && !group.isBlank()) ? group : "-";
    }

    private String formatSignedAmount(BigDecimal amount, String type) {
        String base = formatCurrency(amount);
        if ("INCOME".equalsIgnoreCase(type)) {
            return "+" + base;
        } else if ("EXPENSE".equalsIgnoreCase(type)) {
            return "-" + base;
        }
        return base;
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe) + " VNĐ";
    }

    private String mapTransactionType(String type) {
        if (type == null) {
            return "Không xác định";
        }
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "INCOME" -> "Thu nhập";
            case "EXPENSE" -> "Chi tiêu";
            case "ADJUSTMENT" -> "Kiểm kê";
            default -> type;
        };
    }
}
