package com.pfms.presentation.report;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử ReportPresenter - Bộ điều phối phân hệ Báo cáo Tài chính MVP")
class ReportPresenterTest {

    private ReportView view;
    private ReportGUIController controller;
    private ReportPresenter presenter;

    @BeforeEach
    void setUp() {
        this.view = Mockito.mock(ReportView.class);
        this.controller = Mockito.mock(ReportGUIController.class);
        this.presenter = new ReportPresenter(this.view, this.controller);
    }

    @Test
    @DisplayName("Ném NullPointerException khi tham số constructor bị null")
    void testConstructorValidation() {
        assertThrows(NullPointerException.class, () -> new ReportPresenter(null, this.controller));
        assertThrows(NullPointerException.class, () -> new ReportPresenter(this.view, null));
    }

    @Test
    @DisplayName("loadCashFlowReport nạp dữ liệu thành công và định dạng summary chính xác")
    void testLoadCashFlowReportSuccess() {
        MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO(
                "2026-10", new BigDecimal("20000000"), new BigDecimal("12000000"),
                new BigDecimal("8000000"), new BigDecimal("40.0"));
        CashFlowReportResponseDTO report = new CashFlowReportResponseDTO(
                List.of(m1), new BigDecimal("20000000"), new BigDecimal("12000000"), new BigDecimal("8000000"));

        when(this.controller.generateCashFlowReport(10, 2026)).thenReturn(report);

        this.presenter.loadCashFlowReport(10, 2026);

        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.view).renderCashFlowReport(eq(report), summaryCaptor.capture());

