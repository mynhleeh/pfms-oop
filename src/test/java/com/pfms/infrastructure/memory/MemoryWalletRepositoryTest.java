package com.pfms.infrastructure.memory;

import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.model.wallet.WalletStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MemoryWalletRepository Unit Tests")
class MemoryWalletRepositoryTest {

    private MemoryWalletRepository repository;
    private Wallet defaultCashWallet;
    private Wallet savingsWallet;
    private Wallet archivedWallet;

    @BeforeEach
    void setUp() {
        repository = new MemoryWalletRepository();

        defaultCashWallet = new Wallet(
                "w-default",
                "Tiền mặt",
                "Ví tiền mặt mặc định",
                true,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.ZERO
        );

        savingsWallet = new Wallet(
                "w-savings",
                "Tiết kiệm",
                "Quỹ dự phòng khẩn cấp",
                false,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("5000000.00")
        );

        archivedWallet = new Wallet(
                "w-archived",
                "Tài khoản cũ",
                "Ví đã ngừng sử dụng",
                false,
                LocalDateTime.now(),
                new ArchivedWalletState(),
                Money.ZERO
        );
    }

    // region Constructor Tests
    @Nested
    @DisplayName("Tests for Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize with default Wallet::getId extractor")
        void shouldInitializeWithDefaultIdExtractor() {
            assertNotNull(repository);
            assertTrue(repository.findAll().isEmpty());

            repository.save(defaultCashWallet);
            assertTrue(repository.existsById("w-default"));
        }
    }
    // endregion

    // region FindByName Tests
    @Nested
    @DisplayName("Tests for findByName(String name)")
    class FindByNameTests {

        @Test
        @DisplayName("Should return wallet when name matches exactly")
        void shouldReturnWalletWhenNameMatchesExactly() {
            repository.save(defaultCashWallet);

            Optional<Wallet> found = repository.findByName("Tiền mặt");

            assertTrue(found.isPresent());
            assertEquals("w-default", found.get().getId());
            assertEquals("Tiền mặt", found.get().getName());
        }

        @Test
        @DisplayName("Should return wallet when name matches case-insensitively")
        void shouldReturnWalletWhenNameMatchesCaseInsensitively() {
            repository.save(defaultCashWallet);

            Optional<Wallet> lower = repository.findByName("tiền mặt");
            Optional<Wallet> upper = repository.findByName("TIỀN MẶT");

            assertTrue(lower.isPresent());
            assertEquals(defaultCashWallet, lower.get());
            assertTrue(upper.isPresent());
            assertEquals(defaultCashWallet, upper.get());
        }

        @Test
        @DisplayName("Should return wallet when name has surrounding whitespace")
        void shouldReturnWalletWhenNameHasSurroundingWhitespace() {
            repository.save(defaultCashWallet);

            Optional<Wallet> found = repository.findByName("   Tiền mặt   ");

            assertTrue(found.isPresent());
            assertEquals(defaultCashWallet, found.get());
        }

        @Test
        @DisplayName("Should return empty Optional when name does not exist")
        void shouldReturnEmptyWhenNameDoesNotExist() {
            repository.save(defaultCashWallet);

            Optional<Wallet> found = repository.findByName("Ví Đầu Tư");

            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.findByName(null));
        }
    }
    // endregion

    // region ExistsByName Tests
    @Nested
    @DisplayName("Tests for existsByName(String name)")
    class ExistsByNameTests {

        @Test
        @DisplayName("Should return true when name exists exactly")
        void shouldReturnTrueWhenNameExists() {
            repository.save(defaultCashWallet);

            assertTrue(repository.existsByName("Tiền mặt"));
        }

        @Test
        @DisplayName("Should return true when name matches case-insensitively or with whitespace")
        void shouldReturnTrueWhenNameMatchesCaseInsensitiveOrWhitespace() {
            repository.save(defaultCashWallet);

            assertTrue(repository.existsByName("tiền mặt"));
            assertTrue(repository.existsByName("  TIỀN MẶT  "));
        }

        @Test
        @DisplayName("Should return true when wallet exists in archived state")
        void shouldReturnTrueWhenWalletIsArchived() {
            repository.save(archivedWallet);

            assertTrue(repository.existsByName("Tài khoản cũ"));
        }

        @Test
        @DisplayName("Should return false when name does not exist")
        void shouldReturnFalseWhenNameDoesNotExist() {
            repository.save(defaultCashWallet);

            assertFalse(repository.existsByName("Ví Không Tồn Tại"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.existsByName(null));
        }
    }
    // endregion

    // region FindByStatus Tests
    @Nested
    @DisplayName("Tests for findByStatus(WalletStatus status)")
    class FindByStatusTests {

        @Test
        @DisplayName("Should return only active wallets when filtering by ACTIVE")
        void shouldReturnOnlyActiveWallets() {
            repository.save(defaultCashWallet);
            repository.save(savingsWallet);
            repository.save(archivedWallet);

            List<Wallet> activeWallets = repository.findByStatus(WalletStatus.ACTIVE);

            assertEquals(2, activeWallets.size());
            assertTrue(activeWallets.contains(defaultCashWallet));
            assertTrue(activeWallets.contains(savingsWallet));
            assertFalse(activeWallets.contains(archivedWallet));
        }

        @Test
        @DisplayName("Should return only archived wallets when filtering by ARCHIVED")
        void shouldReturnOnlyArchivedWallets() {
            repository.save(defaultCashWallet);
            repository.save(archivedWallet);

            List<Wallet> archivedWallets = repository.findByStatus(WalletStatus.ARCHIVED);

            assertEquals(1, archivedWallets.size());
            assertTrue(archivedWallets.contains(archivedWallet));
        }

        @Test
        @DisplayName("Should return empty list when no wallets match status")
        void shouldReturnEmptyListWhenNoWalletsMatchStatus() {
            repository.save(defaultCashWallet);

            List<Wallet> archived = repository.findByStatus(WalletStatus.ARCHIVED);

            assertNotNull(archived);
            assertTrue(archived.isEmpty());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when status is null")
        void shouldThrowExceptionWhenStatusIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.findByStatus(null));
        }
    }
    // endregion

    // region CountByStatus Tests
    @Nested
    @DisplayName("Tests for countByStatus(WalletStatus status)")
    class CountByStatusTests {

        @Test
        @DisplayName("Should return correct count for each status")
        void shouldReturnCorrectCountForEachStatus() {
            repository.save(defaultCashWallet);
            repository.save(savingsWallet);
            repository.save(archivedWallet);

            assertEquals(2, repository.countByStatus(WalletStatus.ACTIVE));
            assertEquals(1, repository.countByStatus(WalletStatus.ARCHIVED));
        }

        @Test
        @DisplayName("Should return zero when no wallets match status")
        void shouldReturnZeroWhenNoWalletsMatchStatus() {
            repository.save(defaultCashWallet);

            assertEquals(0, repository.countByStatus(WalletStatus.ARCHIVED));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when status is null")
        void shouldThrowExceptionWhenStatusIsNull() {
            assertThrows(IllegalArgumentException.class, () -> repository.countByStatus(null));
        }
    }
    // endregion

    // region FindDefaultWallet Tests
    @Nested
    @DisplayName("Tests for findDefaultWallet()")
    class FindDefaultWalletTests {

        @Test
        @DisplayName("Should return default wallet when it exists in repository")
        void shouldReturnDefaultWalletWhenExists() {
            repository.save(savingsWallet);
            repository.save(defaultCashWallet);

            Optional<Wallet> result = repository.findDefaultWallet();

            assertTrue(result.isPresent());
            assertEquals(defaultCashWallet, result.get());
            assertTrue(result.get().isDefaultWallet());
        }

        @Test
        @DisplayName("Should return empty Optional when no default wallet exists")
        void shouldReturnEmptyWhenNoDefaultWalletExists() {
            repository.save(savingsWallet);
            repository.save(archivedWallet);

            Optional<Wallet> result = repository.findDefaultWallet();

            assertTrue(result.isEmpty());
        }
    }
    // endregion

    // region Clear Tests
    @Nested
    @DisplayName("Tests for clear()")
    class ClearTests {

        @Test
        @DisplayName("Should clear all wallets from repository")
        void shouldClearAllWallets() {
            repository.save(defaultCashWallet);
            repository.save(savingsWallet);
            assertEquals(2, repository.findAll().size());

            repository.clear();

            assertEquals(0, repository.findAll().size());
            assertFalse(repository.existsById("w-default"));
            assertFalse(repository.existsByName("Tiền mặt"));
        }
    }
    // endregion

    // region Concurrency Tests
    @Nested
    @DisplayName("Tests for Concurrent Operations")
    class ConcurrencyTests {

        @Test
        @DisplayName("Should handle concurrent wallet saves and reads safely")
        void shouldHandleConcurrentOperationsSafely() throws InterruptedException {
            int threadCount = 10;
            int walletsPerThread = 50;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                final int threadIndex = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < walletsPerThread; i++) {
                            String walletId = "w-" + threadIndex + "-" + i;
                            String walletName = "Ví-" + threadIndex + "-" + i;
                            Wallet w = new Wallet(
                                    walletId,
                                    walletName,
                                    "Note",
                                    false,
                                    LocalDateTime.now(),
                                    new ActiveWalletState(),
                                    Money.ZERO
                            );
                            repository.save(w);
                            repository.findByName(walletName);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Concurrent operations timed out");
            executor.shutdown();

            assertEquals(threadCount * walletsPerThread, repository.findAll().size());
        }
    }
    // endregion
}

