package com.pfms.domain.model.common;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Đối tượng giá trị bất biến đại diện cho ngày phát sinh giao dịch.
 * Đảm bảo ngày giao dịch hợp lệ và không vượt quá ngày hiện tại.
 */
public final class TransactionDate implements Comparable<TransactionDate> {

    // region Constants
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    // endregion

    // region State
    private final LocalDate date;
    private final String isoString;
    // endregion

    // region Constructors
    public TransactionDate(LocalDate date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Ngày giao dịch không được để trống (null).");
        }
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new BusinessException(
                    ErrorCode.ERR_FIN_001,
                    "Ngày giao dịch không được vượt quá ngày hiện tại: " + date + " > " + today);
        }
        this.date = date;
        this.isoString = date.format(ISO_FORMATTER);
    }
    // endregion

    // region Factories
    public static TransactionDate of(int year, int month, int day) {
        try {
            LocalDate parsedDate = LocalDate.of(year, month, day);
            return new TransactionDate(parsedDate);
        } catch (DateTimeException ex) {
            throw new BusinessException(
                    ErrorCode.ERR_FIN_001,
                    "Ngày không hợp lệ trên lịch Gregory: " + year + "-" + month + "-" + day);
        }
    }

    public static TransactionDate of(LocalDate date) {
        return new TransactionDate(date);
    }

    public static TransactionDate now() {
        return new TransactionDate(LocalDate.now());
    }

    public static TransactionDate parse(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Chuỗi ngày giao dịch không được để trống.");
        }
        try {
            LocalDate parsedDate = LocalDate.parse(rawDate.trim(), ISO_FORMATTER);
            return new TransactionDate(parsedDate);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(
                    ErrorCode.ERR_FIN_001,
                    "Chuỗi ngày không đúng định dạng chuẩn ISO-8601 (YYYY-MM-DD): " + rawDate);
        }
    }
    // endregion

    // region Getters
    public LocalDate getDate() {
        return this.date;
    }

    public String getIsoString() {
        return this.isoString;
    }

    public String getYearMonth() {
        return this.date.format(YEAR_MONTH_FORMATTER);
    }
    // endregion

    // region Comparison Methods
    public boolean isBefore(TransactionDate other) {
        if (other == null) {
            throw new NullPointerException("Đối tượng ngày so sánh không được null");
        }
        return this.date.isBefore(other.date);
    }

    public boolean isAfter(TransactionDate other) {
        if (other == null) {
            throw new NullPointerException("Đối tượng ngày so sánh không được null");
        }
        return this.date.isAfter(other.date);
    }

    public boolean isEqual(TransactionDate other) {
        if (other == null) {
            return false;
        }
        return this.date.isEqual(other.date);
    }

    @Override
    public int compareTo(TransactionDate other) {
        if (other == null) {
            throw new NullPointerException("Đối tượng TransactionDate so sánh không được null");
        }
        return this.date.compareTo(other.date);
    }
    // endregion

    // region Object Overrides
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TransactionDate other)) {
            return false;
        }
        return this.date.equals(other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date);
    }

    @Override
    public String toString() {
        return this.isoString;
    }
    // endregion
}
