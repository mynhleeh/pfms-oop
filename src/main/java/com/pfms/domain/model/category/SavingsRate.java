package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Tỷ lệ tiết kiệm trong hệ thống PFMS: (Dòng tiền thuần / Tổng thu nhập) * 100%.
 * Đo lường hiệu quả tích lũy; tự động trả về 0.00% khi không có thu nhập.
 */
public class SavingsRate implements Comparable<SavingsRate> {

    // region State
    private final BigDecimal rate;
    private final String unit;
    private static final String DEFAULT_UNIT = "%";
    public static final SavingsRate ZERO = new SavingsRate(BigDecimal.ZERO);
    // endregion

    // region Constructors
    public SavingsRate(BigDecimal rate) {
        if (rate == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Tỷ lệ tiết kiệm không được để null");
        }
        this.rate = rate.setScale(2, RoundingMode.HALF_EVEN);
        this.unit = DEFAULT_UNIT;
    }
    // endregion

    // region Factories
    public static SavingsRate of(BigDecimal netCashFlow, BigDecimal totalIncome) {
        if (netCashFlow == null || totalIncome == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Dữ liệu tính tỷ lệ tiết kiệm không được để null");
        }
        if (totalIncome.signum() <= 0) {
            return ZERO;
        }

        BigDecimal calculatedRate = netCashFlow
                .divide(totalIncome, 4, RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_EVEN);

        return new SavingsRate(calculatedRate);
    }
    // endregion

    // region Queries
    public BigDecimal getRate() {
        return this.rate;
    }

    public String getUnit() {
        return this.unit;
    }

    public boolean isPositive() {
        return this.rate.signum() > 0;
    }

    public boolean isNegative() {
        return this.rate.signum() < 0;
    }

    public boolean isZero() {
        return this.rate.signum() == 0;
    }

    public String format() {
        return this.rate.toPlainString() + this.unit;
    }
    // endregion

    // region Comparable & Value Object Contract
    @Override
    public int compareTo(SavingsRate other) {
        Objects.requireNonNull(other, "Đối tượng SavingsRate so sánh không được để null");
        return this.rate.compareTo(other.rate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SavingsRate other)) {
            return false;
        }
        return this.rate.compareTo(other.rate) == 0
                && Objects.equals(this.unit, other.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.rate.stripTrailingZeros(), this.unit);
    }

    @Override
    public String toString() {
        return String.format("SavingsRate[rate=%s, unit=%s]", this.rate.toPlainString(), this.unit);
    }
    // endregion
}
