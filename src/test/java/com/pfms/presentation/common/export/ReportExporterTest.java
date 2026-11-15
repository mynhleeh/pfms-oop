package com.pfms.presentation.common.export;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử ReportExporter - Ngữ cảnh xuất báo cáo GoF Strategy")
class ReportExporterTest {

    @Nested
    @DisplayName("Kiểm thử Khởi tạo và Thiết lập Chiến lược")
    class LifecycleTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi khởi tạo với strategy là null")
        void constructor_NullStrategy_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new ReportExporter(null)
            );
            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi gọi setStrategy với null")
        void setStrategy_NullStrategy_ThrowsIllegalArgumentException() {
            ReportExportStrategy mockStrategy = Mockito.mock(ReportExportStrategy.class);
            ReportExporter exporter = new ReportExporter(mockStrategy);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> exporter.setStrategy(null)
            );
            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Lưu giữ đúng thể hiện strategy được tiêm vào")
        void constructorAndGetter_ValidStrategy_ReturnsSameInstance() {
            ReportExportStrategy mockStrategy = Mockito.mock(ReportExportStrategy.class);
            ReportExporter exporter = new ReportExporter(mockStrategy);

            assertSame(mockStrategy, exporter.getStrategy());
        }
    }

    @Nested
    @DisplayName("Kiểm thử Ủy quyền Thực thi")
    class DelegationTests {

        @Test
        @DisplayName("Ủy quyền xuất báo cáo dòng tiền cho strategy hiện hành")
        void exportCashFlow_DelegatesToStrategy() {
            ReportExportStrategy mockStrategy = Mockito.mock(ReportExportStrategy.class);
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();
            Mockito.when(mockStrategy.exportCashFlow(report)).thenReturn("MOCK_CASH_FLOW");

            ReportExporter exporter = new ReportExporter(mockStrategy);
            String result = exporter.exportCashFlow(report);

            assertEquals("MOCK_CASH_FLOW", result);
            Mockito.verify(mockStrategy, Mockito.times(1)).exportCashFlow(report);
        }

        @Test
        @DisplayName("Ủy quyền xuất báo cáo cơ cấu thu chi cho strategy hiện hành")
        void exportSpendingStructure_DelegatesToStrategy() {
            ReportExportStrategy mockStrategy = Mockito.mock(ReportExportStrategy.class);
            SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                    "EXPENSE",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 31),
                    BigDecimal.TEN,
                    List.of()
            );
            Mockito.when(mockStrategy.exportSpendingStructure(report)).thenReturn("MOCK_SPENDING_STRUCTURE");

            ReportExporter exporter = new ReportExporter(mockStrategy);
            String result = exporter.exportSpendingStructure(report);

            assertEquals("MOCK_SPENDING_STRUCTURE", result);
            Mockito.verify(mockStrategy, Mockito.times(1)).exportSpendingStructure(report);
        }
    }

    @Nested
    @DisplayName("Kiểm thử Tráo đổi Chiến lược Động (Dynamic Strategy Switching)")
    class DynamicSwitchingTests {

        @Test
        @DisplayName("Chuyển đổi linh hoạt giữa JsonReportStrategy và TextTableReportStrategy tại runtime")
        void setStrategy_DynamicSwitching_SwitchesOutputFormatAtRuntime() {
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();

            ReportExporter exporter = new ReportExporter(new JsonReportStrategy());
            String jsonOutput = exporter.exportCashFlow(report);
            assertNotNull(jsonOutput);
            assertTrue(jsonOutput.contains("\"months\""));
            assertTrue(jsonOutput.contains("\"avgIncome\""));

            // Hoán đổi chiến lược sang TextTableReportStrategy
            exporter.setStrategy(new TextTableReportStrategy());
            String textOutput = exporter.exportCashFlow(report);
            assertNotNull(textOutput);
            assertTrue(textOutput.contains("BÁO CÁO CHUỖI DÒNG TIỀN"));
            assertTrue(textOutput.contains("Tháng"));
            assertFalse(textOutput.contains("\"months\""));
        }
    }
}
