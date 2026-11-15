package com.pfms.presentation.dto.category;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO kết xuất thông tin chi tiết của Nhóm nhu cầu trả về cho tầng giao diện (UC-CAT-01..04).
 */
public final class NeedGroupResponseDTO {

    // region State
    private final UUID needGroupId;
    private final String name;
    private final String transactionType;
    private final String status;
    private final String icon;
    private final String color;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public NeedGroupResponseDTO(
            UUID needGroupId,
            String name,
            String transactionType,
            String status,
            String icon,
            String color) {

        this(needGroupId, name, transactionType, status, icon, color, null);
    }

    public NeedGroupResponseDTO(
            UUID needGroupId,
            String name,
            String transactionType,
            String status,
            String icon,
            String color,
            LocalDateTime createdAt) {

        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.name = normalizeEmpty(name);
        this.transactionType = normalizeOrDefault(transactionType, "EXPENSE");
        this.status = normalizeOrDefault(status, "ACTIVE");
        this.icon = normalizeEmpty(icon);
        this.color = normalizeEmpty(color);
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters & Queries
    public UUID getNeedGroupId() {
        return needGroupId;
    }

    public String getName() {
        return name;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getStatus() {
        return status;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isDeleted() {
        return "DELETED".equalsIgnoreCase(status);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NeedGroupResponseDTO that)) {
            return false;
        }
        return Objects.equals(needGroupId, that.needGroupId)
                && Objects.equals(name, that.name)
                && Objects.equals(transactionType, that.transactionType)
                && Objects.equals(status, that.status)
                && Objects.equals(icon, that.icon)
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(needGroupId, name, transactionType, status, icon, color);
    }

    @Override
    public String toString() {
        return "NeedGroupResponseDTO{"
                + "needGroupId=" + needGroupId
                + ", name='" + name + '\''
                + ", transactionType='" + transactionType + '\''
                + ", status='" + status + '\''
                + ", icon='" + icon + '\''
                + ", color='" + color + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
    // endregion

    // region Helpers
    private static String normalizeEmpty(String raw) {
        return (raw == null) ? "" : raw.trim();
    }

    private static String normalizeOrDefault(String raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? fallback : trimmed.toUpperCase(Locale.ROOT);
    }
    // endregion
}

