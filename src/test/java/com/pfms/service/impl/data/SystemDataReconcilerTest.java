package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SystemDataReconciler Unit Tests")
class SystemDataReconcilerTest {

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate utility class")
    void testUtilityClassDesign() throws Exception {
        Constructor<SystemDataReconciler> constructor = SystemDataReconciler.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    @DisplayName("reconcile should return perfect report when datasets are empty")
    void reconcileShouldReturnPerfectReportWhenEmpty() {
        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());

        assertNotNull(report);
        assertTrue(report.isFullyConsistent());
        assertTrue(report.isWalletBalanceConsistent());
        assertTrue(report.isTransferConsistent());
        assertTrue(report.isCategoryHierarchyConsistent());
        assertTrue(report.isFeeConsistent());
        assertEquals(0, report.getScannedWallets());
        assertEquals(0, report.getScannedTransactions());
        assertEquals(0, report.getScannedCategories());
        assertEquals(0, report.getScannedAdjustments());
        assertTrue(report.getIssues().isEmpty());
    }

    @Test
    @DisplayName("reconcile should flag issue when wallet current balance does not match transaction flow")
    void reconcileShouldFlagWalletBalanceMismatch() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Tiền Mặt", "Ví chính", false);
        wallet.credit(Money.of("500000.00")); // Số dư hiện tại = 500,000

        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Thu nhập", TransactionType.INCOME, "icon", "#fff");
        Category category = new Category(UUID.randomUUID(), "Lương", group.getNeedGroupId(), TransactionType.INCOME, "icon", "#fff");
        // Giao dịch thu 200,000 nhưng ví lại có 500,000 -> Không khớp
        Transaction incomeTxn = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.INCOME, wallet, category, Money.of("200000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 15)), "Nhận lương", null);

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                List.of(wallet),
                List.of(incomeTxn),
                List.of(category),
                List.of(group),
                Collections.emptyList());

