package com.pfms.presentation.dto.data;

import java.util.Objects;

/**
 * DTO yêu cầu phục hồi dữ liệu hệ thống từ tệp sao lưu JSON (FR-DAT-02).
 * Đóng gói đường dẫn tệp sao lưu trên đĩa cục bộ.
 */
public final class RestoreBackupRequestDTO {

    // region State
    private final String backupFilePath;
    // endregion

    // region Constructors
    public RestoreBackupRequestDTO(String backupFilePath) {
        this.backupFilePath = (backupFilePath == null) ? null : backupFilePath.trim();
    }
    // endregion

    // region Getters
    public String getBackupFilePath() {
        return this.backupFilePath;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestoreBackupRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.backupFilePath, that.backupFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.backupFilePath);
    }

    @Override
    public String toString() {
        return "RestoreBackupRequestDTO{" +
                "backupFilePath='" + this.backupFilePath + '\'' +
                '}';
    }
    // endregion
}
