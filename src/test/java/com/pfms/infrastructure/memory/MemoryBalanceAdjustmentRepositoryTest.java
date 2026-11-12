package com.pfms.infrastructure.memory;

import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử đơn vị cho kho lưu trữ MemoryBalanceAdjustmentRepository.
 * Kiểm chứng toàn diện các thao tác dữ liệu trên bộ nhớ RAM, cơ chế Append-only bất biến,
 * thuật toán sắp xếp đối soát theo thời gian và tính an toàn luồng trong môi trường đồng thời.
 */
@DisplayName("MemoryBalanceAdjustmentRepository Unit Tests")
class MemoryBalanceAdjustmentRepositoryTest {

    private MemoryBalanceAdjustmentRepository repository;
    private UUID walletId1;
    private UUID walletId2;

    private BalanceAdjustmentRecord record1Wallet1;
    private BalanceAdjustmentRecord record2Wallet1;
    private BalanceAdjustmentRecord record3Wallet1;
    private BalanceAdjustmentRecord record1Wallet2;

    @BeforeEach
    void setUp() {
        repository = new MemoryBalanceAdjustmentRepository();

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

    // region Constructor Tests
    @Nested
    @DisplayName("Tests for Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize empty repository with BalanceAdjustmentRecord::getRecordId extractor")
        void shouldInitializeEmptyRepository() {
            assertNotNull(repository);
            assertTrue(repository.findAll().isEmpty());

            repository.save(record1Wallet1);
            assertTrue(repository.existsById(record1Wallet1.getRecordId()));
        }
    }
    // endregion

