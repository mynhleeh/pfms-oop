package com.pfms.presentation.dto.report;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO đóng gói dữ liệu dòng tiền và tỷ lệ tiết kiệm của một tháng
 * Phục vụ kết xuất từng dòng trong bảng báo cáo chuỗi dòng tiền 12 tháng.
 */
public final class MonthlyCashFlowResponseDTO {

    // region State
    private final String yearMonth;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal netCashFlow;
    private final BigDecimal savingsRate;
    // endregion

    // region Constructors
    public MonthlyCashFlowResponseDTO(String yearMonth) {
        this(yearMonth, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public MonthlyCashFlowResponseDTO(
            String yearMonth,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal netCashFlow,
            BigDecimal savingsRate) {

        this.yearMonth = Objects.requireNonNull(yearMonth, "Tháng năm báo cáo không được để null").trim();
        this.totalIncome = Objects.requireNonNull(totalIncome, "Tổng thu nhập không được để null");
        this.totalExpense = Objects.requireNonNull(totalExpense, "Tổng chi tiêu không được để null");
        this.netCashFlow = (netCashFlow != null) ? netCashFlow : totalIncome.subtract(totalExpense);
        this.savingsRate = (savingsRate != null) ? savingsRate : BigDecimal.ZERO;
    }
    // endregion

    // region Factory
    public static MonthlyCashFlowResponseDTO empty(String yearMonth) {
        return new MonthlyCashFlowResponseDTO(yearMonth);
    }
    // endregion

    // region Getters
    public String getYearMonth() {
        return this.yearMonth;
    }

    public BigDecimal getTotalIncome() {
        return this.totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return this.totalExpense;
    }

    public BigDecimal getNetCashFlow() {
        return this.netCashFlow;
    }

    public BigDecimal getSavingsRate() {
        return this.savingsRate;
    }

    public boolean isSurplus() {
        return this.netCashFlow.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDeficit() {
        return this.netCashFlow.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isBalanced() {
        return this.netCashFlow.compareTo(BigDecimal.ZERO) == 0;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MonthlyCashFlowResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.yearMonth, that.yearMonth)
                && (this.totalIncome.compareTo(that.totalIncome) == 0)
                && (this.totalExpense.compareTo(that.totalExpense) == 0)
                && (this.netCashFlow.compareTo(that.netCashFlow) == 0)
                && (this.savingsRate.compareTo(that.savingsRate) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.yearMonth,
                this.totalIncome.stripTrailingZeros(),
                this.totalExpense.stripTrailingZeros(),
                this.netCashFlow.stripTrailingZeros(),
                this.savingsRate.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "MonthlyCashFlowResponseDTO{" +
                "yearMonth='" + this.yearMonth + '\'' +
                ", totalIncome=" + this.totalIncome +
                ", totalExpense=" + this.totalExpense +
                ", netCashFlow=" + this.netCashFlow +
                ", savingsRate=" + this.savingsRate +
                '}';
    }
    // endregion
}
