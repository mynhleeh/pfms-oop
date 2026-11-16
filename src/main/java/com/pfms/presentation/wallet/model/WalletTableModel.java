package com.pfms.presentation.wallet.model;

import com.pfms.presentation.dto.wallet.WalletResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Mô hình dữ liệu bảng danh sách ví (kế thừa AbstractTableModel).
 * Đóng gói danh sách WalletResponseDTO và định dạng hiển thị cho từng cột.
 */
public class WalletTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "Tên ví tài chính", "Số dư khả dụng", "Trạng thái", "Ghi chú", "Thao tác"
    };

    private final List<WalletResponseDTO> wallets = new ArrayList<>();
    private final DecimalFormat currencyFormatter;

    public WalletTableModel() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
    }

    public void setWallets(List<WalletResponseDTO> newWallets) {
        this.wallets.clear();
        if (newWallets != null) {
            this.wallets.addAll(newWallets);
        }
        fireTableDataChanged();
    }

    public WalletResponseDTO getWalletAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < this.wallets.size()) {
            return this.wallets.get(rowIndex);
        }
        return null;
    }

    public List<WalletResponseDTO> getWallets() {
        return Collections.unmodifiableList(this.wallets);
    }

    @Override
    public int getRowCount() {
        return this.wallets.size();
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= this.wallets.size()) {
            return null;
        }

        WalletResponseDTO wallet = this.wallets.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> wallet.getName();
            case 1 -> formatCurrency(wallet.getBalance());
            case 2 -> mapStatus(wallet.getStatus());
            case 3 -> wallet.getNote();
            case 4 -> wallet;
            default -> null;
        };
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe) + " VNĐ";
    }

    private String mapStatus(String status) {
        if (status == null) {
            return "Không xác định";
        }
        return switch (status.toUpperCase(Locale.ROOT)) {
            case "ACTIVE" -> "Hoạt động";
            case "ARCHIVED" -> "Lưu trữ";
            case "DELETED" -> "Đã xóa";
            default -> status;
        };
    }
}
