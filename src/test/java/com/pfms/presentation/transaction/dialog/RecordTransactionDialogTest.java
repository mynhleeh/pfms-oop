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

@DisplayName("Kiểm thử RecordTransactionDialog - Hộp thoại ghi nhận giao dịch thu chi JavaFX")
class RecordTransactionDialogTest {

    private TransactionGUIController transactionController;
    private WalletGUIController walletController;
    private CategoryGUIController categoryController;

    private WalletResponseDTO sampleWallet;
    private WalletResponseDTO secondWallet;
    private CategoryResponseDTO sampleExpenseCategory;
    private CategoryResponseDTO sampleIncomeCategory;
    private CategoryHierarchyResponseDTO expenseGroup;
    private CategoryHierarchyResponseDTO incomeGroup;
    private RecordTransactionDialog dialog;

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
        secondWallet = new WalletResponseDTO(
                UUID.randomUUID(), "Tài khoản VCB", new BigDecimal("1234567"), "VND", false, "ACTIVE", "Tiết kiệm", LocalDateTime.now());
        when(walletController.loadWalletsSummary()).thenReturn(
                new WalletsSummaryResponseDTO(List.of(sampleWallet, secondWallet), new BigDecimal("6234567"), "VND", Collections.emptyMap()));

        sampleExpenseCategory = new CategoryResponseDTO(
                UUID.randomUUID(), "Ăn uống", UUID.randomUUID(), "Thiết yếu", "EXPENSE", "ACTIVE");
        sampleIncomeCategory = new CategoryResponseDTO(
                UUID.randomUUID(), "Lương", UUID.randomUUID(), "Cố định", "INCOME", "ACTIVE");

