package com.pfms.domain.model.wallet;

/**
 * Trạng thái vòng đời của nguồn tiền (Ví) trong hệ thống PFMS.
 * Quản lý trạng thái khả dụng cho các biến động dòng tiền và lưu trữ lịch sử.
 */
public enum WalletStatus {

    // region Constants
    ACTIVE("ACTIVE", "Đang hoạt động"),
    ARCHIVED("ARCHIVED", "Đã lưu trữ");
    // endregion

    // region State
    private final String code;
    private final String displayLabel;
    // endregion

    // region Constructors
    WalletStatus(String code, String displayLabel) {
        this.code = code;
        this.displayLabel = displayLabel;
    }
    // endregion

    // region Getters
    public String getCode() {
        return this.code;
    }

    public String getDisplayLabel() {
        return this.displayLabel;
    }
    // endregion

    // region Queries
    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }
    // endregion

    // region Parsers
    public static WalletStatus fromString(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái ví không được để trống.");
        }
        String normalized = rawValue.trim().toUpperCase();
        for (WalletStatus status : values()) {
            if (status.name().equals(normalized) || status.code.equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái ví không hợp lệ: " + rawValue);
    }
    // endregion
}
