package com.pfms.presentation.dto.data;

import java.util.Objects;

/**
 * DTO yêu cầu xuất bản sao lưu dữ liệu tài chính của hệ thống (FR-DAT-01).
 * Đóng gói đường dẫn thư mục/tệp đích và phạm vi xuất dữ liệu (FULL hoặc TRANSACTIONS).
 */
public final class ExportBackupRequestDTO {

    // region Constants
    public static final String DEFAULT_SCOPE = "FULL";
    public static final String TRANSACTIONS_SCOPE = "TRANSACTIONS";
    // endregion

    // region State
    private final String destinationPath;
    private final String scope;
    // endregion

    // region Constructors
    public ExportBackupRequestDTO(String destinationPath) {
        this(destinationPath, DEFAULT_SCOPE);
    }

    public ExportBackupRequestDTO(String destinationPath, String scope) {
        this.destinationPath = (destinationPath == null) ? null : destinationPath.trim();
        if (scope == null || scope.trim().isEmpty()) {
            this.scope = DEFAULT_SCOPE;
        } else {
            this.scope = scope.trim().toUpperCase();
        }
    }
    // endregion

    // region Getters
    public String getDestinationPath() {
        return this.destinationPath;
    }

    public String getScope() {
        return this.scope;
    }

    public boolean isFullScope() {
        return DEFAULT_SCOPE.equalsIgnoreCase(this.scope);
    }

    public boolean isTransactionsScope() {
        return TRANSACTIONS_SCOPE.equalsIgnoreCase(this.scope);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExportBackupRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.destinationPath, that.destinationPath)
                && Objects.equals(this.scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.destinationPath, this.scope);
    }

    @Override
    public String toString() {
        return "ExportBackupRequestDTO{" +
                "destinationPath='" + this.destinationPath + '\'' +
                ", scope='" + this.scope + '\'' +
                '}';
    }
    // endregion
}
