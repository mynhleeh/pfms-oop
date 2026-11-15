package com.pfms.presentation.view.transaction.dialog;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.presentation.controller.CategoryGUIController;
import com.pfms.presentation.controller.TransactionGUIController;
import com.pfms.presentation.controller.WalletGUIController;
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

@DisplayName("Kiểm thử RecordTransactionDialog - Hộp thoại ghi nhận giao dịch thu chi")
class RecordTransactionDialogTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private WalletResponseDTO sampleWallet;
    private CategoryResponseDTO sampleExpenseCategory;
    private CategoryResponseDTO sampleIncomeCategory;
    private RecordTransactionDialog dialog;

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
        sampleIncomeCategory = new CategoryResponseDTO(
                UUID.randomUUID(), "Lương", UUID.randomUUID(), "Cố định", "INCOME", "ACTIVE");

        CategoryHierarchyResponseDTO expenseGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Thiết yếu", "ACTIVE", List.of(sampleExpenseCategory));
        CategoryHierarchyResponseDTO incomeGroup = new CategoryHierarchyResponseDTO(
                "INCOME", UUID.randomUUID(), "Cố định", "ACTIVE", List.of(sampleIncomeCategory));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(expenseGroup, incomeGroup));

        dialog = new RecordTransactionDialog((JFrame) null, transactionController, walletController, categoryController);
        dialog.setErrorConsumer(msg -> {});
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại nạp đúng danh sách ví, danh mục chi tiêu ban đầu và cờ thành công là false")
    void testInitialDialogState() {
        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedTransaction());
        assertEquals("Chi tiêu", dialog.getSelectedTransactionType());
        assertNotNull(dialog.getDateInputValue());
        assertEquals("", dialog.getAmountInputValue());
        assertEquals("", dialog.getNoteInputValue());
        assertSame(sampleWallet, dialog.getSelectedWallet());
        assertSame(sampleExpenseCategory, dialog.getSelectedCategory());
    }

    @Test
    @DisplayName("Lưu giao dịch chi tiêu thành công và gọi đúng controller")
    void testRecordExpenseSuccess() {
        TransactionResponseDTO response = new TransactionResponseDTO(
                UUID.randomUUID(), sampleWallet.getWalletId(), "Ví Tiền mặt",
                sampleExpenseCategory.getCategoryId(), "Ăn uống", new BigDecimal("150000"),
                "EXPENSE", LocalDate.now(), "Ăn trưa", new BigDecimal("4850000"), LocalDateTime.now());

        when(transactionController.handleRecordTransaction(
                eq(sampleWallet.getWalletId()),
                eq(sampleExpenseCategory.getCategoryId()),
                eq("150000"),
                anyString(),
                eq("Ăn trưa"),
                eq("0"),
                isNull()))
                .thenReturn(response);

        dialog.setAmountInputValue("150000");
        dialog.setNoteInputValue("Ăn trưa");
        dialog.triggerSave();

        assertTrue(dialog.isSuccess());
        assertSame(response, dialog.getCreatedTransaction());
        verify(transactionController).handleRecordTransaction(
                eq(sampleWallet.getWalletId()),
                eq(sampleExpenseCategory.getCategoryId()),
                eq("150000"),
                anyString(),
                eq("Ăn trưa"),
                eq("0"),
                isNull());
    }

    @Test
    @DisplayName("Bắt giữ ngoại lệ nghiệp vụ và cờ thành công vẫn là false khi ghi nhận thất bại")
    void testRecordTransactionBusinessExceptionHandled() {
        when(transactionController.handleRecordTransaction(
                any(), any(), eq("-50000"), any(), any(), any(), any()))
                .thenThrow(new BusinessException(ErrorCode.ERR_FIN_001, "Số tiền giao dịch phải là số dương lớn hơn 0"));

        dialog.setAmountInputValue("-50000");
        dialog.triggerSave();

        assertFalse(dialog.isSuccess());
        assertNull(dialog.getCreatedTransaction());
        assertEquals("Số tiền giao dịch phải là số dương lớn hơn 0", dialog.getErrorMessage());
    }
}