        String summary = summaryCaptor.getValue();
        assertTrue(summary.contains("Thu nhập BQ: 20,000,000 VNĐ"));
        assertTrue(summary.contains("Chi tiêu BQ: 12,000,000 VNĐ"));
        assertTrue(summary.contains("Tiết kiệm BQ: 8,000,000 VNĐ"));
        assertSame(report, this.presenter.getCurrentCashFlowReport());
    }

    @Test
    @DisplayName("loadCashFlowReport nhận kết quả null hiển thị tóm tắt mặc định")
    void testLoadCashFlowReportNull() {
        when(this.controller.generateCashFlowReport(anyInt(), anyInt())).thenReturn(null);

        this.presenter.loadCashFlowReport(5, 2026);

        verify(this.view).renderCashFlowReport(null, "Thu nhập BQ: 0 VNĐ | Chi tiêu BQ: 0 VNĐ | Tiết kiệm BQ: 0 VNĐ");
    }

    @Test
    @DisplayName("loadCashFlowReport xử lý ngoại lệ và hiển thị thông báo lỗi")
    void testLoadCashFlowReportException() {
        when(this.controller.generateCashFlowReport(anyInt(), anyInt())).thenThrow(new RuntimeException("Lỗi máy chủ"));

        this.presenter.loadCashFlowReport(5, 2026);

        verify(this.view).showError(contains("Không thể tạo báo cáo dòng tiền: Lỗi máy chủ"));
    }

    @Test
    @DisplayName("loadSpendingStructureReport nạp dữ liệu cơ cấu thành công và định dạng summary")
    void testLoadSpendingStructureReportSuccess() {
        NeedGroupShareResponseDTO g1 = new NeedGroupShareResponseDTO(
                UUID.randomUUID(), "Thiết yếu", "ACTIVE", new BigDecimal("10000000"),
                new BigDecimal("100.0"), Collections.emptyList());
        SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                "EXPENSE", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                new BigDecimal("10000000"), List.of(g1));

        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), eq("EXPENSE"))).thenReturn(report);

        this.presenter.loadSpendingStructureReport(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), "EXPENSE");

        ArgumentCaptor<String> summaryCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.view).renderSpendingStructureReport(eq(report), summaryCaptor.capture());

        String summary = summaryCaptor.getValue();
        assertTrue(summary.contains("Tổng số tiền: 10,000,000 VNĐ"));
        assertSame(report, this.presenter.getCurrentStructureReport());
    }

    @Test
    @DisplayName("loadSpendingStructureReport nhận null hiển thị tóm tắt mặc định")
    void testLoadSpendingStructureReportNull() {
        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString())).thenReturn(null);

        this.presenter.loadSpendingStructureReport(null, null, "EXPENSE");

        verify(this.view).renderSpendingStructureReport(null, "Tổng số tiền: 0 VNĐ");
    }

    @Test
    @DisplayName("loadSpendingStructureReport xử lý ngoại lệ và hiển thị thông báo lỗi")
    void testLoadSpendingStructureReportException() {
        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenThrow(new IllegalArgumentException("Ngày không hợp lệ"));

        this.presenter.loadSpendingStructureReport(LocalDate.now(), LocalDate.now(), "EXPENSE");

        verify(this.view).showError(contains("Không thể tạo báo cáo cơ cấu: Ngày không hợp lệ"));
    }

    @Test
    @DisplayName("reloadReports gọi cả hai phương thức nạp báo cáo")
    void testReloadReports() {
        when(this.controller.generateCashFlowReport(anyInt(), anyInt())).thenReturn(null);
        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString())).thenReturn(null);

        this.presenter.reloadReports(11, 2026, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15), "EXPENSE");

        verify(this.controller).generateCashFlowReport(11, 2026);
        verify(this.controller).generateSpendingStructureReport(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15), "EXPENSE");
    }

    @Test
    @DisplayName("onExportCashFlowClicked cảnh báo khi chưa nạp báo cáo")
    void testExportCashFlowWithoutReport() {
        this.presenter.onExportCashFlowClicked();

        verify(this.view).showError("Vui lòng nạp báo cáo dòng tiền trước khi xuất!");
        verify(this.controller, never()).exportCashFlow(any());
    }

    @Test
    @DisplayName("onExportCashFlowClicked xuất thành công và mở hộp thoại")
    void testExportCashFlowSuccess() {
        CashFlowReportResponseDTO report = new CashFlowReportResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        when(this.controller.generateCashFlowReport(10, 2026)).thenReturn(report);
        when(this.controller.exportCashFlow(report)).thenReturn("EXPORT TEXT CONTENT");

        this.presenter.loadCashFlowReport(10, 2026);
        this.presenter.onExportCashFlowClicked();

        verify(this.controller).exportCashFlow(report);
        verify(this.view).openExportDialog(eq("Xuất Báo cáo Dòng tiền"), eq("EXPORT TEXT CONTENT"));
    }

    @Test
    @DisplayName("onExportCashFlowClicked bắt ngoại lệ và báo lỗi")
    void testExportCashFlowException() {
        CashFlowReportResponseDTO report = new CashFlowReportResponseDTO(
                Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        when(this.controller.generateCashFlowReport(10, 2026)).thenReturn(report);
        when(this.controller.exportCashFlow(report)).thenThrow(new RuntimeException("IO Error"));

        this.presenter.loadCashFlowReport(10, 2026);
        this.presenter.onExportCashFlowClicked();

        verify(this.view).showError(contains("Xuất báo cáo thất bại: IO Error"));
    }

    @Test
    @DisplayName("onExportSpendingStructureClicked cảnh báo khi chưa nạp báo cáo")
    void testExportSpendingStructureWithoutReport() {
        this.presenter.onExportSpendingStructureClicked();

        verify(this.view).showError("Vui lòng nạp báo cáo cơ cấu trước khi xuất!");
        verify(this.controller, never()).exportSpendingStructure(any());
    }

    @Test
    @DisplayName("onExportSpendingStructureClicked xuất thành công và mở hộp thoại")
    void testExportSpendingStructureSuccess() {
        SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                "EXPENSE", LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, Collections.emptyList());
        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString())).thenReturn(report);
        when(this.controller.exportSpendingStructure(report)).thenReturn("STRUCTURE EXPORT TEXT");

        this.presenter.loadSpendingStructureReport(LocalDate.now(), LocalDate.now(), "EXPENSE");
        this.presenter.onExportSpendingStructureClicked();

        verify(this.controller).exportSpendingStructure(report);
        verify(this.view).openExportDialog(eq("Xuất Báo cáo Cơ cấu Thu Chi"), eq("STRUCTURE EXPORT TEXT"));
    }

    @Test
    @DisplayName("onExportSpendingStructureClicked bắt ngoại lệ và báo lỗi")
    void testExportSpendingStructureException() {
        SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                "EXPENSE", LocalDate.now(), LocalDate.now(), BigDecimal.ZERO, Collections.emptyList());
        when(this.controller.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString())).thenReturn(report);
        when(this.controller.exportSpendingStructure(report)).thenThrow(new RuntimeException("Render error"));

        this.presenter.loadSpendingStructureReport(LocalDate.now(), LocalDate.now(), "EXPENSE");
        this.presenter.onExportSpendingStructureClicked();

        verify(this.view).showError(contains("Xuất báo cáo thất bại: Render error"));
    }
}
