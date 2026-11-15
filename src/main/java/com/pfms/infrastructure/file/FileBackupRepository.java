package com.pfms.infrastructure.file;

import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.repository.BackupRepository;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cài đặt kho lưu trữ tệp sao lưu dữ liệu hệ thống ({@link BackupRepository})
 * thực hiện tuần tự hóa JSON và lưu trữ tệp trên đĩa cục bộ theo chuẩn UTF-8 nguyên tử.
 */
public class FileBackupRepository implements BackupRepository {

    // region Constructors
    public FileBackupRepository() {
        // Khởi tạo mặc định
    }
    // endregion

    // region Commands
    @Override
    public long writeBackup(Path targetPath, BackupPayload payload) {
        Objects.requireNonNull(targetPath, "Đường dẫn tệp đích không được để null");
        Objects.requireNonNull(payload, "Dữ liệu sao lưu không được để null");
        String jsonContent = BackupJsonSerializer.toJson(payload);
        return BackupStorageSupport.writeUtf8Safely(targetPath, jsonContent);
    }
    // endregion

    // region Queries
    @Override
    public Path resolveWritableTargetPath(String destinationDirectoryPath, String scope, LocalDateTime exportedAt) {
        return BackupStorageSupport.resolveWritableTargetPath(destinationDirectoryPath, scope, exportedAt);
    }

    @Override
    public BackupPayload readBackup(String backupFilePath) {
        Path backupPath = BackupStorageSupport.resolveReadableBackupPath(backupFilePath);
        String jsonContent = BackupStorageSupport.readUtf8String(backupPath);
        return BackupJsonDeserializer.fromJson(jsonContent);
    }
    // endregion
}
