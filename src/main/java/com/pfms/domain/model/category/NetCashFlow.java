package com.pfms.domain.model.category;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Dòng tiền thuần trong hệ thống PFMS (Tổng thu nhập - Tổng chi tiêu).
 * Đo lường mức thặng dư (dương), thâm hụt (âm) hoặc cân bằng (zero) trong kỳ.
 */
public class NetCashFlow implements Comparable<NetCashFlow> {

    // region State
    private final BigDecimal amount;
    private final String currency;
    private static final String DEFAULT_CURRENCY = "VNĐ";
    // endregion

    // region Constructors
    public NetCashFlow(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Dữ liệu số tiền không được để null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = DEFAULT_CURRENCY;
    }
    // endregion

    // region Factories
    public static NetCashFlow of(BigDecimal totalIncome, BigDecimal totalExpense) {
        if (totalIncome == null || totalExpense == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Tổng thu nhập và tổng chi tiêu không được để null");
        }
        return new NetCashFlow(totalIncome.subtract(totalExpense));
    }
    // endregion

    // region Queries
    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public boolean isPositive() {
        return this.amount.signum() > 0;
    }

    public boolean isNegative() {
        return this.amount.signum() < 0;
    }

    public boolean isZero() {
        return this.amount.signum() == 0;
    }

    public String format() {
        String prefix = this.amount.signum() >= 0 ? "+" : "-";
        return prefix + this.amount.abs().toPlainString() + " " + this.currency;
    }
    // endregion

    // region Comparable & Value Object Contract
    @Override
    public int compareTo(NetCashFlow other) {
        Objects.requireNonNull(other, "Đối tượng NetCashFlow so sánh không được để null");
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NetCashFlow other)) {
            return false;
        }
        return this.amount.compareTo(other.amount) == 0
                && Objects.equals(this.currency, other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.amount.stripTrailingZeros(), this.currency);
    }

    @Override
    public String toString() {
        return String.format("NetCashFlow[amount=%s, currency=%s]", this.amount.toPlainString(), this.currency);
    }
    // endregion
}
