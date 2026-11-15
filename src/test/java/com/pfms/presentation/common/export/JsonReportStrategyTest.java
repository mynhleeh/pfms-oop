package com.pfms.presentation.common.export;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pfms.presentation.dto.report.CashFlowReportResponseDTO;
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

@DisplayName("Kiểm thử JsonReportStrategy - Chiến lược xuất báo cáo JSON")
class JsonReportStrategyTest {

    private JsonReportStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new JsonReportStrategy();
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
        @DisplayName("Xuất báo cáo dòng tiền rỗng thành chuỗi JSON hợp lệ")
        void exportCashFlow_EmptyReport_ReturnsValidJson() {
            CashFlowReportResponseDTO report = CashFlowReportResponseDTO.empty();

            String json = strategy.exportCashFlow(report);

            assertNotNull(json);
            assertFalse(json.isBlank());

            JsonElement parsed = JsonParser.parseString(json);
            assertTrue(parsed.isJsonObject());
            JsonObject obj = parsed.getAsJsonObject();

            assertTrue(obj.has("months"));
            assertTrue(obj.get("months").isJsonArray());
            assertEquals(0, obj.get("months").getAsJsonArray().size());
            assertEquals(0, obj.get("avgIncome").getAsBigDecimal().compareTo(BigDecimal.ZERO));
            assertEquals(0, obj.get("avgExpense").getAsBigDecimal().compareTo(BigDecimal.ZERO));
            assertEquals(0, obj.get("avgNetCashFlow").getAsBigDecimal().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Xuất báo cáo dòng tiền có dữ liệu đầy đủ thành chuỗi JSON")
        void exportCashFlow_WithData_ReturnsDetailedJson() {
            MonthlyCashFlowResponseDTO month1 = new MonthlyCashFlowResponseDTO(
                    "2026-01",
                    new BigDecimal("15000000"),
                    new BigDecimal("8000000"),
                    new BigDecimal("7000000"),
                    new BigDecimal("46.67")
            );
            MonthlyCashFlowResponseDTO month2 = new MonthlyCashFlowResponseDTO(
                    "2026-02",
                    new BigDecimal("20000000"),
                    new BigDecimal("12000000"),
                    new BigDecimal("8000000"),
                    new BigDecimal("40.00")
            );

            CashFlowReportResponseDTO report = new CashFlowReportResponseDTO(
                    List.of(month1, month2),
                    new BigDecimal("17500000"),
                    new BigDecimal("10000000"),
                    new BigDecimal("7500000")
            );

            String json = strategy.exportCashFlow(report);

            assertNotNull(json);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            assertEquals(2, obj.get("months").getAsJsonArray().size());

            JsonObject firstMonth = obj.get("months").getAsJsonArray().get(0).getAsJsonObject();
            assertEquals("2026-01", firstMonth.get("yearMonth").getAsString());
            assertEquals(0, firstMonth.get("totalIncome").getAsBigDecimal().compareTo(new BigDecimal("15000000")));
            assertEquals(0, firstMonth.get("savingsRate").getAsBigDecimal().compareTo(new BigDecimal("46.67")));
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
        @DisplayName("Xuất báo cáo cơ cấu thu chi chuẩn định dạng ISO-8601 cho ngày tháng")
        void exportSpendingStructure_WithGroups_ReturnsValidJsonWithIsoDates() {
            LocalDate startDate = LocalDate.of(2026, 1, 1);
            LocalDate endDate = LocalDate.of(2026, 1, 31);
            UUID groupId = UUID.randomUUID();

            NeedGroupShareResponseDTO group = new NeedGroupShareResponseDTO(
                    groupId,
                    "Thiết yếu",
                    "ACTIVE",
                    new BigDecimal("5000000"),
                    new BigDecimal("62.50"),
                    List.of()
            );

            SpendingStructureReportResponseDTO report = new SpendingStructureReportResponseDTO(
                    "EXPENSE",
                    startDate,
                    endDate,
                    new BigDecimal("8000000"),
                    List.of(group)
            );

            String json = strategy.exportSpendingStructure(report);

            assertNotNull(json);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            assertEquals("EXPENSE", obj.get("transactionType").getAsString());
            assertEquals("2026-01-01", obj.get("startDate").getAsString());
            assertEquals("2026-01-31", obj.get("endDate").getAsString());
            assertEquals(0, obj.get("totalAmount").getAsBigDecimal().compareTo(new BigDecimal("8000000")));
            assertEquals(1, obj.get("groups").getAsJsonArray().size());

            JsonObject firstGroup = obj.get("groups").getAsJsonArray().get(0).getAsJsonObject();
            assertEquals("Thiết yếu", firstGroup.get("name").getAsString());
            assertEquals("ACTIVE", firstGroup.get("status").getAsString());
        }
    }
}
