package com.pfms.presentation.report;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử ReportPane - Bảng điều khiển phân hệ Báo cáo Tài chính JavaFX")
class ReportPaneTest {

    private ReportGUIController reportController;
    private ReportPane pane;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

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

        JavaFxTestHelper.runOnFxThread(() -> {
            pane = new ReportPane(reportController);
        });
    }

    @Test
    @DisplayName("Khởi tạo ReportPane nạp thành công dữ liệu BarChart, PieChart và 2 bảng dữ liệu")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getTabPane());
            assertEquals(2, pane.getTabPane().getTabs().size());

            assertEquals(1, pane.getCashFlowTableData().size());
            assertEquals("2026-10", pane.getCashFlowTableData().get(0).getYearMonth());
            assertEquals(3, pane.getCashFlowChart().getData().size()); // 3 series: Thu, Chi, Net
            assertTrue(pane.getCashFlowSummaryText().contains("Thu nhập BQ: 20,000,000 VNĐ"));

            assertEquals(1, pane.getStructureTableData().size());
            assertEquals("Thiết yếu", pane.getStructureTableData().get(0).getName());
            assertEquals(1, pane.getStructurePieChart().getData().size());
            assertTrue(pane.getStructureSummaryText().contains("Tổng số tiền: 10,000,000 VNĐ"));
        });
    }

    @Test
    @DisplayName("Làm mới báo cáo gọi lại controller sinh báo cáo tài chính")
    void testReloadReportsCallsController() {
        JavaFxTestHelper.runOnFxThread(() -> {
            pane.reloadReports();
        });
        verify(reportController, atLeastOnce()).generateCashFlowReport(anyInt(), anyInt());
        verify(reportController, atLeastOnce()).generateSpendingStructureReport(any(LocalDate.class), any(LocalDate.class), anyString());
    }

    @Test
    @DisplayName("Các hàm showMessage, showWarning, showError chuyển tiếp chính xác đến consumer")
    void testNotificationConsumers() {
        AtomicReference<String> msgRef = new AtomicReference<>();
        AtomicReference<String> warnRef = new AtomicReference<>();
        AtomicReference<String> errRef = new AtomicReference<>();

        pane.setMessageConsumer(msgRef::set);
        pane.setWarningConsumer(warnRef::set);
        pane.setErrorConsumer(errRef::set);

        pane.showMessage("Xuất thành công");
        assertEquals("Xuất thành công", msgRef.get());

        pane.showWarning("Thời gian không hợp lệ");
        assertEquals("Thời gian không hợp lệ", warnRef.get());

        pane.showError("Lỗi hệ thống");
        assertEquals("Lỗi hệ thống", errRef.get());
    }
}
