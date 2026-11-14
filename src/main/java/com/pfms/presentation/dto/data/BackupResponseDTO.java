package com.pfms.presentation.dto.data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO đóng gói kết quả phản hồi sau khi hoàn tất xuất bản sao lưu dữ liệu (FR-DAT-01).
 * Cung cấp thông tin đường dẫn tệp, kích thước, thời điểm và số lượng bản ghi của từng thực thể.
 */
public final class BackupResponseDTO {

    // region State
    private final String filePath;
    private final long fileSizeBytes;
    private final LocalDateTime exportedAt;
    private final String scope;

    private final int walletCount;
    private final int categoryCount;
    private final int transactionCount;
    private final int adjustmentCount;
    // endregion

    // region Constructors
    public BackupResponseDTO(
            String filePath,
            long fileSizeBytes,
            LocalDateTime exportedAt,
            String scope,
            int walletCount,
            int categoryCount,
            int transactionCount,
            int adjustmentCount) {

        this.filePath = (filePath == null) ? "" : filePath.trim();
        this.fileSizeBytes = Math.max(0L, fileSizeBytes);
        this.exportedAt = (exportedAt == null) ? LocalDateTime.now() : exportedAt;
        this.scope = (scope == null || scope.trim().isEmpty())
                ? ExportBackupRequestDTO.DEFAULT_SCOPE
                : scope.trim().toUpperCase();
        this.walletCount = Math.max(0, walletCount);
        this.categoryCount = Math.max(0, categoryCount);
        this.transactionCount = Math.max(0, transactionCount);
        this.adjustmentCount = Math.max(0, adjustmentCount);
    }
    // endregion

    // region Getters
    public String getFilePath() {
        return this.filePath;
    }

    public long getFileSizeBytes() {
        return this.fileSizeBytes;
    }

    public LocalDateTime getExportedAt() {
        return this.exportedAt;
    }

    public String getScope() {
        return this.scope;
    }

    public int getWalletCount() {
        return this.walletCount;
    }

    public int getCategoryCount() {
        return this.categoryCount;
    }

    public int getTransactionCount() {
        return this.transactionCount;
    }

    public int getAdjustmentCount() {
        return this.adjustmentCount;
    }

    public int getTotalRecordCount() {
        return this.walletCount
                + this.categoryCount
                + this.transactionCount
                + this.adjustmentCount;
    }

    public boolean isFullScope() {
        return ExportBackupRequestDTO.DEFAULT_SCOPE.equalsIgnoreCase(this.scope);
    }

    public boolean isTransactionsScope() {
        return ExportBackupRequestDTO.TRANSACTIONS_SCOPE.equalsIgnoreCase(this.scope);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BackupResponseDTO that)) {
            return false;
        }
        return this.fileSizeBytes == that.fileSizeBytes
                && this.walletCount == that.walletCount
                && this.categoryCount == that.categoryCount
                && this.transactionCount == that.transactionCount
                && this.adjustmentCount == that.adjustmentCount
                && Objects.equals(this.filePath, that.filePath)
                && Objects.equals(this.exportedAt, that.exportedAt)
                && Objects.equals(this.scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.filePath,
                this.fileSizeBytes,
                this.exportedAt,
                this.scope,
                this.walletCount,
                this.categoryCount,
                this.transactionCount,
                this.adjustmentCount);
    }

    @Override
    public String toString() {
        return "BackupResponseDTO{" +
                "filePath='" + this.filePath + '\'' +
                ", fileSizeBytes=" + this.fileSizeBytes +
                ", exportedAt=" + this.exportedAt +
                ", scope='" + this.scope + '\'' +
                ", walletCount=" + this.walletCount +
                ", categoryCount=" + this.categoryCount +
                ", transactionCount=" + this.transactionCount +
                ", adjustmentCount=" + this.adjustmentCount +
                ", totalRecordCount=" + getTotalRecordCount() +
                '}';
    }
    // endregion
}

