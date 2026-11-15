package com.pfms.service.impl.data;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.BackupRepository;
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
import java.util.List;
import java.util.Objects;

/**
 * Cài đặt dịch vụ quản trị dữ liệu ({@link DataManagementService}).
 * Điều phối xuất sao lưu ra tệp đĩa, phục hồi dữ liệu và kiểm tra toàn vẹn tài
 * chính qua các collaborator nội bộ.
 */
public class DataManagementServiceImpl implements DataManagementService {

        // region State
        private final WalletRepository walletRepository;
        private final TransactionRepository transactionRepository;
        private final CategoryRepository categoryRepository;
        private final NeedGroupRepository needGroupRepository;
        private final BalanceAdjustmentRepository balanceAdjustmentRepository;
        private final BackupRepository backupRepository;
        // endregion

        // region Constructors
        public DataManagementServiceImpl(
                        WalletRepository walletRepository,
                        TransactionRepository transactionRepository,
                        CategoryRepository categoryRepository,
                        NeedGroupRepository needGroupRepository,
                        BalanceAdjustmentRepository balanceAdjustmentRepository,
                        BackupRepository backupRepository) {

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
                this.backupRepository = Objects.requireNonNull(
                                backupRepository, "BackupRepository không được để null");
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
                Path targetPath = backupRepository.resolveWritableTargetPath(
                                request.getDestinationPath(),
                                request.getScope(),
                                exportedAt);
                BackupPayload payload = collectBackupPayload(request.isTransactionsScope());
                long fileSizeBytes = backupRepository.writeBackup(targetPath, payload);

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

                BackupPayload payload = backupRepository.readBackup(request.getBackupFilePath());
                ReconciliationReportResponseDTO report = SystemDataReconciler.reconcile(payload);

                if (!report.isFullyConsistent()) {
                        String issueDetail = report.getIssues().isEmpty()
                                        ? "Ràng buộc toàn vẹn số dư hoặc danh mục bị vi phạm"
                                        : report.getIssues().get(0).getMessage();
                        throw restoreIntegrityError(request.getBackupFilePath(), issueDetail);
                }
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
                return SystemDataReconciler.reconcile(collectBackupPayload(false));
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

        private DataCorruptionException restoreIntegrityError(String backupFilePath, String issueDetail) {
                String fileName = (backupFilePath == null || backupFilePath.trim().isEmpty())
                                ? "backup.json"
                                : Path.of(backupFilePath).getFileName().toString();
                return new DataCorruptionException(
                                ErrorCode.ERR_DAT_002,
                                "Dữ liệu tệp sao lưu vi phạm ràng buộc toàn vẹn: " + issueDetail,
                                fileName,
                                1,
                                new IllegalStateException(issueDetail));
        }

        private void replaceAllRepositories(BackupPayload payload) {
                BackupPayload previousSnapshot = collectBackupPayload(false);

                try {
                        getClearSteps().forEach(Runnable::run);
                        getSaveSteps(payload).forEach(Runnable::run);
                } catch (RuntimeException exception) {
                        StorageException primaryException = (exception instanceof StorageException storageException)
                                        ? storageException
                                        : new StorageException(
                                                        "Lỗi ghi đè kho lưu trữ trong quá trình phục hồi dữ liệu",
                                                        exception);
                        rollbackRepositories(primaryException, previousSnapshot);
                        throw primaryException;
                }
        }

        private void rollbackRepositories(StorageException primaryException, BackupPayload snapshot) {
                getClearSteps().forEach(step -> RollbackSupport.runRollbackStep(step, primaryException));
                getSaveSteps(snapshot).forEach(step -> RollbackSupport.runRollbackStep(step, primaryException));
        }

        private List<Runnable> getClearSteps() {
                return List.of(
                                transactionRepository::deleteAll,
                                balanceAdjustmentRepository::deleteAll,
                                categoryRepository::deleteAll,
                                needGroupRepository::deleteAll,
                                walletRepository::deleteAll);
        }

        private List<Runnable> getSaveSteps(BackupPayload payload) {
                return List.of(
                                () -> walletRepository.saveAll(payload.getWallets()),
                                () -> needGroupRepository.saveAll(payload.getNeedGroups()),
                                () -> categoryRepository.saveAll(payload.getCategories()),
                                () -> balanceAdjustmentRepository.saveAll(payload.getAdjustments()),
                                () -> transactionRepository.saveAll(payload.getTransactions()));
        }
        // endregion
}
