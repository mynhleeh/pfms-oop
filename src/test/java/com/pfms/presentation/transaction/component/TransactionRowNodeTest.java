package com.pfms.presentation.transaction.component;

import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng giao dịch phẳng (TransactionRowNode)")
class TransactionRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng giao dịch Thu nhập không phụ phí hiển thị chính xác các thành phần")
    void testIncomeTransactionWithoutFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            TransactionItemViewModel item = new TransactionItemViewModel(
                    UUID.randomUUID(), 1, "16/11/2026", "Thu Nhập Định Kỳ",
                    "Lương Chính", "Ví MB Bank", "25,000,000", true,
                    "Lương tháng 11", "30,000,000", false, ""
            );

            TransactionRowNode node = new TransactionRowNode(item, null, null);

            assertEquals("16/11/2026", node.getDateLabel().getText());
            assertEquals("+25,000,000 ₫", node.getAmountLabel().getText());
            assertTrue(node.getAmountLabel().getStyle().contains("-color-income"));
            assertNull(node.getFeeBadge());
            assertEquals("MB Bank", node.getWalletLabel().getText());
            assertEquals("Lương Chính", node.getCategoryLabel().getText());
            assertEquals("Lương tháng 11", node.getNoteLabel().getText());
            assertNotNull(node.getBtnEdit());
            assertNotNull(node.getBtnDelete());
        });
    }

    @Test
    @DisplayName("Khởi tạo dòng giao dịch Chi tiêu có phụ phí hiển thị huy hiệu phụ phí màu hổ phách")
    void testExpenseTransactionWithFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            TransactionItemViewModel item = new TransactionItemViewModel(
                    UUID.randomUUID(), 2, "16/11/2026", "Nhu Cầu Thiết Yếu",
                    "Ăn Uống", "Ví VPBank", "1,250,000", false,
                    "Mua WinMart", "5,000,000", true, "25,000"
            );

            TransactionRowNode node = new TransactionRowNode(item, null, null);

            assertEquals("-1,250,000 ₫", node.getAmountLabel().getText());
            assertTrue(node.getAmountLabel().getStyle().contains("-color-expense"));
            assertNotNull(node.getFeeBadge());
            assertEquals("⚡ Phí: 25,000 ₫", node.getFeeBadge().getText());
            assertTrue(node.getFeeBadge().getStyle().contains("#F59E0B"));
            assertEquals("VPBank", node.getWalletLabel().getText());
        });
    }

    @Test
    @DisplayName("Nút Sửa và Nút Xóa kích hoạt chính xác Consumer callback truyền vào")
    void testActionButtonsTriggerCallbacks() {
        JavaFxTestHelper.runOnFxThread(() -> {
            AtomicBoolean editTriggered = new AtomicBoolean(false);
            AtomicBoolean deleteTriggered = new AtomicBoolean(false);

            TransactionItemViewModel item = new TransactionItemViewModel(
                    UUID.randomUUID(), 3, "15/11/2026", "",
                    "Cà Phê", "Tiền Mặt", "65,000", false,
                    "", "4,000,000", false, ""
            );

            TransactionRowNode node = new TransactionRowNode(
                    item,
                    vm -> editTriggered.set(true),
                    vm -> deleteTriggered.set(true)
            );

            assertEquals("—", node.getNoteLabel().getText());
            assertEquals(0.0, node.getNoteLabel().getMinWidth());
            assertEquals("Tiền Mặt", node.getWalletLabel().getText());
            assertEquals(52.0, node.getBtnEdit().getPrefWidth());
            assertEquals(52.0, node.getBtnDelete().getPrefWidth());
            assertEquals(Double.MAX_VALUE, node.getMaxWidth());

            node.getBtnEdit().fire();
            assertTrue(editTriggered.get());

            node.getBtnDelete().fire();
            assertTrue(deleteTriggered.get());
        });
    }
}
