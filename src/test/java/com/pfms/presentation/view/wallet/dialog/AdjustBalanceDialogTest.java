package com.pfms.presentation.view.wallet.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử AdjustBalanceDialog - Hộp thoại kiểm kê điều chỉnh số dư")
class AdjustBalanceDialogTest {

    private WalletGUIController walletController;
    private WalletResponseDTO wallet;
    private AdjustBalanceDialog dialog;

    @BeforeEach
    void setUp() {
        walletController = Mockito.mock(WalletGUIController.class);
        wallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("2000000"), "VND", false, "ACTIVE", "Ghi chú", LocalDateTime.now());
        dialog = new AdjustBalanceDialog((JFrame) null, walletController, wallet);
        dialog.setErrorConsumer(msg -> {});
        dialog.setInfoConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hiển thị số dư hiện tại và cờ thành công ban đầu là false")
    void testInitialState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getAdjustmentResponse());
        assertNotNull(dialog.getDateInputValue());
    }

    @Test
    @DisplayName("Thực hiện kiểm kê thành công và đóng hộp thoại")
    void testAdjustBalanceSuccess() {
        BalanceAdjustmentResponseDTO response = new BalanceAdjustmentResponseDTO(
                UUID.randomUUID(), wallet.getWalletId(), new BigDecimal("2000000"),
                new BigDecimal("2500000"), new BigDecimal("500000"), "VND",
                "Kiểm đếm cuối tháng", LocalDate.now(), LocalDateTime.now());

        when(walletController.handleAdjustBalance(
                eq(wallet.getWalletId()), eq("2500000"), eq("Kiểm đếm cuối tháng"), anyString()))
                .thenReturn(response);

        dialog.setActualBalanceInputValue("2500000");
        dialog.setReasonInputValue("Kiểm đếm cuối tháng");
        dialog.triggerAdjust();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getAdjustmentResponse());
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ và cờ thành công vẫn là false khi kiểm kê thất bại")
    void testAdjustBalanceBusinessExceptionHandled() {
        when(walletController.handleAdjustBalance(
                eq(wallet.getWalletId()), eq("-100"), eq("Lý do sai"), anyString()))
                .thenThrow(new BusinessException(ErrorCode.ERR_FIN_001, "Số dư thực tế không được là số âm"));

        dialog.setActualBalanceInputValue("-100");
        dialog.setReasonInputValue("Lý do sai");
        dialog.triggerAdjust();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getAdjustmentResponse());
        assertEquals("Số dư thực tế không được là số âm", dialog.getErrorMessage());
    }
}
