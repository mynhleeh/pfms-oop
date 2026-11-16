package com.pfms.presentation.transaction.viewmodel;

import java.util.UUID;

/**
 * Mô hình dữ liệu hiển thị một dòng giao dịch thu chi trên bảng giao diện (UC-TRN-03).
 * Bản ghi bất biến (Java 17 Record) phục vụ kiến trúc MVP Passive View.
 */
public record TransactionItemViewModel(
        UUID id,
        int displayOrder,
        String dateFormatted,
        String groupName,
        String categoryName,
        String walletName,
        String formattedAmount,
        boolean isIncome,
        String note,
        String balanceAfterFormatted,
        boolean hasFee,
        String feeFormatted
) {
    /**
     * Constructor tương thích ngược cho mã nguồn và bài kiểm thử không truyền fee.
     */
    public TransactionItemViewModel(
            UUID id,
            int displayOrder,
            String dateFormatted,
            String groupName,
            String categoryName,
            String walletName,
            String formattedAmount,
            boolean isIncome,
            String note,
            String balanceAfterFormatted) {
        this(id, displayOrder, dateFormatted, groupName, categoryName, walletName, formattedAmount, isIncome, note, balanceAfterFormatted, false, "");
    }

    /**
     * Constructor tương thích ngược cho mã nguồn và bài kiểm thử không truyền groupName và fee.
     */
    public TransactionItemViewModel(
            UUID id,
            int displayOrder,
            String dateFormatted,
            String categoryName,
            String walletName,
            String formattedAmount,
            boolean isIncome,
            String note,
            String balanceAfterFormatted) {
        this(id, displayOrder, dateFormatted, "", categoryName, walletName, formattedAmount, isIncome, note, balanceAfterFormatted, false, "");
    }
}
