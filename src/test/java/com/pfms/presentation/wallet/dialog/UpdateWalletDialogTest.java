package com.pfms.presentation.wallet.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử UpdateWalletDialog - Hộp thoại cập nhật ví")
class UpdateWalletDialogTest {

    private WalletGUIController walletController;
    private WalletResponseDTO existingWallet;
    private UpdateWalletDialog dialog;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        existingWallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Cũ", new BigDecimal("1000000"), "VND", false, "ACTIVE", "Ghi chú cũ", LocalDateTime.now());
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new UpdateWalletDialog(walletController, existingWallet);
            dialog.setErrorConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo nạp đúng thông tin ví hiện hành vào biểu mẫu")
    void testInitialValuesLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals("Ví Cũ", dialog.getNameInputValue());
            assertEquals("Ghi chú cũ", dialog.getNoteInputValue());
            assertFalse(dialog.isSuccess());
            assertNull(dialog.getUpdatedWallet());
        });
    }

    @Test
    @DisplayName("Lưu cập nhật thành công khi dữ liệu hợp lệ")
    void testUpdateWalletSuccess() {
        WalletResponseDTO updated = new WalletResponseDTO(
                existingWallet.getWalletId(), "Ví Mới", new BigDecimal("1000000"), "VND", false, "ACTIVE", "Ghi chú mới", LocalDateTime.now());
        when(walletController.handleUpdateWallet(existingWallet.getWalletId(), "Ví Mới", "Ghi chú mới")).thenReturn(updated);

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setNameInputValue("Ví Mới");
            dialog.setNoteInputValue("Ghi chú mới");
            dialog.triggerSave();

            assertTrue(dialog.isSuccess());
            assertSame(updated, dialog.getUpdatedWallet());
        });
        verify(walletController).handleUpdateWallet(existingWallet.getWalletId(), "Ví Mới", "Ghi chú mới");
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ và cờ thành công vẫn là false khi cập nhật thất bại")
    void testUpdateWalletBusinessExceptionHandled() {
        when(walletController.handleUpdateWallet(existingWallet.getWalletId(), "Ví Trùng", ""))
                .thenThrow(new BusinessException(ErrorCode.ERR_NAM_002, "Tên ví đã tồn tại"));

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setNameInputValue("Ví Trùng");
            dialog.setNoteInputValue("");
            dialog.triggerSave();

            assertFalse(dialog.isSuccess());
            assertNull(dialog.getUpdatedWallet());
            assertEquals("Tên ví đã tồn tại", dialog.getErrorMessage());
        });
    }
}