    // region Save Tests
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
        @DisplayName("Should throw NullPointerException when saving null entity")
        void shouldRejectNullEntity() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.save(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Should throw UnsupportedOperationException when attempting to update an existing record (Append-only invariant)")
        void shouldPreventUpdatingExistingRecord() {
            repository.save(record1Wallet1);

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.save(record1Wallet1));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
        }
    }
    // endregion

    // region DeleteById Tests
    @Nested
    @DisplayName("Tests for deleteById(UUID id) - Audit Invariant Safeguard")
    class DeleteByIdOperationTests {

        @Test
        @DisplayName("Should throw UnsupportedOperationException and keep record intact when attempting to delete existing record")
        void shouldThrowUnsupportedOperationExceptionWhenDeletingExistingRecord() {
            repository.save(record1Wallet1);

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.deleteById(record1Wallet1.getRecordId()));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
            assertTrue(repository.existsById(record1Wallet1.getRecordId()),
                    "Bản ghi kiểm kê phải tiếp tục tồn tại nguyên vẹn sau khi gọi deleteById");
        }

        @Test
        @DisplayName("Should throw UnsupportedOperationException even when id does not exist")
        void shouldThrowUnsupportedOperationExceptionEvenWhenRecordDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();

            UnsupportedOperationException ex = assertThrows(
                    UnsupportedOperationException.class,
                    () -> repository.deleteById(nonExistentId));

            assertTrue(ex.getMessage().contains("bất biến kiểm toán"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when id is null")
        void shouldRejectNullIdWithNullPointerException() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.deleteById(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }
    // endregion

    // region FindByWalletId Tests
    @Nested
    @DisplayName("Tests for findByWalletId(UUID walletId)")
    class FindByWalletIdTests {

        @Test
        @DisplayName("Should return adjustment records for specific wallet sorted by adjustedAt descending")
        void shouldReturnAdjustmentsSortedDescending() {
            repository.save(record1Wallet1); // -10 days
            repository.save(record3Wallet1); // today
            repository.save(record2Wallet1); // -5 days
            repository.save(record1Wallet2); // different wallet

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(walletId1);

            assertEquals(3, list.size());
            assertEquals(record3Wallet1.getRecordId(), list.get(0).getRecordId());
            assertEquals(record2Wallet1.getRecordId(), list.get(1).getRecordId());
            assertEquals(record1Wallet1.getRecordId(), list.get(2).getRecordId());
        }

        @Test
        @DisplayName("Should rank later inserted record higher when two adjustments occur on the same date")
        void shouldRankLaterInsertedRecordHigherWhenAdjustedOnSameDate() {
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
                    "Bản ghi chèn sau trong cùng ngày phải đứng trước bản ghi chèn trước");
            assertEquals(morningRecord.getRecordId(), list.get(1).getRecordId());
        }

        @Test
        @DisplayName("Should isolate records strictly between different wallets")
        void shouldIsolateRecordsBetweenWallets() {
            repository.save(record1Wallet1);
            repository.save(record1Wallet2);

            List<BalanceAdjustmentRecord> wallet1List = repository.findByWalletId(walletId1);
            List<BalanceAdjustmentRecord> wallet2List = repository.findByWalletId(walletId2);

            assertEquals(1, wallet1List.size());
            assertEquals(record1Wallet1.getRecordId(), wallet1List.get(0).getRecordId());

            assertEquals(1, wallet2List.size());
            assertEquals(record1Wallet2.getRecordId(), wallet2List.get(0).getRecordId());
        }

        @Test
        @DisplayName("Should return empty list when wallet has no adjustment history")
        void shouldReturnEmptyListWhenWalletHasNoAdjustments() {
            repository.save(record1Wallet1);

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(UUID.randomUUID());

            assertNotNull(list);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when walletId is null")
        void shouldRejectNullWalletId() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.findByWalletId(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }

        @Test
        @DisplayName("Should return unmodifiable list preventing external mutation")
        void shouldReturnUnmodifiableList() {
            repository.save(record1Wallet1);

            List<BalanceAdjustmentRecord> list = repository.findByWalletId(walletId1);

            assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
        }
    }
    // endregion

    // region FindLatestByWalletId Tests
    @Nested
    @DisplayName("Tests for findLatestByWalletId(UUID walletId)")
    class FindLatestByWalletIdTests {

        @Test
        @DisplayName("Should return latest adjustment record for wallet with multiple records")
        void shouldReturnLatestAdjustmentRecord() {
            repository.save(record1Wallet1); // -10 days
            repository.save(record2Wallet1); // -5 days
            repository.save(record3Wallet1); // today

            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(walletId1);

            assertTrue(latest.isPresent());
            assertEquals(record3Wallet1.getRecordId(), latest.get().getRecordId());
            assertEquals(record3Wallet1.getAdjustedAt(), latest.get().getAdjustedAt());
        }

        @Test
        @DisplayName("Should return later inserted record when two records share the same latest date")
        void shouldReturnLaterInsertedRecordWhenSameLatestDate() {
            LocalDate testDate = LocalDate.now();

            BalanceAdjustmentRecord recordA = new BalanceAdjustmentRecord(
                    UUID.fromString("10000000-0000-0000-0000-000000000020"),
                    walletId1,
                    Money.of("1000000.00"),
                    Money.of("1020000.00"),
                    TransactionDate.of(testDate),
                    "Kiểm kê A");

            BalanceAdjustmentRecord recordB = new BalanceAdjustmentRecord(
                    UUID.fromString("10000000-0000-0000-0000-000000000021"),
                    walletId1,
                    Money.of("1020000.00"),
                    Money.of("1050000.00"),
                    TransactionDate.of(testDate),
                    "Kiểm kê B");

            repository.save(recordA);
            repository.save(recordB);

            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(walletId1);

            assertTrue(latest.isPresent());
            assertEquals(recordB.getRecordId(), latest.get().getRecordId(),
                    "Bản ghi chèn sau cùng trong ngày mới nhất phải được trả về");
        }

        @Test
        @DisplayName("Should return empty Optional when wallet has no adjustment records")
        void shouldReturnEmptyOptionalWhenNoHistory() {
            Optional<BalanceAdjustmentRecord> latest = repository.findLatestByWalletId(UUID.randomUUID());

            assertTrue(latest.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when walletId is null")
        void shouldRejectNullWalletId() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> repository.findLatestByWalletId(null));

            assertTrue(ex.getMessage().contains("không được để null"));
        }
    }
    // endregion

    // region Generic Operations Tests
    @Nested
    @DisplayName("Tests for Generic Repository Operations (findById, findAll, existsById)")
    class GenericOperationsTests {

        @Test
        @DisplayName("Should find record by id successfully")
        void shouldFindById() {
            repository.save(record1Wallet1);

            Optional<BalanceAdjustmentRecord> found = repository.findById(record1Wallet1.getRecordId());

            assertTrue(found.isPresent());
            assertEquals(record1Wallet1, found.get());
        }

        @Test
        @DisplayName("Should return empty Optional when findById non-existent id")
        void shouldReturnEmptyWhenFindByIdNotFound() {
            Optional<BalanceAdjustmentRecord> found = repository.findById(UUID.randomUUID());

            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when findById null")
        void shouldRejectNullIdOnFindById() {
            assertThrows(NullPointerException.class, () -> repository.findById(null));
        }

        @Test
        @DisplayName("Should check existence by id correctly")
        void shouldCheckExistsById() {
            repository.save(record1Wallet1);

            assertTrue(repository.existsById(record1Wallet1.getRecordId()));
            assertFalse(repository.existsById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should return all saved records in unmodifiable list")
        void shouldFindAll() {
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
    // endregion

    // region Clear Tests
    @Nested
    @DisplayName("Tests for clear()")
    class ClearTests {

        @Test
        @DisplayName("Should clear storage and reset insertion sequence")
        void shouldClearStorageAndResetSequence() {
            repository.save(record1Wallet1);
            repository.save(record2Wallet1);

            assertEquals(2, repository.findAll().size());

            repository.clear();

            assertTrue(repository.findAll().isEmpty());
            assertFalse(repository.existsById(record1Wallet1.getRecordId()));
            assertFalse(repository.existsById(record2Wallet1.getRecordId()));

            // Sau khi clear, bản ghi mới có thể được lưu lại mà không bị lỗi
            assertDoesNotThrow(() -> repository.save(record1Wallet1));
            assertEquals(1, repository.findAll().size());
        }
    }
    // endregion

    // region Concurrency Tests
    @Nested
    @DisplayName("Tests for Concurrent Operations")
    class ConcurrencyTests {

        @Test
        @DisplayName("Should handle concurrent saves from multiple threads safely")
        void shouldHandleConcurrentSavesSafely() throws InterruptedException {
            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            LocalDate today = LocalDate.now();

            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                                UUID.randomUUID(),
                                walletId1,
                                Money.of("1000000.00"),
                                Money.of(String.format("%d0000.00", 101 + idx)),
                                TransactionDate.of(today.minusDays(idx)),
                                "Kiểm kê luồng " + idx
                        );
                        repository.save(record);
                    } catch (Exception e) {
                        fail("Exception in concurrent save thread: " + e.getMessage());
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            boolean completed = doneLatch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            assertTrue(completed, "Các tác vụ ghi đồng thời phải hoàn thành trong 5 giây");
            assertEquals(threadCount, repository.findAll().size());
            assertEquals(threadCount, repository.findByWalletId(walletId1).size());
            assertTrue(repository.findLatestByWalletId(walletId1).isPresent());
        }
    }
    // endregion
}

