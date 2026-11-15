package com.pfms.presentation.common.export;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Chiến lược định dạng và xuất báo cáo tài chính theo mẫu thiết kế GoF Strategy.
 * Hỗ trợ chuyển đổi DTO báo cáo sang các định dạng hiển thị hoặc lưu trữ khác nhau.
 */
public interface ReportExportStrategy {

    // region Operations
    String exportCashFlow(CashFlowReportResponseDTO report);

    String exportSpendingStructure(SpendingStructureReportResponseDTO report);
    // endregion
}
