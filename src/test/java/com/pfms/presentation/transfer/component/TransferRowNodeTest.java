package com.pfms.presentation.transfer.component;

import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử thành phần hiển thị dòng chuyển tiền (TransferRowNode)")
class TransferRowNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo dòng chuyển tiền không phụ phí hiển thị chính xác ví nguồn, ví đích và số tiền")
    void testTransferRowWithoutFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            TransferItemViewModel item = new TransferItemViewModel(
                    UUID.randomUUID(), 1, "16/11/2026", "Ví Tiền Mặt", "Ví MB Bank",
                    "1,000,000", new BigDecimal("1000000"), "Nạp tiền vào tài khoản",
                    new BigDecimal("4000000"), new BigDecimal("11000000"), false, ""
            );

            TransferRowNode node = new TransferRowNode(item, null);

            assertEquals("16/11/2026", node.getDateLabel().getText());
            assertEquals("Tiền Mặt", node.getFromWalletLabel().getText());
            assertEquals("MB Bank", node.getToWalletLabel().getText());
            assertEquals("1,000,000 ₫", node.getAmountLabel().getText());
            assertNull(node.getFeeBadge());
            assertEquals("Nạp tiền vào tài khoản", node.getNoteLabel().getText());
            assertEquals(52.0, node.getBtnDelete().getPrefWidth());
        });
    }

    @Test
    @DisplayName("Khởi tạo dòng chuyển tiền có phụ phí hiển thị fee badge hổ phách")
    void testTransferRowWithFee() {
        JavaFxTestHelper.runOnFxThread(() -> {
            TransferItemViewModel item = new TransferItemViewModel(
                    UUID.randomUUID(), 2, "16/11/2026", "MB Bank", "VPBank",
                    "5,000,000", new BigDecimal("5000000"), "Chuyển liên ngân hàng",
                    new BigDecimal("5000000"), new BigDecimal("5000000"), true, "5,500"
            );

            TransferRowNode node = new TransferRowNode(item, null);

            assertEquals("MB Bank", node.getFromWalletLabel().getText());
            assertEquals("VPBank", node.getToWalletLabel().getText());
            assertNotNull(node.getFeeBadge());
            assertEquals("⚡ Phí: 5,500 ₫", node.getFeeBadge().getText());
            assertTrue(node.getFeeBadge().getStyle().contains("#F59E0B"));
            assertEquals(Double.MAX_VALUE, node.getMaxWidth());
            assertEquals(0.0, node.getNoteLabel().getMinWidth());
        });
    }

    @Test
    @DisplayName("Nút Xóa trên dòng chuyển tiền kích hoạt callback onDelete")
    void testDeleteButtonTriggerCallback() {
        JavaFxTestHelper.runOnFxThread(() -> {
            AtomicBoolean deleteCalled = new AtomicBoolean(false);
            TransferItemViewModel item = new TransferItemViewModel(
                    UUID.randomUUID(), 3, "15/11/2026", null, null,
                    "500,000", new BigDecimal("500000"), null,
                    BigDecimal.ZERO, BigDecimal.ZERO, false, ""
            );

            TransferRowNode node = new TransferRowNode(item, vm -> deleteCalled.set(true));

            assertEquals("—", node.getFromWalletLabel().getText());
            assertEquals("—", node.getToWalletLabel().getText());
            assertEquals("—", node.getNoteLabel().getText());

            node.getBtnDelete().fire();
            assertTrue(deleteCalled.get());
        });
    }
}
