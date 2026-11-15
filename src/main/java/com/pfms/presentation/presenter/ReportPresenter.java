package com.pfms.presentation.presenter;

import com.pfms.presentation.contract.ReportView;
import com.pfms.presentation.controller.ReportGUIController;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.view.common.ViewUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

/**
 * Bộ điều phối giao diện phân hệ Báo cáo &amp; Thống kê Tài chính theo mô hình MVP.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public class ReportPresenter {

    // region Fields
    private final ReportView view;
    private final ReportGUIController controller;
    private CashFlowReportResponseDTO currentCashFlowReport;
    private SpendingStructureReportResponseDTO currentStructureReport;
    // endregion

    // region Constructors
    public ReportPresenter(ReportView view, ReportGUIController controller) {
        this.view = Objects.requireNonNull(view, "ReportView không được để null");
        this.controller = Objects.requireNonNull(controller, "ReportGUIController không được để null");
    }
    // endregion

    // region Actions
    public void onExportCashFlowClicked() {
        if (this.currentCashFlowReport == null) {
            this.view.showError("Vui lòng nạp báo cáo dòng tiền trước khi xuất!");
            return;
        }
        try {
            String content = this.controller.exportCashFlow(this.currentCashFlowReport);
            this.view.openExportDialog("Xuất Báo cáo Dòng tiền", content);
        } catch (Exception ex) {
            this.view.showError("Xuất báo cáo thất bại: " + ex.getMessage());
        }
    }

    public void onExportSpendingStructureClicked() {
        if (this.currentStructureReport == null) {
            this.view.showError("Vui lòng nạp báo cáo cơ cấu trước khi xuất!");
            return;
        }
        try {
            String content = this.controller.exportSpendingStructure(this.currentStructureReport);
            this.view.openExportDialog("Xuất Báo cáo Cơ cấu Thu Chi", content);
        } catch (Exception ex) {
            this.view.showError("Xuất báo cáo thất bại: " + ex.getMessage());
        }
    }
    // endregion

    // region Data
    public void loadCashFlowReport(int endMonth, int endYear) {
        try {
            this.currentCashFlowReport = this.controller.generateCashFlowReport(endMonth, endYear);
            if (this.currentCashFlowReport != null) {
                String avgInc = ViewUtils.formatAmount(this.currentCashFlowReport.getAvgIncome());
                String avgExp = ViewUtils.formatAmount(this.currentCashFlowReport.getAvgExpense());
                String avgNet = ViewUtils.formatAmount(this.currentCashFlowReport.getAvgNetCashFlow());
                String summary = String.format(
                        "Thu nhập BQ: %s VNĐ | Chi tiêu BQ: %s VNĐ | Tiết kiệm BQ: %s VNĐ",
                        avgInc, avgExp, avgNet);
                this.view.renderCashFlowReport(this.currentCashFlowReport, summary);
            } else {
                this.view.renderCashFlowReport(null, "Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");
            }
        } catch (Exception ex) {
            this.view.showError("Không thể tạo báo cáo dòng tiền: " + ex.getMessage());
        }
    }

    public void loadSpendingStructureReport(LocalDate startDate, LocalDate endDate, String type) {
        try {
            LocalDate start = (startDate != null) ? startDate : LocalDate.now().withDayOfMonth(1);
            LocalDate end = (endDate != null) ? endDate : LocalDate.now();
            this.currentStructureReport = this.controller.generateSpendingStructureReport(start, end, type);
            if (this.currentStructureReport != null) {
                String total = ViewUtils.formatAmount(this.currentStructureReport.getTotalAmount());
                String prev = ViewUtils.formatAmount(this.currentStructureReport.getPreviousTotalAmount());
                String diff = ViewUtils.formatAmount(this.currentStructureReport.getDifferenceAmount());
                BigDecimal pct = this.currentStructureReport.getPercentageChange() != null
                        ? this.currentStructureReport.getPercentageChange() : BigDecimal.ZERO;
                String summary = String.format(Locale.US,
                        "Tổng số tiền: %s VNĐ | Kỳ trước: %s VNĐ | Chênh lệch: %s VNĐ (%.1f%%)",
                        total, prev, diff, pct);
                this.view.renderSpendingStructureReport(this.currentStructureReport, summary);
            } else {
                this.view.renderSpendingStructureReport(null, "Tổng số tiền: 0 VNĐ");
            }
        } catch (Exception ex) {
            this.view.showError("Không thể tạo báo cáo cơ cấu: " + ex.getMessage());
        }
    }

    public void reloadReports(int endMonth, int endYear, LocalDate startDate, LocalDate endDate, String type) {
        loadCashFlowReport(endMonth, endYear);
        loadSpendingStructureReport(startDate, endDate, type);
    }
    // endregion

    // region Getters
    public ReportView getView() { return this.view; }
    public ReportGUIController getController() { return this.controller; }
    public CashFlowReportResponseDTO getCurrentCashFlowReport() { return this.currentCashFlowReport; }
    public SpendingStructureReportResponseDTO getCurrentStructureReport() { return this.currentStructureReport; }
    // endregion

    // region Helpers
    // endregion
}
