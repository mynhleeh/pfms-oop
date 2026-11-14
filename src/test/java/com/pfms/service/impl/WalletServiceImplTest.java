package com.pfms.service.impl;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.infrastructure.memory.MemoryBalanceAdjustmentRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.infrastructure.memory.MemoryWalletRepository;
import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WalletServiceImpl Unit Tests")
class WalletServiceImplTest {

    private MemoryWalletRepository walletRepository;
    private MemoryBalanceAdjustmentRepository balanceAdjustmentRepository;
    private MemoryTransactionRepository transactionRepository;
    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletRepository = new MemoryWalletRepository();
        balanceAdjustmentRepository = new MemoryBalanceAdjustmentRepository();
        transactionRepository = new MemoryTransactionRepository();
        walletService = new WalletServiceImpl(walletRepository, balanceAdjustmentRepository, transactionRepository);
    }

    @Nested
    @DisplayName("Constructor Dependency Injection Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Ném NullPointerException khi WalletRepository null")
        void shouldThrowExceptionWhenWalletRepositoryIsNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    new WalletServiceImpl(null, balanceAdjustmentRepository, transactionRepository));
            assertTrue(ex.getMessage().contains("WalletRepository"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi BalanceAdjustmentRepository null")
        void shouldThrowExceptionWhenBalanceAdjustmentRepositoryIsNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    new WalletServiceImpl(walletRepository, null, transactionRepository));
            assertTrue(ex.getMessage().contains("BalanceAdjustmentRepository"));
        }

        @Test
        @DisplayName("Ném NullPointerException khi TransactionRepository null")
        void shouldThrowExceptionWhenTransactionRepositoryIsNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    new WalletServiceImpl(walletRepository, balanceAdjustmentRepository, null));
            assertTrue(ex.getMessage().contains("TransactionRepository"));
        }
    }

    @Nested
    @DisplayName("initializeDefaultWallet() Tests")
    class InitializeDefaultWalletTests {

        @Test
        @DisplayName("Tạo ví 'Tiền mặt' mặc định ở trạng thái ACTIVE và số dư 0.00 khi kho chưa có ví mặc định")
        void shouldInitializeDefaultWalletWhenRepositoryIsEmpty() {
            // Act
            walletService.initializeDefaultWallet();

            // Assert
            Optional<Wallet> defaultWalletOpt = walletRepository.findDefaultWallet();
            assertTrue(defaultWalletOpt.isPresent(), "Phải tồn tại ví mặc định trong kho sau khi khởi tạo");

            Wallet defaultWallet = defaultWalletOpt.get();
            assertEquals("Tiền mặt", defaultWallet.getName());
            assertTrue(defaultWallet.isDefaultWallet());
            assertEquals(EntityStatus.ACTIVE, defaultWallet.getStatus());
            assertEquals(Money.ZERO, defaultWallet.getCurrentBalance());
            assertNotNull(defaultWallet.getId());
        }

        @Test
        @DisplayName("Không tạo thêm ví mới nếu hệ thống đã tồn tại ví mặc định (Idempotency)")
        void shouldNotCreateNewWalletWhenDefaultWalletAlreadyExists() {
            // Arrange
            Wallet existingDefault = new Wallet(UUID.randomUUID(), "Tiền mặt", "Ví đã có từ trước", true);
            walletRepository.save(existingDefault);
            assertEquals(1, walletRepository.findAll().size());

            // Act
            walletService.initializeDefaultWallet();

            // Assert
            assertEquals(1, walletRepository.findAll().size(), "Số lượng ví không được tăng thêm");
            Optional<Wallet> defaultWalletOpt = walletRepository.findDefaultWallet();
            assertTrue(defaultWalletOpt.isPresent());
            assertEquals(existingDefault.getId(), defaultWalletOpt.get().getId(), "Mã ví mặc định cũ phải được giữ nguyên");
            assertEquals("Ví đã có từ trước", defaultWalletOpt.get().getNote());
        }
    }

    @Nested
    @DisplayName("createWallet() Tests")
    class CreateWalletTests {

        @Test
        @DisplayName("Tạo ví mới thành công với thông tin hợp lệ (Happy Path)")
        void shouldCreateWalletSuccessfullyWhenRequestIsValid() {
            // Arrange
            CreateWalletRequestDTO request = new CreateWalletRequestDTO("Thẻ Vietcombank", "Tài khoản nhận lương");

            // Act
            WalletResponseDTO response = walletService.createWallet(request);

            // Assert
            assertNotNull(response);
            assertNotNull(response.getId());
            assertEquals("Thẻ Vietcombank", response.getName());
            assertEquals("Tài khoản nhận lương", response.getNote());
            assertEquals(0, response.getBalance().compareTo(java.math.BigDecimal.ZERO));
            assertEquals("VNĐ", response.getCurrency());
            assertFalse(response.isDefaultWallet());
            assertEquals("ACTIVE", response.getStatus());
            assertNotNull(response.getCreatedAt());

            // Kiểm tra kho lưu trữ
            Optional<Wallet> savedWallet = walletRepository.findById(response.getId());
            assertTrue(savedWallet.isPresent());
            assertEquals("Thẻ Vietcombank", savedWallet.get().getName());
        }

        @Test
        @DisplayName("Từ chối tạo ví khi tên ví đã tồn tại (không phân biệt hoa thường)")
        void shouldRejectCreationWhenWalletNameAlreadyExists() {
            // Arrange
            CreateWalletRequestDTO firstRequest = new CreateWalletRequestDTO("Ví MoMo", "Ví ban đầu");
            walletService.createWallet(firstRequest);

            // Act & Assert (Trùng chữ hoa/thường)
            CreateWalletRequestDTO duplicateRequest = new CreateWalletRequestDTO("  ví momo  ", "Trùng lặp");
            com.pfms.domain.exception.DuplicateEntityException ex = assertThrows(
                    com.pfms.domain.exception.DuplicateEntityException.class,
                    () -> walletService.createWallet(duplicateRequest)
            );

            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_NAM_002, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("Ví MoMo") || ex.getMessage().contains("ví momo"));
        }

        @Test
        @DisplayName("Chấp nhận tên ví tại cận biên 1 ký tự và 50 ký tự (Boundary)")
        void shouldAcceptBoundaryWalletNamesOf1And50Chars() {
            WalletResponseDTO minWallet = walletService.createWallet(new CreateWalletRequestDTO("A", "Biên dưới"));
            assertEquals("A", minWallet.getName());

            String exact50 = "V".repeat(50);
            WalletResponseDTO maxWallet = walletService.createWallet(new CreateWalletRequestDTO(exact50, "Biên trên"));
            assertEquals(exact50, maxWallet.getName());
        }

        @Test
        @DisplayName("Kiểm tra định dạng tên ví (ERR_NAM_001) và ghi chú trước khi truy vấn existsByName")
        void shouldValidateNameAndNoteFormatBeforeQueryingRepositoryOnCreate() {
            MemoryWalletRepository guardWalletRepo = new MemoryWalletRepository() {
                @Override
                public boolean existsByName(String name) {
                    fail("Không được phép truy vấn existsByName khi tên hoặc ghi chú chưa qua bước xác thực định dạng");
                    return false;
                }
            };
            WalletServiceImpl guardedService = new WalletServiceImpl(
                    guardWalletRepo, balanceAdjustmentRepository, transactionRepository);

            String name51Chars = "A".repeat(51);
            com.pfms.domain.exception.BusinessException nameEx = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> guardedService.createWallet(new CreateWalletRequestDTO(name51Chars, "Ghi chú hợp lệ"))
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_NAM_001, nameEx.getErrorCode());

            String note256Chars = "N".repeat(256);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> guardedService.createWallet(new CreateWalletRequestDTO("Ví Hợp Lệ", note256Chars))
            );
        }

        @Test
        @DisplayName("Ném NullPointerException khi request null")
        void shouldThrowExceptionWhenRequestIsNull() {
            assertThrows(NullPointerException.class, () -> walletService.createWallet(null));
        }
    }

    @Nested
    @DisplayName("getWalletsSummary() Tests")
    class GetWalletsSummaryTests {

        @Test
        @DisplayName("Gán tỷ trọng 0.00% cho mọi ví khi tổng tài sản bằng không (Zero Division Safety)")
        void shouldReturnZeroSharesWhenTotalBalanceIsZero() {
            // Arrange
            walletService.createWallet(new CreateWalletRequestDTO("Ví Tiền mặt"));
            walletService.createWallet(new CreateWalletRequestDTO("Ví MoMo"));

            // Act
            WalletsSummaryDTO summary = walletService.getWalletsSummary(EntityStatus.ACTIVE);

            // Assert
            assertNotNull(summary);
            assertEquals(2, summary.getWalletCount());
            assertEquals(0, summary.getTotalBalance().compareTo(java.math.BigDecimal.ZERO));
            for (WalletResponseDTO w : summary.getWallets()) {
                assertEquals(new java.math.BigDecimal("0.00"), summary.getWalletShare(w.getId()));
            }
        }

        @Test
        @DisplayName("Tính đúng tỷ trọng phân bổ làm tròn ngân hàng và sắp xếp giảm dần theo tỷ trọng")
        void shouldCalculateSharesAndSortDescendingWhenBalancesArePositive() {
            // Arrange: Tạo 3 ví với số dư khác nhau
            Wallet w1 = new Wallet(UUID.randomUUID(), "Ví 1", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("1000000.00"));
            Wallet w2 = new Wallet(UUID.randomUUID(), "Ví 2", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("3000000.00"));
            Wallet w3 = new Wallet(UUID.randomUUID(), "Ví 3", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("2000000.00"));

            walletRepository.save(w1);
            walletRepository.save(w2);
            walletRepository.save(w3);

            // Act
            WalletsSummaryDTO summary = walletService.getWalletsSummary(null);

            // Assert
            assertEquals(3, summary.getWalletCount());
            assertEquals(0, new java.math.BigDecimal("6000000.00").compareTo(summary.getTotalBalance()));

            // Tỷ trọng: Ví 2 (50.00%), Ví 3 (33.33%), Ví 1 (16.67%)
            assertEquals(new java.math.BigDecimal("50.00"), summary.getWalletShare(w2.getId()));
            assertEquals(new java.math.BigDecimal("33.33"), summary.getWalletShare(w3.getId()));
            assertEquals(new java.math.BigDecimal("16.67"), summary.getWalletShare(w1.getId()));

            // Kiểm tra thứ tự sắp xếp giảm dần của Map
            java.util.List<UUID> sortedKeys = new java.util.ArrayList<>(summary.getWalletShares().keySet());
            assertEquals(w2.getId(), sortedKeys.get(0));
            assertEquals(w3.getId(), sortedKeys.get(1));
            assertEquals(w1.getId(), sortedKeys.get(2));
        }

        @Test
        @DisplayName("Loại ví ARCHIVED có số dư bằng không khỏi bảng cơ cấu tỷ trọng")
        void shouldExcludeArchivedWalletWithZeroBalanceFromShares() {
            // Arrange
            Wallet activeWallet = new Wallet(UUID.randomUUID(), "Ví Active", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("500000.00"));
            Wallet archivedZeroWallet = new Wallet(UUID.randomUUID(), "Ví Archived Rỗng", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ArchivedWalletState(), Money.ZERO);

            walletRepository.save(activeWallet);
            walletRepository.save(archivedZeroWallet);

            // Act
            WalletsSummaryDTO summary = walletService.getWalletsSummary(null);

            // Assert
            assertEquals(2, summary.getWalletCount());
            assertEquals(1, summary.getWalletShares().size(), "Chỉ có ví có số dư hợp lệ trong bảng tỷ trọng");
            assertTrue(summary.getWalletShares().containsKey(activeWallet.getId()));
            assertFalse(summary.getWalletShares().containsKey(archivedZeroWallet.getId()));
            assertEquals(new java.math.BigDecimal("100.00"), summary.getWalletShare(activeWallet.getId()));
        }

        @Test
        @DisplayName("Lọc danh sách ví theo trạng thái")
        void shouldFilterWalletsByStatus() {
            // Arrange
            Wallet activeWallet = new Wallet(UUID.randomUUID(), "Ví Active", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("100.00"));
            Wallet archivedWallet = new Wallet(UUID.randomUUID(), "Ví Archived", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ArchivedWalletState(), Money.of("200.00"));

            walletRepository.save(activeWallet);
            walletRepository.save(archivedWallet);

            // Act & Assert
            WalletsSummaryDTO activeOnly = walletService.getWalletsSummary(EntityStatus.ACTIVE);
            assertEquals(1, activeOnly.getWalletCount());
            assertEquals("Ví Active", activeOnly.getWallets().get(0).getName());

            WalletsSummaryDTO archivedOnly = walletService.getWalletsSummary(EntityStatus.ARCHIVED);
            assertEquals(1, archivedOnly.getWalletCount());
            assertEquals("Ví Archived", archivedOnly.getWallets().get(0).getName());

            WalletsSummaryDTO all = walletService.getWalletsSummary(null);
            assertEquals(2, all.getWalletCount());
        }

        @Test
        @DisplayName("Tính chính xác tỷ trọng với quy mô tài sản lớn (hàng trăm tỷ VNĐ) không tràn số")
        void shouldHandleLargeBalancesWithBankersRounding() {
            Wallet w1 = new Wallet(UUID.randomUUID(), "Quỹ Đầu Tư A", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(),
                    Money.of("100000000000.00"));
            Wallet w2 = new Wallet(UUID.randomUUID(), "Quỹ Đầu Tư B", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(),
                    Money.of("700000000000.00"));

            walletRepository.save(w1);
            walletRepository.save(w2);

            WalletsSummaryDTO summary = walletService.getWalletsSummary(EntityStatus.ACTIVE);
            assertEquals(0, new java.math.BigDecimal("800000000000.00").compareTo(summary.getTotalBalance()));
            // 100 / 800 = 12.50%, 700 / 800 = 87.50%
            assertEquals(new java.math.BigDecimal("87.50"), summary.getWalletShare(w2.getId()));
            assertEquals(new java.math.BigDecimal("12.50"), summary.getWalletShare(w1.getId()));
        }
    }

    @Nested
    @DisplayName("updateWallet() Tests")
    class UpdateWalletTests {

        @Test
        @DisplayName("Cập nhật thông tin ví thành công khi tên mới hợp lệ (Happy Path)")
        void shouldUpdateWalletSuccessfullyWhenDataIsValid() {
            // Arrange
            WalletResponseDTO created = walletService.createWallet(new CreateWalletRequestDTO("Ví Cũ", "Ghi chú cũ"));
            UpdateWalletRequestDTO updateRequest = new UpdateWalletRequestDTO(created.getId(), "Ví Mới", "Ghi chú mới");

            // Act
            WalletResponseDTO updated = walletService.updateWallet(updateRequest);

            // Assert
            assertEquals(created.getId(), updated.getId());
            assertEquals("Ví Mới", updated.getName());
            assertEquals("Ghi chú mới", updated.getNote());

            Wallet saved = walletRepository.findById(created.getId()).orElseThrow();
            assertEquals("Ví Mới", saved.getName());
            assertEquals("Ghi chú mới", saved.getNote());
        }

        @Test
        @DisplayName("Cho phép giữ nguyên tên cũ khi chỉ cập nhật ghi chú")
        void shouldAllowKeepingSameNameWhenOnlyUpdatingNote() {
            // Arrange
            WalletResponseDTO created = walletService.createWallet(new CreateWalletRequestDTO("Ví Tiết Kiệm", "Ghi chú ban đầu"));
            UpdateWalletRequestDTO updateRequest = new UpdateWalletRequestDTO(created.getId(), "ví tiết kiệm", "Ghi chú cập nhật");

            // Act
            WalletResponseDTO updated = walletService.updateWallet(updateRequest);

            // Assert
            assertEquals(created.getId(), updated.getId());
            assertEquals("ví tiết kiệm", updated.getName());
            assertEquals("Ghi chú cập nhật", updated.getNote());
        }

        @Test
        @DisplayName("Từ chối cập nhật khi tên mới trùng với ví khác trong hệ thống")
        void shouldRejectUpdateWhenNewNameDuplicatesAnotherWallet() {
            // Arrange
            walletService.createWallet(new CreateWalletRequestDTO("Ví Vietcombank"));
            WalletResponseDTO wallet2 = walletService.createWallet(new CreateWalletRequestDTO("Ví Techcombank"));

            UpdateWalletRequestDTO duplicateRequest = new UpdateWalletRequestDTO(wallet2.getId(), "ví vietcombank", "Cố tình trùng");

            // Act & Assert
            com.pfms.domain.exception.DuplicateEntityException ex = assertThrows(
                    com.pfms.domain.exception.DuplicateEntityException.class,
                    () -> walletService.updateWallet(duplicateRequest)
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_NAM_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException khi không tìm thấy mã ví cần sửa")
        void shouldThrowEntityNotFoundExceptionWhenWalletDoesNotExist() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            UpdateWalletRequestDTO request = new UpdateWalletRequestDTO(nonExistentId, "Tên Bất Kỳ");

            // Act & Assert
            com.pfms.domain.exception.EntityNotFoundException ex = assertThrows(
                    com.pfms.domain.exception.EntityNotFoundException.class,
                    () -> walletService.updateWallet(request)
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_NOT_FOUND, ex.getErrorCode());
        }

        @Test
        @DisplayName("Xác thực định dạng tên mới (ERR_NAM_001) và ghi chú mới trước khi kiểm tra existsByName")
        void shouldValidateNewNameAndNoteFormatBeforeCheckingDuplicateOnUpdate() {
            Wallet existing = new Wallet(UUID.randomUUID(), "Ví Gốc", "Ghi chú gốc", false);
            MemoryWalletRepository guardWalletRepo = new MemoryWalletRepository() {
                @Override
                public boolean existsByName(String name) {
                    fail("Không được phép gọi existsByName khi tên mới hoặc ghi chú mới sai định dạng");
                    return false;
                }
            };
            guardWalletRepo.save(existing);

            WalletServiceImpl guardedService = new WalletServiceImpl(
                    guardWalletRepo, balanceAdjustmentRepository, transactionRepository);

            String invalidName51 = "B".repeat(51);
            com.pfms.domain.exception.BusinessException nameEx = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> guardedService.updateWallet(new UpdateWalletRequestDTO(existing.getId(), invalidName51, "Hợp lệ"))
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_NAM_001, nameEx.getErrorCode());

            String invalidNote256 = "C".repeat(256);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> guardedService.updateWallet(new UpdateWalletRequestDTO(existing.getId(), "Tên Mới Hợp Lệ", invalidNote256))
            );
        }

        @Test
        @DisplayName("Ném NullPointerException khi request null")
        void shouldThrowExceptionWhenRequestIsNull() {
            assertThrows(NullPointerException.class, () -> walletService.updateWallet(null));
        }
    }

    @Nested
    @DisplayName("archiveWallet() & activateWallet() Tests")
    class StateTransitionTests {

        @Test
        @DisplayName("Lưu trữ ví hoạt động thành công (Happy Path)")
        void shouldArchiveWalletSuccessfully() {
            // Arrange
            WalletResponseDTO created = walletService.createWallet(new CreateWalletRequestDTO("Ví Cần Lưu Trữ"));

            // Act
            WalletResponseDTO archived = walletService.archiveWallet(created.getId());

            // Assert
            assertEquals("ARCHIVED", archived.getStatus());
            Wallet saved = walletRepository.findById(created.getId()).orElseThrow();
            assertEquals(EntityStatus.ARCHIVED, saved.getStatus());
        }

        @Test
        @DisplayName("Từ chối lưu trữ ví mặc định của hệ thống")
        void shouldRejectArchivingDefaultWallet() {
            // Arrange
            walletService.initializeDefaultWallet();
            Wallet defaultWallet = walletRepository.findDefaultWallet().orElseThrow();

            // Act & Assert
            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> walletService.archiveWallet(defaultWallet.getId())
            );
            assertTrue(ex.getMessage().contains("mặc định"));
        }

        @Test
        @DisplayName("Kích hoạt lại ví đã lưu trữ về trạng thái hoạt động (Happy Path)")
        void shouldActivateWalletSuccessfully() {
            // Arrange
            WalletResponseDTO created = walletService.createWallet(new CreateWalletRequestDTO("Ví Tạm Dừng"));
            walletService.archiveWallet(created.getId());

            // Act
            WalletResponseDTO activated = walletService.activateWallet(created.getId());

            // Assert
            assertEquals("ACTIVE", activated.getStatus());
            Wallet saved = walletRepository.findById(created.getId()).orElseThrow();
            assertEquals(EntityStatus.ACTIVE, saved.getStatus());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException khi không tìm thấy mã ví")
        void shouldThrowEntityNotFoundExceptionWhenWalletDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();
            assertThrows(com.pfms.domain.exception.EntityNotFoundException.class,
                    () -> walletService.archiveWallet(nonExistentId));
            assertThrows(com.pfms.domain.exception.EntityNotFoundException.class,
                    () -> walletService.activateWallet(nonExistentId));
        }

        @Test
        @DisplayName("Ném NullPointerException khi walletId null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> walletService.archiveWallet(null));
            assertThrows(NullPointerException.class, () -> walletService.activateWallet(null));
        }
    }

    @Nested
    @DisplayName("deleteWallet() Tests")
    class DeleteWalletTests {

        @Test
        @DisplayName("Xóa vĩnh viễn ví rỗng lịch sử thành công (Happy Path)")
        void shouldDeleteEmptyWalletSuccessfully() {
            // Arrange
            WalletResponseDTO created = walletService.createWallet(new CreateWalletRequestDTO("Ví Tạm Xóa"));
            UUID walletId = created.getId();
            assertTrue(walletRepository.findById(walletId).isPresent());

            // Act
            walletService.deleteWallet(walletId);

            // Assert
            assertTrue(walletRepository.findById(walletId).isEmpty(), "Ví rỗng phải bị xóa khỏi kho lưu trữ");
        }

        @Test
        @DisplayName("Từ chối xóa ví mặc định của hệ thống")
        void shouldRejectDeletingDefaultWallet() {
            // Arrange
            walletService.initializeDefaultWallet();
            Wallet defaultWallet = walletRepository.findDefaultWallet().orElseThrow();

            // Act & Assert
            com.pfms.domain.exception.BusinessException ex = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.deleteWallet(defaultWallet.getId())
            );
            assertTrue(ex.getMessage().contains("mặc định"));
        }

        @Test
        @DisplayName("Từ chối xóa ví khi đã phát sinh giao dịch thu chi trong lịch sử")
        void shouldRejectDeletingWalletWithTransactions() {
            // Arrange
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Giao Dịch", "", false);
            walletRepository.save(wallet);

            Category category = new Category(UUID.randomUUID(), "Ăn uống", UUID.randomUUID(),
                    com.pfms.domain.model.transaction.TransactionType.EXPENSE, "food.png", "#FF0000");
            IncomeExpenseTransaction tx = new IncomeExpenseTransaction(
                    UUID.randomUUID(), com.pfms.domain.model.transaction.TransactionType.EXPENSE,
                    wallet, category, Money.of("50000.00"), TransactionDate.now(), "Ăn trưa", null
            );
            transactionRepository.save(tx);

            // Act & Assert
            com.pfms.domain.exception.BusinessException ex = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.deleteWallet(wallet.getId())
            );
            assertTrue(ex.getMessage().contains("giao dịch"));
        }

        @Test
        @DisplayName("Từ chối xóa ví khi đã phát sinh bản ghi kiểm kê số dư")
        void shouldRejectDeletingWalletWithBalanceAdjustments() {
            // Arrange
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Kiểm Kê", "", false);
            walletRepository.save(wallet);

            BalanceAdjustmentRecord record = new BalanceAdjustmentRecord(
                    UUID.randomUUID(), wallet.getId(), Money.ZERO, Money.of("100000.00"), TransactionDate.now(), "Kiểm kê đầu kỳ"
            );
            balanceAdjustmentRepository.save(record);

            // Act & Assert
            com.pfms.domain.exception.BusinessException ex = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.deleteWallet(wallet.getId())
            );
            assertTrue(ex.getMessage().contains("kiểm kê"));
        }

        @Test
        @DisplayName("Ném EntityNotFoundException khi không tìm thấy mã ví cần xóa")
        void shouldThrowEntityNotFoundExceptionWhenWalletDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();
            assertThrows(com.pfms.domain.exception.EntityNotFoundException.class,
                    () -> walletService.deleteWallet(nonExistentId));
        }

        @Test
        @DisplayName("Ném NullPointerException khi walletId null")
        void shouldThrowExceptionWhenWalletIdIsNull() {
            assertThrows(NullPointerException.class, () -> walletService.deleteWallet(null));
        }
    }

    @Nested
    @DisplayName("adjustBalance() Tests")
    class AdjustBalanceTests {

        @Test
        @DisplayName("Điều chỉnh số dư kiểm kê thành công và lưu vết kiểm toán (Happy Path)")
        void shouldAdjustBalanceSuccessfullyWhenDataIsValid() {
            // Arrange
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Tiền Mặt", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("100000.00"));
            walletRepository.save(wallet);

            AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(
                    wallet.getId(),
                    new java.math.BigDecimal("80000.00"),
                    "Kiểm kê thực tế thấy thiếu tiền"
            );

            // Act
            BalanceAdjustmentResponseDTO response = walletService.adjustBalance(request);

            // Assert
            assertNotNull(response);
            assertEquals(wallet.getId(), response.getWalletId());
            assertEquals(0, new java.math.BigDecimal("100000.00").compareTo(response.getPreviousBalance()));
            assertEquals(0, new java.math.BigDecimal("80000.00").compareTo(response.getActualBalance()));
            assertEquals(0, new java.math.BigDecimal("-20000.00").compareTo(response.getDifference()));
            assertTrue(response.isDeficit());
            assertEquals("Kiểm kê thực tế thấy thiếu tiền", response.getReason());

            // Kiểm tra số dư ví trong kho đã đổi
            Wallet updatedWallet = walletRepository.findById(wallet.getId()).orElseThrow();
            assertEquals(0, new java.math.BigDecimal("80000.00").compareTo(updatedWallet.getCurrentBalance().getAmount()));

            // Kiểm tra bản ghi kiểm kê đã lưu vào kho
            java.util.List<BalanceAdjustmentRecord> records = balanceAdjustmentRepository.findByWalletId(wallet.getId());
            assertEquals(1, records.size());
            assertEquals(0, new java.math.BigDecimal("-20000.00").compareTo(records.get(0).getDifference().getAmount()));
        }

        @Test
        @DisplayName("Từ chối điều chỉnh số dư khi ví không ở trạng thái ACTIVE")
        void shouldRejectAdjustmentWhenWalletIsNotActive() {
            // Arrange
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Đã Lưu Trữ", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ArchivedWalletState(), Money.of("100000.00"));
            walletRepository.save(wallet);

            AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(
                    wallet.getId(),
                    new java.math.BigDecimal("90000.00"),
                    "Cố tình kiểm kê ví lưu trữ"
            );

            // Act & Assert
            com.pfms.domain.exception.BusinessException ex = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.adjustBalance(request)
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_WAL_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Từ chối điều chỉnh khi số dư thực tế trùng với số dư hiện tại (Chênh lệch bằng 0)")
        void shouldRejectAdjustmentWhenDifferenceIsZero() {
            // Arrange
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Khớp", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("100000.00"));
            walletRepository.save(wallet);

            AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(
                    wallet.getId(),
                    new java.math.BigDecimal("100000.00"),
                    "Không chênh lệch"
            );

            // Act & Assert
            com.pfms.domain.exception.BusinessException ex = assertThrows(
                    com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.adjustBalance(request)
            );
            assertEquals(com.pfms.domain.exception.ErrorCode.ERR_FIN_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Ném EntityNotFoundException khi không tìm thấy ví cần kiểm kê")
        void shouldThrowEntityNotFoundExceptionWhenWalletDoesNotExist() {
            UUID nonExistentId = UUID.randomUUID();
            AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(
                    nonExistentId,
                    new java.math.BigDecimal("50000.00")
            );

            assertThrows(com.pfms.domain.exception.EntityNotFoundException.class,
                    () -> walletService.adjustBalance(request));
        }

        @Test
        @DisplayName("Ném NullPointerException khi request null")
        void shouldThrowExceptionWhenRequestIsNull() {
            assertThrows(NullPointerException.class, () -> walletService.adjustBalance(null));
        }

        @Test
        @DisplayName("Chấp nhận ngày kiểm kê năm nhuận (29/02/2024) và từ chối ngày kiểm kê tương lai (Temporal Invariants)")
        void shouldHandleLeapYearDateAndRejectFutureAdjustmentDate() {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Lịch Pháp", "", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("100000.00"));
            walletRepository.save(wallet);

            java.time.LocalDate leapDay = java.time.LocalDate.of(2024, 2, 29);
            AdjustBalanceRequestDTO leapRequest = new AdjustBalanceRequestDTO(
                    wallet.getId(), new java.math.BigDecimal("150000.00"), "Kiểm kê ngày nhuận", leapDay);
            BalanceAdjustmentResponseDTO response = walletService.adjustBalance(leapRequest);
            assertEquals(leapDay, response.getAdjustedAt());

            java.time.LocalDate futureDay = java.time.LocalDate.now().plusDays(1);
            AdjustBalanceRequestDTO futureRequest = new AdjustBalanceRequestDTO(
                    wallet.getId(), new java.math.BigDecimal("200000.00"), "Ngày tương lai", futureDay);
            assertThrows(com.pfms.domain.exception.BusinessException.class,
                    () -> walletService.adjustBalance(futureRequest));
        }

        @Test
        @DisplayName("Hoàn nguyên trạng thái số dư ví về trước kiểm kê nếu lưu bản ghi kiểm kê phát sinh StorageException")
        void shouldRollbackWalletBalanceWhenSavingAdjustmentRecordThrowsStorageException() {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Kiểm Toán Nguyên Tử", "Ghi chú ban đầu", false,
                    java.time.LocalDateTime.now(), new com.pfms.domain.model.wallet.ActiveWalletState(), Money.of("500000.00"));
            walletRepository.save(wallet);

            MemoryBalanceAdjustmentRepository failingAdjustmentRepo = new MemoryBalanceAdjustmentRepository() {
                @Override
                public BalanceAdjustmentRecord save(BalanceAdjustmentRecord entity) {
                    throw new com.pfms.domain.exception.StorageException(
                            com.pfms.domain.exception.ErrorCode.ERR_SYS_001,
                            "Lỗi ghi tệp bản ghi kiểm kê",
                            new java.io.IOException("Simulated disk write failure")
                    );
                }
            };

            WalletServiceImpl atomicWalletService = new WalletServiceImpl(
                    walletRepository, failingAdjustmentRepo, transactionRepository);

            AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(
                    wallet.getId(),
                    new java.math.BigDecimal("350000.00"),
                    "Kiểm kê lỗi đĩa"
            );

            assertThrows(
                    com.pfms.domain.exception.StorageException.class,
                    () -> atomicWalletService.adjustBalance(request)
            );

            Wallet persistedWallet = walletRepository.findById(wallet.getId()).orElseThrow();
            assertEquals(
                    Money.of("500000.00"),
                    persistedWallet.getCurrentBalance(),
                    "Số dư ví trong kho lưu trữ phải được hoàn nguyên về 500,000.00 VNĐ khi lưu vết kiểm toán thất bại"
            );
            assertTrue(failingAdjustmentRepo.findByWalletId(wallet.getId()).isEmpty());
        }
    }
}

