package com.pfms.presentation.report.component;

import com.pfms.presentation.dto.report.NeedGroupShareResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng cơ cấu nhóm nhu cầu (SpendingStructureRowNode)")
class SpendingStructureRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng cơ cấu nhóm nhu cầu hiển thị đầy đủ tỷ trọng, số tiền hiện tại, kỳ trước và chênh lệch thuần số")
    void testSpendingStructureRowNodeDisplay() {
        JavaFxTestHelper.runOnFxThread(() -> {
            NeedGroupShareResponseDTO item = new NeedGroupShareResponseDTO(
                    UUID.randomUUID(),
                    "Nhu Cầu Thiết Yếu",
                    "ACTIVE",
                    new BigDecimal("2223590"),
                    new BigDecimal("45.8"),
                    new BigDecimal("3450000"),
                    new BigDecimal("-1226410"),
                    new BigDecimal("-35.5"),
                    false,
                    Collections.emptyList()
            );

            SpendingStructureRowNode node = new SpendingStructureRowNode(item);

            assertEquals("Nhu Cầu Thiết Yếu", node.getNameLabel().getText());
            assertEquals("45.8%", node.getShareLabel().getText());
            assertTrue(node.getCurrentAmountLabel().getText().contains("2,223,590"));
            assertTrue(node.getPrevAmountLabel().getText().contains("3,450,000"));
            assertTrue(node.getDiffLabel().getText().contains("1,226,410"));

            // Xác thực quy chuẩn màu sắc: hiện tại màu vàng (#E5C07B), kỳ trước màu trắng (#FAFAFA), chênh lệch âm màu đỏ (#FB7185)
            assertTrue(node.getCurrentAmountLabel().getStyle().contains("#E5C07B"));
            assertTrue(node.getPrevAmountLabel().getStyle().contains("#FAFAFA"));
            assertTrue(node.getDiffLabel().getStyle().contains("#FB7185"));
        });
    }

    @Test
    @DisplayName("Chênh lệch dương hiển thị màu xanh và tiền tố cộng")
    void testSpendingStructureWithPositiveDiff() {
        JavaFxTestHelper.runOnFxThread(() -> {
            NeedGroupShareResponseDTO item = new NeedGroupShareResponseDTO(
                    UUID.randomUUID(),
                    "Thu Nhập Khác",
                    "ACTIVE",
                    new BigDecimal("5000000"),
                    new BigDecimal("50.0"),
                    new BigDecimal("3000000"),
                    new BigDecimal("2000000"),
                    new BigDecimal("66.7"),
                    false,
                    Collections.emptyList()
            );

            SpendingStructureRowNode node = new SpendingStructureRowNode(item, "INCOME");

            assertTrue(node.getDiffLabel().getText().startsWith("+"));
            assertTrue(node.getDiffLabel().getStyle().contains("#4ADE80")); // Chênh lệch dương màu xanh
            assertTrue(node.getCurrentAmountLabel().getStyle().contains("#E5C07B")); // Kỳ này màu vàng
            assertTrue(node.getPrevAmountLabel().getStyle().contains("#FAFAFA")); // Kỳ trước màu trắng
        });
    }

    @Test
    @DisplayName("Kỳ trước rỗng hiển thị gạch ngang và chênh lệch 0")
    void testSpendingStructureWithZeroDiff() {
        JavaFxTestHelper.runOnFxThread(() -> {
            NeedGroupShareResponseDTO item = new NeedGroupShareResponseDTO(
                    UUID.randomUUID(),
                    "Đầu Tư",
                    new BigDecimal("5000000"),
                    new BigDecimal("25.0"),
                    Collections.emptyList()
            );

            SpendingStructureRowNode node = new SpendingStructureRowNode(item);

            assertEquals("Đầu Tư", node.getNameLabel().getText());
            assertEquals("25.0%", node.getShareLabel().getText());
            assertEquals("0 ₫", node.getDiffLabel().getText());
        });
    }
}
