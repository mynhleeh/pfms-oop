package com.pfms.presentation.dto.category;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO kết xuất dữ liệu chi tiết của một danh mục phân loại chi tiết cấp 3
 * kèm thông tin nhóm nhu cầu cha và loại thu chi trả về cho tầng giao diện (UC-CAT-01, UC-CAT-02, UC-CAT-04).
 */
public final class CategoryResponseDTO {

    // region State
    private final UUID categoryId;
    private final String name;
    private final UUID parentGroupId;
    private final String parentGroupName;
    private final String transactionType;
    private final String status;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public CategoryResponseDTO(
            UUID categoryId,
            String name,
            UUID parentGroupId,
            String parentGroupName,
            String transactionType,
            String status) {

        this(categoryId, name, parentGroupId, parentGroupName, transactionType, status, null);
    }

    public CategoryResponseDTO(
            UUID categoryId,
            String name,
            UUID parentGroupId,
            String parentGroupName,
            String transactionType,
            String status,
            LocalDateTime createdAt) {

        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.parentGroupId = Objects.requireNonNull(parentGroupId, "Mã nhóm nhu cầu cha không được để null");
        this.name = (name == null) ? "" : name.trim();
        this.parentGroupName = (parentGroupName == null) ? "" : parentGroupName.trim();
        this.transactionType = (transactionType == null || transactionType.trim().isEmpty())
                ? "EXPENSE"
                : transactionType.trim().toUpperCase();
        this.status = (status == null || status.trim().isEmpty())
                ? "ACTIVE"
                : status.trim().toUpperCase();
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters & Queries
    public UUID getCategoryId() {
        return this.categoryId;
    }

    public UUID getId() {
        return this.categoryId;
    }

    public String getName() {
        return this.name;
    }

    public UUID getParentGroupId() {
        return this.parentGroupId;
    }

    public UUID getNeedGroupId() {
        return this.parentGroupId;
    }

    public String getParentGroupName() {
        return this.parentGroupName;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public String getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    public boolean isDeleted() {
        return "DELETED".equalsIgnoreCase(this.status);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.categoryId, that.categoryId)
                && Objects.equals(this.parentGroupId, that.parentGroupId)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.parentGroupName, that.parentGroupName)
                && Objects.equals(this.transactionType, that.transactionType)
                && Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.categoryId, this.parentGroupId, this.name,
                this.parentGroupName, this.transactionType, this.status);
    }

    @Override
    public String toString() {
        return "CategoryResponseDTO{" +
                "categoryId=" + this.categoryId +
                ", name='" + this.name + '\'' +
                ", parentGroupId=" + this.parentGroupId +
                ", parentGroupName='" + this.parentGroupName + '\'' +
                ", transactionType='" + this.transactionType + '\'' +
                ", status='" + this.status + '\'' +
                ", createdAt=" + this.createdAt +
                '}';
    }
    // endregion
}

