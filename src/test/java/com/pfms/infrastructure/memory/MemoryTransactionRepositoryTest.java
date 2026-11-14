package com.pfms.infrastructure.memory;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.*;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemoryTransactionRepository Unit Tests")
class MemoryTransactionRepositoryTest {

    private MemoryTransactionRepository repository;
    private Wallet walletCash;
    private Wallet walletBank;
    private Category categorySalary;
    private Category categoryFood;
    private Category categoryTransferFee;

    private IncomeExpenseTransaction incomeTx;
    private IncomeExpenseTransaction expenseTx;
    private TransferTransaction transferTx;

    private final UUID incomeTxId = UUID.randomUUID();
    private final UUID expenseTxId = UUID.randomUUID();
    private final UUID transferTxId = UUID.randomUUID();
    private final UUID catSalaryId = UUID.randomUUID();
    private final UUID catFoodId = UUID.randomUUID();
    private final UUID catTransferFeeId = UUID.randomUUID();
    private final UUID needGroupId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = new MemoryTransactionRepository();

        walletCash = new Wallet(
                "wallet-cash-id",
                "Tiền mặt",
                "Ví tiền mặt",
                true,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("10000000.00"));

        walletBank = new Wallet(
                "wallet-bank-id",
                "Ngân hàng",
                "Tài khoản thanh toán",
                false,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("5000000.00"));

        categorySalary = new Category(catSalaryId, "Lương chính", needGroupId, TransactionType.INCOME, "icon", "color");
        categoryFood = new Category(catFoodId, "Ăn uống", needGroupId, TransactionType.EXPENSE, "icon", "color");
        categoryTransferFee = new Category(catTransferFeeId, "Phí ngân hàng", needGroupId, TransactionType.EXPENSE, "icon", "color");

        incomeTx = new IncomeExpenseTransaction(
                incomeTxId,
                TransactionType.INCOME,
                walletBank,
                categorySalary,
                Money.of("15000000.00"),
                TransactionDate.of(LocalDate.of(2026, 3, 1)),
                "Nhận lương tháng 3",
                null);

        TransactionFee expenseFee = new TransactionFee(
                UUID.randomUUID(),
                expenseTxId,
                Money.of("5000.00"),
                walletCash,
                categoryTransferFee);

        expenseTx = new IncomeExpenseTransaction(
                expenseTxId,
                TransactionType.EXPENSE,
                walletCash,
                categoryFood,
                Money.of("200000.00"),
                TransactionDate.of(LocalDate.of(2026, 3, 5)),
                "Ăn trưa",
                expenseFee);

