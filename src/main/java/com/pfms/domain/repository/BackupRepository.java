package com.pfms.domain.repository;

import com.pfms.domain.model.data.BackupPayload;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Kho lưu trữ trừu tượng phục vụ việc xuất và phục hồi tệp sao lưu dữ liệu toàn
 * hệ thống.
 */
public interface BackupRepository {

    // region Commands

    long writeBackup(Path targetPath, BackupPayload payload);
    // endregion

    // region Queries

    Path resolveWritableTargetPath(String destinationDirectoryPath, String scope, LocalDateTime exportedAt);

    BackupPayload readBackup(String backupFilePath);
    // endregion
}
