package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.BackupRepository;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.infrastructure.file.FileBackupRepository;
import com.pfms.infrastructure.memory.MemoryBalanceAdjustmentRepository;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryNeedGroupRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.infrastructure.memory.MemoryWalletRepository;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataManagementServiceImpl Test Suite")
class DataManagementServiceImplTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private NeedGroupRepository needGroupRepository;
    private BalanceAdjustmentRepository balanceAdjustmentRepository;
    private BackupRepository backupRepository;

    private DataManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        walletRepository = new MemoryWalletRepository();
        transactionRepository = new MemoryTransactionRepository();
        categoryRepository = new MemoryCategoryRepository();
        needGroupRepository = new MemoryNeedGroupRepository();
        balanceAdjustmentRepository = new MemoryBalanceAdjustmentRepository();
        backupRepository = new FileBackupRepository();

        service = new DataManagementServiceImpl(
                walletRepository,
                transactionRepository,
                categoryRepository,
                needGroupRepository,
                balanceAdjustmentRepository,
                backupRepository);
    }

    @Nested
    @DisplayName("Constructor and Dependency Injection Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should initialize successfully when all repositories are provided")
        void shouldInitializeSuccessfullyWithValidRepositories() {
            assertNotNull(service);
        }

        @Test
        @DisplayName("Should throw NullPointerException when WalletRepository is null")
        void shouldThrowWhenWalletRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            null,
                            transactionRepository,
                            categoryRepository,
                            needGroupRepository,
                            balanceAdjustmentRepository,
                            backupRepository));
            assertTrue(ex.getMessage().contains("WalletRepository không được để null"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when TransactionRepository is null")
        void shouldThrowWhenTransactionRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            walletRepository,
                            null,
                            categoryRepository,
                            needGroupRepository,
                            balanceAdjustmentRepository,
                            backupRepository));
            assertTrue(ex.getMessage().contains("TransactionRepository không được để null"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when CategoryRepository is null")
        void shouldThrowWhenCategoryRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            walletRepository,
                            transactionRepository,
                            null,
                            needGroupRepository,
                            balanceAdjustmentRepository,
                            backupRepository));
            assertTrue(ex.getMessage().contains("CategoryRepository không được để null"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when NeedGroupRepository is null")
        void shouldThrowWhenNeedGroupRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            walletRepository,
                            transactionRepository,
                            categoryRepository,
                            null,
                            balanceAdjustmentRepository,
                            backupRepository));
            assertTrue(ex.getMessage().contains("NeedGroupRepository không được để null"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when BalanceAdjustmentRepository is null")
        void shouldThrowWhenBalanceAdjustmentRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            walletRepository,
                            transactionRepository,
                            categoryRepository,
                            needGroupRepository,
                            null,
                            backupRepository));
            assertTrue(ex.getMessage().contains("BalanceAdjustmentRepository không được để null"));
        }

        @Test
        @DisplayName("Should throw NullPointerException when BackupRepository is null")
        void shouldThrowWhenBackupRepositoryIsNull() {
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> new DataManagementServiceImpl(
                            walletRepository,
                            transactionRepository,
                            categoryRepository,
                            needGroupRepository,
                            balanceAdjustmentRepository,
                            null));
            assertTrue(ex.getMessage().contains("BackupRepository không được để null"));
        }
    }

    @Nested
    @DisplayName("Export Backup Data Tests")
    class ExportBackupDataTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Should reject null ExportBackupRequestDTO")
        void shouldRejectNullRequest() {
            assertThrows(NullPointerException.class, () -> service.exportBackupData(null));
        }

        @Test
        @DisplayName("Should reject null or blank destination path")
        void shouldRejectNullOrBlankDestinationPath() {
            assertThrows(StorageException.class, () ->
                    service.exportBackupData(new ExportBackupRequestDTO(null)));
            assertThrows(StorageException.class, () ->
                    service.exportBackupData(new ExportBackupRequestDTO("")));
            assertThrows(StorageException.class, () ->
                    service.exportBackupData(new ExportBackupRequestDTO("   ")));
        }

        @Test
        @DisplayName("Should reject invalid scope")
        void shouldRejectInvalidScope() {
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(tempDir.toString(), "CUSTOM_SCOPE");
            assertThrows(BusinessException.class, () -> service.exportBackupData(req));
        }

        @Test
        @DisplayName("Should reject destination path pointing to existing regular file")
        void shouldRejectExistingFileDestinationPath() throws Exception {
            Path existingFile = Files.writeString(tempDir.resolve("existing.json"), "{}");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(existingFile.toString());
            assertThrows(StorageException.class, () -> service.exportBackupData(req));
        }

        @Test
        @DisplayName("Should reject destination path carrying a file extension")
        void shouldRejectFileExtensionDestinationPath() {
            Path filePath = tempDir.resolve("manual_backup.json");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(filePath.toString());
            assertThrows(StorageException.class, () -> service.exportBackupData(req));
        }

        @Test
        @DisplayName("Should export backup data successfully with FULL scope using auto-generated filename")
        void shouldExportSuccessfullyWithFullScope() throws Exception {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Tiền Mặt", "Ví 1", true);
            wallet.credit(Money.of("500000.00"));
            walletRepository.save(wallet);

            NeedGroup group = new NeedGroup(UUID.randomUUID(), "Ăn Uống", TransactionType.EXPENSE, "food", "#FF0000");
            needGroupRepository.save(group);

            Category category = new Category(UUID.randomUUID(), "Cơm Trưa", group.getNeedGroupId(), TransactionType.EXPENSE, "lunch", "#FF0000");
            categoryRepository.save(category);

            IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet, category,
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Ăn trưa cơm tấm", null);
            transactionRepository.save(txn);

            BalanceAdjustmentRecord adj = wallet.adjustBalance(Money.of("450000.00"), TransactionDate.of(LocalDate.now()), "Kiểm kê");
            balanceAdjustmentRepository.save(adj);

            ExportBackupRequestDTO req = new ExportBackupRequestDTO(tempDir.toString(), "FULL");

            ExportBackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            Path exportedFile = Path.of(res.getFilePath());
            assertEquals(tempDir.toAbsolutePath().normalize(), exportedFile.getParent());
            assertTrue(exportedFile.getFileName().toString().matches("pfms_export_full_\\d{8}_\\d{6}\\.json"));
            assertTrue(res.getFileSizeBytes() > 0);
            assertTrue(res.isFullScope());
            assertEquals(1, res.getWalletCount());
            assertEquals(1, res.getCategoryCount());
            assertEquals(1, res.getTransactionCount());
            assertEquals(1, res.getAdjustmentCount());
            assertEquals(4, res.getTotalRecordCount());

            assertTrue(Files.exists(exportedFile));
            byte[] raw = Files.readAllBytes(exportedFile);
            assertFalse(raw.length >= 3
                    && (raw[0] & 0xFF) == 0xEF
                    && (raw[1] & 0xFF) == 0xBB
                    && (raw[2] & 0xFF) == 0xBF);

            String content = Files.readString(exportedFile, StandardCharsets.UTF_8);
            assertTrue(content.contains("\"wallets\""));
            assertTrue(content.contains("\"needGroups\""));
            assertTrue(content.contains("\"categories\""));
            assertTrue(content.contains("\"transactions\""));
            assertTrue(content.contains("\"adjustments\""));
            assertTrue(content.contains("Ví Tiền Mặt"));
            assertTrue(content.contains("Ăn trưa cơm tấm"));
            assertTrue(content.contains("50000.00"));
            assertTrue(content.contains("450000.00"));
        }

        @Test
        @DisplayName("Should export backup data successfully with TRANSACTIONS scope")
        void shouldExportSuccessfullyWithTransactionsScope() throws Exception {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Tiền Mặt", "Ví 1", true);
            walletRepository.save(wallet);
            NeedGroup group = new NeedGroup(UUID.randomUUID(), "Ăn Uống", TransactionType.EXPENSE, "food", "#FF0000");
            needGroupRepository.save(group);
            Category category = new Category(UUID.randomUUID(), "Cơm Trưa", group.getNeedGroupId(), TransactionType.EXPENSE, "lunch", "#FF0000");
            categoryRepository.save(category);

            IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet, category,
                    Money.of("35000.00"), TransactionDate.of(LocalDate.now()), "Bánh mì", null);
            transactionRepository.save(txn);

            ExportBackupRequestDTO req = new ExportBackupRequestDTO(tempDir.toString(), "TRANSACTIONS");

            ExportBackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            Path exportedFile = Path.of(res.getFilePath());
            assertTrue(exportedFile.getFileName().toString().matches("pfms_export_transactions_\\d{8}_\\d{6}\\.json"));
            assertTrue(res.isTransactionsScope());
            assertEquals(0, res.getWalletCount());
            assertEquals(0, res.getCategoryCount());
            assertEquals(0, res.getAdjustmentCount());
            assertEquals(1, res.getTransactionCount());

            String content = Files.readString(exportedFile, StandardCharsets.UTF_8);
            assertTrue(content.contains("\"wallets\": []"));
            assertTrue(content.contains("\"transactions\""));
            assertTrue(content.contains("Bánh mì"));
            assertTrue(content.contains("35000.00"));
        }

        @Test
        @DisplayName("Should auto-create destination directories if they do not exist")
        void shouldCreateParentDirectoriesWhenNeeded() {
            Path nestedDir = tempDir.resolve("nested/dir/sub");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(nestedDir.toString());

            ExportBackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            assertTrue(Files.isDirectory(nestedDir));
            assertTrue(Files.exists(Path.of(res.getFilePath())));
        }

        @Test
        @DisplayName("Should export transfer transaction with fee correctly")
        void shouldExportTransferTransactionWithFee() throws Exception {
            Wallet source = new Wallet(UUID.randomUUID(), "Ví Nguồn", "", false);
            Wallet dest = new Wallet(UUID.randomUUID(), "Ví Đích", "", false);
            walletRepository.save(source);
            walletRepository.save(dest);

            NeedGroup feeGroup = new NeedGroup(UUID.randomUUID(), "Chi Phí Khác", TransactionType.EXPENSE, "fee", "#999");
            needGroupRepository.save(feeGroup);
            Category feeCat = new Category(UUID.randomUUID(), "Phí Chuyển", feeGroup.getNeedGroupId(), TransactionType.EXPENSE, "fee", "#999");
            categoryRepository.save(feeCat);

            UUID trfId = UUID.randomUUID();
            TransactionFee fee = new TransactionFee(UUID.randomUUID(), trfId, Money.of("3300.00"), source, feeCat);
            TransferTransaction trf = new TransferTransaction(
                    trfId, source, dest, Money.of("100000.00"), TransactionDate.of(LocalDate.now()), "Chuyển liên ngân hàng", fee);
            transactionRepository.save(trf);

            ExportBackupRequestDTO req = new ExportBackupRequestDTO(tempDir.toString());

            ExportBackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            String content = Files.readString(Path.of(res.getFilePath()), StandardCharsets.UTF_8);
            assertTrue(content.contains("TRANSFER"));
            assertTrue(content.contains("\"sourceWalletId\""));
            assertTrue(content.contains("\"destinationWalletId\""));
            assertTrue(content.contains("\"fee\""));
            assertTrue(content.contains("3300.00"));
        }
    }

    @Nested
    @DisplayName("ReconcileSystemData Tests")
    class ReconciliationTests {

        private Wallet wallet1;
        private Wallet wallet2;
        private NeedGroup expenseGroup;
        private NeedGroup incomeGroup;
        private Category expenseCat;
        private Category incomeCat;
        private Category feeCat;

        @BeforeEach
        void initDomainData() {
            wallet1 = new Wallet(UUID.randomUUID(), "Ví Tiền Mặt", "Ví 1", false);
            wallet2 = new Wallet(UUID.randomUUID(), "Ví Ngân Hàng", "Ví 2", false);
            walletRepository.save(wallet1);
            walletRepository.save(wallet2);

            incomeGroup = new NeedGroup(UUID.randomUUID(), "Thu Nhập", TransactionType.INCOME, "income", "#00FF00");
            expenseGroup = new NeedGroup(UUID.randomUUID(), "Chi Tiêu Thiết Yếu", TransactionType.EXPENSE, "expense", "#FF0000");
            needGroupRepository.save(incomeGroup);
            needGroupRepository.save(expenseGroup);

            incomeCat = new Category(UUID.randomUUID(), "Lương", incomeGroup.getNeedGroupId(), TransactionType.INCOME, "salary", "#00FF00");
            expenseCat = new Category(UUID.randomUUID(), "Ăn Uống", expenseGroup.getNeedGroupId(), TransactionType.EXPENSE, "food", "#FF0000");
            feeCat = new Category(UUID.randomUUID(), "Phí Chuyển Tiền", expenseGroup.getNeedGroupId(), TransactionType.EXPENSE, "fee", "#999999");
            categoryRepository.save(incomeCat);
            categoryRepository.save(expenseCat);
            categoryRepository.save(feeCat);
        }

        @Test
        @DisplayName("Should return perfect report when empty database")
        void shouldReturnPerfectReportWhenEmpty() {
            walletRepository.deleteAll();
            needGroupRepository.deleteAll();
            categoryRepository.deleteAll();

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertTrue(report.isFullyConsistent());
            assertEquals(0, report.getTotalIssues());
            assertEquals(0, report.getScannedWallets());
            assertEquals(0, report.getScannedTransactions());
        }

        @Test
        @DisplayName("Should return perfect report when all transactions and adjustments match balances")
        void shouldReturnPerfectReportWhenConsistent() {
            wallet1.credit(Money.of("1000000.00"));
            walletRepository.save(wallet1);
            IncomeExpenseTransaction incomeTxn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.INCOME, wallet1, incomeCat,
                    Money.of("1000000.00"), TransactionDate.of(LocalDate.now()), "Lương tháng", null);
            transactionRepository.save(incomeTxn);

            UUID transferId = UUID.randomUUID();
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), transferId, Money.of("5000.00"), wallet1, feeCat);
            TransferTransaction transferTxn = new TransferTransaction(
                    transferId, wallet1, wallet2, Money.of("200000.00"),
                    TransactionDate.of(LocalDate.now()), "Chuyển tiền ví", fee);
            wallet1.debit(Money.of("205000.00"));
            wallet2.credit(Money.of("200000.00"));
            walletRepository.save(wallet1);
            walletRepository.save(wallet2);
            transactionRepository.save(transferTxn);

            wallet2.debit(Money.of("50000.00"));
            walletRepository.save(wallet2);
            IncomeExpenseTransaction expenseTxn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet2, expenseCat,
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Ăn trưa", null);
            transactionRepository.save(expenseTxn);

            BalanceAdjustmentRecord adj = wallet1.adjustBalance(Money.of("800000.00"), TransactionDate.of(LocalDate.now()), "Kiểm kê");
            walletRepository.save(wallet1);
            balanceAdjustmentRepository.save(adj);

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertTrue(report.isFullyConsistent());
            assertTrue(report.isWalletBalanceConsistent());
            assertTrue(report.isTransferConsistent());
            assertTrue(report.isCategoryHierarchyConsistent());
            assertTrue(report.isFeeConsistent());
            assertEquals(0, report.getTotalIssues());
            assertEquals(2, report.getScannedWallets());
            assertEquals(3, report.getScannedTransactions());
            assertEquals(3, report.getScannedCategories());
            assertEquals(1, report.getScannedAdjustments());
        }

        @Test
        @DisplayName("Should detect wallet balance mismatch when balance does not match history")
        void shouldDetectBalanceMismatch() {
            wallet1.credit(Money.of("100000.00"));
            walletRepository.save(wallet1);

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertFalse(report.isFullyConsistent());
            assertFalse(report.isWalletBalanceConsistent());
            assertFalse(report.getIssues().isEmpty());
            assertTrue(report.getIssues().stream().anyMatch(i -> "BALANCE_MISMATCH".equals(i.getIssueType())));
        }

        @Test
        @DisplayName("Should detect transfer inconsistency when source wallet does not exist in repository")
        void shouldDetectTransferWithMissingSourceWallet() {
            Wallet orphanWallet = new Wallet(UUID.randomUUID(), "Ví ngoài hệ thống", "", false);
            TransferTransaction invalidTransfer = new TransferTransaction(
                    UUID.randomUUID(), orphanWallet, wallet2, Money.of("50000.00"),
                    TransactionDate.of(LocalDate.now()), "Chuyển từ ví mồ côi", null);
            transactionRepository.save(invalidTransfer);

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertFalse(report.isTransferConsistent());
            assertTrue(report.getIssues().stream().anyMatch(i -> "TRANSFER_INCONSISTENCY".equals(i.getIssueType())));
        }

        @Test
        @DisplayName("Should detect category inconsistency when category is orphaned or transaction type mismatch")
        void shouldDetectCategoryInconsistencies() {
            Category orphanCategory = new Category(
                    UUID.randomUUID(), "Danh mục mồ côi", UUID.randomUUID(), TransactionType.EXPENSE, "orphan", "#000");
            categoryRepository.save(orphanCategory);

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertFalse(report.isCategoryHierarchyConsistent());
            assertTrue(report.getIssues().stream().anyMatch(i -> "CATEGORY_INCONSISTENCY".equals(i.getIssueType())));
        }

        @Test
        @DisplayName("Should detect fee inconsistency when fee category is purged from repository")
        void shouldDetectFeeInconsistencyWhenFeeCategoryMissing() {
            UUID txnId = UUID.randomUUID();
            TransactionFee fee = new TransactionFee(
                    UUID.randomUUID(), txnId, Money.of("5000.00"), wallet1, feeCat);
            IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                    txnId, TransactionType.EXPENSE, wallet1, expenseCat, Money.of("50000.00"),
                    TransactionDate.of(LocalDate.now()), "Chi phí kèm phụ phí", fee);
            transactionRepository.save(txn);

            categoryRepository.deleteById(feeCat.getCategoryId());

            ReconciliationReportResponseDTO report = service.reconcileSystemData();

            assertFalse(report.isFeeConsistent());
            assertTrue(report.getIssues().stream().anyMatch(i -> "FEE_INCONSISTENCY".equals(i.getIssueType())));
        }
    }

    @Nested
    @DisplayName("restoreBackupData Tests")
    class RestoreBackupDataTests {

        @Test
        @DisplayName("Should throw NullPointerException when RestoreBackupRequestDTO is null")
        void shouldThrowWhenRequestDtoIsNull() {
            assertThrows(NullPointerException.class, () -> service.restoreBackupData(null));
        }

        @Test
        @DisplayName("Should throw StorageException when backup file path is null or blank")
        void shouldThrowWhenBackupFilePathIsBlankOrNull() {
            assertThrows(StorageException.class, () -> service.restoreBackupData(new RestoreBackupRequestDTO(null)));
            assertThrows(StorageException.class, () -> service.restoreBackupData(new RestoreBackupRequestDTO("   ")));
        }

        @Test
        @DisplayName("Should throw StorageException when backup file does not exist")
        void shouldThrowWhenBackupFileDoesNotExist(@TempDir Path tempDir) {
            Path nonExistent = tempDir.resolve("not_found.json");
            assertThrows(StorageException.class, () -> service.restoreBackupData(new RestoreBackupRequestDTO(nonExistent.toString())));
        }

        @Test
        @DisplayName("Should throw DataCorruptionException when backup file contains invalid JSON syntax")
        void shouldThrowWhenBackupFileIsCorruptedJson(@TempDir Path tempDir) throws Exception {
            Path corruptFile = tempDir.resolve("corrupt.json");
            Files.writeString(corruptFile, "{ invalid json structure ");

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(corruptFile.toString())));
            assertEquals(ErrorCode.ERR_DAT_001, ex.getErrorCode());
        }

        @Test
        @DisplayName("Should throw DataCorruptionException when backup data violates integrity")
        void shouldThrowWhenBackupDataViolatesIntegrity(@TempDir Path tempDir) throws Exception {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Lỗi Số Dư", "", true);
            wallet.credit(Money.of("100000.00")); // currentBalance = 100000.00

            NeedGroup group = new NeedGroup(UUID.randomUUID(), "Sinh Hoạt", TransactionType.EXPENSE, "home", "#000");
            Category category = new Category(UUID.randomUUID(), "Điện Nước", group.getNeedGroupId(), TransactionType.EXPENSE, "electric", "#000");

            // Transaction amount 50,000 but wallet balance was not deducted in transactions or adjustments
            IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet, category,
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Tiền điện", null);

            // Reconciler will detect balance mismatch: initial balance 0 + txn (-50,000) != currentBalance (100,000)
            BackupPayload payload = new BackupPayload(
                    List.of(wallet),
                    List.of(group),
                    List.of(category),
                    List.of(txn),
                    List.of());

            Path backupFile = tempDir.resolve("integrity_error.json");
            backupRepository.writeBackup(backupFile, payload);

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString())));
            assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("vi phạm ràng buộc toàn vẹn"));
        }

        @Test
        @DisplayName("Should successfully restore backup data and atomicaly replace repositories")
        void shouldSuccessfullyRestoreBackupAndReplaceRepositories(@TempDir Path tempDir) throws Exception {
            // Seed initial old state
            Wallet oldWallet = new Wallet(UUID.randomUUID(), "Ví Cũ", "", true);
            walletRepository.save(oldWallet);
            assertEquals(1, walletRepository.findAll().size());

            // Build fresh backup state
            Wallet newWallet1 = new Wallet(UUID.randomUUID(), "Ví Phục Hồi 1", "", true);
            BalanceAdjustmentRecord adj = newWallet1.adjustBalance(
                    Money.of("1000000.00"), TransactionDate.of(LocalDate.now()), "Kiểm kê ban đầu");

            Wallet newWallet2 = new Wallet(UUID.randomUUID(), "Ví Phục Hồi 2", "", false);

            NeedGroup newGroup = new NeedGroup(UUID.randomUUID(), "Ăn Uống", TransactionType.EXPENSE, "food", "#F00");
            Category expenseCat = new Category(UUID.randomUUID(), "Cơm Trưa", newGroup.getNeedGroupId(), TransactionType.EXPENSE, "lunch", "#F00");
            Category feeCat = new Category(UUID.randomUUID(), "Phí Chuyển", newGroup.getNeedGroupId(), TransactionType.EXPENSE, "fee", "#999");

            IncomeExpenseTransaction ieTxn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, newWallet1, expenseCat,
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Ăn trưa", null);
            newWallet1.debit(Money.of("50000.00"));

            UUID transferId = UUID.randomUUID();
            TransactionFee fee = new TransactionFee(UUID.randomUUID(), transferId, Money.of("1000.00"), newWallet1, feeCat);
            TransferTransaction trfTxn = new TransferTransaction(
                    transferId, newWallet1, newWallet2, Money.of("200000.00"),
                    TransactionDate.of(LocalDate.now()), "Chuyển tiền ví 2", fee);
            newWallet1.debit(Money.of("201000.00"));
            newWallet2.credit(Money.of("200000.00"));

            BackupPayload payload = new BackupPayload(
                    List.of(newWallet1, newWallet2),
                    List.of(newGroup),
                    List.of(expenseCat, feeCat),
                    List.of(ieTxn, trfTxn),
                    List.of(adj));

            Path backupFile = tempDir.resolve("valid_backup.json");
            backupRepository.writeBackup(backupFile, payload);

            // Execute restore
            RestoreBackupResponseDTO result = service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString()));

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals("Phục hồi dữ liệu hệ thống thành công", result.getMessage());
            assertEquals(2, result.getRestoredWallets());
            assertEquals(2, result.getRestoredCategories());
            assertEquals(2, result.getRestoredTransactions());
            assertEquals(1, result.getRestoredAdjustments());

            // Repositories must match restored data (oldWallet should be gone!)
            List<Wallet> restoredWallets = walletRepository.findAll();
            assertEquals(2, restoredWallets.size());
            assertFalse(restoredWallets.stream().anyMatch(w -> w.getId().equals(oldWallet.getId())));
            assertTrue(restoredWallets.stream().anyMatch(w -> w.getId().equals(newWallet1.getId())));
            assertTrue(restoredWallets.stream().anyMatch(w -> w.getId().equals(newWallet2.getId())));

            assertEquals(1, needGroupRepository.findAll().size());
            assertEquals(2, categoryRepository.findAll().size());
            assertEquals(2, transactionRepository.findAll().size());
            assertEquals(1, balanceAdjustmentRepository.findAll().size());

            // System data reconciler report must be fully consistent
            ReconciliationReportResponseDTO report = service.reconcileSystemData();
            assertTrue(report.isFullyConsistent());
            assertTrue(report.getIssues().isEmpty());
        }

        @Test
        @DisplayName("Should reject TRANSACTIONS-only backup missing wallet references on restore and keep current data")
        void shouldRejectTransactionsOnlyBackupMissingWallet(@TempDir Path tempDir) {
            Wallet wallet = new Wallet(UUID.randomUUID(), "Ví Hiện Có", "", true);
            wallet.credit(Money.of("100000.00"));
            walletRepository.save(wallet);

            NeedGroup group = new NeedGroup(UUID.randomUUID(), "Ăn Uống", TransactionType.EXPENSE, "food", "#F00");
            needGroupRepository.save(group);
            Category cat = new Category(UUID.randomUUID(), "Cơm", group.getNeedGroupId(), TransactionType.EXPENSE, "rice", "#F00");
            categoryRepository.save(cat);

            IncomeExpenseTransaction txn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, wallet, cat,
                    Money.of("25000.00"), TransactionDate.of(LocalDate.now()), "Cơm trưa", null);
            transactionRepository.save(txn);

            ExportBackupResponseDTO exported = service.exportBackupData(
                    new ExportBackupRequestDTO(tempDir.toString(), "TRANSACTIONS"));

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(exported.getFilePath())));

            assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
            assertEquals(1, walletRepository.findAll().size());
            assertEquals(1, categoryRepository.findAll().size());
            assertEquals(1, transactionRepository.findAll().size());
        }

        @Test
        @DisplayName("Should reject backup when adjustment record references missing wallet")
        void shouldRejectBackupWhenAdjustmentReferencesMissingWallet(@TempDir Path tempDir) throws Exception {
            Wallet orphanWallet = new Wallet(UUID.randomUUID(), "Ví Mồ Côi", "", false);
            BalanceAdjustmentRecord adj = orphanWallet.adjustBalance(
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Điều chỉnh mồ côi");

            BackupPayload payload = new BackupPayload(
                    List.of(), List.of(), List.of(), List.of(), List.of(adj));
            Path backupFile = tempDir.resolve("orphan_adjustment.json");
            backupRepository.writeBackup(backupFile, payload);

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString())));

            assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
        }

        @Test
        @DisplayName("Should rollback all 5 repositories atomically when saveAll throws StorageException")
        void shouldRollbackAllRepositoriesWhenSaveAllFails(@TempDir Path tempDir) throws Exception {
            AtomicBoolean failOnFirstSaveAll = new AtomicBoolean(true);
            MemoryTransactionRepository flakyTxnRepo = new MemoryTransactionRepository() {
                @Override
                public List<Transaction> saveAll(Iterable<Transaction> entities) {
                    if (failOnFirstSaveAll.compareAndSet(true, false)) {
                        throw new StorageException("Mô phỏng lỗi ghi ổ đĩa", new IOException("Disk full"));
                    }
                    return super.saveAll(entities);
                }
            };

            DataManagementServiceImpl serviceWithFlakyRepo = new DataManagementServiceImpl(
                    walletRepository, flakyTxnRepo, categoryRepository, needGroupRepository, balanceAdjustmentRepository, backupRepository);

            Wallet oldWallet = new Wallet(UUID.randomUUID(), "Ví Ban Đầu", "", true);
            BalanceAdjustmentRecord oldAdj = oldWallet.adjustBalance(
                    Money.of("300000.00"), TransactionDate.of(LocalDate.now()), "Số dư đầu kỳ");
            walletRepository.save(oldWallet);
            balanceAdjustmentRepository.save(oldAdj);

            NeedGroup oldGroup = new NeedGroup(UUID.randomUUID(), "Sinh Hoạt", TransactionType.EXPENSE, "home", "#000");
            needGroupRepository.save(oldGroup);
            Category oldCategory = new Category(
                    UUID.randomUUID(), "Tiền Điện", oldGroup.getNeedGroupId(), TransactionType.EXPENSE, "power", "#000");
            categoryRepository.save(oldCategory);

            IncomeExpenseTransaction oldTxn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.EXPENSE, oldWallet, oldCategory,
                    Money.of("50000.00"), TransactionDate.of(LocalDate.now()), "Hóa đơn cũ", null);
            flakyTxnRepo.save(oldTxn);

            Wallet newWallet = new Wallet(UUID.randomUUID(), "Ví Mới", "", true);
            BalanceAdjustmentRecord newAdj = newWallet.adjustBalance(
                    Money.of("800000.00"), TransactionDate.of(LocalDate.now()), "Nạp từ backup");
            NeedGroup newGroup = new NeedGroup(UUID.randomUUID(), "Thu Nhập", TransactionType.INCOME, "cash", "#0F0");
            Category newCategory = new Category(
                    UUID.randomUUID(), "Lương", newGroup.getNeedGroupId(), TransactionType.INCOME, "salary", "#0F0");
            IncomeExpenseTransaction newTxn = new IncomeExpenseTransaction(
                    UUID.randomUUID(), TransactionType.INCOME, newWallet, newCategory,
                    Money.of("200000.00"), TransactionDate.of(LocalDate.now()), "Thưởng", null);
            newWallet.credit(Money.of("200000.00"));

            BackupPayload backupPayload = new BackupPayload(
                    List.of(newWallet), List.of(newGroup), List.of(newCategory), List.of(newTxn), List.of(newAdj));
            Path backupFile = tempDir.resolve("rollback_test.json");
            backupRepository.writeBackup(backupFile, backupPayload);

            assertThrows(StorageException.class,
                    () -> serviceWithFlakyRepo.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString())));

            assertEquals(1, walletRepository.findAll().size());
            assertEquals(oldWallet.getId(), walletRepository.findAll().get(0).getId());
            assertEquals(1, needGroupRepository.findAll().size());
            assertEquals(oldGroup.getNeedGroupId(), needGroupRepository.findAll().get(0).getNeedGroupId());
            assertEquals(1, categoryRepository.findAll().size());
            assertEquals(oldCategory.getCategoryId(), categoryRepository.findAll().get(0).getCategoryId());
            assertEquals(1, balanceAdjustmentRepository.findAll().size());
            assertEquals(oldAdj.getRecordId(), balanceAdjustmentRepository.findAll().get(0).getRecordId());
            assertEquals(1, flakyTxnRepo.findAll().size());
            assertEquals(oldTxn.getTransactionId(), flakyTxnRepo.findAll().get(0).getTransactionId());
        }

        @Test
        @DisplayName("Should report ERR_DAT_002 duplicate wallet ID message rather than balance mismatch when duplicate IDs exist")
        void shouldRejectDuplicateWalletIdWithDuplicateMessage(@TempDir Path tempDir) throws Exception {
            UUID dupWalletId = UUID.randomUUID();
            Wallet firstWallet = new Wallet(dupWalletId, "Ví Trùng 1", "", true);
            Wallet secondWallet = new Wallet(dupWalletId, "Ví Trùng 2", "", false);
            secondWallet.credit(Money.of("100000.00"));

            BackupPayload dupPayload = new BackupPayload(
                    List.of(firstWallet, secondWallet), List.of(), List.of(), List.of(), List.of());
            Path backupFile = tempDir.resolve("dup_wallet.json");
            backupRepository.writeBackup(backupFile, dupPayload);

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString())));

            assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
            assertTrue(ex.getMessage().contains("Trùng mã ví"),
                    () -> "Expected duplicate wallet message, but was: " + ex.getMessage());
        }

        @Test
        @DisplayName("Should reject backup with negative wallet balance as ERR_DAT_002 end-to-end and keep existing repositories intact")
        void shouldRejectNegativeWalletBalanceWithErrDat002EndToEnd(@TempDir Path tempDir) throws Exception {
            Wallet existingWallet = new Wallet(UUID.randomUUID(), "Ví Hiện Tại", "", true);
            walletRepository.save(existingWallet);

            String negativeWalletJson = """
            {
              "wallets": [
                {
                  "walletId": "11111111-1111-1111-1111-111111111111",
                  "name": "Ví Số Dư Âm",
                  "currentBalance": "-50000.00",
                  "status": "ACTIVE",
                  "isDefault": true,
                  "note": "",
                  "createdAt": "2026-01-01T10:00:00"
                }
              ],
              "needGroups": [
                {
                  "groupId": "33333333-3333-3333-3333-333333333333",
                  "groupName": "Chi tiêu",
                  "flowType": "EXPENSE",
                  "groupStatus": "ACTIVE",
                  "icon": "",
                  "color": "",
                  "createdAt": "2026-01-01T10:00:00"
                }
              ],
              "categories": [
                {
                  "categoryId": "44444444-4444-4444-4444-444444444444",
                  "name": "Ăn uống",
                  "groupId": "33333333-3333-3333-3333-333333333333",
                  "flowType": "EXPENSE",
                  "status": "ACTIVE",
                  "icon": "",
                  "color": "",
                  "createdAt": "2026-01-01T10:00:00"
                }
              ],
              "transactions": [
                {
                  "transactionId": "55555555-5555-5555-5555-555555555555",
                  "amount": "50000.00",
                  "transactionDate": "2026-01-05",
                  "note": "",
                  "createdAt": "2026-01-05T12:00:00",
                  "recordType": "INCOME_EXPENSE",
                  "transactionType": "EXPENSE",
                  "walletId": "11111111-1111-1111-1111-111111111111",
                  "categoryId": "44444444-4444-4444-4444-444444444444",
                  "fee": null
                }
              ],
              "adjustments": []
            }
            """;
            Path backupFile = tempDir.resolve("negative_wallet.json");
            Files.writeString(backupFile, negativeWalletJson, StandardCharsets.UTF_8);

            DataCorruptionException ex = assertThrows(
                    DataCorruptionException.class,
                    () -> service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString())));

            assertEquals(ErrorCode.ERR_DAT_002, ex.getErrorCode());
            assertEquals(1, walletRepository.findAll().size());
            assertEquals(existingWallet.getId(), walletRepository.findAll().get(0).getId());
        }
    }
}

