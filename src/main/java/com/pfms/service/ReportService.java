package com.pfms.service;

import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Dịch vụ báo cáo và thống kê tài chính cá nhân.
 * Cung cấp dữ liệu dòng tiền 12 tháng liên tục và phân tích cơ cấu thu chi theo nhóm nhu cầu.
 */
public interface ReportService {

    // region Queries

    CashFlowReportResponseDTO generateCashFlowReport(CashFlowReportRequestDTO request);

    default CashFlowReportResponseDTO generateCashFlowReport(int endMonth, int endYear) {
        return generateCashFlowReport(new CashFlowReportRequestDTO(endMonth, endYear));
    }

    SpendingStructureReportResponseDTO generateSpendingStructureReport(SpendingStructureRequestDTO request);

    // endregion
}
