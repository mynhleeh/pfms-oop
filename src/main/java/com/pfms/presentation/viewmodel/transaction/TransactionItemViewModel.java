package com.pfms.presentation.viewmodel.transaction;

import java.util.UUID;

/**
 * Mô hình dữ liệu hiển thị một dòng giao dịch thu chi trên bảng giao diện (UC-TRN-03).
 * Bản ghi bất biến (Java 17 Record) phục vụ kiến trúc MVP Passive View.
 */
public record TransactionItemViewModel(
        UUID id,
        int displayOrder,
        String dateFormatted,
        String categoryName,
        String walletName,
        String formattedAmount,
        boolean isIncome,
        String note,
        String balanceAfterFormatted
) {
}
