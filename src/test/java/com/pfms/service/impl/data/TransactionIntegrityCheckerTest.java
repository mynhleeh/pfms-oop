package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationIssueResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TransactionIntegrityChecker Unit Tests")
class TransactionIntegrityCheckerTest {

    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private Category expenseCategory;
    private Category incomeCategory;
    private TransactionDate validDate;

    @BeforeEach
    void setUp() {
        sourceWallet = new Wallet(UUID.randomUUID(), "Ví nguồn", "", false);
        destinationWallet = new Wallet(UUID.randomUUID(), "Ví đích", "", false);
        expenseCategory = new Category(
                UUID.randomUUID(), "Chi phí", UUID.randomUUID(), TransactionType.EXPENSE, "icon", "#fff");
        incomeCategory = new Category(
                UUID.randomUUID(), "Thu nhập", UUID.randomUUID(), TransactionType.INCOME, "icon", "#fff");
        validDate = TransactionDate.of(LocalDate.of(2026, 3, 15));
    }

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<TransactionIntegrityChecker> constructor =
                TransactionIntegrityChecker.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("checkTransfers should pass for valid transfer with matching fee wallet")
    void testCheckTransfers_ValidTransfer_Success() {
        UUID transferId = UUID.randomUUID();
        TransactionFee fee = new TransactionFee(
                UUID.randomUUID(), transferId, Money.of(5_000), sourceWallet, expenseCategory);
        TransferTransaction transfer = new TransferTransaction(
                transferId, sourceWallet, destinationWallet, Money.of(100_000), validDate, "Chuyển tiền", fee);

        Map<UUID, Wallet> walletMap = Map.of(
                sourceWallet.getId(), sourceWallet, destinationWallet.getId(), destinationWallet);
        Map<UUID, Category> categoryMap = Map.of(expenseCategory.getCategoryId(), expenseCategory);
        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkTransfers(
                walletMap, List.of(transfer), categoryMap, issues);

        assertTrue(consistent);
        assertTrue(issues.isEmpty());
    }

    @Test
    @DisplayName("checkTransfers should flag transfer when source wallet is missing in walletMap")
    void testCheckTransfers_MissingSourceWallet_Flagged() {
        TransferTransaction transfer = new TransferTransaction(
                UUID.randomUUID(), sourceWallet, destinationWallet, Money.of(100_000), validDate, "Chuyển tiền", null);

        Map<UUID, Wallet> walletMap = Map.of(destinationWallet.getId(), destinationWallet);
        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkTransfers(
                walletMap, List.of(transfer), Map.of(), issues);

        assertFalse(consistent);
        assertEquals(1, issues.size());
        assertEquals("TRANSFER_INCONSISTENCY", issues.get(0).getIssueType());
        assertTrue(issues.get(0).getMessage().contains("ví nguồn"));
    }

    @Test
    @DisplayName("checkTransfers should flag transfer when destination wallet is missing in walletMap")
    void testCheckTransfers_MissingDestinationWallet_Flagged() {
        TransferTransaction transfer = new TransferTransaction(
                UUID.randomUUID(), sourceWallet, destinationWallet, Money.of(100_000), validDate, "Chuyển tiền", null);

        Map<UUID, Wallet> walletMap = Map.of(sourceWallet.getId(), sourceWallet);
        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkTransfers(
                walletMap, List.of(transfer), Map.of(), issues);

        assertFalse(consistent);
        assertEquals(1, issues.size());
        assertEquals("TRANSFER_INCONSISTENCY", issues.get(0).getIssueType());
        assertTrue(issues.get(0).getMessage().contains("ví đích"));
    }

    @Test
    @DisplayName("checkFees should flag issue when fee parentTransactionId does not match parent transaction")
    void testCheckFees_ParentTransactionMismatch_Flagged() {
        UUID txnId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        TransactionFee fee = new TransactionFee(
                UUID.randomUUID(), otherId, Money.of(5_000), sourceWallet, expenseCategory);

        Transaction mockTxn = org.mockito.Mockito.mock(Transaction.class);
        org.mockito.Mockito.when(mockTxn.getTransactionId()).thenReturn(txnId);
        org.mockito.Mockito.when(mockTxn.getFee()).thenReturn(java.util.Optional.of(fee));

        Map<UUID, Category> categoryMap = Map.of(expenseCategory.getCategoryId(), expenseCategory);
        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkFees(List.of(mockTxn), categoryMap, issues);

        assertFalse(consistent);
        assertEquals(1, issues.size());
        assertEquals("FEE_INCONSISTENCY", issues.get(0).getIssueType());
        assertTrue(issues.get(0).getMessage().contains("không khớp với giao dịch"));
    }

    @Test
    @DisplayName("checkFees should flag issue when fee category is missing in categoryMap")
    void testCheckFees_MissingCategoryInMap_Flagged() {
        UUID txnId = UUID.randomUUID();
        TransactionFee fee = new TransactionFee(
                UUID.randomUUID(), txnId, Money.of(5_000), sourceWallet, expenseCategory);
        IncomeExpenseTransaction ie = new IncomeExpenseTransaction(
                txnId, TransactionType.EXPENSE, sourceWallet, expenseCategory,
                Money.of(100_000), validDate, "Chi tiêu", fee);

        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkFees(List.of(ie), Map.of(), issues);

        assertFalse(consistent);
        assertEquals(1, issues.size());
        assertEquals("FEE_INCONSISTENCY", issues.get(0).getIssueType());
        assertTrue(issues.get(0).getMessage().contains("không tồn tại"));
    }

    @Test
    @DisplayName("checkFees should flag issue when fee category in categoryMap is of type INCOME")
    void testCheckFees_FeeCategoryNotExpenseInMap_Flagged() {
        UUID txnId = UUID.randomUUID();
        TransactionFee fee = new TransactionFee(
                UUID.randomUUID(), txnId, Money.of(5_000), sourceWallet, expenseCategory);
        IncomeExpenseTransaction ie = new IncomeExpenseTransaction(
                txnId, TransactionType.EXPENSE, sourceWallet, expenseCategory,
                Money.of(100_000), validDate, "Chi tiêu", fee);

        Category mismatchedCategory = new Category(
                expenseCategory.getCategoryId(), "Tên đổi thành thu", UUID.randomUUID(),
                TransactionType.INCOME, "icon", "#000");
        Map<UUID, Category> categoryMap = Map.of(expenseCategory.getCategoryId(), mismatchedCategory);
        List<ReconciliationIssueResponseDTO> issues = new ArrayList<>();

        boolean consistent = TransactionIntegrityChecker.checkFees(List.of(ie), categoryMap, issues);

        assertFalse(consistent);
        assertTrue(issues.stream().anyMatch(i -> "FEE_INCONSISTENCY".equals(i.getIssueType())
                && i.getMessage().contains("EXPENSE")));
    }
}
