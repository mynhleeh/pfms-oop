package com.pfms.presentation.dto.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói tỷ trọng, tổng số tiền và các danh mục con của một nhóm nhu cầu
 * cấp 2 (UC-REP-02).
 * Phục vụ kết xuất cấu trúc phân cấp cấp 2 trong bảng phân tích cơ cấu chi
 * tiêu/thu nhập.
 */
public final class NeedGroupShareResponseDTO {

    // region State
    private final UUID needGroupId;
    private final String name;
    private final String status;
    private final BigDecimal totalAmount;
    private final BigDecimal share;
    private final List<CategoryShareResponseDTO> categories;
    // endregion

    // region Constructors
    public NeedGroupShareResponseDTO(
            UUID needGroupId,
            String name,
            BigDecimal totalAmount,
            BigDecimal share,
            List<CategoryShareResponseDTO> categories) {

        this(needGroupId, name, "ACTIVE", totalAmount, share, categories);
    }

    public NeedGroupShareResponseDTO(
            UUID needGroupId,
            String name,
            String status,
            BigDecimal totalAmount,
            BigDecimal share,
            List<CategoryShareResponseDTO> categories) {

        this.needGroupId = Objects.requireNonNull(needGroupId, "Mã nhóm nhu cầu không được để null");
        this.name = (name == null) ? "" : name.trim();
        this.status = (status == null || status.trim().isEmpty())
                ? "ACTIVE"
                : status.trim().toUpperCase();
        this.totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        this.share = (share != null) ? share : BigDecimal.ZERO;
        this.categories = (categories == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(categories));
    }
    // endregion

    // region Getters
    public UUID getNeedGroupId() {
        return this.needGroupId;
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

    public List<CategoryShareResponseDTO> getCategories() {
        return this.categories;
    }

    public int getCategoryCount() {
        return this.categories.size();
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
        if (!(o instanceof NeedGroupShareResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.needGroupId, that.needGroupId)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.status, that.status)
                && (this.totalAmount.compareTo(that.totalAmount) == 0)
                && (this.share.compareTo(that.share) == 0)
                && Objects.equals(this.categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.needGroupId,
                this.name,
                this.status,
                this.totalAmount.stripTrailingZeros(),
                this.share.stripTrailingZeros(),
                this.categories);
    }

    @Override
    public String toString() {
        return "NeedGroupShareResponseDTO{" +
                "needGroupId=" + this.needGroupId +
                ", name='" + this.name + '\'' +
                ", status='" + this.status + '\'' +
                ", totalAmount=" + this.totalAmount +
                ", share=" + this.share +
                ", categoryCount=" + this.categories.size() +
                '}';
    }
    // endregion
}
