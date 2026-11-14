package com.pfms.domain.model.common;

import com.pfms.common.util.Preconditions;

/**
 * Trạng thái vòng đời của các thực thể miền trong hệ thống PFMS (Ví, Danh mục, Nhóm nhu cầu).
 * Định nghĩa thống nhất các trạng thái khả dụng cho luồng xử lý tài chính và lưu trữ lịch sử.
 */
public enum EntityStatus {

    // region Constants
    ACTIVE("ACTIVE", "Đang hoạt động"),
    ARCHIVED("ARCHIVED", "Đã lưu trữ"),
    DELETED("DELETED", "Đã xóa");
    // endregion

    // region State
    private final String code;
    private final String displayLabel;
    // endregion

    // region Constructors
    EntityStatus(String code, String displayLabel) {
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

    public boolean isDeleted() {
        return this == DELETED;
    }
    // endregion

    // region Parsers
    public static EntityStatus fromString(String rawValue) {
        String nonBlank = Preconditions.requireNonBlank(rawValue, "Trạng thái không được để trống.");
        String normalized = nonBlank.toUpperCase();
        for (EntityStatus status : values()) {
            if (status.name().equals(normalized) || status.code.equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + rawValue);
    }
    // endregion
}

