package com.pfms.presentation.wallet.component;

import com.pfms.presentation.wallet.viewmodel.WalletItemViewModel;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử WalletCardNode Component")
class WalletCardNodeTest {

    @BeforeAll
    static void initFx() {
        JavaFxTestHelper.initPlatform();
    }

    @Test
    @DisplayName("Khởi tạo WalletCardNode hiển thị đúng tên ví và số dư")
    void testWalletCardNodeRendering() {
        JavaFxTestHelper.runOnFxThread(() -> {
            WalletItemViewModel vm = new WalletItemViewModel(
                    UUID.randomUUID(), 1, "Ví Tiền Mặt",
                    new BigDecimal("5000000"), "5,000,000 VNĐ",
                    "Đang hoạt động", "Chi tiêu hàng ngày", true, true);

            WalletCardNode card = new WalletCardNode(vm,
                    item -> {}, item -> {}, item -> {}, item -> {}, item -> {});

            assertNotNull(card);
            assertEquals(vm, card.getItem());
            assertTrue(card.getStyleClass().contains("wallet-card"));
        });
    }

    @Test
    @DisplayName("Kích hoạt sự kiện nút bấm trên thẻ ví thành công")
    void testWalletCardNodeActions() {
        JavaFxTestHelper.runOnFxThread(() -> {
            WalletItemViewModel vm = new WalletItemViewModel(
                    UUID.randomUUID(), 1, "Tài khoản VCB",
                    new BigDecimal("20000000"), "20,000,000 VNĐ",
                    "Đang hoạt động", "Lương công ty", true, false);

            AtomicBoolean adjustClicked = new AtomicBoolean(false);
            AtomicBoolean editClicked = new AtomicBoolean(false);
            AtomicBoolean toggleClicked = new AtomicBoolean(false);
            AtomicBoolean deleteClicked = new AtomicBoolean(false);
            AtomicBoolean defaultClicked = new AtomicBoolean(false);

            WalletCardNode card = new WalletCardNode(vm,
                    item -> adjustClicked.set(true),
                    item -> editClicked.set(true),
                    item -> toggleClicked.set(true),
                    item -> deleteClicked.set(true));

            card.triggerAdjustAction();
            assertTrue(adjustClicked.get(), "Nút điều chỉnh số dư phải được kích hoạt");

            card.triggerEditAction();
            assertTrue(editClicked.get(), "Nút sửa ví phải được kích hoạt");

            card.triggerToggleStatusAction();
            assertTrue(toggleClicked.get(), "Nút lưu trữ/kích hoạt phải được kích hoạt");

            card.triggerDeleteAction();
            assertTrue(deleteClicked.get(), "Nút xóa ví phải được kích hoạt");

            card.triggerSetDefaultAction(); // Không có button đặt mặc định nên không lỗi
            assertFalse(defaultClicked.get(), "Nút đặt mặc định đã được loại bỏ");
        });
    }
}
