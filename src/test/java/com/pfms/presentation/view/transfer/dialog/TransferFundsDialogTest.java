package com.pfms.presentation.view.transfer.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransferGUIController;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Kiểm thử TransferFundsDialog - Hộp thoại chuyển tiền nội bộ giữa hai ví")
class TransferFundsDialogTest {

    private TransferGUIController transferController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private WalletResponseDTO walletSource;
    private WalletResponseDTO walletDest;
    private CategoryResponseDTO sampleFeeCategory;
    private TransferFundsDialog dialog;

    @BeforeEach
    void setUp() {
        transferController = Mockito.mock(TransferGUIController.class);
        walletController = Mockito.mock(WalletGUIController.class);
        categoryController = Mockito.mock(CategoryGUIController.class);

        walletSource = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Tiền mặt", LocalDateTime.now());
        walletDest = new WalletResponseDTO(
                UUID.randomUUID(), "Ví MB Bank", new BigDecimal("10000000"), "VND", false, "ACTIVE", "Tài khoản", LocalDateTime.now());

        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(walletSource, walletDest), new BigDecimal("15000000"), "VND", Collections.emptyMap()));

        sampleFeeCategory = new CategoryResponseDTO(
                UUID.randomUUID(), "Phí chuyển khoản", UUID.randomUUID(), "Phí", "EXPENSE", "ACTIVE");
        CategoryHierarchyResponseDTO feeGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Phí giao dịch", "ACTIVE", List.of(sampleFeeCategory));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(feeGroup));

        dialog = new TransferFundsDialog((JFrame) null, transferController, walletController, categoryController);
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại có ví nguồn, ví đích và cờ thành công là false")
    void testInitialState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedTransfer());
        assertNotNull(dialog.getDateInputValue());
        assertEquals("", dialog.getAmountInputValue());
        assertEquals("", dialog.getNoteInputValue());
        assertNotNull(dialog.getSourceWallet());
        assertNotNull(dialog.getDestinationWallet());
    }

    @Test
    @DisplayName("Chuyển tiền thành công giữa hai ví khác nhau và gọi đúng controller")
    void testTransferFundsSuccess() {
        dialog.setSourceWallet(walletSource);
        dialog.setDestinationWallet(walletDest);
        dialog.setAmountInputValue("1000000");
        dialog.setNoteInputValue("Nạp tiền ngân hàng");

        TransferResponseDTO response = new TransferResponseDTO(
                UUID.randomUUID(), walletSource.getWalletId(), "Ví Tiền mặt",
                walletDest.getWalletId(), "Ví MB Bank", new BigDecimal("1000000"),
                LocalDate.now(), "Nạp tiền ngân hàng", new BigDecimal("4000000"),
                new BigDecimal("11000000"), LocalDateTime.now());

        when(transferController.handleTransferFunds(
                eq(walletSource.getWalletId()),
                eq(walletDest.getWalletId()),
                eq("1000000"),
                anyString(),
                eq("Nạp tiền ngân hàng"),
                eq("0"),
                isNull()))
                .thenReturn(response);

        dialog.triggerTransfer();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getCreatedTransfer());
        verify(transferController).handleTransferFunds(
                eq(walletSource.getWalletId()),
                eq(walletDest.getWalletId()),
                eq("1000000"),
                anyString(),
                eq("Nạp tiền ngân hàng"),
                eq("0"),
                isNull());
    }

    @Test
    @DisplayName("Báo lỗi khi ví nguồn và ví đích trùng nhau mà không gọi controller")
    void testSameSourceAndDestWalletError() {
        dialog.setSourceWallet(walletSource);
        dialog.setDestinationWallet(walletSource);
        dialog.setAmountInputValue("500000");

        dialog.triggerTransfer();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedTransfer());
        assertEquals("Ví nguồn và ví đích không được trùng nhau.", dialog.getErrorMessage());
        verifyNoInteractions(transferController);
    }

    @Test
    @DisplayName("Bắt giữ BusinessException khi số tiền vượt quá số dư hoặc không hợp lệ")
    void testTransferFundsBusinessExceptionHandled() {
        dialog.setSourceWallet(walletSource);
        dialog.setDestinationWallet(walletDest);
        dialog.setAmountInputValue("999999999");

        when(transferController.handleTransferFunds(any(), any(), eq("999999999"), any(), any(), any(), any()))
                .thenThrow(new BusinessException(ErrorCode.ERR_WAL_001, "Số dư ví nguồn không đủ"));

        dialog.triggerTransfer();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedTransfer());
        assertEquals("Số dư ví nguồn không đủ", dialog.getErrorMessage());
    }
}
