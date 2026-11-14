package com.pfms.presentation.dto.data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO đóng gói kết quả phản hồi sau khi hoàn tất quy trình phục hồi dữ liệu từ
 * bản sao lưu (FR-DAT-02).
 * Cung cấp trạng thái thành công, thông điệp, thời điểm và số lượng thực thể đã phục hồi.
 */
public final class RestoreBackupResponseDTO {

    // region State
    private final boolean success;
    private final String message;
    private final LocalDateTime restoredAt;

    private final int restoredWallets;
    private final int restoredCategories;
    private final int restoredTransactions;
    private final int restoredAdjustments;
    // endregion

    // region Constructors
    public RestoreBackupResponseDTO(
            boolean success,
            String message,
            LocalDateTime restoredAt,
            int restoredWallets,
            int restoredCategories,
            int restoredTransactions,
            int restoredAdjustments) {

        this.success = success;
        this.message = (message == null) ? "" : message.trim();
        this.restoredAt = (restoredAt == null) ? LocalDateTime.now() : restoredAt;
        this.restoredWallets = Math.max(0, restoredWallets);
        this.restoredCategories = Math.max(0, restoredCategories);
        this.restoredTransactions = Math.max(0, restoredTransactions);
        this.restoredAdjustments = Math.max(0, restoredAdjustments);
    }
    // endregion

    // region Factory
    public static RestoreBackupResponseDTO success(
            String message,
            int restoredWallets,
            int restoredCategories,
            int restoredTransactions,
            int restoredAdjustments) {

        return new RestoreBackupResponseDTO(
                true,
                message,
                LocalDateTime.now(),
                restoredWallets,
                restoredCategories,
                restoredTransactions,
                restoredAdjustments);
    }

    public static RestoreBackupResponseDTO failure(String message) {
        return new RestoreBackupResponseDTO(
                false,
                message,
                LocalDateTime.now(),
                0, 0, 0, 0);
    }
    // endregion

    // region Getters
    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getRestoredAt() {
        return this.restoredAt;
    }

    public int getRestoredWallets() {
        return this.restoredWallets;
    }

    public int getRestoredCategories() {
        return this.restoredCategories;
    }

    public int getRestoredTransactions() {
        return this.restoredTransactions;
    }

    public int getRestoredAdjustments() {
        return this.restoredAdjustments;
    }

    public int getTotalRestoredRecords() {
        return this.restoredWallets
                + this.restoredCategories
                + this.restoredTransactions
                + this.restoredAdjustments;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestoreBackupResponseDTO that)) {
            return false;
        }
        return this.success == that.success
                && this.restoredWallets == that.restoredWallets
                && this.restoredCategories == that.restoredCategories
                && this.restoredTransactions == that.restoredTransactions
                && this.restoredAdjustments == that.restoredAdjustments
                && Objects.equals(this.message, that.message)
                && Objects.equals(this.restoredAt, that.restoredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.success,
                this.message,
                this.restoredAt,
                this.restoredWallets,
                this.restoredCategories,
                this.restoredTransactions,
                this.restoredAdjustments);
    }

    @Override
    public String toString() {
        return "RestoreBackupResponseDTO{" +
                "success=" + this.success +
                ", message='" + this.message + '\'' +
                ", restoredAt=" + this.restoredAt +
                ", restoredWallets=" + this.restoredWallets +
                ", restoredCategories=" + this.restoredCategories +
                ", restoredTransactions=" + this.restoredTransactions +
                ", restoredAdjustments=" + this.restoredAdjustments +
                ", totalRestoredRecords=" + getTotalRestoredRecords() +
                '}';
    }
    // endregion
}

