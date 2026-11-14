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
    private final BigDecimal shareInGroup;
    private final BigDecimal previousTotalAmount;
    private final BigDecimal differenceAmount;
    private final BigDecimal percentageChange;
    private final boolean isNew;
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

        this(categoryId, name, status, totalAmount, share,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    public CategoryShareResponseDTO(
            UUID categoryId,
            String name,
            String status,
            BigDecimal totalAmount,
            BigDecimal share,
            BigDecimal shareInGroup) {

        this(categoryId, name, status, totalAmount, share, shareInGroup,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    public CategoryShareResponseDTO(
            UUID categoryId,
            String name,
            String status,
            BigDecimal totalAmount,
            BigDecimal share,
            BigDecimal shareInGroup,
            BigDecimal previousTotalAmount,
            BigDecimal differenceAmount,
            BigDecimal percentageChange,
            boolean isNew) {

        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.name = (name == null) ? "" : name.trim();
        this.status = (status == null || status.trim().isEmpty())
                ? "ACTIVE"
                : status.trim().toUpperCase();
        this.totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        this.share = (share != null) ? share : BigDecimal.ZERO;
        this.shareInGroup = (shareInGroup != null) ? shareInGroup : BigDecimal.ZERO;
        this.previousTotalAmount = (previousTotalAmount != null) ? previousTotalAmount : BigDecimal.ZERO;
        this.differenceAmount = (differenceAmount != null) ? differenceAmount : BigDecimal.ZERO;
        this.percentageChange = (percentageChange != null) ? percentageChange : BigDecimal.ZERO;
        this.isNew = isNew;
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

    public BigDecimal getShareInGroup() {
        return this.shareInGroup;
    }

    public BigDecimal getPreviousTotalAmount() {
        return this.previousTotalAmount;
    }

    public BigDecimal getDifferenceAmount() {
        return this.differenceAmount;
    }

    public BigDecimal getPercentageChange() {
        return this.percentageChange;
    }

    public boolean isNew() {
        return this.isNew;
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
                && (this.share.compareTo(that.share) == 0)
                && (this.shareInGroup.compareTo(that.shareInGroup) == 0)
                && (this.previousTotalAmount.compareTo(that.previousTotalAmount) == 0)
                && (this.differenceAmount.compareTo(that.differenceAmount) == 0)
                && (this.percentageChange.compareTo(that.percentageChange) == 0)
                && (this.isNew == that.isNew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.categoryId,
                this.name,
                this.status,
                this.totalAmount.stripTrailingZeros(),
                this.share.stripTrailingZeros(),
                this.shareInGroup.stripTrailingZeros(),
                this.previousTotalAmount.stripTrailingZeros(),
                this.differenceAmount.stripTrailingZeros(),
                this.percentageChange.stripTrailingZeros(),
                this.isNew);
    }

    @Override
    public String toString() {
        return "CategoryShareResponseDTO{" +
                "categoryId=" + this.categoryId +
                ", name='" + this.name + '\'' +
                ", status='" + this.status + '\'' +
                ", totalAmount=" + this.totalAmount +
                ", share=" + this.share +
                ", shareInGroup=" + this.shareInGroup +
                ", isNew=" + this.isNew +
                '}';
    }
    // endregion
}
