package com.pfms.service;

import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;

/**
 * Giao diện dịch vụ ứng dụng điều phối các ca sử dụng Báo cáo và Thống kê Tài chính (ReportService).
 * <p>
 * Dịch vụ vận hành hoàn toàn ở chế độ chỉ đọc (Read-only), chịu trách nhiệm truy xuất dữ liệu từ các
 * kho lưu trữ để tổng hợp chuỗi thời gian dòng tiền 12 tháng liên tục (UC-REP-01) và phân tích cơ cấu
 * phân bổ thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết (UC-REP-02).
 * </p>
 */
public interface ReportService {

    // region Queries

    /**
     * Lập báo cáo tổng hợp dòng tiền trong chuỗi 12 tháng liên tục lùi tính từ mốc tháng/năm chỉ định (UC-REP-01).
     *
     * @param request DTO chứa tháng và năm kết thúc kỳ thống kê
     * @return {@link CashFlowReportResponseDTO} chứa số liệu 12 tháng và các chỉ số bình quân năm
     * @throws com.pfms.domain.exception.ValidationException nếu tháng hoặc năm không hợp lệ
     * @throws com.pfms.domain.exception.BusinessException   nếu có lỗi vi phạm quy tắc nghiệp vụ
     */
    CashFlowReportResponseDTO generateCashFlowReport(CashFlowReportRequestDTO request);

    /**
     * Phương thức tiện ích lập báo cáo dòng tiền theo tháng và năm kết thúc.
     *
     * @param endMonth tháng kết thúc (1 - 12)
     * @param endYear  năm kết thúc (>= 2000)
     * @return {@link CashFlowReportResponseDTO} chứa số liệu 12 tháng và các chỉ số bình quân năm
     */
    default CashFlowReportResponseDTO generateCashFlowReport(int endMonth, int endYear) {
        return generateCashFlowReport(new CashFlowReportRequestDTO(endMonth, endYear));
    }

    /**
     * Lập báo cáo phân tích cơ cấu thu chi 2 cấp theo nhóm nhu cầu và danh mục chi tiết (UC-REP-02).
     *
     * @param request DTO chứa khoảng ngày thống kê và loại luồng tiền (EXPENSE hoặc INCOME)
     * @return {@link SpendingStructureReportResponseDTO} chứa bảng cơ cấu 2 cấp và tỷ trọng phần trăm
     * @throws com.pfms.domain.exception.ValidationException nếu ngày bắt đầu lớn hơn ngày kết thúc
     * @throws com.pfms.domain.exception.BusinessException   nếu có lỗi vi phạm quy tắc nghiệp vụ
     */
    SpendingStructureReportResponseDTO generateSpendingStructureReport(SpendingStructureRequestDTO request);

    // endregion
}

