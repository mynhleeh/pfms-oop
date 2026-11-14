package com.pfms.presentation.dto.report;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói tỷ trọng và tổng số tiền của một danh mục.
 * Phục vụ kết xuất từng dòng chi tiết trong bảng phân tích cơ cấu chi tiêu/thu
 * nhập.
 */
public final class CategoryShareResponseDTO {

    // region State
    private final UUID categoryId;
    private final String name;
    private final String status;
    private final BigDecimal totalAmount;
    private final BigDecimal share;
    // endregion

    // region Constructors
    public CategoryShareResponseDTO(
            UUID categoryId,
            String name,
            BigDecimal totalAmount,
            BigDecimal share) {

        this(categoryId, name, "ACTIVE", totalAmount, share);
    }

    public CategoryShareResponseDTO(
            UUID categoryId,
            String name,
            String status,
            BigDecimal totalAmount,
            BigDecimal share) {

        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.name = (name == null) ? "" : name.trim();
        this.status = (status == null || status.trim().isEmpty())
                ? "ACTIVE"
                : status.trim().toUpperCase();
        this.totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        this.share = (share != null) ? share : BigDecimal.ZERO;
    }
    // endregion

    // region Getters
    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public BigDecimal getShare() {
        return this.share;
    }
    // endregion

    // region Queries
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
        if (!(o instanceof CategoryShareResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.categoryId, that.categoryId)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.status, that.status)
                && (this.totalAmount.compareTo(that.totalAmount) == 0)
                && (this.share.compareTo(that.share) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.categoryId,
                this.name,
                this.status,
                this.totalAmount.stripTrailingZeros(),
                this.share.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "CategoryShareResponseDTO{" +
                "categoryId=" + this.categoryId +
                ", name='" + this.name + '\'' +
                ", status='" + this.status + '\'' +
                ", totalAmount=" + this.totalAmount +
                ", share=" + this.share +
                '}';
    }
    // endregion
}
