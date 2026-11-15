package com.pfms.infrastructure.file;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFactory;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.file.support.JsonDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử kho lưu trữ tệp JsonTransactionRepository")
class JsonTransactionRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonDataStore dataStore;
    private JsonTransactionRepository repository;

    private Wallet wallet1;
    private Wallet wallet2;
    private Category catFood;
    private Category catSalary;
    private Category catFee;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("test_pfms_transactions.json");
        this.dataStore = new JsonDataStore(storageFile);
        this.repository = new JsonTransactionRepository(dataStore);

        this.wallet1 = new Wallet(
                UUID.randomUUID(),
                "Ví Tiền Mặt",
                "",
                true,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("10000000.00"));

        this.wallet2 = new Wallet(
                UUID.randomUUID(),
                "Ví Ngân Hàng",
                "",
                false,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("5000000.00"));

        UUID gId = UUID.randomUUID();
        this.catFood = new Category(
                UUID.randomUUID(),
                "Ăn Uống",
                gId,
                TransactionType.EXPENSE,
                "utensils",
                "#FF0000");

        this.catSalary = new Category(
                UUID.randomUUID(),
                "Tiền Lương",
                gId,
                TransactionType.INCOME,
                "money-bill",
                "#00FF00");

        this.catFee = new Category(
                UUID.randomUUID(),
                "Phí Giao Dịch",
                gId,
                TransactionType.EXPENSE,
                "receipt",
                "#888888");
    }

    @Nested
    @DisplayName("Khởi tạo và kiểm tra tham số")
    class ConstructorTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi JsonDataStore là null")
        void shouldThrowExceptionWhenDataStoreIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonTransactionRepository(null));
        }
    }

    @Nested
    @DisplayName("Thao tác CRUD cơ bản")
    class CrudOperationsTests {

        @Test
        @DisplayName("Lưu mới và nạp lại giao dịch đa hình qua findById")
        void shouldSaveAndFindByIdPolymorphically() {
            UUID ieTxId = UUID.randomUUID();
            IncomeExpenseTransaction ieTx = TransactionFactory.createIncomeExpense(
                    ieTxId,
                    TransactionType.EXPENSE,
                    wallet1,
                    catFood,
                    Money.of("150000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Ăn trưa",
                    Money.of("5000.00"),
                    catFee);

            UUID trfTxId = UUID.randomUUID();
            TransferTransaction trfTx = TransactionFactory.createTransfer(
                    trfTxId,
                    wallet1,
                    wallet2,
                    Money.of("1000000.00"),
                    TransactionDate.of(2026, 3, 2),
                    "Chuyển khoản",
                    Money.ZERO,
                    null);

            repository.save(ieTx);
            repository.save(trfTx);

            Optional<Transaction> foundIe = repository.findById(ieTxId);
            assertTrue(foundIe.isPresent());
            assertTrue(foundIe.get() instanceof IncomeExpenseTransaction);
            assertEquals(Money.of("150000.00"), foundIe.get().getAmount());
            assertTrue(foundIe.get().getFee().isPresent());
            assertEquals(Money.of("5000.00"), foundIe.get().getFee().get().getAmount());

            Optional<Transaction> foundTrf = repository.findById(trfTxId);
            assertTrue(foundTrf.isPresent());
            assertTrue(foundTrf.get() instanceof TransferTransaction);
            assertEquals(Money.of("1000000.00"), foundTrf.get().getAmount());

            // Xác thực tính bền vững trên đĩa vật lý qua instance mới
            JsonDataStore freshStore = new JsonDataStore(dataStore.getStoragePath());
            JsonTransactionRepository freshRepo = new JsonTransactionRepository(freshStore);
            assertEquals(2, freshRepo.findAll().size());
        }

        @Test
        @DisplayName("Cập nhật giao dịch đã tồn tại (Upsert)")
        void shouldUpdateExistingTransaction() {
            UUID txId = UUID.randomUUID();
            IncomeExpenseTransaction initial = TransactionFactory.createIncomeExpense(
                    txId,
                    TransactionType.EXPENSE,
                    wallet1,
                    catFood,
                    Money.of("100000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Ghi chú cũ",
                    Money.ZERO,
                    null);
            repository.save(initial);

            IncomeExpenseTransaction updated = TransactionFactory.createIncomeExpense(
                    txId,
                    TransactionType.EXPENSE,
                    wallet1,
                    catFood,
                    Money.of("200000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Ghi chú mới",
                    Money.ZERO,
                    null);
            repository.save(updated);

            List<Transaction> all = repository.findAll();
            assertEquals(1, all.size());
            assertEquals(Money.of("200000.00"), all.get(0).getAmount());
            assertEquals("Ghi chú mới", all.get(0).getNote());
        }

        @Test
        @DisplayName("Xóa giao dịch theo ID và kiểm tra existsById")
        void shouldDeleteByIdAndVerifyExistence() {
            UUID txId = UUID.randomUUID();
            IncomeExpenseTransaction tx = TransactionFactory.createIncomeExpense(
                    txId,
                    TransactionType.INCOME,
                    wallet1,
                    catSalary,
                    Money.of("5000000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Lương",
                    Money.ZERO,
                    null);

            repository.save(tx);
            assertTrue(repository.existsById(txId));

            boolean deleted = repository.deleteById(txId);
            assertTrue(deleted);
            assertFalse(repository.existsById(txId));
            assertTrue(repository.findById(txId).isEmpty());

            assertFalse(repository.deleteById(txId));
        }

        @Test
        @DisplayName("Xóa toàn bộ giao dịch qua deleteAll")
        void shouldDeleteAllTransactions() {
            repository.save(TransactionFactory.createIncomeExpense(
                    TransactionType.EXPENSE, wallet1, catFood, Money.of("10000.00"),
                    TransactionDate.of(2026, 3, 1), "", Money.ZERO, null));
            repository.save(TransactionFactory.createIncomeExpense(
                    TransactionType.INCOME, wallet1, catSalary, Money.of("20000.00"),
                    TransactionDate.of(2026, 3, 2), "", Money.ZERO, null));
            assertEquals(2, repository.findAll().size());

            repository.deleteAll();
            assertTrue(repository.findAll().isEmpty());
        }

        @Test
        @DisplayName("Từ chối tham số null trong các thao tác CRUD")
        void shouldThrowExceptionOnNullArguments() {
            assertThrows(NullPointerException.class, () -> repository.save(null));
            assertThrows(NullPointerException.class, () -> repository.findById(null));
            assertThrows(NullPointerException.class, () -> repository.existsById(null));
            assertThrows(NullPointerException.class, () -> repository.deleteById(null));
        }
    }

    @Nested
    @DisplayName("Truy vấn nghiệp vụ chuyên biệt")
    class BusinessQueriesTests {

        private UUID tx1Id;
        private UUID tx2Id;
        private UUID tx3Id;
        private UUID tx4Id;

        @BeforeEach
        void seedData() {
            // Tx1: Thu chi chi tiêu 100k, 2026-03-01, Ví 1, Ăn uống, kèm phí 5k Ví 1
            this.tx1Id = UUID.randomUUID();
            IncomeExpenseTransaction tx1 = TransactionFactory.createIncomeExpense(
                    tx1Id,
                    TransactionType.EXPENSE,
                    wallet1,
                    catFood,
                    Money.of("100000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Ăn trưa",
                    Money.of("5000.00"),
                    catFee);
            repository.save(tx1);

            // Tx2: Thu chi thu nhập 10tr, 2026-03-05, Ví 1, Tiền lương
            this.tx2Id = UUID.randomUUID();
            IncomeExpenseTransaction tx2 = TransactionFactory.createIncomeExpense(
                    tx2Id,
                    TransactionType.INCOME,
                    wallet1,
                    catSalary,
                    Money.of("10000000.00"),
                    TransactionDate.of(2026, 3, 5),
                    "Lương tháng 3",
                    Money.ZERO,
                    null);
            repository.save(tx2);

            // Tx3: Chuyển tiền 2tr, 2026-03-10, Ví 1 -> Ví 2, kèm phí 10k Ví 1
            this.tx3Id = UUID.randomUUID();
            TransferTransaction tx3 = TransactionFactory.createTransfer(
                    tx3Id,
                    wallet1,
                    wallet2,
                    Money.of("2000000.00"),
                    TransactionDate.of(2026, 3, 10),
                    "Chuyển tiền tiết kiệm",
                    Money.of("10000.00"),
                    catFee);
            repository.save(tx3);

            // Tx4: Thu chi chi tiêu 500k, 2026-02-15, Ví 2, Ăn uống
            this.tx4Id = UUID.randomUUID();
            IncomeExpenseTransaction tx4 = TransactionFactory.createIncomeExpense(
                    tx4Id,
                    TransactionType.EXPENSE,
                    wallet2,
                    catFood,
                    Money.of("500000.00"),
                    TransactionDate.of(2026, 2, 15),
                    "Tiệc tháng 2",
                    Money.ZERO,
                    null);
            repository.save(tx4);
        }

        @Test
        @DisplayName("Lọc giao dịch liên quan đến ví (UUID và String) theo thứ tự ngày giảm dần")
        void shouldFindByWalletId() {
            // Ví 1: có Tx1 (chính + phí), Tx2 (chính), Tx3 (nguồn + phí) -> 3 giao dịch
            List<Transaction> wallet1Txs = repository.findByWalletId(wallet1.getId());
            assertEquals(3, wallet1Txs.size());
            assertEquals(tx3Id, wallet1Txs.get(0).getTransactionId()); // 2026-03-10
            assertEquals(tx2Id, wallet1Txs.get(1).getTransactionId()); // 2026-03-05
            assertEquals(tx1Id, wallet1Txs.get(2).getTransactionId()); // 2026-03-01

            // Kiểm tra phương thức nạp chồng String
            List<Transaction> wallet1StringTxs = repository.findByWalletId(wallet1.getId().toString());
            assertEquals(3, wallet1StringTxs.size());

            // Ví 2: có Tx3 (đích) và Tx4 (chính) -> 2 giao dịch
            List<Transaction> wallet2Txs = repository.findByWalletId(wallet2.getId());
            assertEquals(2, wallet2Txs.size());
            assertEquals(tx3Id, wallet2Txs.get(0).getTransactionId());
            assertEquals(tx4Id, wallet2Txs.get(1).getTransactionId());

            assertThrows(NullPointerException.class, () -> repository.findByWalletId((UUID) null));
            assertThrows(IllegalArgumentException.class, () -> repository.findByWalletId("  "));
        }

        @Test
        @DisplayName("Lọc danh sách giao dịch theo khoảng ngày")
        void shouldFindByDateRange() {
            LocalDate startMarch = LocalDate.of(2026, 3, 1);
            LocalDate endMarch = LocalDate.of(2026, 3, 31);
            List<Transaction> marchTxs = repository.findByDateRange(startMarch, endMarch);
            assertEquals(3, marchTxs.size());
            assertEquals(tx3Id, marchTxs.get(0).getTransactionId());
            assertEquals(tx2Id, marchTxs.get(1).getTransactionId());
            assertEquals(tx1Id, marchTxs.get(2).getTransactionId());

            List<Transaction> febTxs = repository.findByDateRange(
                    LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
            assertEquals(1, febTxs.size());
            assertEquals(tx4Id, febTxs.get(0).getTransactionId());

            assertThrows(IllegalArgumentException.class, () -> repository.findByDateRange(endMarch, startMarch));
        }

        @Test
        @DisplayName("Lọc giao dịch thu chi theo khoảng ngày và loại thu chi")
        void shouldFindByDateRangeAndType() {
            LocalDate start = LocalDate.of(2026, 3, 1);
            LocalDate end = LocalDate.of(2026, 3, 31);

            List<Transaction> expenseTxs = repository.findByDateRangeAndType(start, end, TransactionType.EXPENSE);
            assertEquals(1, expenseTxs.size());
            assertEquals(tx1Id, expenseTxs.get(0).getTransactionId());

            List<Transaction> incomeTxs = repository.findByDateRangeAndType(start, end, TransactionType.INCOME);
            assertEquals(1, incomeTxs.size());
            assertEquals(tx2Id, incomeTxs.get(0).getTransactionId());
        }

        @Test
        @DisplayName("Kiểm tra và lọc giao dịch theo danh mục (chính và phí)")
        void shouldFindByCategoryIdAndExistsByCategoryId() {
            // Danh mục Ăn uống: Tx1 và Tx4
            List<Transaction> foodTxs = repository.findByCategoryId(catFood.getCategoryId());
            assertEquals(2, foodTxs.size());
            assertTrue(repository.existsByCategoryId(catFood.getCategoryId()));

            // Danh mục Phí giao dịch: gắn trên phí của Tx1 và Tx3
            List<Transaction> feeTxs = repository.findByCategoryId(catFee.getCategoryId());
            assertEquals(2, feeTxs.size());
            assertTrue(repository.existsByCategoryId(catFee.getCategoryId()));

            // Danh mục không dùng
            UUID unusedCatId = UUID.randomUUID();
            assertTrue(repository.findByCategoryId(unusedCatId).isEmpty());
            assertFalse(repository.existsByCategoryId(unusedCatId));

            assertThrows(NullPointerException.class, () -> repository.findByCategoryId(null));
            assertThrows(NullPointerException.class, () -> repository.existsByCategoryId(null));
        }

        @Test
        @DisplayName("Tính tổng số tiền gốc của các giao dịch thu chi theo bộ lọc")
        void shouldSumAmountByFilter() {
            LocalDate start = LocalDate.of(2026, 3, 1);
            LocalDate end = LocalDate.of(2026, 3, 31);

            BigDecimal sumExpense = repository.sumAmountByFilter(start, end, TransactionType.EXPENSE);
            assertEquals(new BigDecimal("100000.00"), sumExpense);

            BigDecimal sumIncome = repository.sumAmountByFilter(start, end, TransactionType.INCOME);
            assertEquals(new BigDecimal("10000000.00"), sumIncome);

            BigDecimal emptySum = repository.sumAmountByFilter(
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), TransactionType.EXPENSE);
            assertEquals(new BigDecimal("0.00"), emptySum);
        }
    }
}
