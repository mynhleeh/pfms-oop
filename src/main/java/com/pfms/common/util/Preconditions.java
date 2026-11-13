package com.pfms.common.util;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tiện ích kiểm tra tiền điều kiện (Guard Clauses) cho tham số đầu vào.
 * Tự động ném {@link IllegalArgumentException} khi dữ liệu không hợp lệ.
 */
public final class Preconditions {

    // region Constructors
    private Preconditions() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Guards
    public static String requireNonBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return str.trim();
    }

    public static BigDecimal requirePositive(BigDecimal amount, String message) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return amount;
    }

    public static BigDecimal requireNonNegative(BigDecimal amount, String message) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(message);
        }
        return amount;
    }

    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được sau ngày kết thúc: " + startDate + " > " + endDate);
        }
    }
    // endregion
}

