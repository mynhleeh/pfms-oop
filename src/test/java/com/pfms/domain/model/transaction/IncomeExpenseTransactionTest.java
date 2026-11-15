package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IncomeExpenseTransaction Domain Model Tests")
class IncomeExpenseTransactionTest {

        private Wallet activeWallet;
        private Wallet secondaryWallet;
        private Category activeExpenseCategory;
        private Category activeIncomeCategory;
        private TransactionDate validDate;

        @BeforeEach
        void setUp() {
                activeWallet = new Wallet("Ví tiền mặt", "Ví tiêu dùng hàng ngày", false);
                activeWallet.credit(Money.of(5_000_000));

                secondaryWallet = new Wallet("Ví tiết kiệm", "Ví dự phòng", false);
                secondaryWallet.credit(Money.of(10_000_000));

                activeExpenseCategory = new Category(
                                UUID.randomUUID(), "Ăn uống", UUID.randomUUID(),
                                TransactionType.EXPENSE, "food_icon", "#FF5722");

                activeIncomeCategory = new Category(
                                UUID.randomUUID(), "Tiền lương", UUID.randomUUID(),
                                TransactionType.INCOME, "salary_icon", "#4CAF50");

                validDate = TransactionDate.of(2026, 3, 15);
        }

        @Nested
        @DisplayName("Happy Path Tests")
        class HappyPathTests {

                @Test
                @DisplayName("Khởi tạo giao dịch chi tiêu hợp lệ không có phụ phí thành công")
                void testCreateExpenseTransaction_WithoutFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money amount = Money.of(250_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        amount, validDate, "Ăn trưa đối tác", null);

                        assertEquals(txId, tx.getTransactionId());
                        assertEquals(TransactionType.EXPENSE, tx.getTransactionType());
                        assertEquals(activeWallet, tx.getWallet());
                        assertEquals(activeExpenseCategory, tx.getCategory());
                        assertEquals(amount, tx.getAmount());
                        assertEquals(validDate, tx.getTransactionDate());
                        assertEquals("Ăn trưa đối tác", tx.getNote());
                        assertTrue(tx.getFee().isEmpty());
                        assertNotNull(tx.getCreatedAt());
                }

