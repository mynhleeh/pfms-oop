package com.pfms.presentation.transaction.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
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

@DisplayName("Kiểm thử UpdateTransactionDialog - Hộp thoại cập nhật giao dịch")
class UpdateTransactionDialogTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private WalletResponseDTO sampleWallet;
    private CategoryResponseDTO sampleExpenseCategory;
    private TransactionResponseDTO existingTxn;
    private UpdateTransactionDialog dialog;

    @BeforeEach
    void setUp() {
        transactionController = Mockito.mock(TransactionGUIController.class);
        walletController = Mockito.mock(WalletGUIController.class);
        categoryController = Mockito.mock(CategoryGUIController.class);

        sampleWallet = new WalletResponseDTO(
                UUID.randomUUID(), "Ví Tiền mặt", new BigDecimal("5000000"), "VND", true, "ACTIVE", "Tiêu vặt", LocalDateTime.now());
        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(sampleWallet), new BigDecimal("5000000"), "VND", Collections.emptyMap()));

        sampleExpenseCategory = new CategoryResponseDTO(
                UUID.randomUUID(), "Ăn uống", UUID.randomUUID(), "Thiết yếu", "EXPENSE", "ACTIVE");
        CategoryHierarchyResponseDTO expenseGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Thiết yếu", "ACTIVE", List.of(sampleExpenseCategory));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(expenseGroup));

        existingTxn = new TransactionResponseDTO(
                UUID.randomUUID(), sampleWallet.getWalletId(), "Ví Tiền mặt",
                sampleExpenseCategory.getCategoryId(), "Ăn uống", new BigDecimal("150000"),
                "EXPENSE", LocalDate.of(2026, 11, 15), "Ăn trưa", new BigDecimal("4850000"), LocalDateTime.now());

        dialog = new UpdateTransactionDialog(
                (JFrame) null, transactionController, walletController, categoryController, existingTxn);
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo nạp đầy đủ thông tin giao dịch hiện hành vào form")
    void testInitialValuesLoaded() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getUpdatedTransaction());
        assertEquals("150000", dialog.getAmountInputValue());
        assertEquals("Ăn trưa", dialog.getNoteInputValue());
        assertEquals("2026-11-15", dialog.getDateInputValue());
    }

    @Test
    @DisplayName("Cập nhật giao dịch thành công và gọi đúng controller")
    void testUpdateTransactionSuccess() {
        TransactionResponseDTO updated = new TransactionResponseDTO(
                existingTxn.getTransactionId(), sampleWallet.getWalletId(), "Ví Tiền mặt",
                sampleExpenseCategory.getCategoryId(), "Ăn uống", new BigDecimal("200000"),
                "EXPENSE", LocalDate.of(2026, 11, 15), "Ăn tối", new BigDecimal("4800000"), LocalDateTime.now());

        when(transactionController.handleUpdateTransaction(
                eq(existingTxn.getTransactionId()),
                eq(sampleWallet.getWalletId()),
                eq("200000"),
                eq("2026-11-15"),
                eq(sampleExpenseCategory.getCategoryId()),
                eq("Ăn tối"),
                isNull(),
                isNull()))
                .thenReturn(updated);

        dialog.setAmountInputValue("200000");
        dialog.setNoteInputValue("Ăn tối");
        dialog.triggerSave();

        assertTrue(dialog.isSuccess());
        assertSame(updated, dialog.getUpdatedTransaction());
        verify(transactionController).handleUpdateTransaction(
                eq(existingTxn.getTransactionId()),
                eq(sampleWallet.getWalletId()),
                eq("200000"),
                eq("2026-11-15"),
                eq(sampleExpenseCategory.getCategoryId()),
                eq("Ăn tối"),
                isNull(),
                isNull());
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ và cờ thành công vẫn là false khi cập nhật thất bại")
    void testUpdateTransactionBusinessExceptionHandled() {
        when(transactionController.handleUpdateTransaction(
                any(), any(), eq("-100"), any(), any(), any(), any(), any()))
                .thenThrow(new BusinessException(ErrorCode.ERR_FIN_001, "Số tiền giao dịch phải là số dương lớn hơn 0"));

        dialog.setAmountInputValue("-100");
        dialog.triggerSave();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getUpdatedTransaction());
        assertEquals("Số tiền giao dịch phải là số dương lớn hơn 0", dialog.getErrorMessage());
    }

    @Test
    @DisplayName("Renderer của wallet combo chỉ hiển thị tên ví thuần túy không chứa số dư trong ngoặc")
    void testWalletComboRendererPureName() {
        javax.swing.ListCellRenderer<? super WalletResponseDTO> renderer = dialog.getWalletCombo().getRenderer();
        assertNotNull(renderer);

        java.awt.Component comp = renderer.getListCellRendererComponent(
                new javax.swing.JList<>(), sampleWallet, 0, false, false);
        assertTrue(comp instanceof javax.swing.JLabel);
        javax.swing.JLabel label = (javax.swing.JLabel) comp;
        assertEquals("Ví Tiền mặt", label.getText());
        assertFalse(label.getText().contains("(5,000,000 VNĐ)"));
        assertFalse(label.getText().contains("("));
    }

    @Test
    @DisplayName("availableBalanceLabel hiển thị chính xác số dư khả dụng bên dưới ô dropdown")
    void testAvailableBalanceLabelDisplaysBalance() {
        assertNotNull(dialog.getAvailableBalanceLabel());
        assertTrue(dialog.getAvailableBalanceLabel().isVisible());
        assertTrue(dialog.getAvailableBalanceLabel().getText().contains("5,000,000 VNĐ"));
    }
}
