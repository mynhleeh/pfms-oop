package com.pfms.domain.repository;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử hợp đồng định hình hành vi cho WalletRepository.
 */
@DisplayName("WalletRepository Contract Tests")
class WalletRepositoryContractTest {

    // region Test Doubles
    static class InMemoryWalletRepositoryStub implements WalletRepository {
        private final Map<String, Wallet> storage = new LinkedHashMap<>();

        @Override
        public Wallet save(Wallet entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Entity must not be null");
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Wallet> findById(String id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Wallet> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(storage.values()));
        }

        @Override
        public boolean existsById(String id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return storage.containsKey(id);
        }

        @Override
        public boolean deleteById(String id) {
            if (id == null) {
                throw new IllegalArgumentException("ID must not be null");
            }
            return storage.remove(id) != null;
        }

        @Override
        public Optional<Wallet> findByName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name must not be null");
            }
            String trimmed = name.trim();
            return storage.values().stream()
                    .filter(w -> w.getName().equalsIgnoreCase(trimmed))
                    .findFirst();
        }

        @Override
        public boolean existsByName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name must not be null");
            }
            String trimmed = name.trim();
            return storage.values().stream()
                    .anyMatch(w -> w.getName().equalsIgnoreCase(trimmed));
        }

        @Override
        public List<Wallet> findByStatus(WalletStatus status) {
            if (status == null) {
                throw new IllegalArgumentException("Status must not be null");
            }
            return storage.values().stream()
                    .filter(w -> w.getStatus() == status)
                    .toList();
        }

        @Override
        public long countByStatus(WalletStatus status) {
            if (status == null) {
                throw new IllegalArgumentException("Status must not be null");
            }
            return storage.values().stream()
                    .filter(w -> w.getStatus() == status)
                    .count();
        }

        @Override
        public Optional<Wallet> findDefaultWallet() {
            return storage.values().stream()
                    .filter(Wallet::isDefaultWallet)
                    .findFirst();
        }
    }
    // endregion

    private InMemoryWalletRepositoryStub repository;
    private Wallet defaultCashWallet;
    private Wallet savingsWallet;
    private Wallet archivedWallet;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWalletRepositoryStub();

        defaultCashWallet = new Wallet(
                "w-default",
                "Tiền mặt",
                "Ví tiền mặt mặc định",
                true,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.ZERO);

        savingsWallet = new Wallet(
                "w-savings",
                "Tiết kiệm",
                "Quỹ dự phòng khẩn cấp",
                false,
                LocalDateTime.now(),
                new ActiveWalletState(),
                Money.of("5000000.00"));

        archivedWallet = new Wallet(
                "w-archived",
                "Tài khoản cũ",
                "Ví đã ngừng sử dụng",
                false,
                LocalDateTime.now(),
                new ArchivedWalletState(),
                Money.ZERO);
    }

    // region FindByName Tests
    @Nested
    @DisplayName("Tests for findByName(String name)")
    class FindByNameTests {

        @Test
        @DisplayName("Should return wallet when name matches exactly")
        void shouldReturnWalletWhenNameMatchesExactly() {
            repository.save(defaultCashWallet);

            Optional<Wallet> result = repository.findByName("Tiền mặt");

            assertTrue(result.isPresent());
            assertEquals(defaultCashWallet, result.get());
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

            Optional<Wallet> result = repository.findByName("   Tiền mặt   ");

            assertTrue(result.isPresent());
            assertEquals(defaultCashWallet, result.get());
        }

        @Test
        @DisplayName("Should return empty Optional when name does not exist")
        void shouldReturnEmptyWhenNameDoesNotExist() {
            repository.save(defaultCashWallet);

            Optional<Wallet> result = repository.findByName("Chứng khoán");

            assertTrue(result.isEmpty());
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
        @DisplayName("Should return true when wallet exists but is archived")
        void shouldReturnTrueWhenWalletIsArchived() {
            repository.save(archivedWallet);

            assertTrue(repository.existsByName("Tài khoản cũ"));
        }

        @Test
        @DisplayName("Should return false when name does not exist")
        void shouldReturnFalseWhenNameDoesNotExist() {
            repository.save(defaultCashWallet);

            assertFalse(repository.existsByName("Ví Đầu Tư"));
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

    // region Generic CRUD Integration Tests
    @Nested
    @DisplayName("Tests for inherited GenericRepository operations")
    class InheritedCrudTests {

        @Test
        @DisplayName("Should support CRUD operations seamlessly on Wallet entity")
        void shouldSupportCrudOperations() {
            repository.save(defaultCashWallet);

            assertTrue(repository.existsById(defaultCashWallet.getId()));
            assertEquals(defaultCashWallet, repository.findById(defaultCashWallet.getId()).orElseThrow());
            assertEquals(1, repository.findAll().size());

            assertTrue(repository.deleteById(defaultCashWallet.getId()));
            assertFalse(repository.existsById(defaultCashWallet.getId()));
            assertEquals(0, repository.findAll().size());
        }
    }
    // endregion
}