        expenseGroup = new CategoryHierarchyResponseDTO(
                "EXPENSE", UUID.randomUUID(), "Thiết yếu", "ACTIVE", List.of(sampleExpenseCategory));
        incomeGroup = new CategoryHierarchyResponseDTO(
                "INCOME", UUID.randomUUID(), "Cố định", "ACTIVE", List.of(sampleIncomeCategory));
        when(categoryController.loadCategoryHierarchy()).thenReturn(List.of(expenseGroup, incomeGroup));

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog = new RecordTransactionDialog(null, transactionController, walletController, categoryController);
            dialog.setErrorConsumer(msg -> {});
        });
    }

    @Test
    @DisplayName("Khởi tạo hộp thoại nạp đúng danh sách ví, chưa chọn nhóm thì danh mục bị khóa và cờ thành công là false")
    void testInitialDialogState() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.isSuccess());
            assertNull(dialog.getCreatedTransaction());
            assertEquals("Chi tiêu", dialog.getSelectedTransactionType());
            assertNotNull(dialog.getDateInputValue());
            assertEquals("", dialog.getAmountInputValue());
            assertEquals("", dialog.getNoteInputValue());
            assertSame(sampleWallet, dialog.getSelectedWallet());

            // Chưa chọn nhóm thì categoryCombo bị vô hiệu hóa và getSelectedCategory() trả về null
            assertNull(dialog.getSelectedGroup());
            assertTrue(dialog.getCategoryCombo().isDisable());
            assertNull(dialog.getSelectedCategory());

            // Khi chọn nhóm, categoryCombo được mở khóa và nạp danh mục của nhóm đó
            dialog.setSelectedGroup(expenseGroup);
            assertFalse(dialog.getCategoryCombo().isDisable());
            assertSame(sampleExpenseCategory, dialog.getSelectedCategory());
        });
    }

    @Test
    @DisplayName("Lưu giao dịch chi tiêu thành công khi chọn nhóm và danh mục hợp lệ")
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

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setSelectedGroup(expenseGroup);
            dialog.setAmountInputValue("150000");
            dialog.setNoteInputValue("Ăn trưa");
            dialog.triggerSave();

            assertTrue(dialog.isSuccess());
            assertSame(response, dialog.getCreatedTransaction());
        });

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

        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setSelectedGroup(expenseGroup);
            dialog.setAmountInputValue("-50000");
            dialog.triggerSave();

            assertFalse(dialog.isSuccess());
            assertNull(dialog.getCreatedTransaction());
            assertEquals("Số tiền giao dịch phải là số dương lớn hơn 0", dialog.getErrorMessage());
        });
    }

    @Test
    @DisplayName("availableBalanceLabel hiển thị đúng chuỗi định dạng số dư khả dụng khi chọn ví")
    void testAvailableBalanceLabelOnWalletSelection() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(dialog.getAvailableBalanceLabel());
            assertTrue(dialog.getAvailableBalanceLabel().isVisible());
            assertEquals("Số dư khả dụng: 5,000,000 VNĐ", dialog.getAvailableBalanceLabel().getText());

            dialog.setSelectedWallet(secondWallet);

            assertTrue(dialog.getAvailableBalanceLabel().isVisible());
            assertEquals("Số dư khả dụng: 1,234,567 VNĐ", dialog.getAvailableBalanceLabel().getText());
        });
    }

    @Test
    @DisplayName("availableBalanceLabel ẩn đi (isVisible = false) khi ví không được chọn hoặc là null")
    void testAvailableBalanceLabelHiddenWhenNoWalletSelected() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertNotNull(dialog.getAvailableBalanceLabel());
            dialog.setSelectedWallet(null);
            assertFalse(dialog.getAvailableBalanceLabel().isVisible());
        });
    }

    @Test
    @DisplayName("Type Switcher toggle chuyển đổi linh hoạt giữa Khoản Chi Tiêu và Khoản Thu Nhập")
    void testTypeSwitcherToggle() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals("Chi tiêu", dialog.getSelectedTransactionType());
            dialog.setSelectedGroup(expenseGroup);
            assertSame(sampleExpenseCategory, dialog.getSelectedCategory());

            dialog.getIncomeTypeBtn().doClick();
            assertEquals("Thu nhập", dialog.getSelectedTransactionType());
            assertNull(dialog.getSelectedGroup());
            assertTrue(dialog.getCategoryCombo().isDisable());
            dialog.setSelectedGroup(incomeGroup);
            assertSame(sampleIncomeCategory, dialog.getSelectedCategory());

            dialog.getExpenseTypeBtn().doClick();
            assertEquals("Chi tiêu", dialog.getSelectedTransactionType());
            assertNull(dialog.getSelectedGroup());
            assertTrue(dialog.getCategoryCombo().isDisable());
            dialog.setSelectedGroup(expenseGroup);
            assertSame(sampleExpenseCategory, dialog.getSelectedCategory());
        });
    }

    @Test
    @DisplayName("Ràng buộc phân cấp: Chưa chọn nhóm thì categoryCombo bị khóa; chọn nhóm thì chỉ hiện danh mục của nhóm đó")
    void testGroupAndCategoryHierarchyBinding() {
        JavaFxTestHelper.runOnFxThread(() -> {
            // 1. Chưa chọn nhóm
            assertNull(dialog.getSelectedGroup());
            assertTrue(dialog.getCategoryCombo().isDisable());
            assertEquals(0, dialog.getCategoryCombo().getItems().size());

            // 2. Chọn nhóm Thiết yếu (EXPENSE)
            dialog.setSelectedGroup(expenseGroup);
            assertFalse(dialog.getCategoryCombo().isDisable());
            assertEquals(1, dialog.getCategoryCombo().getItems().size());
            assertEquals("Ăn uống", dialog.getCategoryCombo().getItems().get(0).getName());

            // 3. Chuyển lại về chưa chọn nhóm (null)
            dialog.setSelectedGroup(null);
            assertTrue(dialog.getCategoryCombo().isDisable());
            assertEquals(0, dialog.getCategoryCombo().getItems().size());
        });
    }

    @Test
    @DisplayName("Kiểm tra báo lỗi khi lưu giao dịch mà chưa chọn nhóm danh mục hoặc chưa chọn danh mục")
    void testValidationWhenGroupOrCategoryNotSelected() {
        JavaFxTestHelper.runOnFxThread(() -> {
            dialog.setAmountInputValue("100000");

            // Chưa chọn nhóm
            dialog.setSelectedGroup(null);
            dialog.triggerSave();
            assertFalse(dialog.isSuccess());
            assertEquals("Vui lòng chọn nhóm danh mục.", dialog.getErrorMessage());

            // Chọn nhóm nhưng xóa hết category
            dialog.setSelectedGroup(expenseGroup);
            dialog.getCategoryCombo().getItems().clear();
            dialog.triggerSave();
            assertFalse(dialog.isSuccess());
            assertEquals("Vui lòng chọn danh mục thu chi.", dialog.getErrorMessage());
        });
    }

    @Test
    @DisplayName("Amount in words hint tự động định dạng số tiền trực tiếp khi người dùng nhập")
    void testAmountInWordsLiveFormatting() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertEquals("0 VNĐ", dialog.getAmountInWordsHint().getText());

            dialog.setAmountInputValue("250000");
            assertEquals("250,000 VNĐ", dialog.getAmountInWordsHint().getText());

            dialog.setAmountInputValue("10000000");
            assertEquals("10,000,000 VNĐ", dialog.getAmountInWordsHint().getText());

            dialog.setAmountInputValue("abc");
            assertEquals("0 VNĐ", dialog.getAmountInWordsHint().getText());
        });
    }

    @Test
    @DisplayName("Fee Box tự động mở rộng theo Accordion khi tích chọn Có phụ phí phát sinh và yêu cầu chọn nhóm")
    void testFeeBoxAccordionToggle() {
        JavaFxTestHelper.runOnFxThread(() -> {
            assertFalse(dialog.getFeeCheckBox().isSelected());
            assertFalse(dialog.getFeeDetailsPanel().isVisible());
            assertTrue(dialog.getFeeAmountField().isDisable());
            assertTrue(dialog.getFeeGroupCombo().isDisable());
            assertTrue(dialog.getFeeCategoryCombo().isDisable());

            dialog.getFeeCheckBox().doClick();
            assertTrue(dialog.getFeeCheckBox().isSelected());
            assertTrue(dialog.getFeeDetailsPanel().isVisible());
            assertFalse(dialog.getFeeAmountField().isDisable());
            assertFalse(dialog.getFeeGroupCombo().isDisable());
            // Chưa chọn nhóm phụ phí -> danh mục bị disabled
            assertTrue(dialog.getFeeCategoryCombo().isDisable());

            // Chọn nhóm phụ phí -> mở khóa danh mục phụ phí
            dialog.getFeeGroupCombo().setValue(expenseGroup);
            assertFalse(dialog.getFeeCategoryCombo().isDisable());
            assertEquals(1, dialog.getFeeCategoryCombo().getItems().size());

            dialog.getFeeCheckBox().doClick();
            assertFalse(dialog.getFeeCheckBox().isSelected());
            assertFalse(dialog.getFeeDetailsPanel().isVisible());
            assertTrue(dialog.getFeeAmountField().isDisable());
            assertTrue(dialog.getFeeGroupCombo().isDisable());
            assertTrue(dialog.getFeeCategoryCombo().isDisable());
            assertEquals("0", dialog.getFeeAmountField().getText());
        });
    }
}
