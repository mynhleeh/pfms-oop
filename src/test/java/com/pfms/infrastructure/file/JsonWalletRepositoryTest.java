package com.pfms.infrastructure.file;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.ActiveWalletState;
import com.pfms.domain.model.wallet.ArchivedWalletState;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.file.support.JsonDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Kiểm thử kho lưu trữ tệp JsonWalletRepository")
class JsonWalletRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonDataStore dataStore;
    private JsonWalletRepository repository;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("test_pfms_wallets.json");
        this.dataStore = new JsonDataStore(storageFile);
        this.repository = new JsonWalletRepository(dataStore);
    }

    @Nested
    @DisplayName("Khởi tạo và kiểm tra tham số")
    class ConstructorTests {

        @Test
        @DisplayName("Từ chối khởi tạo khi JsonDataStore là null")
        void shouldThrowExceptionWhenDataStoreIsNull() {
            assertThrows(NullPointerException.class, () -> new JsonWalletRepository(null));
        }
    }

    @Nested
    @DisplayName("Thao tác CRUD cơ bản")
    class CrudOperationsTests {

        @Test
        @DisplayName("Lưu mới và nạp lại ví qua findById")
        void shouldSaveAndFindById() {
            UUID walletId = UUID.randomUUID();
            Wallet wallet = new Wallet(
                    walletId,
                    "Ví Tiền Mặt",
                    "Ghi chú",
                    true,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("500000.00"));

            Wallet saved = repository.save(wallet);
            assertNotNull(saved);
            assertEquals(walletId, saved.getId());

            Optional<Wallet> found = repository.findById(walletId);
            assertTrue(found.isPresent());
            assertEquals("Ví Tiền Mặt", found.get().getName());
            assertTrue(found.get().isDefaultWallet());

            // Kiểm tra tính bền vững trên đĩa vật lý qua instance DataStore mới
            JsonDataStore freshStore = new JsonDataStore(dataStore.getStoragePath());
            JsonWalletRepository freshRepo = new JsonWalletRepository(freshStore);
            Optional<Wallet> diskFound = freshRepo.findById(walletId);
            assertTrue(diskFound.isPresent());
            assertEquals("Ví Tiền Mặt", diskFound.get().getName());
        }

        @Test
        @DisplayName("Cập nhật ví đã tồn tại (Upsert)")
        void shouldUpdateExistingWallet() {
            UUID walletId = UUID.randomUUID();
            Wallet initial = new Wallet(
                    walletId,
                    "Ví Cũ",
                    "Ghi chú cũ",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("100000.00"));
            repository.save(initial);

            Wallet updated = new Wallet(
                    walletId,
                    "Ví Mới",
                    "Ghi chú mới",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("300000.00"));
            repository.save(updated);

            List<Wallet> all = repository.findAll();
            assertEquals(1, all.size());
            assertEquals("Ví Mới", all.get(0).getName());
            assertEquals(Money.of("300000.00"), all.get(0).getCurrentBalance());
        }

        @Test
        @DisplayName("Xóa ví theo ID và kiểm tra existsById")
        void shouldDeleteByIdAndVerifyExistence() {
            UUID walletId = UUID.randomUUID();
            Wallet wallet = new Wallet(
                    walletId,
                    "Ví Cần Xóa",
                    "",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.ZERO);

            repository.save(wallet);
            assertTrue(repository.existsById(walletId));

            boolean deleted = repository.deleteById(walletId);
            assertTrue(deleted);
            assertFalse(repository.existsById(walletId));
            assertTrue(repository.findById(walletId).isEmpty());

            // Xóa lại ID không tồn tại
            assertFalse(repository.deleteById(walletId));
        }

        @Test
        @DisplayName("Xóa toàn bộ ví qua deleteAll")
        void shouldDeleteAllWallets() {
            repository.save(new Wallet("Ví 1", "", false));
            repository.save(new Wallet("Ví 2", "", false));
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

        private UUID activeWalletId;
        private UUID archivedWalletId;
        private UUID defaultWalletId;

        @BeforeEach
        void seedData() {
            this.defaultWalletId = UUID.randomUUID();
            Wallet defaultWallet = new Wallet(
                    defaultWalletId,
                    "Tiền Mặt",
                    "Ví mặc định hệ thống",
                    true,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("1000000.00"));
            repository.save(defaultWallet);

            this.activeWalletId = UUID.randomUUID();
            Wallet activeWallet = new Wallet(
                    activeWalletId,
                    "Ngân Hàng VCB",
                    "Tài khoản thanh toán",
                    false,
                    LocalDateTime.now(),
                    new ActiveWalletState(),
                    Money.of("5000000.00"));
            repository.save(activeWallet);

            this.archivedWalletId = UUID.randomUUID();
            Wallet archivedWallet = new Wallet(
                    archivedWalletId,
                    "Ví Cũ Đóng",
                    "Đã lưu trữ",
                    false,
                    LocalDateTime.now(),
                    new ArchivedWalletState(),
                    Money.ZERO);
            repository.save(archivedWallet);
        }

        @Test
        @DisplayName("Tìm kiếm ví theo tên không phân biệt hoa thường và cắt khoảng trắng")
        void shouldFindByNameCaseInsensitiveAndTrimmed() {
            Optional<Wallet> found = repository.findByName("  tiền mặt  ");
            assertTrue(found.isPresent());
            assertEquals(defaultWalletId, found.get().getId());

            Optional<Wallet> notFound = repository.findByName("Không Tồn Tại");
            assertTrue(notFound.isEmpty());

            assertThrows(NullPointerException.class, () -> repository.findByName(null));
        }

        @Test
        @DisplayName("Kiểm tra tồn tại ví theo tên")
        void shouldCheckExistenceByName() {
            assertTrue(repository.existsByName("ngân hàng vcb"));
            assertTrue(repository.existsByName("  NGÂN HÀNG VCB  "));
            assertFalse(repository.existsByName("Khác"));

            assertThrows(NullPointerException.class, () -> repository.existsByName(null));
        }

        @Test
        @DisplayName("Lọc danh sách ví theo trạng thái")
        void shouldFindByStatus() {
            List<Wallet> activeList = repository.findByStatus(EntityStatus.ACTIVE);
            assertEquals(2, activeList.size());

            List<Wallet> archivedList = repository.findByStatus(EntityStatus.ARCHIVED);
            assertEquals(1, archivedList.size());
            assertEquals(archivedWalletId, archivedList.get(0).getId());

            List<Wallet> deletedList = repository.findByStatus(EntityStatus.DELETED);
            assertTrue(deletedList.isEmpty());

            assertThrows(NullPointerException.class, () -> repository.findByStatus(null));
        }

        @Test
        @DisplayName("Đếm số lượng ví theo trạng thái")
        void shouldCountByStatus() {
            assertEquals(2, repository.countByStatus(EntityStatus.ACTIVE));
            assertEquals(1, repository.countByStatus(EntityStatus.ARCHIVED));
            assertEquals(0, repository.countByStatus(EntityStatus.DELETED));

            assertThrows(NullPointerException.class, () -> repository.countByStatus(null));
        }

        @Test
        @DisplayName("Truy vấn ví mặc định của hệ thống")
        void shouldFindDefaultWallet() {
            Optional<Wallet> defaultWallet = repository.findDefaultWallet();
            assertTrue(defaultWallet.isPresent());
            assertEquals(defaultWalletId, defaultWallet.get().getId());
            assertTrue(defaultWallet.get().isDefaultWallet());
        }
    }
}
