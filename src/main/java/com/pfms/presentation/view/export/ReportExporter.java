package com.pfms.presentation.view.export;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Ngữ cảnh (Context) quản lý và điều phối chiến lược xuất báo cáo tài chính
 * Cho phép hoán đổi linh hoạt các định dạng kết xuất dữ liệu tại thời điểm
 * chạy.
 */
public final class ReportExporter {

    // region State
    private ReportExportStrategy strategy;
    // endregion

    // region Constructors
    public ReportExporter(ReportExportStrategy strategy) {
        setStrategy(strategy);
    }
    // endregion

    // region Getters
    public ReportExportStrategy getStrategy() {
        return this.strategy;
    }
    // endregion

    // region Setters
    public void setStrategy(ReportExportStrategy strategy) {
        Preconditions.checkArgument(strategy != null, "ReportExportStrategy không được để null");
        this.strategy = strategy;
    }
    // endregion

    // region Operations
    public String exportCashFlow(CashFlowReportResponseDTO report) {
        return this.strategy.exportCashFlow(report);
    }

    public String exportSpendingStructure(SpendingStructureReportResponseDTO report) {
        return this.strategy.exportSpendingStructure(report);
    }
    // endregion
}
