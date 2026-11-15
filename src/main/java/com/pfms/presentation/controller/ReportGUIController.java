package com.pfms.presentation.controller;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.presentation.view.export.ReportExportStrategy;
import com.pfms.presentation.view.export.ReportExporter;
import com.pfms.presentation.view.export.TextTableReportStrategy;
import com.pfms.service.ReportService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Báo cáo &amp; Thống kê tài chính (REP).
 * Tiếp nhận yêu cầu báo cáo dòng tiền và cơ cấu chi tiêu, tích hợp
 * ReportExporter (GoF Strategy) để kết xuất dữ liệu.
 */
public final class ReportGUIController {

    // region State
    private final ReportService reportService;
    private final ReportExporter reportExporter;
    // endregion

    // region Constructors
    public ReportGUIController(ReportService reportService, ReportExporter reportExporter) {
        this.reportService = Objects.requireNonNull(reportService, "ReportService không được để null");
        this.reportExporter = Objects.requireNonNull(reportExporter, "ReportExporter không được để null");
    }

    public ReportGUIController(ReportService reportService) {
        this(reportService, new ReportExporter(new TextTableReportStrategy()));
    }
    // endregion

    // region Operations
    // region Cash Flow Report
    public CashFlowReportResponseDTO generateCashFlowReport(int endMonth, int endYear) {
        if (endMonth < 1 || endMonth > 12) {
            throw new IllegalArgumentException("Tháng kết thúc phải từ 1 đến 12");
        }
        if (endYear < 1900 || endYear > 2100) {
            throw new IllegalArgumentException("Năm kết thúc phải từ 1900 đến 2100");
        }
        return this.reportService.generateCashFlowReport(new CashFlowReportRequestDTO(endMonth, endYear));
    }

    public CashFlowReportResponseDTO generateCashFlowReport(String rawEndMonth, String rawEndYear) {
        boolean monthBlank = (rawEndMonth == null || rawEndMonth.isBlank());
        boolean yearBlank = (rawEndYear == null || rawEndYear.isBlank());

        if (monthBlank && yearBlank) {
            return generateCashFlowReport();
        }

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        if (!monthBlank) {
            try {
                month = Integer.parseInt(rawEndMonth.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Tháng kết thúc phải là số nguyên hợp lệ", ex);
            }
        }

        if (!yearBlank) {
            try {
                year = Integer.parseInt(rawEndYear.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Năm kết thúc phải là số nguyên hợp lệ", ex);
            }
        }

        return generateCashFlowReport(month, year);
    }

    public CashFlowReportResponseDTO generateCashFlowReport() {
        return this.reportService.generateCashFlowReport(CashFlowReportRequestDTO.now());
    }
    // endregion

    // region Spending Structure Report
    public SpendingStructureReportResponseDTO generateSpendingStructureReport(
            LocalDate startDate, LocalDate endDate, String transactionType) {

        Preconditions.requireValidDateRange(startDate, endDate);
        return this.reportService.generateSpendingStructureReport(
                new SpendingStructureReportRequestDTO(startDate, endDate, transactionType));
    }

    public SpendingStructureReportResponseDTO generateSpendingStructureReport(
            String rawStartDate, String rawEndDate, String rawTransactionType) {

        LocalDate startDate = InputValidator.parseOptionalDate(rawStartDate, "Ngày bắt đầu");
        LocalDate endDate = InputValidator.parseOptionalDate(rawEndDate, "Ngày kết thúc");
        Preconditions.requireValidDateRange(startDate, endDate);
        String type = InputValidator.validateTransactionType(rawTransactionType, "EXPENSE");

        return this.reportService.generateSpendingStructureReport(
                new SpendingStructureReportRequestDTO(startDate, endDate, type));
    }

    public SpendingStructureReportResponseDTO generateSpendingStructureReport() {
        return this.reportService.generateSpendingStructureReport(new SpendingStructureReportRequestDTO());
    }
    // endregion

    // region Export Report
    public void setExportStrategy(ReportExportStrategy strategy) {
        Preconditions.checkArgument(strategy != null, "ReportExportStrategy không được để null");
        this.reportExporter.setStrategy(strategy);
    }

    public ReportExporter getReportExporter() {
        return this.reportExporter;
    }

    public String exportCashFlow(CashFlowReportResponseDTO report) {
        return this.reportExporter.exportCashFlow(report);
    }

    public String exportCashFlow(CashFlowReportResponseDTO report, ReportExportStrategy strategy) {
        setExportStrategy(strategy);
        return this.reportExporter.exportCashFlow(report);
    }

    public String exportSpendingStructure(SpendingStructureReportResponseDTO report) {
        return this.reportExporter.exportSpendingStructure(report);
    }

    public String exportSpendingStructure(SpendingStructureReportResponseDTO report, ReportExportStrategy strategy) {
        setExportStrategy(strategy);
        return this.reportExporter.exportSpendingStructure(report);
    }
    // endregion
    // endregion
}
