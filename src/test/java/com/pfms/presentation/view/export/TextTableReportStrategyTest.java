package com.pfms.presentation.view.export;

import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
import com.pfms.presentation.dto.report.CategoryShareResponseDTO;
import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.presentation.dto.report.SpendingStructureReportResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử TextTableReportStrategy - Chiến lược xuất báo cáo tệp văn bản dạng bảng")
class TextTableReportStrategyTest {

    private TextTableReportStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TextTableReportStrategy();
    }

    @Nested
    @DisplayName("Kiểm thử exportCashFlow")
    class ExportCashFlowTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi DTO báo cáo dòng tiền là null")
        void exportCashFlow_NullReport_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> strategy.exportCashFlow(null)
            );
            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Xuất báo cáo dòng tiền rỗng vẫn bảo đảm cấu trúc tiêu đề và các chỉ số bình quân")
        void exportCashFlow_EmptyReport_RendersHeaderAndAverages() {
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();

            String output = strategy.exportCashFlow(report);

            assertNotNull(output);
            assertTrue(output.contains("BÁO CÁO CHUỖI DÒNG TIỀN"));
            assertTrue(output.contains("Tháng"));
            assertTrue(output.contains("Thu nhập"));
            assertTrue(output.contains("Chi tiêu"));
            assertTrue(output.contains("Dòng tiền thuần"));
            assertTrue(output.contains("Tỷ lệ tiết kiệm"));
            assertTrue(output.contains("Bình quân thu:"));
            assertTrue(output.contains("0 VNĐ"));
        }

        @Test
        @DisplayName("Xuất báo cáo dòng tiền có dữ liệu định dạng phân tách hàng nghìn và phần trăm chuẩn")
        void exportCashFlow_WithData_RendersFormattedColumnsAndValues() {
            MonthlyCashFlowResponseDTO m1 = new MonthlyCashFlowResponseDTO(
                    "2026-01",
                    new BigDecimal("15000000"),
                    new BigDecimal("8000000"),
                    new BigDecimal("7000000"),
                    new BigDecimal("46.67")
            );
            MonthlyCashFlowResponseDTO m2 = new MonthlyCashFlowResponseDTO(
                    "2026-02",
                    new BigDecimal("20000000"),
                    new BigDecimal("12000000"),
                    new BigDecimal("8000000"),
                    new BigDecimal("40.00")
            );

            CashFlowReportResponseDTO report = new CashFlowReportResponseDTO(
                    List.of(m1, m2),
                    new BigDecimal("17500000"),
                    new BigDecimal("10000000"),
                    new BigDecimal("7500000")
            );

            String output = strategy.exportCashFlow(report);

            assertNotNull(output);
            assertTrue(output.contains("2026-01"));
            assertTrue(output.contains("15,000,000 VNĐ"));
            assertTrue(output.contains("8,000,000 VNĐ"));
            assertTrue(output.contains("7,000,000 VNĐ"));
            assertTrue(output.contains("46.67%"));

            assertTrue(output.contains("2026-02"));
            assertTrue(output.contains("20,000,000 VNĐ"));
            assertTrue(output.contains("12,000,000 VNĐ"));
            assertTrue(output.contains("8,000,000 VNĐ"));
            assertTrue(output.contains("40.00%"));

            assertTrue(output.contains("17,500,000 VNĐ"));
            assertTrue(output.contains("10,000,000 VNĐ"));
            assertTrue(output.contains("7,500,000 VNĐ"));
        }
    }

    @Nested
    @DisplayName("Kiểm thử exportSpendingStructure")
    class ExportSpendingStructureTests {

        @Test
        @DisplayName("Ném IllegalArgumentException khi DTO cơ cấu thu chi là null")
        void exportSpendingStructure_NullReport_ThrowsIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> strategy.exportSpendingStructure(null)
            );
            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Xuất báo cáo cơ cấu thu chi 2 cấp có nhãn trạng thái và định dạng tỷ trọng")
        void exportSpendingStructure_WithHierarchy_RendersTwoLevelsAndStatus() {
            LocalDate startDate = LocalDate.of(2026, 1, 1);
            LocalDate endDate = LocalDate.of(2026, 1, 31);

            CategoryShareResponseDTO c1 = new CategoryShareResponseDTO(
                    UUID.randomUUID(),
                    "Ăn uống",
                    "ACTIVE",
                    new BigDecimal("4000000"),
                    new BigDecimal("50.00"),
                    new BigDecimal("80.00"),
                    new BigDecimal("3500000"),
                    new BigDecimal("500000"),
                    new BigDecimal("14.29"),
                    false
            );

            CategoryShareResponseDTO c2 = new CategoryShareResponseDTO(
                    UUID.randomUUID(),
                    "Cà phê cũ",
                    "DELETED",
                    new BigDecimal("1000000"),
                    new BigDecimal("12.50"),
                    new BigDecimal("20.00"),
                    BigDecimal.ZERO,
                    new BigDecimal("1000000"),
                    BigDecimal.ZERO,
                    true
            );

            NeedGroupShareResponseDTO group = new NeedGroupShareResponseDTO(
                    UUID.randomUUID(),
                    "Thiết yếu",
                    "ACTIVE",
                    new BigDecimal("5000000"),
                    new BigDecimal("62.50"),
                    List.of(c1, c2)
            );

            SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                    "EXPENSE",
                    startDate,
                    endDate,
                    new BigDecimal("8000000"),
                    new BigDecimal("7000000"),
                    new BigDecimal("1000000"),
                    new BigDecimal("14.29"),
                    false,
                    List.of(group)
            );

            String output = strategy.exportSpendingStructure(report);

            assertNotNull(output);
            assertTrue(output.contains("BÁO CÁO CƠ CẤU THU CHI"));
            assertTrue(output.contains("CHI TIÊU"));
            assertTrue(output.contains("Từ ngày: 2026-01-01 đến ngày: 2026-01-31"));
            assertTrue(output.contains("Tổng số tiền: 8,000,000 VNĐ"));

            assertTrue(output.contains("Thiết yếu"));
            assertTrue(output.contains("5,000,000 VNĐ"));
            assertTrue(output.contains("62.50%"));

            assertTrue(output.contains("Ăn uống"));
            assertTrue(output.contains("4,000,000 VNĐ"));
            assertTrue(output.contains("50.00%"));

            assertTrue(output.contains("Cà phê cũ"));
            assertTrue(output.contains("(đã xóa)"));
            assertTrue(output.contains("[Mới]"));
            assertTrue(output.contains("1,000,000 VNĐ"));
        }
    }
}
