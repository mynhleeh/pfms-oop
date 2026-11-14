package com.pfms.presentation.dto.category;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu yêu cầu đổi tên hiển thị hoặc chuyển đổi nhóm nhu cầu cha trực thuộc
 * của danh mục chi tiết cấp 3 (UC-CAT-02).
 */
public final class RenameCategoryRequestDTO {

    // region State
    private final UUID categoryId;
    private final String name;
    private final UUID newParentGroupId;
    // endregion

    // region Constructors
    public RenameCategoryRequestDTO(UUID categoryId, String name) {
        this(categoryId, name, null);
    }

    public RenameCategoryRequestDTO(UUID categoryId, String name, UUID newParentGroupId) {
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.name = (name == null) ? null : name.trim();
        this.newParentGroupId = newParentGroupId;
    }
    // endregion

    // region Getters & Queries
    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getName() {
        return this.name;
    }

    public UUID getNewParentGroupId() {
        return this.newParentGroupId;
    }

    public boolean hasNewParentGroup() {
        return this.newParentGroupId != null;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenameCategoryRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.categoryId, that.categoryId)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.newParentGroupId, that.newParentGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.categoryId, this.name, this.newParentGroupId);
    }

    @Override
    public String toString() {
        return "RenameCategoryRequestDTO{" +
                "categoryId=" + this.categoryId +
                ", name='" + this.name + '\'' +
                ", newParentGroupId=" + this.newParentGroupId +
                '}';
    }
    // endregion
}

