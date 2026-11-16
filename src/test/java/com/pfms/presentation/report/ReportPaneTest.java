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
    @DisplayName("Khởi tạo ReportPane nạp thành công 2 bảng dữ liệu dòng tiền, cơ cấu và hiển thị tóm tắt")
    void testInitialDataLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(pane.getTabPane());
            assertEquals(2, pane.getTabPane().getTabs().size());

            assertEquals(1, pane.getCashFlowTableData().size());
            assertEquals("2026-10", pane.getCashFlowTableData().get(0).getYearMonth());
            assertEquals(1, pane.getCashFlowStreamBox().getChildren().size());
            assertTrue(pane.getCashFlowSummaryText().contains("Thu nhập BQ: 20,000,000 VNĐ"));

            assertEquals(1, pane.getStructureTableData().size());
            assertEquals("Thiết yếu", pane.getStructureTableData().get(0).getName());
            assertEquals(1, pane.getStructureStreamBox().getChildren().size());
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

    @Test
    @DisplayName("Hàng thẻ KPI hiển thị chuẩn xác số liệu định lượng và tự động cập nhật khi chuyển đổi Tab")
    void testKpiOverviewCardsRenderingAndTabSwitch() {
        JavaFxTestHelper.runOnFxThread(() -> {
            // Khi ở Tab 0 (Báo cáo Dòng tiền 12 tháng)
            assertEquals(0, pane.getTabPane().getSelectionModel().getSelectedIndex());
            assertEquals("THU NHẬP BÌNH QUÂN (12T)", pane.getKpiLabel1().getText());
            assertTrue(pane.getKpiValue1().getText().contains("20,000,000"));
            assertEquals("CHI TIÊU BÌNH QUÂN (12T)", pane.getKpiLabel2().getText());
            assertTrue(pane.getKpiValue2().getText().contains("12,000,000"));
            assertEquals("TIẾT KIỆM BÌNH QUÂN (12T)", pane.getKpiLabel3().getText());
            assertTrue(pane.getKpiValue3().getText().contains("8,000,000"));

            // Chuyển sang Tab 1 (Cơ cấu Thu Chi)
            pane.getTabPane().getSelectionModel().select(1);
            assertEquals("TỔNG SỐ TIỀN KỲ NÀY", pane.getKpiLabel1().getText());
            assertTrue(pane.getKpiValue1().getText().contains("10,000,000"));
            assertTrue(pane.getKpiValue1().getStyleClass().contains("kpi-value-gold"));
            assertEquals("KỲ SO SÁNH LIỀN TRƯỚC", pane.getKpiLabel2().getText());
            assertTrue(pane.getKpiValue2().getStyleClass().contains("kpi-value-white"));
            assertEquals("CHÊNH LỆCH SO VỚI KỲ TRƯỚC", pane.getKpiLabel3().getText());

            // Chuyển ngược lại Tab 0
            pane.getTabPane().getSelectionModel().select(0);
            assertEquals("THU NHẬP BÌNH QUÂN (12T)", pane.getKpiLabel1().getText());
            assertTrue(pane.getKpiValue1().getText().contains("20,000,000"));
        });
    }

    @Test
    @DisplayName("Các nút Quick Preset khoảng thời gian cập nhật đúng DatePicker và gọi lại loadStructure")
    void testQuickDatePresets() {
        JavaFxTestHelper.runOnFxThread(() -> {
            LocalDate now = LocalDate.now();

            // Nhấn chip "Năm nay"
            pane.getChipYear().fire();
            assertEquals(LocalDate.of(now.getYear(), 1, 1), pane.getStartDatePicker().getValue());
            assertEquals(now, pane.getEndDatePicker().getValue());
            assertTrue(pane.getChipYear().getStyleClass().contains("quick-chip-active"));

            // Nhấn chip "Tháng này"
            pane.getChipMonth().fire();
            assertEquals(now.withDayOfMonth(1), pane.getStartDatePicker().getValue());
            assertEquals(now, pane.getEndDatePicker().getValue());
            assertTrue(pane.getChipMonth().getStyleClass().contains("quick-chip-active"));
            assertFalse(pane.getChipYear().getStyleClass().contains("quick-chip-active"));
        });
    }

    @Test
    @DisplayName("Nút chuyển đổi phân loại (EXPENSE / INCOME) đồng bộ chính xác với typeBox")
    void testTypeSwitcherButtons() {
        JavaFxTestHelper.runOnFxThread(() -> {
            // Mặc định là EXPENSE
            assertEquals("EXPENSE", pane.getTypeBox().getValue());
            assertTrue(pane.getBtnTypeExpense().getStyleClass().contains("type-switch-active"));
            assertFalse(pane.getBtnTypeIncome().getStyleClass().contains("type-switch-active"));

            // Nhấn nút INCOME
            pane.getBtnTypeIncome().fire();
            assertEquals("INCOME", pane.getTypeBox().getValue());
            assertTrue(pane.getBtnTypeIncome().getStyleClass().contains("type-switch-active"));
            assertFalse(pane.getBtnTypeExpense().getStyleClass().contains("type-switch-active"));

            // Nhấn lại EXPENSE
            pane.getBtnTypeExpense().fire();
            assertEquals("EXPENSE", pane.getTypeBox().getValue());
            assertTrue(pane.getBtnTypeExpense().getStyleClass().contains("type-switch-active"));
        });
    }
}
