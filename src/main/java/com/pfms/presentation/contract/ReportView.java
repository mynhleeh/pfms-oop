package com.pfms.presentation.contract;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Hợp đồng hiển thị cho phân hệ Báo cáo &amp; Thống kê Tài chính trong kiến trúc MVP Passive View.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface ReportView extends BaseView {

    void renderCashFlowReport(CashFlowReportResponseDTO report, String summaryText);

    void renderSpendingStructureReport(SpendingStructureReportResponseDTO report, String summaryText);

    void openExportDialog(String title, String content);

    default void openExportDialog() {
        openExportDialog("", "");
    }
}
