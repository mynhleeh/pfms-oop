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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử CreateWalletDialog - Hộp thoại tạo ví mới")
class CreateWalletDialogTest {

    private WalletGUIController walletController;
    private CreateWalletDialog dialog;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new CreateWalletDialog(walletController);
            dialog.setErrorConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại có các trường nhập liệu rỗng và cờ thành công ban đầu là false")
    void testInitialDialogState() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.isSuccess());
            assertNull(dialog.getCreatedWallet());
            assertEquals("", dialog.getNameInputValue());
            assertEquals("", dialog.getNoteInputValue());
        });
    }

    @Test
    @DisplayName("Lưu ví thành công khi người dùng nhập dữ liệu hợp lệ")
    void testSaveWalletSuccess() {
        WalletResponseDTO response = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiết kiệm", BigDecimal.ZERO, "VND", false, "ACTIVE", "Ghi chú", LocalDateTime.now());
        when(walletController.handleCreateWallet("Ví Tiết kiệm", "Ghi chú")).thenReturn(response);

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setNameInputValue("Ví Tiết kiệm");
            dialog.setNoteInputValue("Ghi chú");
            dialog.triggerSave();

            assertTrue(dialog.isSuccess());
            assertSame(response, dialog.getCreatedWallet());
        });
        verify(walletController).handleCreateWallet("Ví Tiết kiệm", "Ghi chú");
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ và cờ thành công vẫn là false khi lưu thất bại")
    void testSaveWalletBusinessExceptionHandled() {
        when(walletController.handleCreateWallet(anyString(), anyString()))
                .thenThrow(new BusinessException(ErrorCode.ERR_NAM_002, "Tên ví đã tồn tại"));

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setNameInputValue("Ví Trùng");
            dialog.triggerSave();

            assertFalse(dialog.isSuccess());
            assertNull(dialog.getCreatedWallet());
            assertNotNull(dialog.getErrorMessage());
            assertTrue(dialog.getErrorMessage().contains("Tên ví đã tồn tại"));
        });
    }
}
