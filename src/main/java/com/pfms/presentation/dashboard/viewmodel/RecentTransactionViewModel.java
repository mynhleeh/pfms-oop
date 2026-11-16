package com.pfms.presentation.dashboard.viewmodel;

import java.util.UUID;

/**
 * Mô hình dữ liệu hiển thị (ViewModel) cho từng dòng giao dịch gần nhất tại Dashboard.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public record RecentTransactionViewModel(
        UUID id,
        String dateFormatted,
        String type,
        String groupName,
        String categoryName,
        String walletName,
        String amountFormatted,
        boolean isIncome,
        String note,
        boolean hasFee,
        String feeFormatted
) {
    /**
     * Constructor tương thích ngược 8 tham số cho mã nguồn và kiểm thử hiện hữu.
     */
    public RecentTransactionViewModel(
            UUID id,
            String dateFormatted,
            String type,
            String categoryName,
            String walletName,
            String amountFormatted,
            boolean isIncome,
            String note
    ) {
        this(id, dateFormatted, type, "", categoryName, walletName, amountFormatted, isIncome, note, false, "");
    }
}
