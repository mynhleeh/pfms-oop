package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.presentation.dto.data.ReconciliationReportDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
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
        ReconciliationReportDTO report = SystemDataReconciler.reconcile(
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

        ReconciliationReportDTO report = SystemDataReconciler.reconcile(
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
        ReconciliationReportDTO report = SystemDataReconciler.reconcile(
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
}
