package com.pfms.presentation.viewmodel.dashboard;

/**
 * Mô hình dữ liệu hiển thị (ViewModel) cho 4 thẻ KPI tại Bảng Điều Khiển Tổng Quan.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public record DashboardKpiViewModel(
        String totalBalanceFormatted,
        String totalBalanceSubtext,
        String incomeFormatted,
        String incomeSubtext,
        String expenseFormatted,
        String expenseSubtext,
        String savingsRateFormatted,
        String savingsRateSubtext
) {
    public static DashboardKpiViewModel empty() {
        return new DashboardKpiViewModel(
                "0 VNĐ",
                "0 ví đang hoạt động",
                "0 VNĐ",
                "0 giao dịch thu",
                "0 VNĐ",
                "0.0% so với thu nhập",
                "0.0%",
                "Mục tiêu tích lũy > 30%"
        );
    }
}
