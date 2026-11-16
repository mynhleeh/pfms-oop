package com.pfms.presentation.dashboard.component;

import com.pfms.presentation.dashboard.viewmodel.RecentTransactionViewModel;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng giao dịch gần nhất Dashboard (DashboardTransactionRowNode)")
class DashboardTransactionRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng giao dịch Thu nhập hiển thị chính xác các thành phần và nhãn Thu Nhập")
    void testIncomeTransactionWithoutFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            RecentTransactionViewModel item = new RecentTransactionViewModel(
                    UUID.randomUUID(), "16/11/2026", "INCOME", "Thu Nhập Định Kỳ",
                    "Lương Chính", "Ví MB Bank", "+25,000,000", true,
                    "Lương tháng 11", false, ""
            );

            DashboardTransactionRowNode node = new DashboardTransactionRowNode(item);

            assertEquals("16/11/2026", node.getDateLabel().getText());
            assertEquals("+25,000,000 ₫", node.getAmountLabel().getText());
            assertTrue(node.getAmountLabel().getStyle().contains("#4ADE80"));
            assertNull(node.getFeeBadge());
            assertEquals("MB Bank", node.getWalletLabel().getText());
            assertEquals("Lương Chính", node.getCategoryLabel().getText());
            assertEquals("Lương tháng 11", node.getNoteLabel().getText());
            assertNotNull(node.getTypeBadgeBox());
            assertFalse(node.getTypeBadgeBox().getChildren().isEmpty());
        });
    }

    @Test
    @DisplayName("Khởi tạo dòng giao dịch Chi tiêu có phụ phí hiển thị fee badge hổ phách và nhãn Chi Tiêu")
    void testExpenseTransactionWithFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            RecentTransactionViewModel item = new RecentTransactionViewModel(
                    UUID.randomUUID(), "16/11/2026", "EXPENSE", "Nhu Cầu Thiết Yếu",
                    "Ăn Uống", "Ví VPBank", "-1,250,000", false,
                    "Mua WinMart", true, "25,000"
            );

            DashboardTransactionRowNode node = new DashboardTransactionRowNode(item);

            assertEquals("-1,250,000 ₫", node.getAmountLabel().getText());
            assertTrue(node.getAmountLabel().getStyle().contains("#FB7185"));
            assertNotNull(node.getFeeBadge());
            assertEquals("⚡ Phí: 25,000 ₫", node.getFeeBadge().getText());
            assertTrue(node.getFeeBadge().getStyle().contains("#F59E0B"));
            assertEquals("VPBank", node.getWalletLabel().getText());
            assertEquals(Double.MAX_VALUE, node.getMaxWidth());
            assertEquals(0.0, node.getNoteLabel().getMinWidth());
        });
    }

    @Test
    @DisplayName("Khởi tạo với thông tin thiếu ghi chú và nhóm nhu cầu thì hiển thị gạch ngang an toàn")
    void testFallbackDashForEmptyFields() {
        JavaFxTestHelper.runOnFxThread(() -> {
            RecentTransactionViewModel item = new RecentTransactionViewModel(
                    UUID.randomUUID(), null, "INCOME", "",
                    null, null, "500,000", true,
                    null, false, null
            );

            DashboardTransactionRowNode node = new DashboardTransactionRowNode(item);

            assertEquals("—", node.getDateLabel().getText());
            assertEquals("—", node.getWalletLabel().getText());
            assertEquals("—", node.getCategoryLabel().getText());
            assertEquals("—", node.getNoteLabel().getText());
            assertEquals("500,000 ₫", node.getAmountLabel().getText());
        });
    }
}
