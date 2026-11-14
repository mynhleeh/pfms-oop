package com.pfms.service.impl.data;

import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.TransactionFee;
import com.pfms.domain.model.transaction.TransactionType;
import com.pfms.domain.model.transaction.TransferTransaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.infrastructure.memory.MemoryBalanceAdjustmentRepository;
import com.pfms.infrastructure.memory.MemoryCategoryRepository;
import com.pfms.infrastructure.memory.MemoryNeedGroupRepository;
import com.pfms.infrastructure.memory.MemoryTransactionRepository;
import com.pfms.infrastructure.memory.MemoryWalletRepository;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.presentation.dto.data.BackupPayloadDTO;
import com.pfms.presentation.dto.data.BackupResponseDTO;
import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ReconciliationReportDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataManagementServiceImpl Test Suite")
class DataManagementServiceImplTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private NeedGroupRepository needGroupRepository;
    private BalanceAdjustmentRepository balanceAdjustmentRepository;

    private DataManagementServiceImpl service;

    @BeforeEach
    void setUp() {
        walletRepository = new MemoryWalletRepository();
        transactionRepository = new MemoryTransactionRepository();
        categoryRepository = new MemoryCategoryRepository();
        needGroupRepository = new MemoryNeedGroupRepository();
        balanceAdjustmentRepository = new MemoryBalanceAdjustmentRepository();

        service = new DataManagementServiceImpl(
                walletRepository,
                transactionRepository,
                categoryRepository,
                needGroupRepository,
                balanceAdjustmentRepository);
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
                            balanceAdjustmentRepository));
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
                            balanceAdjustmentRepository));
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
                            balanceAdjustmentRepository));
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
                            balanceAdjustmentRepository));
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
                            null));
            assertTrue(ex.getMessage().contains("BalanceAdjustmentRepository không được để null"));
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
            String path = tempDir.resolve("backup.json").toString();
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(path, "CUSTOM_SCOPE");
            assertThrows(BusinessException.class, () -> service.exportBackupData(req));
        }

        @Test
        @DisplayName("Should reject destination path that is an existing directory")
        void shouldRejectDirectoryDestinationPath() {
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(tempDir.toString());
            assertThrows(StorageException.class, () -> service.exportBackupData(req));
        }

        @Test
        @DisplayName("Should export backup data successfully with FULL scope")
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

            Path targetFile = tempDir.resolve("full_backup.json");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(targetFile.toString(), "FULL");

            BackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            assertEquals(targetFile.toAbsolutePath().toString(), res.getFilePath());
            assertTrue(res.getFileSizeBytes() > 0);
            assertTrue(res.isFullScope());
            assertEquals(1, res.getWalletCount());
            assertEquals(1, res.getCategoryCount());
            assertEquals(1, res.getTransactionCount());
            assertEquals(1, res.getAdjustmentCount());
            assertEquals(4, res.getTotalRecordCount());

            assertTrue(Files.exists(targetFile));
            String content = Files.readString(targetFile);
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

            Path targetFile = tempDir.resolve("txn_backup.json");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(targetFile.toString(), "TRANSACTIONS");

            BackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            assertTrue(res.isTransactionsScope());
            assertEquals(0, res.getWalletCount());
            assertEquals(0, res.getCategoryCount());
            assertEquals(0, res.getAdjustmentCount());
            assertEquals(1, res.getTransactionCount());

            String content = Files.readString(targetFile);
            assertTrue(content.contains("\"wallets\": [\n  ]"));
            assertTrue(content.contains("\"transactions\""));
            assertTrue(content.contains("Bánh mì"));
            assertTrue(content.contains("35000.00"));
        }

        @Test
        @DisplayName("Should auto-create parent directories if they do not exist")
        void shouldCreateParentDirectoriesWhenNeeded() {
            Path nestedTarget = tempDir.resolve("nested/dir/sub/backup.json");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(nestedTarget.toString());

            BackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            assertTrue(Files.exists(nestedTarget));
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

            Path targetFile = tempDir.resolve("transfer_fee_backup.json");
            ExportBackupRequestDTO req = new ExportBackupRequestDTO(targetFile.toString());

            BackupResponseDTO res = service.exportBackupData(req);

            assertNotNull(res);
            String content = Files.readString(targetFile);
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

            ReconciliationReportDTO report = service.reconcileSystemData();

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

            ReconciliationReportDTO report = service.reconcileSystemData();

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

            ReconciliationReportDTO report = service.reconcileSystemData();

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

            ReconciliationReportDTO report = service.reconcileSystemData();

            assertFalse(report.isTransferConsistent());
            assertTrue(report.getIssues().stream().anyMatch(i -> "TRANSFER_INCONSISTENCY".equals(i.getIssueType())));
        }

        @Test
        @DisplayName("Should detect category inconsistency when category is orphaned or transaction type mismatch")
        void shouldDetectCategoryInconsistencies() {
            Category orphanCategory = new Category(
                    UUID.randomUUID(), "Danh mục mồ côi", UUID.randomUUID(), TransactionType.EXPENSE, "orphan", "#000");
            categoryRepository.save(orphanCategory);

            ReconciliationReportDTO report = service.reconcileSystemData();

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

            ReconciliationReportDTO report = service.reconcileSystemData();

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
            BackupPayloadDTO payload = new BackupPayloadDTO(
                    List.of(wallet),
                    List.of(group),
                    List.of(category),
                    List.of(txn),
                    List.of());

            Path backupFile = tempDir.resolve("integrity_error.json");
            Files.writeString(backupFile, BackupJsonCodec.toJson(payload));

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

            BackupPayloadDTO payload = new BackupPayloadDTO(
                    List.of(newWallet1, newWallet2),
                    List.of(newGroup),
                    List.of(expenseCat, feeCat),
                    List.of(ieTxn, trfTxn),
                    List.of(adj));

            Path backupFile = tempDir.resolve("valid_backup.json");
            Files.writeString(backupFile, BackupJsonCodec.toJson(payload));

            // Execute restore
            RestoreResultDTO result = service.restoreBackupData(new RestoreBackupRequestDTO(backupFile.toString()));

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
            ReconciliationReportDTO report = service.reconcileSystemData();
            assertTrue(report.isFullyConsistent());
            assertTrue(report.getIssues().isEmpty());
        }
    }
}

