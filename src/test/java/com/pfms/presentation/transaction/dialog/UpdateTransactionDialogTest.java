package com.pfms.presentation.transaction.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.category.CategoryGUIController;
import com.pfms.presentation.dto.category.CategoryHierarchyResponseDTO;
import com.pfms.presentation.dto.category.CategoryResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.transaction.TransactionGUIController;
import com.pfms.presentation.wallet.WalletGUIController;
import com.pfms.testutil.JavaFxTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Kiểm thử UpdateTransactionDialog - Hộp thoại cập nhật giao dịch JavaFX")
class UpdateTransactionDialogTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private WalletResponseDTO sampleWallet;
    private CategoryResponseDTO sampleExpenseCategory;
    private CategoryHierarchyResponseDTO expenseGroup;
    private TransactionResponseDTO existingTxn;
    private UpdateTransactionDialog dialog;

    @BeforeAll
    static void initJfx() {
        JavaFxTestHelper.initPlatform();
    }

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
        expenseGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Thiết yếu", "ACTIVE", List.of(sampleExpenseCategory));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(expenseGroup));

        existingTxn = new TransactionResponseDTO(
                UUID.randomUUID(), sampleWallet.getWalletId(), "Ví Tiền mặt",
                sampleExpenseCategory.getCategoryId(), "Ăn uống", new BigDecimal("150000"),
                "EXPENSE", LocalDate.of(2026, 11, 15), "Ăn trưa", new BigDecimal("4850000"), LocalDateTime.now());

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new UpdateTransactionDialog(
                    null, transactionController, walletController, categoryController, existingTxn);
            dialog.setErrorConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo nạp đầy đủ thông tin giao dịch hiện hành vào form")
    void testInitialValuesLoaded() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.isSuccess());
            assertNull(dialog.getUpdatedTransaction());
            assertEquals("150000", dialog.getAmountInputValue());
            assertEquals("Ăn trưa", dialog.getNoteInputValue());
            assertEquals("2026-11-15", dialog.getDateInputValue());
        });
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

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setAmountInputValue("200000");
            dialog.setNoteInputValue("Ăn tối");
            dialog.triggerSave();

            assertTrue(dialog.isSuccess());
            assertSame(updated, dialog.getUpdatedTransaction());
        });

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

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setAmountInputValue("-100");
            dialog.triggerSave();

            assertFalse(dialog.isSuccess());
            assertNull(dialog.getUpdatedTransaction());
            assertEquals("Số tiền giao dịch phải là số dương lớn hơn 0", dialog.getErrorMessage());
        });
    }

    @Test
    @DisplayName("availableBalanceLabel hiển thị chính xác số dư khả dụng bên dưới ô dropdown")
    void testAvailableBalanceLabelDisplaysBalance() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(dialog.getAvailableBalanceLabel());
            assertTrue(dialog.getAvailableBalanceLabel().isVisible());
            assertTrue(dialog.getAvailableBalanceLabel().getText().contains("5,000,000 VNĐ"));
        });
    }

    @Test
    @DisplayName("Phụ phí yêu cầu chọn nhóm chi phí trước khi mở khóa danh mục")
    void testFeeSectionRequiresGroup() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.getFeeCheckBox().isSelected());
            assertTrue(dialog.getFeeAmountField().isDisable());
            assertTrue(dialog.getFeeGroupCombo().isDisable());
            assertTrue(dialog.getFeeCategoryCombo().isDisable());

            dialog.getFeeCheckBox().doClick();
            assertTrue(dialog.getFeeCheckBox().isSelected());
            assertFalse(dialog.getFeeAmountField().isDisable());
            assertFalse(dialog.getFeeGroupCombo().isDisable());
            assertTrue(dialog.getFeeCategoryCombo().isDisable());

            // Chọn nhóm phụ phí
            dialog.getFeeGroupCombo().setValue(expenseGroup);
            assertFalse(dialog.getFeeCategoryCombo().isDisable());
            assertEquals(1, dialog.getFeeCategoryCombo().getItems().size());
        });
    }
}
