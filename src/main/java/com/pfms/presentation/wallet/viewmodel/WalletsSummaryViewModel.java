package com.pfms.presentation.wallet.viewmodel;

import java.math.BigDecimal;

/**
 * Mô hình hiển thị thanh tóm tắt số liệu tổng quan phân hệ ví trong kiến trúc MVP.
 */
public record WalletsSummaryViewModel(
        BigDecimal rawTotalBalance,
        String formattedTotalBalance,
        int activeCount,
        int archivedCount
) {}