        transferTx = new TransferTransaction(
                transferTxId,
                walletBank,
                walletCash,
                Money.of("2000000.00"),
                TransactionDate.of(LocalDate.of(2026, 3, 10)),
                "Rút tiền mặt ATM",
                null);
    }

    @Nested
    @DisplayName("Tests for Constructor and Base State")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize empty repository with proper ID extractor")
        void shouldInitializeWithDefaultIdExtractor() {
            assertNotNull(repository);
            assertTrue(repository.findAll().isEmpty());
            assertEquals(0, repository.findAll().size());

            repository.save(incomeTx);
            assertEquals(1, repository.findAll().size());
            assertTrue(repository.existsById(incomeTxId));
        }
    }

    @Nested
    @DisplayName("Tests for GenericRepository CRUD and Memory Extensions")
    class GenericRepositoryOperationsTests {

        @Test
        @DisplayName("Should save and retrieve transaction by ID")
        void shouldSaveAndFindById() {
            repository.save(incomeTx);

            Optional<Transaction> found = repository.findById(incomeTxId);
            assertTrue(found.isPresent());
            assertEquals(incomeTx, found.get());
        }

        @Test
        @DisplayName("Should throw NullPointerException when null passed to CRUD methods")
        void shouldThrowExceptionWhenNullPassedToCrud() {
            assertThrows(NullPointerException.class, () -> repository.save(null));
            assertThrows(NullPointerException.class, () -> repository.findById(null));
            assertThrows(NullPointerException.class, () -> repository.existsById(null));
            assertThrows(NullPointerException.class, () -> repository.deleteById(null));
        }

        @Test
        @DisplayName("Should return all saved transactions preserving count")
        void shouldReturnAllAndCount() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            assertEquals(2, repository.findAll().size());
            assertTrue(repository.existsById(incomeTxId));
            assertTrue(repository.existsById(expenseTxId));
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should delete transaction by ID and return boolean flag")
        void shouldDeleteById() {
            repository.save(incomeTx);

            assertTrue(repository.deleteById(incomeTxId));
            assertFalse(repository.existsById(incomeTxId));
            assertEquals(0, repository.findAll().size());
            assertFalse(repository.deleteById(incomeTxId));
        }

        @Test
        @DisplayName("Should clear all data from memory")
        void shouldClearAllData() {
            repository.save(incomeTx);
            repository.save(expenseTx);
            assertEquals(2, repository.findAll().size());

            repository.clear();

            assertEquals(0, repository.findAll().size());
            assertTrue(repository.findAll().isEmpty());
            assertFalse(repository.existsById(incomeTxId));
        }

        @Test
        @DisplayName("Should return unmodifiable list throwing exception on mutation")
        void shouldReturnUnmodifiableList() {
            repository.save(incomeTx);
            List<Transaction> list = repository.findAll();

            assertThrows(UnsupportedOperationException.class, list::clear);
            assertThrows(UnsupportedOperationException.class, () -> list.add(expenseTx));
        }

        @Test
        @DisplayName("Should return unmodifiable list from specialized queries")
        void shouldReturnUnmodifiableListFromCustomQueries() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            List<Transaction> byWallet = repository.findByWalletId(walletCash.getId());
            assertThrows(UnsupportedOperationException.class, byWallet::clear);

            List<Transaction> byDate = repository.findByDateRange(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10));
            assertThrows(UnsupportedOperationException.class, byDate::clear);

            List<Transaction> byCategory = repository.findByCategoryId(catSalaryId);
            assertThrows(UnsupportedOperationException.class, byCategory::clear);
        }
    }

    @Nested
    @DisplayName("Tests for findByWalletId")
    class FindByWalletIdTests {

        @Test
        @DisplayName("Should find transactions where wallet participates directly")
        void shouldFindWhenWalletIsInIncomeExpense() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            List<Transaction> cashTxs = repository.findByWalletId(walletCash.getId());
            assertEquals(1, cashTxs.size());
            assertEquals(expenseTxId, cashTxs.get(0).getTransactionId());

            List<Transaction> bankTxs = repository.findByWalletId(walletBank.getId());
            assertEquals(1, bankTxs.size());
            assertEquals(incomeTxId, bankTxs.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should find transactions where wallet is source or destination in transfer")
        void shouldFindWhenWalletIsInTransfer() {
            repository.save(transferTx);

            List<Transaction> sourceTxs = repository.findByWalletId(walletBank.getId());
            assertEquals(1, sourceTxs.size());
            assertEquals(transferTxId, sourceTxs.get(0).getTransactionId());

            List<Transaction> destTxs = repository.findByWalletId(walletCash.getId());
            assertEquals(1, destTxs.size());
            assertEquals(transferTxId, destTxs.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should find transactions where wallet is associated with transaction fee")
        void shouldFindWhenWalletIsAssociatedWithFee() {
            repository.save(expenseTx);

            List<Transaction> feeTxs = repository.findByWalletId(walletCash.getId());
            assertEquals(1, feeTxs.size());
            assertEquals(expenseTxId, feeTxs.get(0).getTransactionId());
            assertTrue(feeTxs.get(0).getFee().isPresent());
            assertEquals(walletCash.getId(), feeTxs.get(0).getFee().get().getWallet().getId());
        }

        @Test
        @DisplayName("Should support UUID overload for findByWalletId")
        void shouldSupportUuidOverload() {
            UUID walletUuid = UUID.randomUUID();
            Wallet uuidWallet = new Wallet(
                    walletUuid.toString(),
                    "Ví UUID",
                    "",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("1000.00"));

            IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    uuidWallet,
                    categorySalary,
                    Money.of("500.00"),
                    TransactionDate.of(LocalDate.of(2026, 3, 2)),
                    "Thu nhập",
                    null);
            repository.save(tx);

            List<Transaction> found = repository.findByWalletId(walletUuid);
            assertEquals(1, found.size());
            assertEquals(tx.getTransactionId(), found.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should throw NullPointerException or IllegalArgumentException on null or blank walletId")
        void shouldThrowOnNullOrBlankWalletId() {
            assertThrows(NullPointerException.class, () -> repository.findByWalletId((UUID) null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId((String) null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId("   "));
        }

        @Test
        @DisplayName("Should return empty list when wallet has no transactions")
        void shouldReturnEmptyListWhenWalletHasNoTransactions() {
            repository.save(incomeTx);

            List<Transaction> notFoundByString = repository.findByWalletId("non-existent-wallet-id");
            assertTrue(notFoundByString.isEmpty());

            List<Transaction> notFoundByUuid = repository.findByWalletId(UUID.randomUUID());
            assertTrue(notFoundByUuid.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests for findByDateRange")
    class FindByDateRangeTests {

        @Test
        @DisplayName("Should find transactions within date range sorted descending by date")
        void shouldFindWithinInclusiveDateRange() {
            repository.save(incomeTx);
            repository.save(expenseTx);
            repository.save(transferTx);

            List<Transaction> result = repository.findByDateRange(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 8));

            assertEquals(2, result.size());
            assertEquals(expenseTxId, result.get(0).getTransactionId());
            assertEquals(incomeTxId, result.get(1).getTransactionId());
        }

        @Test
        @DisplayName("Should find transaction when date range is exactly one single day")
        void shouldFindWhenDateRangeIsSingleDay() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            List<Transaction> result = repository.findByDateRange(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 1));

            assertEquals(1, result.size());
            assertEquals(incomeTxId, result.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should return empty list when no transactions fall in date range")
        void shouldReturnEmptyWhenNoMatchInDateRange() {
            repository.save(incomeTx);

            List<Transaction> result = repository.findByDateRange(
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 31));

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when dates are null or start is after end")
        void shouldThrowOnInvalidDateRange() {
            assertThrows(IllegalArgumentException.class, () ->
                    repository.findByDateRange(null, LocalDate.of(2026, 3, 1)));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.findByDateRange(LocalDate.of(2026, 3, 1), null));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.findByDateRange(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 1)));
        }

        @Test
        @DisplayName("Should sort by createdAt descending when transaction date is equal")
        void shouldSortByCreatedAtDescendingWhenTransactionDateIsEqual() throws InterruptedException {
            IncomeExpenseTransaction txEarly = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    walletBank,
                    categorySalary,
                    Money.of("1000.00"),
                    TransactionDate.of(LocalDate.of(2026, 3, 15)),
                    "Tx Early",
                    null);

            Thread.sleep(5);

            IncomeExpenseTransaction txLate = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    walletBank,
                    categorySalary,
                    Money.of("2000.00"),
                    TransactionDate.of(LocalDate.of(2026, 3, 15)),
                    "Tx Late",
                    null);

            repository.save(txEarly);
            repository.save(txLate);

            List<Transaction> result = repository.findByDateRange(
                    LocalDate.of(2026, 3, 15),
                    LocalDate.of(2026, 3, 15));

            assertEquals(2, result.size());
            assertEquals(txLate.getTransactionId(), result.get(0).getTransactionId());
            assertEquals(txEarly.getTransactionId(), result.get(1).getTransactionId());
        }
    }

    @Nested
    @DisplayName("Tests for findByDateRangeAndType")
    class FindByDateRangeAndTypeTests {

        @Test
        @DisplayName("Should filter by date range and matching TransactionType")
        void shouldFilterByDateRangeAndType() {
            repository.save(incomeTx);
            repository.save(expenseTx);
            repository.save(transferTx);

            List<Transaction> incomes = repository.findByDateRangeAndType(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.INCOME);

            assertEquals(1, incomes.size());
            assertEquals(incomeTxId, incomes.get(0).getTransactionId());

            List<Transaction> expenses = repository.findByDateRangeAndType(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.EXPENSE);

            assertEquals(1, expenses.size());
            assertEquals(expenseTxId, expenses.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should throw NullPointerException when type is null or IllegalArgumentException when dates invalid")
        void shouldThrowWhenParametersAreInvalid() {
            assertThrows(NullPointerException.class, () ->
                    repository.findByDateRangeAndType(
                            LocalDate.of(2026, 3, 1),
                            LocalDate.of(2026, 3, 10),
                            null));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.findByDateRangeAndType(
                            LocalDate.of(2026, 3, 10),
                            LocalDate.of(2026, 3, 1),
                            TransactionType.INCOME));
        }
    }

    @Nested
    @DisplayName("Tests for findByCategoryId and existsByCategoryId")
    class CategoryQueryTests {

        @Test
        @DisplayName("Should find transaction and fee associated with category")
        void shouldFindTransactionAndFeeByCategory() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            List<Transaction> salaryTxs = repository.findByCategoryId(catSalaryId);
            assertEquals(1, salaryTxs.size());
            assertEquals(incomeTxId, salaryTxs.get(0).getTransactionId());

            List<Transaction> foodTxs = repository.findByCategoryId(catFoodId);
            assertEquals(1, foodTxs.size());
            assertEquals(expenseTxId, foodTxs.get(0).getTransactionId());

            List<Transaction> feeCategoryTxs = repository.findByCategoryId(catTransferFeeId);
            assertEquals(1, feeCategoryTxs.size());
            assertEquals(expenseTxId, feeCategoryTxs.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should return true for existsByCategoryId when category exists in transaction or fee")
        void shouldReturnTrueForExistsByCategoryId() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            assertTrue(repository.existsByCategoryId(catSalaryId));
            assertTrue(repository.existsByCategoryId(catFoodId));
            assertTrue(repository.existsByCategoryId(catTransferFeeId));
            assertFalse(repository.existsByCategoryId(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should throw NullPointerException when categoryId is null")
        void shouldThrowWhenCategoryIdIsNull() {
            assertThrows(NullPointerException.class, () -> repository.findByCategoryId(null));
            assertThrows(NullPointerException.class, () -> repository.existsByCategoryId(null));
        }

        @Test
        @DisplayName("Should find transfer transaction and verify existence by fee category")
        void shouldFindTransferTransactionByFeeCategory() {
            UUID transferWithFeeId = UUID.randomUUID();
            TransactionFee transferFee = new TransactionFee(
                    UUID.randomUUID(),
                    transferWithFeeId,
                    Money.of("1000.00"),
                    walletBank,
                    categoryTransferFee);

            TransferTransaction transferWithFee = new TransferTransaction(
                    transferWithFeeId,
                    walletBank,
                    walletCash,
                    Money.of("500000.00"),
                    TransactionDate.of(LocalDate.of(2026, 3, 7)),
                    "Chuyển tiền có phí",
                    transferFee);

            repository.save(transferWithFee);

            List<Transaction> found = repository.findByCategoryId(catTransferFeeId);
            assertEquals(1, found.size());
            assertEquals(transferWithFeeId, found.get(0).getTransactionId());
            assertTrue(repository.existsByCategoryId(catTransferFeeId));
        }
    }

    @Nested
    @DisplayName("Tests for sumAmountByFilter")
    class SumAmountByFilterTests {

        @Test
        @DisplayName("Should sum amount correctly with scale 2")
        void shouldSumAmountCorrectly() {
            IncomeExpenseTransaction income1 = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    walletBank,
                    categorySalary,
                    Money.of("1000000.00"),
                    TransactionDate.of(LocalDate.of(2026, 3, 2)),
                    "Thưởng",
                    null);

            IncomeExpenseTransaction income2 = new IncomeExpenseTransaction(
                    UUID.randomUUID(),
                    TransactionType.INCOME,
                    walletBank,
                    categorySalary,
                    Money.of("2500000.50"),
                    TransactionDate.of(LocalDate.of(2026, 3, 4)),
                    "Lương",
                    null);

            repository.save(income1);
            repository.save(income2);

            BigDecimal sum = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.INCOME);

            assertEquals(new BigDecimal("3500000.50"), sum);
            assertEquals(2, sum.scale());
        }

        @Test
        @DisplayName("Should return 0.00 when no matching transactions exist")
        void shouldReturnZeroWhenNoMatches() {
            BigDecimal sum = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.EXPENSE);

            assertEquals(new BigDecimal("0.00"), sum);
            assertEquals(2, sum.scale());
        }

        @Test
        @DisplayName("Should isolate transaction types when summing amounts")
        void shouldIsolateTransactionTypesWhenSumming() {
            repository.save(incomeTx);
            repository.save(expenseTx);
            repository.save(transferTx);

            BigDecimal incomeSum = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.INCOME);

            BigDecimal expenseSum = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 10),
                    TransactionType.EXPENSE);

            assertEquals(new BigDecimal("15000000.00"), incomeSum);
            assertEquals(new BigDecimal("200000.00"), expenseSum);
        }

        @Test
        @DisplayName("Should throw exception when parameters for sumAmountByFilter are invalid")
        void shouldThrowOnInvalidParametersForSumAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(null, LocalDate.of(2026, 3, 10), TransactionType.INCOME));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 1), null, TransactionType.INCOME));
            assertThrows(NullPointerException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), null));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 1), TransactionType.INCOME));
        }

        @Test
        @DisplayName("Should sum amount correctly when date range is a single day")
        void shouldSumAmountCorrectlyWhenDateRangeIsSingleDay() {
            repository.save(incomeTx);

            BigDecimal sumMatched = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 1),
                    TransactionType.INCOME);
            assertEquals(new BigDecimal("15000000.00"), sumMatched);

            BigDecimal sumNoMatch = repository.sumAmountByFilter(
                    LocalDate.of(2026, 3, 2),
                    LocalDate.of(2026, 3, 2),
                    TransactionType.INCOME);
            assertEquals(new BigDecimal("0.00"), sumNoMatch);
        }
    }

    @Nested
    @DisplayName("Tests for Concurrency")
    class ConcurrencyTests {

        @Test
        @DisplayName("Should support thread-safe concurrent saves without race conditions")
        void shouldSupportConcurrentSaves() throws InterruptedException {
            int threadCount = 10;
            int transactionsPerThread = 50;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < transactionsPerThread; i++) {
                            IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                                    UUID.randomUUID(),
                                    TransactionType.INCOME,
                                    walletBank,
                                    categorySalary,
                                    Money.of("100.00"),
                                    TransactionDate.of(LocalDate.of(2026, 3, (i % 28) + 1)),
                                    "Tx thread " + threadId + " idx " + i,
                                    null);
                            repository.save(tx);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();

            assertEquals(threadCount * transactionsPerThread, repository.findAll().size());
        }
    }
}
