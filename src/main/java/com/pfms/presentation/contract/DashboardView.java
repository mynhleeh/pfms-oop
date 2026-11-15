package com.pfms.presentation.contract;

import com.pfms.presentation.viewmodel.dashboard.DashboardKpiViewModel;
import com.pfms.presentation.viewmodel.dashboard.RecentTransactionViewModel;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Bảng Điều Khiển Tổng Quan (Dashboard) trong kiến trúc MVP.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface DashboardView extends BaseView {

    void renderKpis(DashboardKpiViewModel kpis);

    void renderRecentTransactions(List<RecentTransactionViewModel> txns);

    void setCycleSubtitle(String text);
}
