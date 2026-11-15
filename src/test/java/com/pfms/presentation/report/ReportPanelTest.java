package com.pfms.presentation.report;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử ReportPanel - Bảng điều khiển phân hệ báo cáo tài chính")
class ReportPanelTest {

    private ReportGUIController reportController;
    private ReportPanel panel;

    @BeforeEach
    void setUp() {
        reportController = Mockito.mock(ReportGUIController.class);

        MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO(
                "2026-10", new BigDecimal("20000000"), new BigDecimal("12000000"),
                new BigDecimal("8000000"), new BigDecimal("40.0"));

        CashFlowReportResponseDTO cashFlowReport = new CashFlowReportResponseDTO(
                List.of(m1), new BigDecimal("20000000"), new BigDecimal("12000000"), new BigDecimal("8000000"));

        NeedGroupShareResponseDTO g1 = new NeedGroupShareResponseDTO(
                UUID.randomUUID(), "Thiết yếu", "ACTIVE", new BigDecimal("10000000"),
                new BigDecimal("100.0"), Collections.emptyList());

        SpendingStructureReportResponseDTO structureReport = new SpendingStructureReportResponseDTO(
                "EXPENSE", LocalDate.now().minusDays(30), LocalDate.now(),
                new BigDecimal("10000000"), List.of(g1));

        when(reportController.generateCashFlowReport(anyInt(), anyInt())).thenReturn(cashFlowReport);
        when(reportController.generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString()))
                .thenReturn(structureReport);
        when(reportController.exportCashFlow(any())).thenReturn("SAMPLE CASH FLOW EXPORT");
        when(reportController.exportSpendingStructure(any())).thenReturn("SAMPLE SPENDING EXPORT");

        panel = new ReportPanel(reportController);
        panel.setMessageConsumer(msg -> {});
        panel.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo bảng điều khiển nạp sẵn dữ liệu cho cả 2 tab và hiển thị tóm tắt chính xác")
    void testInitialDataLoaded() {
        assertNotNull(panel.getTabbedPane());
        assertEquals(2, panel.getTabbedPane().getTabCount());

        assertEquals(1, panel.getCashFlowTableModel().getRowCount());
        assertEquals("2026-10", panel.getCashFlowTableModel().getValueAt(0, 0));
        assertTrue(panel.getCashFlowSummaryText().contains("Thu nhập BQ: 20,000,000 VNĐ"));

        assertEquals(1, panel.getSpendingStructureTableModel().getRowCount());
        assertEquals("Thiết yếu", panel.getSpendingStructureTableModel().getValueAt(0, 0));
        assertTrue(panel.getStructureSummaryText().contains("Tổng số tiền: 10,000,000 VNĐ"));
    }

    @Test
    @DisplayName("Làm mới báo cáo gọi lại các hàm tạo báo cáo của ReportGUIController")
    void testReloadReportsCallsController() {
        panel.reloadReports();
        verify(reportController, atLeastOnce()).generateCashFlowReport(anyInt(), anyInt());
        verify(reportController, atLeastOnce()).generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString());
    }

    @Test
    @DisplayName("Ném NullPointerException khi ReportGUIController bị null")
    void testNullControllerThrowsException() {
        assertThrows(NullPointerException.class, () -> new ReportPanel(null));
    }

    @Test
    @DisplayName("Hộp chọn định dạng xuất báo cáo khởi tạo đầy đủ các tùy chọn văn bản và JSON")
    void testFormatBoxesInitialization() {
        assertNotNull(panel.getCashFlowFormatBox());
        assertEquals(2, panel.getCashFlowFormatBox().getItemCount());
        assertEquals("Bảng văn bản (.txt)", panel.getCashFlowFormatBox().getItemAt(0));
        assertEquals("Định dạng JSON (.json)", panel.getCashFlowFormatBox().getItemAt(1));
        assertEquals(0, panel.getCashFlowFormatBox().getSelectedIndex());

        assertNotNull(panel.getSpendingStructureFormatBox());
        assertEquals(2, panel.getSpendingStructureFormatBox().getItemCount());
        assertEquals("Bảng văn bản (.txt)", panel.getSpendingStructureFormatBox().getItemAt(0));
        assertEquals("Định dạng JSON (.json)", panel.getSpendingStructureFormatBox().getItemAt(1));
        assertEquals(0, panel.getSpendingStructureFormatBox().getSelectedIndex());

        panel.getCashFlowFormatBox().setSelectedIndex(1);
        assertEquals(1, panel.getCashFlowFormatBox().getSelectedIndex());
    }
}
