package com.pfms.presentation.dto.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO kết xuất bảng tổng hợp báo cáo dòng tiền 12 tháng liên tục.
 * Chứa danh sách dữ liệu từng tháng và các chỉ số tài chính bình quân năm.
 */
public final class CashFlowReportResponseDTO {

    // region State
    private final List<MonthlyCashFlowResponseDTO> months;
    private final BigDecimal avgIncome;
    private final BigDecimal avgExpense;
    private final BigDecimal avgNetCashFlow;
    // endregion

    // region Constructors
    public CashFlowReportResponseDTO(
            List<MonthlyCashFlowResponseDTO> months,
            BigDecimal avgIncome,
            BigDecimal avgExpense,
            BigDecimal avgNetCashFlow) {

        this.avgIncome = Objects.requireNonNull(avgIncome, "Thu nhập bình quân không được để null");
        this.avgExpense = Objects.requireNonNull(avgExpense, "Chi tiêu bình quân không được để null");
        this.avgNetCashFlow = (avgNetCashFlow != null) ? avgNetCashFlow : avgIncome.subtract(avgExpense);

        this.months = (months == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(months));
    }
    // endregion

    // region Factory
    public static CashFlowReportResponseDTO empty() {
        return new CashFlowReportResponseDTO(
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }
    // endregion

    // region Getters
    public List<MonthlyCashFlowResponseDTO> getMonths() {
        return this.months;
    }

    public int getMonthCount() {
        return this.months.size();
    }

    public BigDecimal getAvgIncome() {
        return this.avgIncome;
    }

    public BigDecimal getAvgExpense() {
        return this.avgExpense;
    }

    public BigDecimal getAvgNetCashFlow() {
        return this.avgNetCashFlow;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CashFlowReportResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.months, that.months)
                && (this.avgIncome.compareTo(that.avgIncome) == 0)
                && (this.avgExpense.compareTo(that.avgExpense) == 0)
                && (this.avgNetCashFlow.compareTo(that.avgNetCashFlow) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.months,
                this.avgIncome.stripTrailingZeros(),
                this.avgExpense.stripTrailingZeros(),
                this.avgNetCashFlow.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "CashFlowReportResponseDTO{" +
                "monthCount=" + this.months.size() +
                ", avgIncome=" + this.avgIncome +
                ", avgExpense=" + this.avgExpense +
                ", avgNetCashFlow=" + this.avgNetCashFlow +
                '}';
    }
    // endregion
}
