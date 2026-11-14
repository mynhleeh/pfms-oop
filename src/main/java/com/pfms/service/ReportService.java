package com.pfms.service;

import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Dịch vụ báo cáo tài chính. Cung cấp chức năng tổng hợp dòng tiền 12 tháng
 * liên tục và phân tích cơ cấu thu chi 2 cấp theo nhóm nhu cầu và danh mục
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
