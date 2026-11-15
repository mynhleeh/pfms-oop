package com.pfms.presentation.wallet.viewmodel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mô hình hiển thị dòng ví trên bảng danh sách trong kiến trúc MVP.
 */
public record WalletItemViewModel(
        UUID id,
        int displayOrder,
        String name,
        BigDecimal rawBalance,
        String formattedBalance,
        String statusText,
        String note,
        boolean isActive
) {}
