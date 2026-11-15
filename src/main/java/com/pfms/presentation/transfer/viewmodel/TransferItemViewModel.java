package com.pfms.presentation.transfer.viewmodel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mô hình dữ liệu hiển thị một dòng chuyển tiền trên giao diện bảng (UC-TRF-02).
 * Bản ghi bất biến (Java 17 Record) phục vụ kiến trúc MVP Passive View.
 */
public record TransferItemViewModel(
        UUID id,
        int displayOrder,
        String dateFormatted,
        String fromWalletName,
        String toWalletName,
        String formattedAmount,
        BigDecimal rawAmount,
        String note,
        BigDecimal fromBalanceAfter,
        BigDecimal toBalanceAfter
) {
}