        assertNotNull(report);
        assertFalse(report.isFullyConsistent());
        assertFalse(report.isWalletBalanceConsistent());
        assertEquals(1, report.getIssues().size());
        assertEquals("BALANCE_MISMATCH", report.getIssues().get(0).getIssueType());
    }

    @Test
    @DisplayName("reconcile should flag transfer inconsistency when source wallet is not in wallets list")
    void reconcileShouldFlagTransferWithMissingSourceWallet() {
        Wallet sourceWallet = new Wallet(UUID.randomUUID(), "Ví Mồ Côi", "Ví", false);
        Wallet destWallet = new Wallet(UUID.randomUUID(), "Ví Đích", "Ví", false);
        TransferTransaction transfer = new TransferTransaction(
                UUID.randomUUID(), sourceWallet, destWallet, Money.of("50000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 10)), "Chuyển từ ví không tồn tại", null);

        // Chỉ đưa destWallet vào hệ thống, sourceWallet bị thiếu
        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                List.of(destWallet),
                List.of(transfer),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());

        assertNotNull(report);
        assertFalse(report.isFullyConsistent());
        assertFalse(report.isTransferConsistent());
        assertTrue(report.getIssues().stream().anyMatch(i -> "TRANSFER_INCONSISTENCY".equals(i.getIssueType())));
    }

    @Test
    @DisplayName("reconcile should flag negative wallet balance even when transaction net flow equals balance")
    void reconcileShouldFlagNegativeWalletBalanceEvenWhenFlowMatches() {
        Wallet negativeWallet = Wallet.reconstitute(
                UUID.randomUUID(), "Ví Số Dư Âm", "Ghi chú", false,
                LocalDateTime.of(2026, 1, 1, 10, 0), new ActiveWalletState(), Money.of("-50000.00"));
        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Chi tiêu", TransactionType.EXPENSE, "icon", "#fff");
        Category category = new Category(
                UUID.randomUUID(), "Ăn uống", group.getNeedGroupId(), TransactionType.EXPENSE, "icon", "#fff");
        Transaction expense = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, negativeWallet, category, Money.of("50000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 15)), "Chi vượt số dư", null);

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                List.of(negativeWallet),
                List.of(expense),
                List.of(category),
                List.of(group),
                Collections.emptyList());

        assertFalse(report.isFullyConsistent());
        assertFalse(report.isWalletBalanceConsistent());
        assertTrue(report.getIssues().stream().anyMatch(i -> "BALANCE_MISMATCH".equals(i.getIssueType())));
    }

    @Test
    @DisplayName("reconcile should flag IncomeExpenseTransaction referencing wallet not present in wallets list")
    void reconcileShouldFlagIncomeExpenseTransactionWithMissingWallet() {
        Wallet orphanWallet = new Wallet(UUID.randomUUID(), "Ví Ngoài Hệ Thống", "", false);
        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Chi tiêu", TransactionType.EXPENSE, "icon", "#fff");
        Category category = new Category(
                UUID.randomUUID(), "Ăn uống", group.getNeedGroupId(), TransactionType.EXPENSE, "icon", "#fff");
        Transaction orphanTxn = new IncomeExpenseTransaction(
                UUID.randomUUID(), TransactionType.EXPENSE, orphanWallet, category, Money.of("30000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 15)), "Giao dịch ví mồ côi", null);

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                Collections.emptyList(),
                List.of(orphanTxn),
                List.of(category),
                List.of(group),
                Collections.emptyList());

        assertFalse(report.isFullyConsistent());
        assertFalse(report.isWalletBalanceConsistent());
        assertTrue(report.getIssues().stream().anyMatch(i -> "BALANCE_MISMATCH".equals(i.getIssueType())));
    }

    @Test
    @DisplayName("reconcile should flag BalanceAdjustmentRecord referencing wallet not present in wallets list")
    void reconcileShouldFlagBalanceAdjustmentWithMissingWallet() {
        BalanceAdjustmentRecord orphanAdj = BalanceAdjustmentRecord.reconstitute(
                UUID.randomUUID(), UUID.randomUUID(), Money.ZERO, Money.of("100000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 15)), "Kiểm kê mồ côi", LocalDateTime.of(2026, 1, 15, 9, 0));

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(orphanAdj));

        assertFalse(report.isFullyConsistent());
        assertFalse(report.isWalletBalanceConsistent());
        assertTrue(report.getIssues().stream().anyMatch(i -> "BALANCE_MISMATCH".equals(i.getIssueType())));
    }

    @Test
    @DisplayName("reconcile(BackupPayload) should accept valid BackupPayload and return report")
    void reconcileWithBackupPayloadShouldReturnReport() {
        BackupPayload payload = BackupPayload.empty();
        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(payload);

        assertNotNull(report);
        assertTrue(report.isFullyConsistent());
    }

    @Test
    @DisplayName("reconcile(BackupPayload) should throw NullPointerException when payload is null")
    void reconcileWithNullBackupPayloadShouldThrowNpe() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> SystemDataReconciler.reconcile((BackupPayload) null));
        assertTrue(ex.getMessage().contains("BackupPayload không được để null"));
    }

    @Test
    @DisplayName("reconcile should flag fee inconsistency when fee category is not of EXPENSE type in system")
    void reconcileShouldFlagFeeWithIncomeCategory() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Chính", "", false);
        wallet.credit(Money.of("50000.00"));
        NeedGroup group = new NeedGroup(UUID.randomUUID(), "Chi Tiêu", TransactionType.EXPENSE, "icon", "#fff");
        Category feeCategory = new Category(UUID.randomUUID(), "Phí Chuyển", group.getNeedGroupId(), TransactionType.EXPENSE, "icon", "#fff");

        UUID txnId = UUID.randomUUID();
        TransactionFee validDomainFee = new TransactionFee(
                UUID.randomUUID(), txnId, Money.of("5000.00"), wallet, feeCategory);
        IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                txnId, TransactionType.EXPENSE, wallet, feeCategory, Money.of("45000.00"),
                TransactionDate.of(LocalDate.of(2026, 1, 15)), "Chi có phí", validDomainFee);

        // Giả lập dữ liệu hệ thống bị biến đổi: danh mục cùng UUID bị đổi sang INCOME
        NeedGroup incomeGroup = new NeedGroup(UUID.randomUUID(), "Thu Nhập", TransactionType.INCOME, "icon", "#fff");
        Category corruptedCategory = new Category(
                feeCategory.getCategoryId(), "Phí Bị Sửa", incomeGroup.getNeedGroupId(), TransactionType.INCOME, "icon", "#fff");

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                List.of(wallet),
                List.of(txn),
                List.of(corruptedCategory),
                List.of(group, incomeGroup),
                Collections.emptyList());

        assertFalse(report.isFullyConsistent());
        assertFalse(report.isFeeConsistent());
        assertTrue(report.getIssues().stream().anyMatch(i -> "FEE_INCONSISTENCY".equals(i.getIssueType())));
    }
}
