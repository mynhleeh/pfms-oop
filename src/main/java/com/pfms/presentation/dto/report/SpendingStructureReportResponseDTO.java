package com.pfms.presentation.dto.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO kết xuất bảng tổng hợp báo cáo cơ cấu thu chi.
 * Chứa thông tin kỳ phân tích, tổng giá trị và danh sách các nhóm nhu cầu cấp
 * 2.
 */
public final class SpendingStructureReportResponseDTO {

    // region State
    private final String transactionType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalAmount;
    private final BigDecimal previousTotalAmount;
    private final BigDecimal differenceAmount;
    private final BigDecimal percentageChange;
    private final boolean isNew;
    private final List<NeedGroupShareResponseDTO> groups;
    // endregion

    // region Constructors
    public SpendingStructureReportResponseDTO(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalAmount,
            List<NeedGroupShareResponseDTO> groups) {

        this(transactionType, startDate, endDate, totalAmount,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, groups);
    }

    public SpendingStructureReportResponseDTO(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalAmount,
            BigDecimal previousTotalAmount,
            BigDecimal differenceAmount,
            BigDecimal percentageChange,
            boolean isNew,
            List<NeedGroupShareResponseDTO> groups) {

        this.transactionType = (transactionType == null || transactionType.trim().isEmpty())
                ? "EXPENSE"
                : transactionType.trim().toUpperCase();

        LocalDate now = LocalDate.now();
        this.startDate = (startDate == null) ? now.withDayOfMonth(1) : startDate;
        this.endDate = (endDate == null) ? now : endDate;
        this.totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        this.previousTotalAmount = (previousTotalAmount != null) ? previousTotalAmount : BigDecimal.ZERO;
        this.differenceAmount = (differenceAmount != null) ? differenceAmount : BigDecimal.ZERO;
        this.percentageChange = (percentageChange != null) ? percentageChange : BigDecimal.ZERO;
        this.isNew = isNew;
        this.groups = (groups == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(groups));
    }
    // endregion

    // region Factory Methods
    public static SpendingStructureReportResponseDTO empty(
            String transactionType,
            LocalDate startDate,
            LocalDate endDate) {

        return new SpendingStructureReportResponseDTO(
                transactionType,
                startDate,
                endDate,
                BigDecimal.ZERO,
                Collections.emptyList());
    }
    // endregion

    // region Getters
    public String getTransactionType() {
        return this.transactionType;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
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

    public List<NeedGroupShareResponseDTO> getGroups() {
        return this.groups;
    }
    // endregion

    // region Queries
    public int getGroupCount() {
        return this.groups.size();
    }

    public boolean isExpense() {
        return "EXPENSE".equalsIgnoreCase(this.transactionType);
    }

    public boolean isIncome() {
        return "INCOME".equalsIgnoreCase(this.transactionType);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpendingStructureReportResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.transactionType, that.transactionType)
                && Objects.equals(this.startDate, that.startDate)
                && Objects.equals(this.endDate, that.endDate)
                && (this.totalAmount.compareTo(that.totalAmount) == 0)
                && (this.previousTotalAmount.compareTo(that.previousTotalAmount) == 0)
                && (this.differenceAmount.compareTo(that.differenceAmount) == 0)
                && (this.percentageChange.compareTo(that.percentageChange) == 0)
                && (this.isNew == that.isNew)
                && Objects.equals(this.groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionType,
                this.startDate,
                this.endDate,
                this.totalAmount.stripTrailingZeros(),
                this.previousTotalAmount.stripTrailingZeros(),
                this.differenceAmount.stripTrailingZeros(),
                this.percentageChange.stripTrailingZeros(),
                this.isNew,
                this.groups);
    }

    @Override
    public String toString() {
        return "SpendingStructureReportResponseDTO{" +
                "transactionType='" + this.transactionType + '\'' +
                ", startDate=" + this.startDate +
                ", endDate=" + this.endDate +
                ", totalAmount=" + this.totalAmount +
                ", isNew=" + this.isNew +
                ", groupCount=" + this.groups.size() +
                '}';
    }
    // endregion
}
