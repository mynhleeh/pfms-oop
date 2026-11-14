package com.pfms.presentation.dto.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO kết xuất cấu trúc phân cấp 3 cấp theo từng nhóm nhu cầu kèm danh sách các danh mục con trực thuộc
 * phục vụ kết xuất cây phân cấp danh mục trên thành phần đồ họa JTree (UC-CAT-04).
 */
public final class CategoryHierarchyResponseDTO {

    // region State
    private final String transactionType;
    private final UUID needGroupId;
    private final String needGroupName;
    private final String needGroupStatus;
    private final List<CategoryResponseDTO> categories;
    // endregion

    // region Constructors
    public CategoryHierarchyResponseDTO(
            String transactionType,
            UUID needGroupId,
            String needGroupName,
            String needGroupStatus,
            List<CategoryResponseDTO> categories) {

        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.transactionType = (transactionType == null || transactionType.trim().isEmpty())
                ? "EXPENSE"
                : transactionType.trim().toUpperCase();
        this.needGroupName = (needGroupName == null) ? "" : needGroupName.trim();
        this.needGroupStatus = (needGroupStatus == null || needGroupStatus.trim().isEmpty())
                ? "ACTIVE"
                : needGroupStatus.trim().toUpperCase();
        this.categories = (categories == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(categories));
    }
    // endregion

    // region Getters & Queries
    public String getTransactionType() {
        return this.transactionType;
    }

    public UUID getNeedGroupId() {
        return this.needGroupId;
    }

    public String getNeedGroupName() {
        return this.needGroupName;
    }

    public String getNeedGroupStatus() {
        return this.needGroupStatus;
    }

    public List<CategoryResponseDTO> getCategories() {
        return this.categories;
    }

    public int getCategoryCount() {
        return this.categories.size();
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.needGroupStatus);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryHierarchyResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.transactionType, that.transactionType)
                && Objects.equals(this.needGroupId, that.needGroupId)
                && Objects.equals(this.needGroupName, that.needGroupName)
                && Objects.equals(this.needGroupStatus, that.needGroupStatus)
                && Objects.equals(this.categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionType, this.needGroupId, this.needGroupName,
                this.needGroupStatus, this.categories);
    }

    @Override
    public String toString() {
        return "CategoryHierarchyResponseDTO{" +
                "transactionType='" + this.transactionType + '\'' +
                ", needGroupId=" + this.needGroupId +
                ", needGroupName='" + this.needGroupName + '\'' +
                ", needGroupStatus='" + this.needGroupStatus + '\'' +
                ", categoriesCount=" + this.categories.size() +
                '}';
    }
    // endregion
}

