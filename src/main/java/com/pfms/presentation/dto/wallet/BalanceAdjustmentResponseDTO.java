package com.pfms.presentation.dto.wallet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.pfms.common.util.Preconditions;

/**
 * DTO đóng gói kết quả kiểm kê và điều chỉnh số dư trả về từ tầng dịch vụ lên
 * giao diện.
 */
public final class BalanceAdjustmentResponseDTO {

    // region State
    private final UUID recordId;
    private final UUID walletId;
    private final BigDecimal previousBalance;
    private final BigDecimal actualBalance;
    private final BigDecimal difference;
    private final String currency;
    private final String reason;
    private final LocalDate adjustedAt;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public BalanceAdjustmentResponseDTO(
            UUID recordId,
            UUID walletId,
            BigDecimal previousBalance,
            BigDecimal actualBalance,
            BigDecimal difference,
            String currency,
            String reason,
            LocalDate adjustedAt,
            LocalDateTime createdAt) {

        this.recordId = Objects.requireNonNull(recordId, "Mã bản ghi kiểm kê không được để null");
        this.walletId = Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        Objects.requireNonNull(previousBalance, "Số dư trước kiểm kê không được để null");
        this.previousBalance = Preconditions.requireNonNegative(previousBalance,
                "Số dư trước kiểm kê không được âm: " + previousBalance);
        Objects.requireNonNull(actualBalance, "Số dư thực tế sau kiểm kê không được để null");
        this.actualBalance = Preconditions.requireNonNegative(actualBalance,
                "Số dư thực tế sau kiểm kê không được âm: " + actualBalance);
        this.difference = (difference != null) ? difference : actualBalance.subtract(previousBalance);
        this.currency = (currency == null || currency.trim().isEmpty()) ? "VND" : currency.trim();
        this.reason = (reason == null) ? "" : reason.trim();
        this.adjustedAt = (adjustedAt == null) ? LocalDate.now() : adjustedAt;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters
    public UUID getRecordId() {
        return this.recordId;
    }

    public UUID getWalletId() {
        return this.walletId;
    }

    public BigDecimal getPreviousBalance() {
        return this.previousBalance;
    }

    public BigDecimal getActualBalance() {
        return this.actualBalance;
    }

    public BigDecimal getDifference() {
        return this.difference;
    }

    public String getCurrency() {
        return this.currency;
    }

    public String getReason() {
        return this.reason;
    }

    public LocalDate getAdjustedAt() {
        return this.adjustedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    // endregion

    // region Queries
    public boolean isSurplus() {
        return this.difference.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isDeficit() {
        return this.difference.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isBalanced() {
        return this.difference.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getAbsoluteDifference() {
        return this.difference.abs();
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceAdjustmentResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.recordId, that.recordId)
                && Objects.equals(this.walletId, that.walletId)
                && (this.previousBalance.compareTo(that.previousBalance) == 0)
                && (this.actualBalance.compareTo(that.actualBalance) == 0)
                && (this.difference.compareTo(that.difference) == 0)
                && Objects.equals(this.currency, that.currency)
                && Objects.equals(this.reason, that.reason)
                && Objects.equals(this.adjustedAt, that.adjustedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.recordId,
                this.walletId,
                this.previousBalance.stripTrailingZeros(),
                this.actualBalance.stripTrailingZeros(),
                this.difference.stripTrailingZeros(),
                this.currency,
                this.reason,
                this.adjustedAt);
    }

    @Override
    public String toString() {
        return "BalanceAdjustmentResponseDTO{" +
                "recordId=" + this.recordId +
                ", walletId=" + this.walletId +
                ", previousBalance=" + this.previousBalance +
                ", actualBalance=" + this.actualBalance +
                ", difference=" + this.difference +
                ", currency='" + this.currency + '\'' +
                ", reason='" + this.reason + '\'' +
                ", adjustedAt=" + this.adjustedAt +
                ", createdAt=" + this.createdAt +
                '}';
    }
    // endregion
}
