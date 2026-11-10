package com.pfms.domain.model.category;

/**
 * Trạng thái của danh mục (Category).
 * Gồm hai trạng thái: đang dùng (ACTIVE) hoặc đã xóa (DELETED).
 */
public enum CategoryStatus {

    // region Constants
    ACTIVE("ACTIVE", "Đang hoạt động"),
    DELETED("DELETED", "Đã xóa");
    // endregion

    // region State
    private final String code;
    private final String displayLabel;
    // endregion

    // region Constructors
    CategoryStatus(String code, String displayLabel) {
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

    public boolean isDeleted() {
        return this == DELETED;
    }
    // endregion

    // region Parsers
    public static CategoryStatus fromString(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái danh mục không được để trống.");
        }
        String normalized = rawValue.trim().toUpperCase();
        for (CategoryStatus status : values()) {
            if (status.name().equals(normalized) || status.code.equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái danh mục không hợp lệ: " + rawValue);
    }
    // endregion
}

