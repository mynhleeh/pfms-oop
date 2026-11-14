package com.pfms.service.impl.data;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.DataCorruptionException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.StorageException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.category.NeedGroup;
import com.pfms.domain.model.transaction.Transaction;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.domain.repository.CategoryRepository;
import com.pfms.domain.repository.NeedGroupRepository;
import com.pfms.domain.repository.TransactionRepository;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.data.BackupPayloadDTO;
import com.pfms.presentation.dto.data.BackupResponseDTO;
import com.pfms.presentation.dto.data.ExportBackupRequestDTO;
import com.pfms.presentation.dto.data.ReconciliationReportDTO;
import com.pfms.presentation.dto.data.RestoreBackupRequestDTO;
import com.pfms.presentation.dto.data.RestoreResultDTO;
import com.pfms.service.DataManagementService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public BackupResponseDTO exportBackupData(ExportBackupRequestDTO requestDto) {
        Objects.requireNonNull(requestDto, "Yêu cầu xuất sao lưu không được để null");

        String destinationPath = requestDto.getDestinationPath();
        if (destinationPath == null || destinationPath.trim().isEmpty()) {
            throw new StorageException("Đường dẫn lưu trữ đích không hợp lệ hoặc rỗng",
                    new IllegalArgumentException("destinationPath cannot be blank"));
        }

        if (!requestDto.isFullScope() && !requestDto.isTransactionsScope()) {
            throw new BusinessException(ErrorCode.ERR_DAT_001,
                    "Phạm vi xuất sao lưu không hợp lệ: " + requestDto.getScope());
        }

        Path targetPath;
        try {
            targetPath = Paths.get(destinationPath).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new StorageException("Đường dẫn lưu trữ không đúng cú pháp hệ điều hành: " + destinationPath, e);
        }

        if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {
            throw new StorageException("Đường dẫn lưu trữ phải là tệp, không được là thư mục: " + destinationPath,
                    new IllegalArgumentException("destinationPath is a directory"));
        }

        List<Wallet> wallets;
        List<NeedGroup> needGroups;
        List<Category> categories;
        List<Transaction> transactions;
        List<BalanceAdjustmentRecord> adjustments;

        if (requestDto.isTransactionsScope()) {
            wallets = Collections.emptyList();
            needGroups = Collections.emptyList();
            categories = Collections.emptyList();
            adjustments = Collections.emptyList();
            transactions = transactionRepository.findAll();
        } else {
            wallets = walletRepository.findAll();
            needGroups = needGroupRepository.findAll();
            categories = categoryRepository.findAll();
            transactions = transactionRepository.findAll();
            adjustments = balanceAdjustmentRepository.findAll();
        }

        BackupPayloadDTO payload = new BackupPayloadDTO(wallets, needGroups, categories, transactions, adjustments);
        String jsonContent = BackupJsonCodec.toJson(payload);
        writeJsonSafely(targetPath, jsonContent);

        long fileSizeBytes;
        try {
            fileSizeBytes = Files.size(targetPath);
        } catch (IOException e) {
            fileSizeBytes = jsonContent.getBytes(StandardCharsets.UTF_8).length;
        }

        return new BackupResponseDTO(
                targetPath.toString(),
                fileSizeBytes,
                LocalDateTime.now(),
                requestDto.getScope(),
                wallets.size(),
                categories.size(),
                transactions.size(),
                adjustments.size());
    }

    @Override
    public RestoreResultDTO restoreBackupData(RestoreBackupRequestDTO requestDto) {
        Objects.requireNonNull(requestDto, "Yêu cầu phục hồi dữ liệu không được để null");

        String backupFilePath = requestDto.getBackupFilePath();
        if (backupFilePath == null || backupFilePath.trim().isEmpty()) {
            throw new StorageException("Đường dẫn tệp phục hồi không hợp lệ hoặc rỗng",
                    new IllegalArgumentException("backupFilePath cannot be blank"));
        }

        Path backupPath;
        try {
            backupPath = Paths.get(backupFilePath).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new StorageException("Đường dẫn lưu trữ không đúng cú pháp hệ điều hành: " + backupFilePath, e);
        }

        if (!Files.exists(backupPath) || !Files.isRegularFile(backupPath)) {
            throw new StorageException("Tệp sao lưu không tồn tại hoặc không phải là tệp hợp lệ: " + backupFilePath,
                    new java.io.FileNotFoundException(backupFilePath));
        }

        String jsonContent;
        try {
            jsonContent = Files.readString(backupPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("Không thể đọc tệp sao lưu: " + backupPath, e);
        }

        BackupPayloadDTO payload = BackupJsonCodec.fromJson(jsonContent);

        ReconciliationReportDTO report = SystemDataReconciler.reconcile(
                payload.getWallets(),
                payload.getTransactions(),
                payload.getCategories(),
                payload.getNeedGroups(),
                payload.getAdjustments());

        if (!report.isFullyConsistent()) {
            String issueDetail = report.getIssues().isEmpty()
                    ? "Ràng buộc toàn vẹn số dư hoặc danh mục bị vi phạm"
                    : report.getIssues().get(0).getMessage();
            throw new DataCorruptionException(
                    ErrorCode.ERR_DAT_002,
                    "Dữ liệu tệp sao lưu vi phạm ràng buộc toàn vẹn: " + issueDetail,
                    backupPath.getFileName().toString(),
                    1,
                    new IllegalStateException(issueDetail));
        }

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

        return RestoreResultDTO.success(
                "Phục hồi dữ liệu hệ thống thành công",
                payload.getWallets().size(),
                payload.getCategories().size(),
                payload.getTransactions().size(),
                payload.getAdjustments().size());
    }
    // endregion

    // region Queries
    @Override
    public ReconciliationReportDTO reconcileSystemData() {
        return SystemDataReconciler.reconcile(
                walletRepository.findAll(),
                transactionRepository.findAll(),
                categoryRepository.findAll(),
                needGroupRepository.findAll(),
                balanceAdjustmentRepository.findAll());
    }
    // endregion

    // region Helpers
    private void writeJsonSafely(Path targetPath, String content) {
        Path parent = targetPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new StorageException("Không thể khởi tạo thư mục lưu trữ: " + parent, e);
            }
        }

        Path tempPath = targetPath.resolveSibling(targetPath.getFileName().toString() + ".tmp");
        try {
            Files.writeString(tempPath, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {
            }
            throw new StorageException("Lỗi ghi tệp sao lưu dữ liệu: " + targetPath, e);
        }
    }
    // endregion
}
