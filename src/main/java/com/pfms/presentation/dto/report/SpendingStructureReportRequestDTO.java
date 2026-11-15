package com.pfms.presentation.dto.report;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

/**
 * DTO yêu cầu phân tích cơ cấu thu chi 2 cấp theo danh mục
 * Đóng gói khoảng ngày thống kê và loại luồng tiền (INCOME hoặc EXPENSE).
 */
public final class SpendingStructureReportRequestDTO {

    // region Constants
    public static final String DEFAULT_TRANSACTION_TYPE = "EXPENSE";
    // endregion

    // region State
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String transactionType;
    // endregion

    // region Constructors
    public SpendingStructureReportRequestDTO() {
        LocalDate now = LocalDate.now();
        this.startDate = now.withDayOfMonth(1);
        this.endDate = now;
        this.transactionType = DEFAULT_TRANSACTION_TYPE;
    }

    public SpendingStructureReportRequestDTO(LocalDate startDate, LocalDate endDate) {
        this(startDate, endDate, DEFAULT_TRANSACTION_TYPE);
    }

    public SpendingStructureReportRequestDTO(LocalDate startDate, LocalDate endDate, String transactionType) {
        this.startDate = startDate;
        this.endDate = endDate;
        if (transactionType == null || transactionType.trim().isEmpty()) {
            this.transactionType = DEFAULT_TRANSACTION_TYPE;
        } else {
            this.transactionType = transactionType.trim().toUpperCase(Locale.ROOT);
        }
    }
    // endregion

    // region Getters
    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public boolean isExpense() {
        return DEFAULT_TRANSACTION_TYPE.equalsIgnoreCase(this.transactionType);
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
        if (!(o instanceof SpendingStructureReportRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.startDate, that.startDate)
                && Objects.equals(this.endDate, that.endDate)
                && Objects.equals(this.transactionType, that.transactionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.endDate, this.transactionType);
    }

    @Override
    public String toString() {
        return "SpendingStructureReportRequestDTO{" +
                "startDate=" + this.startDate +
                ", endDate=" + this.endDate +
                ", transactionType='" + this.transactionType + '\'' +
                '}';
    }
    // endregion
}

