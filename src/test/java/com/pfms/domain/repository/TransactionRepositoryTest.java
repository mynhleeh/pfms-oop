package com.pfms.domain.repository;

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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransactionRepository Tests")
class TransactionRepositoryTest {

    static class InMemoryTransactionRepositoryStub implements TransactionRepository {
        private final Map<UUID, Transaction> storage = new LinkedHashMap<>();

        @Override
        public Transaction save(Transaction entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Thực thể không được để null");
            }
            if (entity.getTransactionId() == null) {
                throw new IllegalArgumentException("Mã giao dịch không được để null");
            }
            storage.put(entity.getTransactionId(), entity);
            return entity;
        }

        @Override
        public Optional<Transaction> findById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("Mã giao dịch không được để null");
            }
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Transaction> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("Mã giao dịch không được để null");
            }
            return storage.containsKey(id);
        }

        @Override
        public boolean deleteById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("Mã giao dịch không được để null");
            }
            return storage.remove(id) != null;
        }

        @Override
        public List<Transaction> findByWalletId(UUID walletId) {
            if (walletId == null) {
                throw new IllegalArgumentException("Mã ví không được để null");
            }
            return findByWalletId(walletId.toString());
        }

        @Override
        public List<Transaction> findByWalletId(String walletId) {
            if (walletId == null || walletId.trim().isEmpty()) {
                throw new IllegalArgumentException("Mã ví không được để null hoặc rỗng");
            }
            String targetId = walletId.trim();
            return storage.values().stream()
                    .filter(tx -> isTransactionRelatedToWallet(tx, targetId))
                    .sorted(Comparator.comparing((Transaction tx) -> tx.getTransactionDate().getDate()).reversed()
                            .thenComparing(Comparator.comparing(Transaction::getCreatedAt).reversed()))
                    .toList();
        }

        @Override
        public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
            validateDateRange(startDate, endDate);
            return storage.values().stream()
                    .filter(tx -> {
                        LocalDate d = tx.getTransactionDate().getDate();
                        return !d.isBefore(startDate) && !d.isAfter(endDate);
                    })
                    .sorted(Comparator.comparing((Transaction tx) -> tx.getTransactionDate().getDate()).reversed()
                            .thenComparing(Comparator.comparing(Transaction::getCreatedAt).reversed()))
                    .toList();
        }

        @Override
        public List<Transaction> findByDateRangeAndType(LocalDate startDate, LocalDate endDate, TransactionType type) {
            validateDateRange(startDate, endDate);
            if (type == null) {
                throw new IllegalArgumentException("Loại thu chi không được để null");
            }
            return storage.values().stream()
                    .filter(tx -> {
                        if (!(tx instanceof IncomeExpenseTransaction iet)) {
                            return false;
                        }
                        LocalDate d = iet.getTransactionDate().getDate();
                        return iet.getTransactionType() == type && !d.isBefore(startDate) && !d.isAfter(endDate);
                    })
                    .sorted(Comparator.comparing((Transaction tx) -> tx.getTransactionDate().getDate()).reversed()
                            .thenComparing(Comparator.comparing(Transaction::getCreatedAt).reversed()))
                    .toList();
        }

        @Override
        public List<Transaction> findByCategoryId(UUID categoryId) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Mã danh mục không được để null");
            }
            return storage.values().stream()
                    .filter(tx -> isTransactionRelatedToCategory(tx, categoryId))
                    .sorted(Comparator.comparing((Transaction tx) -> tx.getTransactionDate().getDate()).reversed()
                            .thenComparing(Comparator.comparing(Transaction::getCreatedAt).reversed()))
                    .toList();
        }

        @Override
        public boolean existsByCategoryId(UUID categoryId) {
            if (categoryId == null) {
                throw new IllegalArgumentException("Mã danh mục không được để null");
            }
            return storage.values().stream()
                    .anyMatch(tx -> isTransactionRelatedToCategory(tx, categoryId));
        }

        @Override
        public BigDecimal sumAmountByFilter(LocalDate startDate, LocalDate endDate, TransactionType type) {
            List<Transaction> matches = findByDateRangeAndType(startDate, endDate, type);
            return matches.stream()
                    .map(tx -> tx.getAmount().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_EVEN);
        }

        private boolean isTransactionRelatedToWallet(Transaction tx, String targetId) {
            if (tx instanceof IncomeExpenseTransaction iet) {
                if (iet.getWallet().getId().toString().equalsIgnoreCase(targetId)) {
                    return true;
                }
            } else if (tx instanceof TransferTransaction tt) {
                if (tt.getSourceWallet().getId().toString().equalsIgnoreCase(targetId)
                        || tt.getDestinationWallet().getId().toString().equalsIgnoreCase(targetId)) {
                    return true;
                }
            }
            return tx.getFee()
                    .map(fee -> fee.getWallet().getId().toString().equalsIgnoreCase(targetId))
                    .orElse(false);
        }

        private boolean isTransactionRelatedToCategory(Transaction tx, UUID categoryId) {
            if (tx instanceof IncomeExpenseTransaction iet) {
                if (iet.getCategory().getCategoryId().equals(categoryId)) {
                    return true;
                }
            }
            return tx.getFee()
                    .map(fee -> fee.getCategory().getCategoryId().equals(categoryId))
                    .orElse(false);
        }

        private void validateDateRange(LocalDate startDate, LocalDate endDate) {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Ngày biên khoảng thời gian không được để null");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
            }
        }
    }

    private InMemoryTransactionRepositoryStub repository;
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
        repository = new InMemoryTransactionRepositoryStub();

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
    @DisplayName("Tests for inherited GenericRepository methods")
    class GenericRepositoryTests {

        @Test
        @DisplayName("Should save and find transaction by ID")
        void shouldSaveAndFindById() {
            repository.save(incomeTx);

            Optional<Transaction> found = repository.findById(incomeTxId);

            assertTrue(found.isPresent());
            assertEquals(incomeTx, found.get());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when save or findById receives null")
        void shouldThrowExceptionWhenNullPassedToCrud() {
            assertThrows(IllegalArgumentException.class, () -> repository.save(null));
            assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
            assertThrows(IllegalArgumentException.class, () -> repository.existsById(null));
            assertThrows(IllegalArgumentException.class, () -> repository.deleteById(null));
        }

        @Test
        @DisplayName("Should return all saved transactions and check exists correctly")
        void shouldReturnAllAndCheckExists() {
            repository.save(incomeTx);
            repository.save(expenseTx);

            assertEquals(2, repository.findAll().size());
            assertTrue(repository.existsById(incomeTxId));
            assertTrue(repository.existsById(expenseTxId));
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should delete transaction by ID")
        void shouldDeleteById() {
            repository.save(incomeTx);

            assertTrue(repository.deleteById(incomeTxId));
            assertFalse(repository.existsById(incomeTxId));
            assertFalse(repository.deleteById(incomeTxId));
        }

        @Test
        @DisplayName("Should return unmodifiable list throwing exception on mutation attempts")
        void shouldReturnUnmodifiableList() {
            repository.save(incomeTx);
            List<Transaction> all = repository.findAll();

            assertThrows(UnsupportedOperationException.class, all::clear);
            assertThrows(UnsupportedOperationException.class, () -> all.add(expenseTx));
        }
    }

    @Nested
    @DisplayName("Tests for findByWalletId")
    class FindByWalletIdTests {

        @Test
        @DisplayName("Should find transactions where wallet is direct participant in IncomeExpense")
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
        @DisplayName("Should find transactions where wallet is source or destination in Transfer")
        void shouldFindWhenWalletIsInTransfer() {
            repository.save(transferTx);

            List<Transaction> sourceTxs = repository.findByWalletId(walletBank.getId());
            assertEquals(1, sourceTxs.size());

            List<Transaction> destTxs = repository.findByWalletId(walletCash.getId());
            assertEquals(1, destTxs.size());
        }

        @Test
        @DisplayName("Should find transactions where wallet is associated with fee")
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
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException on null or blank walletId")
        void shouldThrowOnNullOrBlankWalletId() {
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId((UUID) null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId((String) null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId("   "));
        }
    }

    @Nested
    @DisplayName("Tests for findByDateRange")
    class FindByDateRangeTests {

        @Test
        @DisplayName("Should find transactions within inclusive date range sorted descending by date")
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
        @DisplayName("Should throw IllegalArgumentException when type is null")
        void shouldThrowWhenTypeIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    repository.findByDateRangeAndType(
                            LocalDate.of(2026, 3, 1),
                            LocalDate.of(2026, 3, 10),
                            null));
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
        @DisplayName("Should throw IllegalArgumentException when categoryId is null")
        void shouldThrowWhenCategoryIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.findByCategoryId(null));
            assertThrows(IllegalArgumentException.class, () -> repository.existsByCategoryId(null));
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
        @DisplayName("Should throw IllegalArgumentException when parameters for sumAmountByFilter are invalid")
        void shouldThrowOnInvalidParametersForSumAmount() {
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(null, LocalDate.of(2026, 3, 10), TransactionType.INCOME));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 1), null, TransactionType.INCOME));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), null));
            assertThrows(IllegalArgumentException.class, () ->
                    repository.sumAmountByFilter(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 1), TransactionType.INCOME));
        }
    }
}

