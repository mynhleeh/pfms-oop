package com.pfms.presentation.dto.category;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu yêu cầu đổi tên và cập nhật hiển thị Nhóm nhu cầu (UC-CAT-02, FR-CAT-03).
 */
public final class RenameNeedGroupRequestDTO {

    // region State
    private final UUID needGroupId;
    private final String name;
    private final String icon;
    private final String color;
    // endregion

    // region Constructors
    public RenameNeedGroupRequestDTO(UUID needGroupId, String name) {
        this(needGroupId, name, null, null);
    }

    public RenameNeedGroupRequestDTO(UUID needGroupId, String name, String icon, String color) {
        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.name = trimNullable(name);
        this.icon = trimNullable(icon);
        this.color = trimNullable(color);
    }
    // endregion

    // region Getters & Queries
    public UUID getNeedGroupId() {
        return needGroupId;
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

    public boolean hasAppearanceUpdate() {
        return icon != null || color != null;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenameNeedGroupRequestDTO that)) {
            return false;
        }
        return Objects.equals(needGroupId, that.needGroupId)
                && Objects.equals(name, that.name)
                && Objects.equals(icon, that.icon)
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(needGroupId, name, icon, color);
    }

    @Override
    public String toString() {
        return "RenameNeedGroupRequestDTO{"
                + "needGroupId=" + needGroupId
                + ", name='" + name + '\''
                + ", icon='" + icon + '\''
                + ", color='" + color + '\''
                + '}';
    }
    // endregion

    // region Helpers
    private static String trimNullable(String raw) {
        return (raw == null) ? null : raw.trim();
    }
    // endregion
}

