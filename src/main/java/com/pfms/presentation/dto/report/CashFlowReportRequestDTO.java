package com.pfms.presentation.dto.report;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO yêu cầu truy vấn báo cáo dòng tiền 12 tháng liên tục
 * Đóng gói mốc tháng và năm kết thúc kỳ thống kê.
 */
public final class CashFlowReportRequestDTO {

    // region State
    private final int endMonth;
    private final int endYear;
    // endregion

    // region Constructors
    public CashFlowReportRequestDTO() {
        LocalDate now = LocalDate.now();
        this.endMonth = now.getMonthValue();
        this.endYear = now.getYear();
    }

    public CashFlowReportRequestDTO(int endMonth, int endYear) {
        this.endMonth = endMonth;
        this.endYear = endYear;
    }
    // endregion

    // region Factory
    public static CashFlowReportRequestDTO now() {
        return new CashFlowReportRequestDTO();
    }
    // endregion

    // region Getters
    public int getEndMonth() {
        return this.endMonth;
    }

    public int getEndYear() {
        return this.endYear;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CashFlowReportRequestDTO that)) {
            return false;
        }
        return this.endMonth == that.endMonth
                && this.endYear == that.endYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.endMonth, this.endYear);
    }

    @Override
    public String toString() {
        return "CashFlowReportRequestDTO{" +
                "endMonth=" + this.endMonth +
                ", endYear=" + this.endYear +
                '}';
    }
    // endregion
}
