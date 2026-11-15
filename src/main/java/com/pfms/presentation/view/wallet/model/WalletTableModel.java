package com.pfms.presentation.view.wallet.model;

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
            "#", "Tên ví", "Số dư khả dụng", "Đơn vị", "Trạng thái", "Ví mặc định", "Ghi chú"
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= this.wallets.size()) {
            return null;
        }

        WalletResponseDTO wallet = this.wallets.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> wallet.getName();
            case 2 -> formatCurrency(wallet.getBalance());
            case 3 -> wallet.getCurrency();
            case 4 -> mapStatus(wallet.getStatus());
            case 5 -> wallet.isDefaultWallet() ? "Mặc định" : "-";
            case 6 -> wallet.getNote();
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