                @Test
                @DisplayName("Khởi tạo giao dịch thu nhập hợp lệ có phụ phí thành công")
                void testCreateIncomeTransaction_WithFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money amount = Money.of(20_000_000);
                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(10_000), activeWallet, activeExpenseCategory);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, activeWallet, activeIncomeCategory,
                                        amount, validDate, "Lương tháng 03", fee);

                        assertEquals(txId, tx.getTransactionId());
                        assertEquals(TransactionType.INCOME, tx.getTransactionType());
                        assertEquals(amount, tx.getAmount());
                        assertTrue(tx.getFee().isPresent());
                        assertEquals(fee, tx.getFee().get());
                }

                @Test
                @DisplayName("applyToWallet() với giao dịch thu nhập sẽ ghi có vào ví và trích phụ phí nếu có")
                void testApplyToWallet_IncomeWithFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance(); // 5_000_000
                        Money incomeAmount = Money.of(2_000_000);
                        Money feeAmount = Money.of(20_000);

                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, activeWallet, activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, activeWallet, activeIncomeCategory,
                                        incomeAmount, validDate, "Thu nhập làm thêm", fee);

                        tx.applyToWallet();

                        // 5_000_000 + 2_000_000 - 20_000 = 6_980_000
                        assertEquals(initialBalance.add(incomeAmount).subtract(feeAmount),
                                        activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("applyToWallet() với giao dịch chi tiêu sẽ trừ số tiền và trừ phụ phí khỏi ví")
                void testApplyToWallet_ExpenseWithFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance(); // 5_000_000
                        Money expenseAmount = Money.of(1_500_000);
                        Money feeAmount = Money.of(15_000);

                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, activeWallet, activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        expenseAmount, validDate, "Mua sắm", fee);

                        tx.applyToWallet();

                        // 5_000_000 - 1_500_000 - 15_000 = 3_485_000
                        assertEquals(initialBalance.subtract(expenseAmount).subtract(feeAmount),
                                        activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet() với giao dịch thu nhập sẽ hoàn phí và trừ khoản thu nhập khỏi ví")
                void testRevertFromWallet_IncomeWithFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money incomeAmount = Money.of(1_000_000);
                        Money feeAmount = Money.of(10_000);

                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, activeWallet, activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, activeWallet, activeIncomeCategory,
                                        incomeAmount, validDate, "Thu hồi", fee);

                        tx.applyToWallet();
                        tx.revertFromWallet();

                        assertEquals(initialBalance, activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet() với giao dịch chi tiêu sẽ hoàn trả số tiền và phụ phí vào ví")
                void testRevertFromWallet_ExpenseWithFee_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money expenseAmount = Money.of(500_000);
                        Money feeAmount = Money.of(5_000);

                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, activeWallet, activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        expenseAmount, validDate, "Mua đồ", fee);

                        tx.applyToWallet();
                        tx.revertFromWallet();

                        assertEquals(initialBalance, activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("updateDetails() hoàn nguyên giao dịch cũ và áp dụng hạch toán thông tin mới")
                void testUpdateDetails_SameWallet_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance(); // 5_000_000
                        Money oldAmount = Money.of(400_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        oldAmount, validDate, "Ăn trưa cũ", null);
                        tx.applyToWallet();
                        assertEquals(initialBalance.subtract(oldAmount), activeWallet.getCurrentBalance());

                        // Cập nhật số tiền mới 600k kèm phụ phí 10k
                        Money newAmount = Money.of(600_000);
                        Money feeAmount = Money.of(10_000);
                        TransactionFee newFee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, activeWallet, activeExpenseCategory);

                        tx.updateDetails(activeWallet, activeExpenseCategory, newAmount, validDate, "Ăn trưa buffet",
                                        newFee);

                        // Số dư sau cập nhật: 5_000_000 - 600_000 - 10_000 = 4_390_000
                        assertEquals(initialBalance.subtract(newAmount).subtract(feeAmount),
                                        activeWallet.getCurrentBalance());
                        assertEquals(newAmount, tx.getAmount());
                        assertEquals("Ăn trưa buffet", tx.getNote());
                        assertTrue(tx.getFee().isPresent());
                        assertEquals(newFee, tx.getFee().get());
                }

                @Test
                @DisplayName("updateDetails() chuyển ví thanh toán hoàn nguyên ví cũ và trừ tiền ở ví mới")
                void testUpdateDetails_DifferentWallet_Success() {
                        UUID txId = UUID.randomUUID();
                        Money initialWallet1 = activeWallet.getCurrentBalance(); // 5_000_000
                        Money initialWallet2 = secondaryWallet.getCurrentBalance(); // 10_000_000
                        Money amount = Money.of(1_000_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        amount, validDate, "Chuyển ví thanh toán", null);
                        tx.applyToWallet();
                        assertEquals(initialWallet1.subtract(amount), activeWallet.getCurrentBalance());

                        // Đổi sang secondaryWallet
                        tx.updateDetails(secondaryWallet, activeExpenseCategory, amount, validDate, "Đã đổi ví", null);

                        // Ví cũ được hoàn nguyên, ví mới bị trừ
                        assertEquals(initialWallet1, activeWallet.getCurrentBalance());
                        assertEquals(initialWallet2.subtract(amount), secondaryWallet.getCurrentBalance());
                        assertEquals(secondaryWallet, tx.getWallet());
                }

                @Test
                @DisplayName("applyToWallet() với giao dịch thu nhập độc lập không phụ phí cộng tiền vào ví")
                void testApplyToWallet_IncomeSolo_CreditsWallet() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money incomeAmount = Money.of(1_500_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, activeWallet, activeIncomeCategory,
                                        incomeAmount, validDate, "Lương không phí", null);

                        tx.applyToWallet();

                        assertEquals(initialBalance.add(incomeAmount), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("applyToWallet() với giao dịch chi tiêu độc lập không phụ phí trừ tiền khỏi ví")
                void testApplyToWallet_ExpenseSolo_DebitsWallet() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money expenseAmount = Money.of(750_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        expenseAmount, validDate, "Mua sắm không phí", null);

                        tx.applyToWallet();

                        assertEquals(initialBalance.subtract(expenseAmount), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet() với giao dịch thu nhập không phụ phí trừ tiền đã cộng")
                void testRevertFromWallet_IncomeSolo_DebitsWallet() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money incomeAmount = Money.of(800_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, activeWallet, activeIncomeCategory,
                                        incomeAmount, validDate, "Thu nhập solo", null);

                        tx.applyToWallet();
                        tx.revertFromWallet();

                        assertEquals(initialBalance, activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet() với giao dịch chi tiêu không phụ phí hoàn trả lại tiền ví")
                void testRevertFromWallet_ExpenseSolo_CreditsWallet() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money expenseAmount = Money.of(300_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        expenseAmount, validDate, "Chi tiêu solo", null);

                        tx.applyToWallet();
                        tx.revertFromWallet();

                        assertEquals(initialBalance, activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("reconstitute() giữ nguyên createdAt lịch sử và cho phép ví/danh mục không ACTIVE")
                void testReconstitute_PreservesHistoricalCreatedAtAndAllowsInactiveReferences() {
                        UUID txId = UUID.randomUUID();
                        LocalDateTime historicalTime = LocalDateTime.of(2024, 2, 14, 8, 30, 0);
                        Wallet archivedWallet = activeWallet.copy();
                        archivedWallet.archive();
                        Category deletedCategory = Category.reconstitute(
                                        activeExpenseCategory.getCategoryId(),
                                        "Ăn uống cũ",
                                        activeExpenseCategory.getNeedGroupId(),
                                        TransactionType.EXPENSE,
                                        EntityStatus.DELETED,
                                        "",
                                        "",
                                        historicalTime.minusDays(10));
                        TransactionFee historicalFee = TransactionFee.reconstitute(
                                        UUID.randomUUID(), txId, Money.of(5_000), archivedWallet, deletedCategory);

                        IncomeExpenseTransaction restored = IncomeExpenseTransaction.reconstitute(
                                        txId, TransactionType.EXPENSE, archivedWallet, deletedCategory,
                                        Money.of(120_000), validDate, "Khôi phục từ backup", historicalFee,
                                        historicalTime);

                        assertEquals(txId, restored.getTransactionId());
                        assertSame(archivedWallet, restored.getWallet());
                        assertSame(deletedCategory, restored.getCategory());
                        assertTrue(restored.getFee().isPresent());
                        assertSame(historicalFee, restored.getFee().get());
                        assertEquals(historicalTime, restored.getCreatedAt());
                }

                @Test
                @DisplayName("revertFromWallet(liveWallet) hoàn tiền và phí vào instance ví sống thay vì snapshot cũ")
                void testRevertFromWallet_LiveWalletInstanceWithFee_RevertsLiveBalance() {
                        UUID txId = UUID.randomUUID();
                        Money amount = Money.of(400_000);
                        Money feeAmount = Money.of(10_000);
                        Wallet snapshotWallet = activeWallet.copy();
                        TransactionFee snapshotFee = new TransactionFee(
                                        UUID.randomUUID(), txId, feeAmount, snapshotWallet, activeExpenseCategory);

                        IncomeExpenseTransaction tx = IncomeExpenseTransaction.reconstitute(
                                        txId, TransactionType.EXPENSE, snapshotWallet, activeExpenseCategory,
                                        amount, validDate, "Snapshot tx", snapshotFee,
                                        LocalDateTime.of(2025, 1, 15, 12, 0));

                        activeWallet.debit(amount.add(feeAmount)); // live balance: 5_000_000 - 410_000 = 4_590_000
                        Money snapshotBeforeRevert = snapshotWallet.getCurrentBalance();

                        tx.revertFromWallet(activeWallet);

                        assertEquals(Money.of(5_000_000), activeWallet.getCurrentBalance());
                        assertEquals(snapshotBeforeRevert, snapshotWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("updateDetails() cho phép giữ nguyên danh mục đã xóa mềm nếu không đổi sang danh mục khác")
                void testUpdateDetails_KeepUnchangedDeletedCategory_Success() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(200_000), validDate, "Gốc", null);
                        tx.applyToWallet();

                        activeExpenseCategory.markDeleted();

                        tx.updateDetails(activeWallet, activeExpenseCategory, Money.of(350_000), validDate,
                                        "Sửa số tiền giữ danh mục cũ", null);

                        assertEquals(Money.of(4_650_000), activeWallet.getCurrentBalance());
                        assertEquals(Money.of(350_000), tx.getAmount());
                        assertSame(activeExpenseCategory, tx.getCategory());
                }

                @Test
                @DisplayName("updateDetails(liveOld, newWallet, ...) chuyển ví thành công và rollback đầy đủ ví cũ khi ví mới thiếu tiền")
                void testUpdateDetails_SevenArgsTwoLiveWalletsMoveAndRollback() {
                        UUID txId = UUID.randomUUID();
                        Money oldAmount = Money.of(500_000);
                        Money oldFeeAmount = Money.of(10_000);
                        Wallet snapshotOldWallet = activeWallet.copy();
                        TransactionFee oldFee = new TransactionFee(
                                        UUID.randomUUID(), txId, oldFeeAmount, snapshotOldWallet,
                                        activeExpenseCategory);
                        IncomeExpenseTransaction tx = IncomeExpenseTransaction.reconstitute(
                                        txId, TransactionType.EXPENSE, snapshotOldWallet, activeExpenseCategory,
                                        oldAmount, validDate, "Ban đầu", oldFee, LocalDateTime.of(2025, 2, 1, 9, 0));
                        activeWallet.debit(oldAmount.add(oldFeeAmount)); // live activeWallet = 4_490_000

                        // Case 1: Ví đích thiếu số dư -> phải rollback toàn bộ trên activeWallet (vẫn 4_490_000)
                        Wallet smallTargetWallet = new Wallet("Ví nhỏ");
                        smallTargetWallet.credit(Money.of(100_000));
                        assertThrows(InsufficientBalanceException.class, () -> tx.updateDetails(
                                        activeWallet, smallTargetWallet, activeExpenseCategory, Money.of(600_000),
                                        validDate, "Vượt số dư ví đích", null));
                        assertEquals(Money.of(4_490_000), activeWallet.getCurrentBalance());
                        assertEquals(Money.of(100_000), smallTargetWallet.getCurrentBalance());
                        assertEquals(oldAmount, tx.getAmount());
                        assertEquals("Ban đầu", tx.getNote());

                        // Case 2: Chuyển sang secondaryWallet đủ số dư -> activeWallet hoàn 510k về 5M, secondaryWallet bị trừ 600k còn 9.4M
                        tx.updateDetails(activeWallet, secondaryWallet, activeExpenseCategory, Money.of(600_000),
                                        validDate, "Chuyển ví thành công", null);
                        assertEquals(Money.of(5_000_000), activeWallet.getCurrentBalance());
                        assertEquals(Money.of(9_400_000), secondaryWallet.getCurrentBalance());
                        assertSame(secondaryWallet, tx.getWallet());
                }
        }

        @Nested
        @DisplayName("Null and Invalid Input Tests")
        class NullAndInvalidInputTests {

                @Test
                @DisplayName("Khởi tạo với transactionType null ném NullPointerException")
                void testConstructor_NullTransactionType_ThrowsException() {
                        assertThrows(NullPointerException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), null, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Note", null));
                }

                @Test
                @DisplayName("Khởi tạo với wallet null ném NullPointerException")
                void testConstructor_NullWallet_ThrowsException() {
                        assertThrows(NullPointerException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, null, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Note", null));
                }

                @Test
                @DisplayName("Khởi tạo với category null ném NullPointerException")
                void testConstructor_NullCategory_ThrowsException() {
                        assertThrows(NullPointerException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, null,
                                        Money.of(100_000), validDate, "Note", null));
                }

                @Test
                @DisplayName("Khởi tạo với wallet đã bị xóa ném BusinessException")
                void testConstructor_DeletedWallet_ThrowsException() {
                        Wallet deletedWallet = new Wallet("Ví đã xóa");
                        deletedWallet.delete();

                        BusinessException ex = assertThrows(BusinessException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, deletedWallet,
                                        activeExpenseCategory,
                                        Money.of(100_000), validDate, "Note", null));
                        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Khởi tạo với category đã bị xóa mềm ném BusinessException")
                void testConstructor_DeletedCategory_ThrowsException() {
                        Category deletedCategory = new Category(
                                        UUID.randomUUID(), "Mua sắm", UUID.randomUUID(),
                                        TransactionType.EXPENSE, "", "");
                        deletedCategory.markDeleted();

                        BusinessException ex = assertThrows(BusinessException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, deletedCategory,
                                        Money.of(100_000), validDate, "Note", null));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Khởi tạo giao dịch chi tiêu với danh mục thu nhập ném BusinessException ERR_CAT_001")
                void testConstructor_IncompatibleCategory_ExpenseWithIncomeCategory_ThrowsException() {
                        BusinessException ex = assertThrows(BusinessException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, activeIncomeCategory,
                                        Money.of(100_000), validDate, "Sai danh mục", null));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Khởi tạo giao dịch thu nhập với danh mục chi tiêu ném BusinessException ERR_CAT_001")
                void testConstructor_IncompatibleCategory_IncomeWithExpenseCategory_ThrowsException() {
                        BusinessException ex = assertThrows(BusinessException.class, () -> new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.INCOME, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Sai danh mục", null));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("Khởi tạo với fee có ví không trùng với ví giao dịch ném BusinessException ERR_FEE_001")
                void testConstructor_FeeWithDifferentWallet_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(5_000), secondaryWallet,
                                        activeExpenseCategory);

                        BusinessException ex = assertThrows(BusinessException.class, () -> new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Khác ví", fee));
                        assertEquals(ErrorCode.ERR_FEE_001, ex.getErrorCode());
                }
        }

        @Nested
        @DisplayName("State Transitions and Invariants Tests")
        class StateTransitionsAndInvariantsTests {

                @Test
                @DisplayName("applyToWallet() khi số dư ví không đủ chi trả khoản chi ném InsufficientBalanceException")
                void testApplyToWallet_Expense_InsufficientBalance_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        // Ví có 5_000_000, chi 6_000_000
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(6_000_000), validDate, "Vượt số dư", null);

                        assertThrows(InsufficientBalanceException.class, tx::applyToWallet);
                }

                @Test
                @DisplayName("applyToWallet() khi số dư ví đủ tiền chi tiêu nhưng không đủ trả thêm phụ phí ném InsufficientBalanceException")
                void testApplyToWallet_ExpenseWithFee_TotalExceedsBalance_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        Wallet poorWallet = new Wallet("Ví ít tiền");
                        poorWallet.credit(Money.of(100_000));

                        // Chi 95k + phí 10k = 105k > 100k
                        TransactionFee fee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(10_000), poorWallet, activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, poorWallet, activeExpenseCategory,
                                        Money.of(95_000), validDate, "Hết tiền", fee);

                        assertThrows(InsufficientBalanceException.class, tx::applyToWallet);
                        // Bảo toàn số dư ví không bị trừ dở dang
                        assertEquals(Money.of(100_000), poorWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet() cho thu nhập khi số dư hiện tại không đủ để hoàn nguyên ném InsufficientBalanceException")
                void testRevertFromWallet_Income_InsufficientBalanceToRevert_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        Wallet freshWallet = new Wallet("Ví mới");
                        freshWallet.credit(Money.of(100_000)); // Nguồn ban đầu

                        IncomeExpenseTransaction incomeTx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, freshWallet, activeIncomeCategory,
                                        Money.of(1_000_000), validDate, "Thu nhập", null);
                        incomeTx.applyToWallet(); // Số dư lên 1_100_000

                        // Người dùng chi tiêu mất 1_050_000, số dư còn 50_000
                        freshWallet.debit(Money.of(1_050_000));
                        assertEquals(Money.of(50_000), freshWallet.getCurrentBalance());

                        // Cố gắng hoàn nguyên khoản thu nhập 1_000_000 trong khi ví chỉ còn 50k
                        assertThrows(InsufficientBalanceException.class, incomeTx::revertFromWallet);
                        assertEquals(Money.of(50_000), freshWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("applyToWallet() khi ví bị đưa vào trạng thái lưu trữ ném BusinessException hoặc IllegalStateException")
                void testApplyToWallet_ArchivedWallet_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(50_000), validDate, "Giao dịch", null);

                        activeWallet.archive();

                        assertThrows(Exception.class, tx::applyToWallet);
                }

                @Test
                @DisplayName("updateDetails() với category khác loại thu chi ném BusinessException ERR_CAT_001")
                void testUpdateDetails_IncompatibleCategory_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(50_000), validDate, "Giao dịch", null);

                        BusinessException ex = assertThrows(BusinessException.class,
                                        () -> tx.updateDetails(activeWallet, activeIncomeCategory, Money.of(50_000),
                                                        validDate, "Đổi", null));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("revertFromWallet() khi ví bị lưu trữ ném BusinessException ERR_WAL_001")
                void testRevertFromWallet_ArchivedWallet_ThrowsException() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(50_000), validDate, "Giao dịch", null);
                        tx.applyToWallet();

                        activeWallet.archive();

                        BusinessException ex = assertThrows(BusinessException.class, tx::revertFromWallet);
                        assertEquals(ErrorCode.ERR_WAL_001, ex.getErrorCode());
                }

                @Test
                @DisplayName("updateDetails() khi tham số newAmount hoặc newDate không hợp lệ ném ngoại lệ và không làm thay đổi số dư")
                void testUpdateDetails_InvalidInput_FailsFastWithoutMutatingWallet() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance();
                        Money originalAmount = Money.of(200_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        originalAmount, validDate, "Gốc", null);
                        tx.applyToWallet();
                        assertEquals(initialBalance.subtract(originalAmount), activeWallet.getCurrentBalance());

                        // newAmount âm
                        assertThrows(BusinessException.class, () -> tx.updateDetails(
                                        activeWallet, activeExpenseCategory, Money.of(-50_000), validDate, "Sai tiền", null));

                        // newDate null
                        assertThrows(NullPointerException.class, () -> tx.updateDetails(
                                        activeWallet, activeExpenseCategory, Money.of(300_000), null, "Sai ngày", null));

                        // Số dư ví và trạng thái giao dịch phải được bảo toàn nguyên vẹn
                        assertEquals(initialBalance.subtract(originalAmount), activeWallet.getCurrentBalance());
                        assertEquals(originalAmount, tx.getAmount());
                        assertEquals("Gốc", tx.getNote());
                }

                @Test
                @DisplayName("updateDetails() khi số dư ví không đủ chi trả số tiền mới sẽ tự động rollback về trạng thái ban đầu")
                void testUpdateDetails_Expense_InsufficientBalance_RollsBackToPreviousState() {
                        UUID txId = UUID.randomUUID();
                        Money initialBalance = activeWallet.getCurrentBalance(); // 5_000_000
                        Money originalAmount = Money.of(1_000_000);

                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        originalAmount, validDate, "Chi 1M", null);
                        tx.applyToWallet();
                        // Số dư còn 4_000_000
                        assertEquals(initialBalance.subtract(originalAmount), activeWallet.getCurrentBalance());

                        // Cập nhật số tiền mới 10M (vượt quá 5M của ví)
                        Money hugeAmount = Money.of(10_000_000);
                        assertThrows(InsufficientBalanceException.class, () -> tx.updateDetails(
                                        activeWallet, activeExpenseCategory, hugeAmount, validDate, "Chi 10M", null));

                        // Kiểm chứng Rollback: ví vẫn ở trạng thái đã trừ 1M (số dư 4M), giao dịch vẫn giữ 1M
                        assertEquals(initialBalance.subtract(originalAmount), activeWallet.getCurrentBalance());
                        assertEquals(originalAmount, tx.getAmount());
                        assertEquals("Chi 1M", tx.getNote());
                }

                @Test
                @DisplayName("updateDetails() từ chối khi đổi sang một danh mục khác đang ở trạng thái DELETED (ERR_CAT_001)")
                void testUpdateDetails_ChangeToDifferentDeletedCategory_ThrowsBusinessException() {
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Gốc", null);
                        tx.applyToWallet();

                        Category otherDeletedCategory = new Category(
                                        UUID.randomUUID(), "Danh mục khác đã xóa", UUID.randomUUID(),
                                        TransactionType.EXPENSE, "", "");
                        otherDeletedCategory.markDeleted();

                        BusinessException ex = assertThrows(BusinessException.class, () -> tx.updateDetails(
                                        activeWallet, otherDeletedCategory, Money.of(150_000), validDate, "Đổi", null));
                        assertEquals(ErrorCode.ERR_CAT_001, ex.getErrorCode());
                        assertEquals(Money.of(4_900_000), activeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("revertFromWallet(liveWallet) từ chối khi truyền vào ví khác ID với ví của giao dịch")
                void testRevertFromWallet_LiveWalletIdMismatch_ThrowsIllegalArgumentException() {
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Gốc", null);

                        assertThrows(IllegalArgumentException.class, () -> tx.revertFromWallet(secondaryWallet));
                }

                @Test
                @DisplayName("applyToWallet() cho INCOME với phí giữ snapshot ví tách rời hạch toán đúng trên ví giao dịch và không mutate ví snapshot")
                void testApplyToWallet_IncomeWithDetachedFeeWallet_AvoidsSplitBrain() {
                        Wallet liveWallet = new Wallet("Ví 0đ");
                        Wallet snapshotFeeWallet = liveWallet.copy();
                        UUID txId = UUID.randomUUID();
                        TransactionFee detachedFee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(20_000), snapshotFeeWallet,
                                        activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, liveWallet, activeIncomeCategory,
                                        Money.of(100_000), validDate, "Thu nhập kèm phí", detachedFee);

                        assertDoesNotThrow(tx::applyToWallet);

                        assertEquals(Money.of(80_000), liveWallet.getCurrentBalance());
                        assertEquals(Money.ZERO, snapshotFeeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("applyToWallet() cho EXPENSE với phí giữ snapshot ví tách rời trừ đủ tiền hàng và phí trên ví giao dịch")
                void testApplyToWallet_ExpenseWithDetachedFeeWallet_DebitsLiveWalletIncludingFee() {
                        Wallet snapshotFeeWallet = activeWallet.copy();
                        UUID txId = UUID.randomUUID();
                        TransactionFee detachedFee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(15_000), snapshotFeeWallet,
                                        activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Chi tiêu kèm phí", detachedFee);

                        tx.applyToWallet();

                        assertEquals(Money.of(4_885_000), activeWallet.getCurrentBalance());
                        assertEquals(Money.of(5_000_000), snapshotFeeWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("applyToWallet() cho INCOME ném InsufficientBalanceException và giữ nguyên ví khi thu nhập cộng số dư hiện có nhỏ hơn phí")
                void testApplyToWallet_IncomeWithFeeExceedingProjectedBalance_ThrowsWithoutMutating() {
                        Wallet poorWallet = new Wallet("Ví rỗng");
                        UUID txId = UUID.randomUUID();
                        TransactionFee highFee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(80_000), poorWallet,
                                        activeExpenseCategory);
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.INCOME, poorWallet, activeIncomeCategory,
                                        Money.of(50_000), validDate, "Phí lớn hơn thu", highFee);

                        assertThrows(InsufficientBalanceException.class, tx::applyToWallet);
                        assertEquals(Money.ZERO, poorWallet.getCurrentBalance());
                }

                @Test
                @DisplayName("updateDetails(liveOld, targetWallet, ...) trên INCOME khi thất bại hoàn nguyên cả số dư ví cũ, giữ ví đích nguyên vẹn và khôi phục nguyên bản reference ví/phí")
                void testUpdateDetails_IncomeMoveFails_RollsBackAllReferencesAndTargetBalance() {
                        Wallet sourceLive = new Wallet("Nguồn 100k");
                        sourceLive.credit(Money.of(100_000));
                        Wallet sourceSnapshot = sourceLive.copy();
                        Wallet targetLive = new Wallet("Đích 0đ");
                        UUID txId = UUID.randomUUID();
                        TransactionFee oldFee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(10_000), sourceSnapshot,
                                        activeExpenseCategory);
                        IncomeExpenseTransaction tx = IncomeExpenseTransaction.reconstitute(
                                        txId, TransactionType.INCOME, sourceSnapshot, activeIncomeCategory,
                                        Money.of(50_000), validDate, "Thu cũ", oldFee,
                                        LocalDateTime.of(2025, 2, 1, 9, 0));
                        sourceLive.credit(Money.of(50_000));
                        sourceLive.debit(Money.of(10_000)); // 140_000

                        TransactionFee excessiveNewFee = new TransactionFee(
                                        UUID.randomUUID(), txId, Money.of(80_000), targetLive,
                                        activeExpenseCategory);

                        assertThrows(InsufficientBalanceException.class, () -> tx.updateDetails(
                                        sourceLive, targetLive, activeIncomeCategory, Money.of(50_000),
                                        validDate, "Chuyển sang đích thất bại", excessiveNewFee));

                        assertEquals(Money.of(140_000), sourceLive.getCurrentBalance());
                        assertEquals(Money.ZERO, targetLive.getCurrentBalance());
                        assertSame(sourceSnapshot, tx.getWallet());
                        assertSame(oldFee, tx.getFee().orElseThrow());
                        assertEquals(Money.of(50_000), tx.getAmount());
                        assertEquals("Thu cũ", tx.getNote());
                }
        }

        @Nested
        @DisplayName("Robustness and Identity Tests")
        class RobustnessAndIdentityTests {

                @Test
                @DisplayName("Hai đối tượng cùng transactionId là bằng nhau (equals và hashCode)")
                void testEqualsAndHashCode() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx1 = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Note 1", null);

                        IncomeExpenseTransaction tx2 = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, secondaryWallet, activeExpenseCategory,
                                        Money.of(200_000), validDate, "Note 2", null);

                        IncomeExpenseTransaction tx3 = new IncomeExpenseTransaction(
                                        UUID.randomUUID(), TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Note 1", null);

                        assertEquals(tx1, tx2);
                        assertEquals(tx1.hashCode(), tx2.hashCode());
                        assertNotEquals(tx1, tx3);
                        assertNotEquals(tx1, null);
                        assertNotEquals(tx1, "string");
                }

                @Test
                @DisplayName("toString() trả về chuỗi chứa thông tin định danh, loại và số tiền")
                void testToString() {
                        UUID txId = UUID.randomUUID();
                        IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                        txId, TransactionType.EXPENSE, activeWallet, activeExpenseCategory,
                                        Money.of(100_000), validDate, "Ghi chú test", null);

                        String result = tx.toString();
                        assertTrue(result.contains(txId.toString()));
                        assertTrue(result.contains("EXPENSE"));
                        assertTrue(result.contains("100.000"));
                }
        }
}
