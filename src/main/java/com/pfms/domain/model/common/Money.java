package com.pfms.domain.model.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

import com.pfms.common.util.Preconditions;

/**
 * Đối tượng giá trị bất biến đại diện cho số tiền và xử lý các phép toán tài
 * chính.
 * Đảm bảo tính toán chính xác và áp dụng quy tắc làm tròn ngân hàng.
 */
public final class Money implements Comparable<Money> {

    // region Constants
    public static final String DEFAULT_CURRENCY = "VNĐ";
    public static final int COMPUTATION_SCALE = 2;
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    public static final Money ZERO = new Money(
            BigDecimal.ZERO.setScale(COMPUTATION_SCALE, DEFAULT_ROUNDING),
            DEFAULT_CURRENCY);
    // endregion

    // region State
    private final BigDecimal amount;
    private final String currency;
    // endregion

    // region Constructors
    private Money(BigDecimal amount, String currency) {
        this.amount = amount.setScale(COMPUTATION_SCALE, DEFAULT_ROUNDING);
        this.currency = currency;
    }
    // endregion

    // region Factories
    public static Money of(String rawAmount) {
        String cleanAmount = Preconditions.requireNonBlank(rawAmount, "Số tiền không được rỗng");
        try {
            return new Money(new BigDecimal(cleanAmount), DEFAULT_CURRENCY);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền không đúng định dạng: " + rawAmount, ex);
        }
    }

    public static Money of(BigDecimal value) {
        Objects.requireNonNull(value, "Số tiền không được null");
        return new Money(value, DEFAULT_CURRENCY);
    }

    public static Money of(long rawAmount) {
        return new Money(BigDecimal.valueOf(rawAmount), DEFAULT_CURRENCY);
    }

    public static Money zero() {
        return ZERO;
    }
    // endregion

    // region Arithmetic
    public Money add(Money other) {
        Objects.requireNonNull(other, "Số tiền cộng thêm không được null");
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Số tiền trừ không được null");
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money abs() {
        return new Money(this.amount.abs(), this.currency);
    }

    public Money multiply(long factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Hệ số nhân không được null");
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public Money divide(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Không thể chia cho số không");
        }
        return new Money(this.amount.divide(BigDecimal.valueOf(divisor), COMPUTATION_SCALE, DEFAULT_ROUNDING),
                this.currency);
    }

    public Money divide(BigDecimal divisor) {
        Objects.requireNonNull(divisor, "Số chia không được null");
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Không thể chia cho số không");
        }
        return new Money(this.amount.divide(divisor, COMPUTATION_SCALE, DEFAULT_ROUNDING), this.currency);
    }
    // endregion

    // region Comparisons
    public Money requireNonNegative() {
        if (this.isNegative()) {
            throw new IllegalStateException("Số dư hoặc số tiền không được âm: " + this.amount);
        }
        return this;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Money other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        return this.compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        return this.compareTo(other) <= 0;
    }

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Không thể so sánh với đối tượng null");
        return this.amount.compareTo(other.amount);
    }
    // endregion

    // region Getters
    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getCurrency() {
        return this.currency;
    }
    // endregion

    // region Identity
    public String formatDisplay() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(this.amount.setScale(0, DEFAULT_ROUNDING)) + " " + this.currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Money other))
            return false;
        return this.amount.compareTo(other.amount) == 0 && Objects.equals(this.currency, other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.amount.stripTrailingZeros(), this.currency);
    }

    @Override
    public String toString() {
        return this.amount.toPlainString() + " " + this.currency;
    }
    // endregion
}
