package com.pfms.presentation.transfer.model;

import com.pfms.presentation.dto.transfer.TransferResponseDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Mô hình bảng dữ liệu hiển thị danh sách chuyển tiền nội bộ (UC-TRF-02).
 */
public class TransferTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
            "STT", "Ngày", "Ví nguồn", "Ví đích", "Số tiền", "Phụ phí", "Ghi chú"
    };

    private final List<TransferResponseDTO> transfers;
    private final DecimalFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter;

    public TransferTableModel() {
        this.transfers = new ArrayList<>();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        this.currencyFormatter = new DecimalFormat("#,##0", symbols);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void setTransfers(List<TransferResponseDTO> transfers) {
        this.transfers.clear();
        if (transfers != null) {
            this.transfers.addAll(transfers);
        }
        fireTableDataChanged();
    }

    public List<TransferResponseDTO> getTransfers() {
        return Collections.unmodifiableList(this.transfers);
    }

    public TransferResponseDTO getTransferAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.transfers.size()) {
            return null;
        }
        return this.transfers.get(rowIndex);
    }

    public void clear() {
        this.transfers.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return this.transfers.size();
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
        if (rowIndex < 0 || rowIndex >= this.transfers.size()) {
            return null;
        }

        TransferResponseDTO trf = this.transfers.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> (trf.getTransferDate() != null) ? this.dateFormatter.format(trf.getTransferDate()) : "-";
            case 2 -> (trf.getSourceWalletName() != null) ? trf.getSourceWalletName() : "-";
            case 3 -> (trf.getDestinationWalletName() != null) ? trf.getDestinationWalletName() : "-";
            case 4 -> formatCurrency(trf.getAmount());
            case 5 -> (trf.hasFee() && trf.getFeeAmount() != null && trf.getFeeAmount().compareTo(BigDecimal.ZERO) > 0)
                    ? formatCurrency(trf.getFeeAmount())
                    : "-";
            case 6 -> (trf.getNote() != null) ? trf.getNote() : "";
            default -> null;
        };
    }

    private String formatCurrency(BigDecimal amount) {
        BigDecimal safe = (amount != null) ? amount : BigDecimal.ZERO;
        return this.currencyFormatter.format(safe) + " VNĐ";
    }
}
