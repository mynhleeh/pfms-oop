package com.pfms.presentation.report.component;

import com.pfms.presentation.dto.report.MonthlyCashFlowResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng dòng tiền 12 tháng (CashFlowRowNode)")
class CashFlowRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng báo cáo dòng tiền hiển thị số định lượng chuẩn xác và không có chữ định tính")
    void testCashFlowRowNodeDisplay() {
        JavaFxTestHelper.runOnFxThread(() -> {
            MonthlyCashFlowResponseDTO item = new MonthlyCashFlowResponseDTO(
                    "2026-11",
                    new BigDecimal("27500000"),
                    new BigDecimal("4855000"),
                    new BigDecimal("22645000"),
                    new BigDecimal("82.3")
            );

            CashFlowRowNode node = new CashFlowRowNode(item);

            assertEquals("2026-11", node.getMonthLabel().getText());
            assertTrue(node.getIncomeLabel().getText().contains("27,500,000"));
            assertTrue(node.getIncomeLabel().getText().startsWith("+"));
            assertTrue(node.getExpenseLabel().getText().contains("4,855,000"));
            assertTrue(node.getExpenseLabel().getText().startsWith("-"));
            assertTrue(node.getNetLabel().getText().contains("22,645,000"));
            assertEquals("82.3%", node.getRateLabel().getText());
            // Kiểm tra không chứa các từ định tính
            assertFalse(node.getRateLabel().getText().contains("Tốt"));
            assertFalse(node.getRateLabel().getText().contains("Ổn định"));
        });
    }

    @Test
    @DisplayName("Dòng tiền thâm hụt hiển thị số âm đúng định dạng")
    void testDeficitCashFlowDisplay() {
        JavaFxTestHelper.runOnFxThread(() -> {
            MonthlyCashFlowResponseDTO item = new MonthlyCashFlowResponseDTO(
                    "2026-08",
                    new BigDecimal("10000000"),
                    new BigDecimal("15000000"),
                    new BigDecimal("-5000000"),
                    BigDecimal.ZERO
            );

            CashFlowRowNode node = new CashFlowRowNode(item);

            assertEquals("2026-08", node.getMonthLabel().getText());
            assertTrue(node.getNetLabel().getText().contains("5,000,000"));
            assertEquals("0.0%", node.getRateLabel().getText());
        });
    }
}
