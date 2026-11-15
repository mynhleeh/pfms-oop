package com.pfms.service.impl.data;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.IncomeExpenseTransaction;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ExportBackupResponseDTO;
import com.pfms.presentation.dto.data.ReconciliationReportResponseDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreBackupResponseDTO;
import com.pfms.service.DataManagementService;
import com.pfms.service.impl.common.RollbackSupport;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Cài đặt dịch vụ quản trị dữ liệu ({@link DataManagementService}).
 * Điều phối xuất sao lưu ra tệp đĩa, phục hồi dữ liệu và kiểm tra toàn vẹn tài chính qua các collaborator nội bộ.
 */
public class DataManagementServiceImpl implements DataManagementService {

    // region State
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final NeedGroupRepository needGroupRepository;
    private final BalanceAdjustmentRepository balanceAdjustmentRepository;
    // endregion

    // region Constructors
    public DataManagementServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            NeedGroupRepository needGroupRepository,
            BalanceAdjustmentRepository balanceAdjustmentRepository) {

        this.walletRepository = Objects.requireNonNull(
                walletRepository, "WalletRepository không được để null");
        this.transactionRepository = Objects.requireNonNull(
                transactionRepository, "TransactionRepository không được để null");
        this.categoryRepository = Objects.requireNonNull(
                categoryRepository, "CategoryRepository không được để null");
        this.needGroupRepository = Objects.requireNonNull(
                needGroupRepository, "NeedGroupRepository không được để null");
        this.balanceAdjustmentRepository = Objects.requireNonNull(
                balanceAdjustmentRepository, "BalanceAdjustmentRepository không được để null");
    }
    // endregion

    // region Commands
    @Override
    public ExportBackupResponseDTO exportBackupData(ExportBackupRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu xuất sao lưu không được để null");
        if (!request.isFullScope() && !request.isTransactionsScope()) {
            throw new BusinessException(ErrorCode.ERR_DAT_001,
                    "Phạm vi xuất sao lưu không hợp lệ: " + request.getScope());
        }

        LocalDateTime exportedAt = LocalDateTime.now();
        Path targetPath = BackupStorageSupport.resolveWritableTargetPath(
                request.getDestinationPath(),
                request.getScope(),
                exportedAt);
        BackupPayload payload = collectBackupPayload(request.isTransactionsScope());
        String jsonContent = BackupJsonSerializer.toJson(payload);
        long fileSizeBytes = BackupStorageSupport.writeUtf8Safely(targetPath, jsonContent);

        return new ExportBackupResponseDTO(
                targetPath.toString(),
                fileSizeBytes,
                exportedAt,
                request.getScope(),
                payload.getWallets().size(),
                payload.getCategories().size(),
                payload.getTransactions().size(),
                payload.getAdjustments().size());
    }

    @Override
    public RestoreBackupResponseDTO restoreBackupData(RestoreBackupRequestDTO request) {
        Objects.requireNonNull(request, "Yêu cầu phục hồi dữ liệu không được để null");

        Path backupPath = BackupStorageSupport.resolveReadableBackupPath(request.getBackupFilePath());
        String jsonContent = BackupStorageSupport.readUtf8String(backupPath);
        BackupPayload payload = BackupJsonDeserializer.fromJson(jsonContent);
        validateUniqueIdentifiers(payload, backupPath);

        ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(
                payload.getWallets(),
                payload.getTransactions(),
                payload.getCategories(),
                payload.getNeedGroups(),
                payload.getAdjustments());

        validateRestoreIntegrity(payload, report, backupPath);
        replaceAllRepositories(payload);

        return RestoreBackupResponseDTO.success(
                "Phục hồi dữ liệu hệ thống thành công",
                payload.getWallets().size(),
                payload.getCategories().size(),
                payload.getTransactions().size(),
                payload.getAdjustments().size());
    }
    // endregion

    // region Queries
    @Override
    public ReconciliationReportResponseDTO reconcileSystemData() {
        return SystemDataReconciler.reconcile(
                walletRepository.findAll(),
                transactionRepository.findAll(),
                categoryRepository.findAll(),
                needGroupRepository.findAll(),
                balanceAdjustmentRepository.findAll());
    }
    // endregion

    // region Helpers
    private BackupPayload collectBackupPayload(boolean transactionsOnly) {
        if (transactionsOnly) {
            return new BackupPayload(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    transactionRepository.findAll(),
                    Collections.emptyList());
        }
        return new BackupPayload(
                walletRepository.findAll(),
                needGroupRepository.findAll(),
                categoryRepository.findAll(),
                transactionRepository.findAll(),
                balanceAdjustmentRepository.findAll());
    }

    private void validateUniqueIdentifiers(BackupPayload payload, Path backupPath) {
        Set<UUID> walletIds = new HashSet<>();
        for (Wallet wallet : payload.getWallets()) {
            if (!walletIds.add(wallet.getId())) {
                throw restoreIntegrityError(
                        backupPath, "Trùng mã ví trong bản sao lưu: " + wallet.getId());
            }
        }

        Set<UUID> needGroupIds = new HashSet<>();
        for (NeedGroup needGroup : payload.getNeedGroups()) {
            if (!needGroupIds.add(needGroup.getNeedGroupId())) {
                throw restoreIntegrityError(
                        backupPath, "Trùng mã nhóm nhu cầu trong bản sao lưu: " + needGroup.getNeedGroupId());
            }
        }

        Set<UUID> categoryIds = new HashSet<>();
        for (Category category : payload.getCategories()) {
            if (!categoryIds.add(category.getCategoryId())) {
                throw restoreIntegrityError(
                        backupPath, "Trùng mã danh mục trong bản sao lưu: " + category.getCategoryId());
            }
        }

        Set<UUID> transactionIds = new HashSet<>();
        for (Transaction transaction : payload.getTransactions()) {
            if (!transactionIds.add(transaction.getTransactionId())) {
                throw restoreIntegrityError(
                        backupPath, "Trùng mã giao dịch trong bản sao lưu: " + transaction.getTransactionId());
            }
        }

        Set<UUID> adjustmentIds = new HashSet<>();
        for (BalanceAdjustmentRecord adjustment : payload.getAdjustments()) {
            if (!adjustmentIds.add(adjustment.getRecordId())) {
                throw restoreIntegrityError(
                        backupPath, "Trùng mã bản ghi điều chỉnh số dư: " + adjustment.getRecordId());
            }
        }
    }

    private void validateRestoreIntegrity(
            BackupPayload payload,
            ReconciliationReportResponseDTO report,
            Path backupPath) {
        if (!report.isFullyConsistent()) {
            String issueDetail = report.getIssues().isEmpty()
                    ? "Ràng buộc toàn vẹn số dư hoặc danh mục bị vi phạm"
                    : report.getIssues().get(0).getMessage();
            throw restoreIntegrityError(backupPath, issueDetail);
        }

        Set<UUID> walletIds = new HashSet<>();
        for (Wallet wallet : payload.getWallets()) {
            walletIds.add(wallet.getId());
            if (wallet.getCurrentBalance().isNegative()) {
                throw restoreIntegrityError(
                        backupPath, "Số dư ví không được âm: " + wallet.getName());
            }
        }

        for (Transaction transaction : payload.getTransactions()) {
            if (transaction instanceof IncomeExpenseTransaction ie
                    && !walletIds.contains(ie.getWallet().getId())) {
                throw restoreIntegrityError(
                        backupPath,
                        "Giao dịch thu chi tham chiếu tới ví không có trong bản sao lưu: " + ie.getWallet().getId());
            }
        }

        for (BalanceAdjustmentRecord adjustment : payload.getAdjustments()) {
            if (!walletIds.contains(adjustment.getWalletId())) {
                throw restoreIntegrityError(
                        backupPath,
                        "Bản ghi điều chỉnh tham chiếu tới ví không có trong bản sao lưu: "
                                + adjustment.getWalletId());
            }
        }
    }

    private DataCorruptionException restoreIntegrityError(Path backupPath, String issueDetail) {
        return new DataCorruptionException(
                ErrorCode.ERR_DAT_002,
                "Dữ liệu tệp sao lưu vi phạm ràng buộc toàn vẹn: " + issueDetail,
                backupPath.getFileName().toString(),
                1,
                new IllegalStateException(issueDetail));
    }

    private void replaceAllRepositories(BackupPayload payload) {
        List<Wallet> previousWallets = List.copyOf(walletRepository.findAll());
        List<NeedGroup> previousNeedGroups = List.copyOf(needGroupRepository.findAll());
        List<Category> previousCategories = List.copyOf(categoryRepository.findAll());
        List<BalanceAdjustmentRecord> previousAdjustments = List.copyOf(balanceAdjustmentRepository.findAll());
        List<Transaction> previousTransactions = List.copyOf(transactionRepository.findAll());

        try {
            transactionRepository.deleteAll();
            balanceAdjustmentRepository.deleteAll();
            categoryRepository.deleteAll();
            needGroupRepository.deleteAll();
            walletRepository.deleteAll();

            walletRepository.saveAll(payload.getWallets());
            needGroupRepository.saveAll(payload.getNeedGroups());
            categoryRepository.saveAll(payload.getCategories());
            balanceAdjustmentRepository.saveAll(payload.getAdjustments());
            transactionRepository.saveAll(payload.getTransactions());
        } catch (RuntimeException exception) {
            StorageException primaryException = (exception instanceof StorageException storageException)
                    ? storageException
                    : new StorageException(
                            "Lỗi ghi đè kho lưu trữ trong quá trình phục hồi dữ liệu",
                            exception);
            rollbackRepositories(
                    primaryException,
                    previousWallets,
                    previousNeedGroups,
                    previousCategories,
                    previousAdjustments,
                    previousTransactions);
            throw primaryException;
        }
    }

    private void rollbackRepositories(
            StorageException primaryException,
            List<Wallet> previousWallets,
            List<NeedGroup> previousNeedGroups,
            List<Category> previousCategories,
            List<BalanceAdjustmentRecord> previousAdjustments,
            List<Transaction> previousTransactions) {

        RollbackSupport.runRollbackStep(transactionRepository::deleteAll, primaryException);
        RollbackSupport.runRollbackStep(balanceAdjustmentRepository::deleteAll, primaryException);
        RollbackSupport.runRollbackStep(categoryRepository::deleteAll, primaryException);
        RollbackSupport.runRollbackStep(needGroupRepository::deleteAll, primaryException);
        RollbackSupport.runRollbackStep(walletRepository::deleteAll, primaryException);

        RollbackSupport.runRollbackStep(() -> walletRepository.saveAll(previousWallets), primaryException);
        RollbackSupport.runRollbackStep(() -> needGroupRepository.saveAll(previousNeedGroups), primaryException);
        RollbackSupport.runRollbackStep(() -> categoryRepository.saveAll(previousCategories), primaryException);
        RollbackSupport.runRollbackStep(
                () -> balanceAdjustmentRepository.saveAll(previousAdjustments), primaryException);
        RollbackSupport.runRollbackStep(
                () -> transactionRepository.saveAll(previousTransactions), primaryException);
    }
    // endregion
}
