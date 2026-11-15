package com.pfms.presentation.dto.category;

import java.util.Locale;
import java.util.Objects;

/**
 * DTO đóng gói dữ liệu yêu cầu tạo mới Nhóm nhu cầu (UC-CAT-01, FR-CAT-02).
 */
public final class CreateNeedGroupRequestDTO {

    // region State
    private final String transactionType;
    private final String name;
    private final String icon;
    private final String color;
    // endregion

    // region Constructors
    public CreateNeedGroupRequestDTO(String transactionType, String name) {
        this(transactionType, name, "", "");
    }

    public CreateNeedGroupRequestDTO(String transactionType, String name, String icon, String color) {
        this.transactionType = normalizeType(transactionType);
        this.name = (name == null) ? null : name.trim();
        this.icon = normalizeOptional(icon);
        this.color = normalizeOptional(color);
    }
    // endregion

    // region Getters
    public String getTransactionType() {
        return transactionType;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateNeedGroupRequestDTO that)) {
            return false;
        }
        return Objects.equals(transactionType, that.transactionType)
                && Objects.equals(name, that.name)
                && Objects.equals(icon, that.icon)
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionType, name, icon, color);
    }

    @Override
    public String toString() {
        return "CreateNeedGroupRequestDTO{"
                + "transactionType='" + transactionType + '\''
                + ", name='" + name + '\''
                + ", icon='" + icon + '\''
                + ", color='" + color + '\''
                + '}';
    }
    // endregion

    // region Helpers
    private static String normalizeType(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private static String normalizeOptional(String raw) {
        return (raw == null) ? "" : raw.trim();
    }
    // endregion
}
