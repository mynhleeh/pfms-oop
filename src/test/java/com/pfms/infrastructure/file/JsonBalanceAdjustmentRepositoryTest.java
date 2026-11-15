package com.pfms.infrastructure.file;

import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.infrastructure.file.support.JsonDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử kho lưu trữ tệp JsonBalanceAdjustmentRepository")
class JsonBalanceAdjustmentRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonDataStore dataStore;
    private JsonBalanceAdjustmentRepository repository;

    private UUID wallet1Id;
    private UUID wallet2Id;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("test_pfms_adjustments.json");
        this.dataStore = new JsonDataStore(storageFile);
        this.repository = new JsonBalanceAdjustmentRepository(dataStore);

        this.wallet1Id = UUID.randomUUID();
        this.wallet2Id = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Khởi tạo và kiểm tra tham số")
    class ConstructorTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi JsonDataStore là null")
        void shouldThrowExceptionWhenDataStoreIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonBalanceAdjustmentRepository(null));
        }
    }

    @Nested
    @DisplayName("Thao tác CRUD và cơ chế Append-Only")
    class CrudOperationsTests {

        @Test
        @DisplayName("Lưu mới và nạp lại bản ghi kiểm kê qua findById")
        void shouldSaveAndFindById() {
            UUID recordId = UUID.randomUUID();
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId,
                    wallet1Id,
                    Money.of("500000.00"),
                    Money.of("600000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Kiểm kê đầu tháng");

            BalanceAdjustmentRecord saved = repository.save(record);
            assertNotNull(saved);
            assertEquals(recordId, saved.getRecordId());

            Optional<BalanceAdjustmentRecord> found = repository.findById(recordId);
            assertTrue(found.isPresent());
            assertEquals(Money.of("100000.00"), found.get().getDifference());
            assertEquals("Kiểm kê đầu tháng", found.get().getReason());

            // Kiểm tra tính bền vững từ đĩa vật lý qua instance mới
            JsonDataStore freshStore = new JsonDataStore(dataStore.getStoragePath());
            JsonBalanceAdjustmentRepository freshRepo = new JsonBalanceAdjustmentRepository(freshStore);
            Optional<BalanceAdjustmentRecord> diskFound = freshRepo.findById(recordId);
            assertTrue(diskFound.isPresent());
            assertEquals(recordId, diskFound.get().getRecordId());
        }

        @Test
        @DisplayName("Chặn cập nhật bản ghi kiểm kê đã tồn tại (Append-Only)")
        void shouldThrowUnsupportedOperationExceptionOnUpdate() {
            UUID recordId = UUID.randomUUID();
            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    recordId,
                    wallet1Id,
                    Money.of("100000.00"),
                    Money.of("200000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Lần 1");
            repository.save(record);

            BalanceAdjustmentRecord duplicate = new BalanceAdjustmentRecord(
                    recordId,
                    wallet1Id,
                    Money.of("100000.00"),
                    Money.of("300000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Cố tình sửa");

            assertThrows(UnsupportedOperationException.class, () -> repository.save(duplicate));
        }

        @Test
        @DisplayName("Chặn xóa bản ghi kiểm kê qua deleteById nhằm bảo vệ vết kiểm toán")
        void shouldThrowUnsupportedOperationExceptionOnDeleteById() {
            UUID recordId = UUID.randomUUID();
            assertThrows(UnsupportedOperationException.class, () -> repository.deleteById(recordId));
        }

        @Test
        @DisplayName("Xóa toàn bộ bản ghi kiểm kê qua deleteAll")
        void shouldDeleteAllAdjustments() {
            repository.save(new BalanceAdjustmentRecord(
                    UUID.randomUUID(), wallet1Id, Money.of("100.00"), Money.of("200.00"),
                    TransactionDate.of(2026, 3, 1), "R1"));
            repository.save(new BalanceAdjustmentRecord(
                    UUID.randomUUID(), wallet1Id, Money.of("200.00"), Money.of("300.00"),
                    TransactionDate.of(2026, 3, 2), "R2"));
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

        private UUID rec1Id;
        private UUID rec2Id;
        private UUID rec3Id;

        @BeforeEach
        void seedData() {
            // Rec 1: Ví 1, ngày 2026-03-01
            this.rec1Id = UUID.randomUUID();
            BalanceAdjustmentRecord r1 = new BalanceAdjustmentRecord(
                    rec1Id,
                    wallet1Id,
                    Money.of("1000000.00"),
                    Money.of("1200000.00"),
                    TransactionDate.of(2026, 3, 1),
                    "Kiểm kê đợt 1");
            repository.save(r1);

            // Rec 2: Ví 1, ngày 2026-03-15 (mới hơn)
            this.rec2Id = UUID.randomUUID();
            BalanceAdjustmentRecord r2 = new BalanceAdjustmentRecord(
                    rec2Id,
                    wallet1Id,
                    Money.of("1200000.00"),
                    Money.of("1150000.00"),
                    TransactionDate.of(2026, 3, 15),
                    "Kiểm kê đợt 2");
            repository.save(r2);

            // Rec 3: Ví 2, ngày 2026-03-10
            this.rec3Id = UUID.randomUUID();
            BalanceAdjustmentRecord r3 = new BalanceAdjustmentRecord(
                    rec3Id,
                    wallet2Id,
                    Money.of("5000000.00"),
                    Money.of("4900000.00"),
                    TransactionDate.of(2026, 3, 10),
                    "Kiểm kê ví 2");
            repository.save(r3);
        }

        @Test
        @DisplayName("Truy vấn danh sách bản ghi kiểm kê theo ví sắp xếp ngày giảm dần")
        void shouldFindByWalletId() {
            List<BalanceAdjustmentRecord> wallet1Records = repository.findByWalletId(wallet1Id);
            assertEquals(2, wallet1Records.size());
            assertEquals(rec2Id, wallet1Records.get(0).getRecordId()); // 2026-03-15
            assertEquals(rec1Id, wallet1Records.get(1).getRecordId()); // 2026-03-01

            List<BalanceAdjustmentRecord> wallet2Records = repository.findByWalletId(wallet2Id);
            assertEquals(1, wallet2Records.size());
            assertEquals(rec3Id, wallet2Records.get(0).getRecordId());

            UUID unusedWalletId = UUID.randomUUID();
            assertTrue(repository.findByWalletId(unusedWalletId).isEmpty());

            assertThrows(NullPointerException.class, () -> repository.findByWalletId(null));
        }

        @Test
        @DisplayName("Truy vấn bản ghi kiểm kê gần nhất của ví")
        void shouldFindLatestByWalletId() {
            Optional<BalanceAdjustmentRecord> latestWallet1 = repository.findLatestByWalletId(wallet1Id);
            assertTrue(latestWallet1.isPresent());
            assertEquals(rec2Id, latestWallet1.get().getRecordId());

            Optional<BalanceAdjustmentRecord> latestWallet2 = repository.findLatestByWalletId(wallet2Id);
            assertTrue(latestWallet2.isPresent());
            assertEquals(rec3Id, latestWallet2.get().getRecordId());

            UUID unusedWalletId = UUID.randomUUID();
            assertTrue(repository.findLatestByWalletId(unusedWalletId).isEmpty());

            assertThrows(NullPointerException.class, () -> repository.findLatestByWalletId(null));
        }
    }
}
