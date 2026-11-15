package com.pfms.presentation.report;

import com.pfms.presentation.dto.report.CashFlowReportRequestDTO;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportRequestDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import com.pfms.presentation.common.export.JsonReportStrategy;
import com.pfms.presentation.common.export.ReportExportStrategy;
import com.pfms.presentation.common.export.ReportExporter;
import com.pfms.presentation.common.export.TextTableReportStrategy;
import com.pfms.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử ReportGUIController - Bộ điều phối giao diện phân hệ Báo cáo")
class ReportGUIControllerTest {

    private ReportService reportService;
    private ReportExporter reportExporter;
    private ReportGUIController controller;

    @BeforeEach
    void setUp() {
        reportService = Mockito.mock(ReportService.class);
        reportExporter = Mockito.mock(ReportExporter.class);
        controller = new ReportGUIController(reportService, reportExporter);
    }

    @Nested
    @DisplayName("Kiểm thử Khởi tạo (Constructor)")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi ReportService là null")
        void constructor_NullService_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new ReportGUIController(null, reportExporter));
        }

        @Test
        @DisplayName("Ném NullPointerException khi ReportExporter là null")
        void constructor_NullExporter_ThrowsNullPointerException() {
            assertThrows(NullPointerException.class, () -> new ReportGUIController(reportService, null));
        }

        @Test
        @DisplayName("Khởi tạo thành công với 1 tham số dịch vụ và tự động tạo default exporter TextTableReportStrategy")
        void constructor_OneArg_InstantiatesWithDefaultExporter() {
            ReportGUIController singleArgController = new ReportGUIController(reportService);
            assertNotNull(singleArgController.getReportExporter());
            assertTrue(singleArgController.getReportExporter().getStrategy() instanceof TextTableReportStrategy);
        }
    }

    @Nested
    @DisplayName("Kiểm thử generateCashFlowReport (UC-REP-01)")
    class CashFlowReportTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi tháng kết thúc ngoài khoảng 1 đến 12")
        void generateCashFlowReport_InvalidMonth_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport(0, 2026));
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport(13, 2026));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi năm kết thúc ngoài khoảng hợp lệ")
        void generateCashFlowReport_InvalidYear_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport(11, 1800));
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport(11, 2200));
        }

        @Test
        @DisplayName("Tạo báo cáo dòng tiền với tháng năm kiểu số nguyên hợp lệ")
        void generateCashFlowReport_ValidInts_DelegatesToService() {
            CashFlowReportResponseDTO expected = CashFlowReportResponseDTO.empty();
            when(reportService.generateCashFlowReport(any())).thenReturn(expected);

            CashFlowReportResponseDTO result = controller.generateCashFlowReport(11, 2026);

            assertSame(expected, result);
            ArgumentCaptor<CashFlowReportRequestDTO> captor = ArgumentCaptor.forClass(CashFlowReportRequestDTO.class);
            verify(reportService).generateCashFlowReport(captor.capture());
            CashFlowReportRequestDTO request = captor.getValue();
            assertEquals(11, request.getEndMonth());
            assertEquals(2026, request.getEndYear());
        }

        @Test
        @DisplayName("Tạo báo cáo dòng tiền với tham số chuỗi thô hợp lệ")
        void generateCashFlowReport_ValidRawStrings_DelegatesToService() {
            CashFlowReportResponseDTO expected = CashFlowReportResponseDTO.empty();
            when(reportService.generateCashFlowReport(any())).thenReturn(expected);

            CashFlowReportResponseDTO result = controller.generateCashFlowReport(" 11 ", " 2026 ");

            assertSame(expected, result);
            ArgumentCaptor<CashFlowReportRequestDTO> captor = ArgumentCaptor.forClass(CashFlowReportRequestDTO.class);
            verify(reportService).generateCashFlowReport(captor.capture());
            CashFlowReportRequestDTO request = captor.getValue();
            assertEquals(11, request.getEndMonth());
            assertEquals(2026, request.getEndYear());
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tham số chuỗi tháng năm không phải số")
        void generateCashFlowReport_InvalidRawStrings_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport("tháng 11", "2026"));
            assertThrows(IllegalArgumentException.class, () -> controller.generateCashFlowReport("11", "năm 2026"));
        }

        @Test
        @DisplayName("Tạo báo cáo dòng tiền không tham số mặc định lấy tháng năm hiện tại")
        void generateCashFlowReport_NoArgs_DelegatesToServiceWithCurrentMonthYear() {
            CashFlowReportResponseDTO expected = CashFlowReportResponseDTO.empty();
            when(reportService.generateCashFlowReport(any())).thenReturn(expected);

            CashFlowReportResponseDTO result = controller.generateCashFlowReport();

            assertSame(expected, result);
            ArgumentCaptor<CashFlowReportRequestDTO> captor = ArgumentCaptor.forClass(CashFlowReportRequestDTO.class);
            verify(reportService).generateCashFlowReport(captor.capture());
            CashFlowReportRequestDTO request = captor.getValue();
            LocalDate now = LocalDate.now();
            assertEquals(now.getMonthValue(), request.getEndMonth());
            assertEquals(now.getYear(), request.getEndYear());
        }

        @Test
        @DisplayName("Tạo báo cáo dòng tiền với chuỗi rỗng mặc định lấy tháng năm hiện tại")
        void generateCashFlowReport_BlankRawStrings_DefaultsToCurrentMonthYear() {
            CashFlowReportResponseDTO expected = CashFlowReportResponseDTO.empty();
            when(reportService.generateCashFlowReport(any())).thenReturn(expected);

            CashFlowReportResponseDTO result = controller.generateCashFlowReport("", "  ");

            assertSame(expected, result);
            ArgumentCaptor<CashFlowReportRequestDTO> captor = ArgumentCaptor.forClass(CashFlowReportRequestDTO.class);
            verify(reportService).generateCashFlowReport(captor.capture());
            CashFlowReportRequestDTO request = captor.getValue();
            LocalDate now = LocalDate.now();
            assertEquals(now.getMonthValue(), request.getEndMonth());
            assertEquals(now.getYear(), request.getEndYear());
        }
    }

    @Nested
    @DisplayName("Kiểm thử generateSpendingStructureReport (UC-REP-02)")
    class SpendingStructureReportTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi loại giao dịch không phải INCOME hoặc EXPENSE")
        void generateSpendingStructureReport_InvalidType_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.generateSpendingStructureReport(
                            "01/11/2026", "30/11/2026", "TRANSFER"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi định dạng ngày bắt đầu hoặc kết thúc không hợp lệ")
        void generateSpendingStructureReport_InvalidDate_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> controller.generateSpendingStructureReport(
                            "sai-ngay", "30/11/2026", "EXPENSE"));
            assertThrows(IllegalArgumentException.class,
                    () -> controller.generateSpendingStructureReport(
                            "01/11/2026", "31/02/2026", "EXPENSE"));
        }

        @Test
        @DisplayName("Tạo báo cáo cơ cấu chi tiêu với ngày và loại giao dịch hợp lệ")
        void generateSpendingStructureReport_ValidLocalDate_DelegatesToService() {
            SpendingStructureReportResponseDTO expected = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
            when(reportService.generateSpendingStructureReport(any())).thenReturn(expected);

            SpendingStructureReportResponseDTO result = controller.generateSpendingStructureReport(
                    LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), "EXPENSE");

            assertSame(expected, result);
            ArgumentCaptor<SpendingStructureReportRequestDTO> captor = ArgumentCaptor.forClass(SpendingStructureReportRequestDTO.class);
            verify(reportService).generateSpendingStructureReport(captor.capture());
            SpendingStructureReportRequestDTO request = captor.getValue();
            assertEquals(LocalDate.of(2026, 11, 1), request.getStartDate());
            assertEquals(LocalDate.of(2026, 11, 30), request.getEndDate());
            assertEquals("EXPENSE", request.getTransactionType());
        }

        @Test
        @DisplayName("Tạo báo cáo cơ cấu chi tiêu với chuỗi ngày thô được chuẩn hóa và kiểm thực")
        void generateSpendingStructureReport_ValidRawStrings_DelegatesToService() {
            SpendingStructureReportResponseDTO expected = SpendingStructureReportResponseDTO.empty(
                    "INCOME", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
            when(reportService.generateSpendingStructureReport(any())).thenReturn(expected);

            SpendingStructureReportResponseDTO result = controller.generateSpendingStructureReport(
                    " 01/11/2026 ", " 30/11/2026 ", " income ");

            assertSame(expected, result);
            ArgumentCaptor<SpendingStructureReportRequestDTO> captor = ArgumentCaptor.forClass(SpendingStructureReportRequestDTO.class);
            verify(reportService).generateSpendingStructureReport(captor.capture());
            SpendingStructureReportRequestDTO request = captor.getValue();
            assertEquals(LocalDate.of(2026, 11, 1), request.getStartDate());
            assertEquals(LocalDate.of(2026, 11, 30), request.getEndDate());
            assertEquals("INCOME", request.getTransactionType());
        }

        @Test
        @DisplayName("Tạo báo cáo cơ cấu chi tiêu không tham số mặc định lấy tháng hiện hành và loại EXPENSE")
        void generateSpendingStructureReport_NoArgs_DelegatesToServiceWithDefaults() {
            SpendingStructureReportResponseDTO expected = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", LocalDate.now().withDayOfMonth(1), LocalDate.now());
            when(reportService.generateSpendingStructureReport(any())).thenReturn(expected);

            SpendingStructureReportResponseDTO result = controller.generateSpendingStructureReport();

            assertSame(expected, result);
            ArgumentCaptor<SpendingStructureReportRequestDTO> captor = ArgumentCaptor.forClass(SpendingStructureReportRequestDTO.class);
            verify(reportService).generateSpendingStructureReport(captor.capture());
            SpendingStructureReportRequestDTO request = captor.getValue();
            assertEquals("EXPENSE", request.getTransactionType());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Tích hợp Xuất Báo cáo (GoF Strategy Export Integration)")
    class ExportIntegrationTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi thiết lập ReportExportStrategy là null")
        void setExportStrategy_NullStrategy_ThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> controller.setExportStrategy(null));
        }

        @Test
        @DisplayName("Hoán đổi chiến lược xuất báo cáo thành công")
        void setExportStrategy_ValidStrategy_UpdatesExporter() {
            ReportExportStrategy jsonStrategy = new JsonReportStrategy();
            controller.setExportStrategy(jsonStrategy);
            verify(reportExporter).setStrategy(jsonStrategy);
        }

        @Test
        @DisplayName("Xuất báo cáo dòng tiền ủy quyền cho reportExporter")
        void exportCashFlow_DelegatesToExporter() {
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();
            when(reportExporter.exportCashFlow(report)).thenReturn("exported-cash-flow");

            String result = controller.exportCashFlow(report);

            assertEquals("exported-cash-flow", result);
            verify(reportExporter).exportCashFlow(report);
        }

        @Test
        @DisplayName("Xuất báo cáo dòng tiền với chiến lược cụ thể tự động cập nhật chiến lược trước khi xuất")
        void exportCashFlow_WithStrategy_SetsStrategyAndExports() {
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();
            ReportExportStrategy jsonStrategy = new JsonReportStrategy();
            when(reportExporter.exportCashFlow(report)).thenReturn("{\"avgIncome\": 0}");

            String result = controller.exportCashFlow(report, jsonStrategy);

            assertEquals("{\"avgIncome\": 0}", result);
            verify(reportExporter).setStrategy(jsonStrategy);
            verify(reportExporter).exportCashFlow(report);
        }

        @Test
        @DisplayName("Xuất báo cáo cơ cấu chi tiêu ủy quyền cho reportExporter")
        void exportSpendingStructure_DelegatesToExporter() {
            SpendingStructureReportResponseDTO report = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", LocalDate.now(), LocalDate.now());
            when(reportExporter.exportSpendingStructure(report)).thenReturn("exported-structure");

            String result = controller.exportSpendingStructure(report);

            assertEquals("exported-structure", result);
            verify(reportExporter).exportSpendingStructure(report);
        }

        @Test
        @DisplayName("Xuất báo cáo cơ cấu chi tiêu với chiến lược cụ thể tự động cập nhật chiến lược trước khi xuất")
        void exportSpendingStructure_WithStrategy_SetsStrategyAndExports() {
            SpendingStructureReportResponseDTO report = SpendingStructureReportResponseDTO.empty(
                    "EXPENSE", LocalDate.now(), LocalDate.now());
            ReportExportStrategy jsonStrategy = new JsonReportStrategy();
            when(reportExporter.exportSpendingStructure(report)).thenReturn("{\"totalAmount\": 0}");

            String result = controller.exportSpendingStructure(report, jsonStrategy);

            assertEquals("{\"totalAmount\": 0}", result);
            verify(reportExporter).setStrategy(jsonStrategy);
            verify(reportExporter).exportSpendingStructure(report);
        }
    }
}
