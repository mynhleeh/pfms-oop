package com.pfms.domain.repository;

import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BalanceAdjustmentRepository Tests")
class BalanceAdjustmentRepositoryTest {

    /**
     * Stub cài đặt trong bộ nhớ mô phỏng hành vi của BalanceAdjustmentRepository.
     * Áp dụng cơ chế Append-only: không cho phép cập nhật hoặc xóa bản ghi kiểm kê.
     */
    static class InMemoryBalanceAdjustmentRepositoryStub implements BalanceAdjustmentRepository {
        private final Map<UUID, BalanceAdjustmentRecord> storage = new LinkedHashMap<>();

        @Override
        public BalanceAdjustmentRecord save(BalanceAdjustmentRecord entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Thực thể bản ghi kiểm kê không được để null");
            }
            if (entity.getRecordId() == null) {
                throw new IllegalArgumentException("Mã bản ghi kiểm kê không được để null");
            }
            if (storage.containsKey(entity.getRecordId())) {
                throw new UnsupportedOperationException(
                        "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ cập nhật bản ghi đã tồn tại.");
            }
            storage.put(entity.getRecordId(), entity);
            return entity;
        }

        @Override
        public Optional<BalanceAdjustmentRecord> findById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("Mã bản ghi kiểm kê không được để null");
            }
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<BalanceAdjustmentRecord> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(UUID id) {
            if (id == null) {
                throw new IllegalArgumentException("Mã bản ghi kiểm kê không được để null");
            }
            return storage.containsKey(id);
        }

        @Override
        public List<BalanceAdjustmentRecord> findByWalletId(UUID walletId) {
            if (walletId == null) {
                throw new IllegalArgumentException("Mã ví kiểm kê không được để null");
            }
            List<BalanceAdjustmentRecord> matched = new ArrayList<>(storage.values().stream()
                    .filter(record -> record.getWalletId().equals(walletId))
                    .toList());

            List<UUID> insertionOrder = new ArrayList<>(storage.keySet());

            // Sắp xếp ngày kiểm kê giảm dần; nếu trùng ngày thì giữ thứ tự tạo bản ghi giảm dần (đảo ngược thứ tự chèn)
            matched.sort((r1, r2) -> {
                int dateCompare = r2.getAdjustedAt().compareTo(r1.getAdjustedAt());
                if (dateCompare != 0) {
                    return dateCompare;
                }
                // Đối với bản ghi trùng ngày, ưu tiên bản ghi ghi nhận sau trong storage
                return Integer.compare(
                        insertionOrder.indexOf(r2.getRecordId()),
                        insertionOrder.indexOf(r1.getRecordId()));
            });

            return Collections.unmodifiableList(matched);
        }

        @Override
        public Optional<BalanceAdjustmentRecord> findLatestByWalletId(UUID walletId) {
            if (walletId == null) {
                throw new IllegalArgumentException("Mã ví kiểm kê không được để null");
            }
            List<BalanceAdjustmentRecord> list = findByWalletId(walletId);
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        }
    }

    private InMemoryBalanceAdjustmentRepositoryStub repository;
    private UUID walletId1;
    private UUID walletId2;

    private BalanceAdjustmentRecord record1Wallet1;
    private BalanceAdjustmentRecord record2Wallet1;
    private BalanceAdjustmentRecord record3Wallet1;
    private BalanceAdjustmentRecord record1Wallet2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBalanceAdjustmentRepositoryStub();

        walletId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        walletId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

        LocalDate today = LocalDate.now();

        record1Wallet1 = new BalanceAdjustmentRecord(
                UUID.fromString("10000000-0000-0000-0000-000000000001"),
                walletId1,
                Money.of("1000000.00"),
                Money.of("1050000.00"),
                TransactionDate.of(today.minusDays(10)),
                "Kiểm kê đầu kỳ ví 1");

        record2Wallet1 = new BalanceAdjustmentRecord(
                UUID.fromString("10000000-0000-0000-0000-000000000002"),
                walletId1,
                Money.of("1050000.00"),
                Money.of("980000.00"),
                TransactionDate.of(today.minusDays(5)),
                "Kiểm kê giữa kỳ ví 1");

        record3Wallet1 = new BalanceAdjustmentRecord(
                UUID.fromString("10000000-0000-0000-0000-000000000003"),
                walletId1,
                Money.of("980000.00"),
                Money.of("1000000.00"),
                TransactionDate.of(today),
                "Kiểm kê cuối kỳ ví 1");

        record1Wallet2 = new BalanceAdjustmentRecord(
                UUID.fromString("20000000-0000-0000-0000-000000000001"),
                walletId2,
                Money.of("5000000.00"),
                Money.of("4900000.00"),
                TransactionDate.of(today.minusDays(3)),
                "Kiểm kê ví 2");
    }

    @Nested
    @DisplayName("Tests for save(BalanceAdjustmentRecord entity)")
    class SaveOperationTests {

        @Test
        @DisplayName("Should save new balance adjustment record successfully")
        void shouldSaveNewRecordSuccessfully() {
            BalanceAdjustmentRecord saved = repository.save(record1Wallet1);

            assertNotNull(saved);
            assertEquals(record1Wallet1.getRecordId(), saved.getRecordId());
            assertEquals(1, repository.findAll().size());
            assertTrue(repository.existsById(record1Wallet1.getRecordId()));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when saving null entity")
        void shouldRejectNullEntity() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.save(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Should enforce Audit Trail by preventing update to existing record")
        void shouldPreventUpdatingExistingRecord() {
            repository.save(record1Wallet1);

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.save(record1Wallet1));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
        }
    }

    @Nested
    @DisplayName("Tests for findById(UUID id)")
    class FindByIdOperationTests {

        @Test
        @DisplayName("Should return record when id exists")
        void shouldReturnRecordWhenExists() {
            repository.save(record1Wallet1);

            Optional<BalanceAdjustmentRecord> result = repository.findById(record1Wallet1.getRecordId());

            assertTrue(result.isPresent());
            assertEquals(record1Wallet1, result.get());
        }

        @Test
        @DisplayName("Should return empty Optional when id does not exist")
        void shouldReturnEmptyOptionalWhenNotFound() {
            Optional<BalanceAdjustmentRecord> result = repository.findById(UUID.randomUUID());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when id is null")
        void shouldRejectNullId() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.findById(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }

    @Nested
    @DisplayName("Tests for findAll()")
    class FindAllOperationTests {

        @Test
        @DisplayName("Should return empty list when repository is empty")
        void shouldReturnEmptyListWhenEmpty() {
            List<BalanceAdjustmentRecord> all = repository.findAll();

            assertNotNull(all);
            assertTrue(all.isEmpty());
        }

        @Test
        @DisplayName("Should return all saved records in unmodifiable list")
        void shouldReturnAllSavedRecords() {
            repository.save(record1Wallet1);
            repository.save(record2Wallet1);
            repository.save(record1Wallet2);

            List<BalanceAdjustmentRecord> all = repository.findAll();

            assertEquals(3, all.size());
            assertTrue(all.contains(record1Wallet1));
            assertTrue(all.contains(record2Wallet1));
            assertTrue(all.contains(record1Wallet2));

            assertThrows(UnsupportedOperationException.class, () -> all.remove(0));
        }
    }

    @Nested
    @DisplayName("Tests for existsById(UUID id)")
    class ExistsByIdOperationTests {

        @Test
        @DisplayName("Should return true when record exists")
        void shouldReturnTrueWhenRecordExists() {
            repository.save(record1Wallet1);

            assertTrue(repository.existsById(record1Wallet1.getRecordId()));
        }

        @Test
        @DisplayName("Should return false when record does not exist")
        void shouldReturnFalseWhenRecordDoesNotExist() {
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when id is null")
        void shouldRejectNullId() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.existsById(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }

    @Nested
    @DisplayName("Tests for deleteById(UUID id) - Audit Invariant Safeguard")
    class DeleteByIdOperationTests {

        @Test
        @DisplayName("Should throw UnsupportedOperationException on interface default method when deleting existing record")
        void shouldThrowUnsupportedOperationExceptionWhenDeletingExistingRecord() {
            repository.save(record1Wallet1);

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.deleteById(record1Wallet1.getRecordId()));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
            // Bản ghi vẫn phải tồn tại nguyên vẹn
            assertTrue(repository.existsById(record1Wallet1.getRecordId()));
        }

        @Test
        @DisplayName("Should throw UnsupportedOperationException even when record does not exist")
        void shouldThrowUnsupportedOperationExceptionEvenWhenRecordDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.deleteById(nonExistentId));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when id is null before checking operation")
        void shouldRejectNullIdWithNullPointerException() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.deleteById(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }

    @Nested
    @DisplayName("Tests for findByWalletId(UUID walletId)")
    class FindByWalletIdTests {

        @Test
        @DisplayName("Should return adjustments for specific wallet sorted by adjustedAt descending")
        void shouldReturnAdjustmentsSortedDescending() {
            repository.save(record1Wallet1); // minus 10 days
            repository.save(record3Wallet1); // today
            repository.save(record2Wallet1); // minus 5 days
            repository.save(record1Wallet2); // other wallet

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(walletId1);

            assertEquals(3, list.size());
            assertEquals(record3Wallet1.getRecordId(), list.get(0).getRecordId());
            assertEquals(record2Wallet1.getRecordId(), list.get(1).getRecordId());
            assertEquals(record1Wallet1.getRecordId(), list.get(2).getRecordId());
        }

        @Test
        @DisplayName("Should return empty list when wallet has no adjustment records")
        void shouldReturnEmptyListWhenWalletHasNoAdjustments() {
            repository.save(record1Wallet1);

            UUID emptyWalletId = UUID.randomUUID();
            List<BalanceAdjustmentRecord> list = repository.findByWalletId(emptyWalletId);

            assertNotNull(list);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Should strictly isolate records between different wallets")
        void shouldIsolateRecordsBetweenWallets() {
            repository.save(record1Wallet1);
            repository.save(record1Wallet2);

            List<BalanceAdjustmentRecord> wallet2List = repository.findByWalletId(walletId2);

            assertEquals(1, wallet2List.size());
            assertEquals(record1Wallet2.getRecordId(), wallet2List.get(0).getRecordId());
        }

        @Test
        @DisplayName("Should rank later created record higher when two adjustments occur on the same day")
        void shouldRankLaterCreatedRecordHigherWhenAdjustedOnSameDate() {
            LocalDate testDate = LocalDate.now().minusDays(1);

            BalanceAdjustmentRecord morningRecord = new BalanceAdjustmentRecord(
                    UUID.fromString("10000000-0000-0000-0000-000000000010"),
                    walletId1,
                    Money.of("1000000.00"),
                    Money.of("1020000.00"),
                    TransactionDate.of(testDate),
                    "Kiểm kê sáng");

            BalanceAdjustmentRecord eveningRecord = new BalanceAdjustmentRecord(
                    UUID.fromString("10000000-0000-0000-0000-000000000011"),
                    walletId1,
                    Money.of("1020000.00"),
                    Money.of("1050000.00"),
                    TransactionDate.of(testDate),
                    "Kiểm kê tối");

            repository.save(morningRecord);
            repository.save(eveningRecord);

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(walletId1);

            assertEquals(2, list.size());
            assertEquals(eveningRecord.getRecordId(), list.get(0).getRecordId(),
                    "Bản ghi tạo sau trong cùng ngày phải xếp trước bản ghi tạo trước");
            assertEquals(morningRecord.getRecordId(), list.get(1).getRecordId());

            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(walletId1);
            assertTrue(latest.isPresent());
            assertEquals(eveningRecord.getRecordId(), latest.get().getRecordId(),
                    "findLatestByWalletId phải trả về bản ghi tạo sau cùng trong ngày");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when walletId is null")
        void shouldRejectNullWalletId() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.findByWalletId(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Should return unmodifiable list from findByWalletId")
        void shouldReturnUnmodifiableList() {
            repository.save(record1Wallet1);

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(walletId1);

            assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
        }
    }

    @Nested
    @DisplayName("Tests for findLatestByWalletId(UUID walletId)")
    class FindLatestByWalletIdTests {

        @Test
        @DisplayName("Should return the latest adjustment record for wallet with multiple records")
        void shouldReturnLatestAdjustmentRecord() {
            repository.save(record1Wallet1); // minus 10 days
            repository.save(record2Wallet1); // minus 5 days
            repository.save(record3Wallet1); // today

            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(walletId1);

            assertTrue(latest.isPresent());
            assertEquals(record3Wallet1.getRecordId(), latest.get().getRecordId());
            assertEquals(record3Wallet1.getAdjustedAt(), latest.get().getAdjustedAt());
        }

        @Test
        @DisplayName("Should return empty Optional when wallet has no adjustment history")
        void shouldReturnEmptyOptionalWhenNoHistory() {
            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(walletId1);

            assertTrue(latest.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when walletId is null")
        void shouldRejectNullWalletId() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> repository.findLatestByWalletId(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }
}

