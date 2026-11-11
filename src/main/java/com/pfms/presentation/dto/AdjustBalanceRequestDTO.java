package com.pfms.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu yêu cầu kiểm kê và điều chỉnh số dư thực tế từ giao diện gửi xuống tầng dịch vụ.
 */
public final class AdjustBalanceRequestDTO {

    // region State
    private final UUID walletId;
    private final BigDecimal actualBalance;
    private final String reason;
    private final LocalDate adjustedAt;
    // endregion

    // region Constructors
    public AdjustBalanceRequestDTO(UUID walletId, BigDecimal actualBalance) {
        this(walletId, actualBalance, "", LocalDate.now());
    }

    public AdjustBalanceRequestDTO(UUID walletId, BigDecimal actualBalance, String reason) {
        this(walletId, actualBalance, reason, LocalDate.now());
    }

    public AdjustBalanceRequestDTO(UUID walletId, BigDecimal actualBalance, String reason, LocalDate adjustedAt) {
        if (walletId == null) {
            throw new IllegalArgumentException("Mã ví kiểm kê không được để null");
        }
        if (actualBalance == null) {
            throw new IllegalArgumentException("Số dư thực tế quan sát được không được để null");
        }
        if (actualBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số dư thực tế quan sát được không được âm: " + actualBalance);
        }
        this.walletId = walletId;
        this.actualBalance = actualBalance;
        this.reason = (reason == null) ? "" : reason.trim();
        this.adjustedAt = (adjustedAt == null) ? LocalDate.now() : adjustedAt;
    }
    // endregion

    // region Getters
    public UUID getWalletId() {
        return this.walletId;
    }

    public BigDecimal getActualBalance() {
        return this.actualBalance;
    }

    public String getReason() {
        return this.reason;
    }

    public LocalDate getAdjustedAt() {
        return this.adjustedAt;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdjustBalanceRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.walletId, that.walletId)
                && (this.actualBalance.compareTo(that.actualBalance) == 0)
                && Objects.equals(this.reason, that.reason)
                && Objects.equals(this.adjustedAt, that.adjustedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.walletId,
                this.actualBalance.stripTrailingZeros(),
                this.reason,
                this.adjustedAt
        );
    }

    @Override
    public String toString() {
        return "AdjustBalanceRequestDTO{" +
                "walletId=" + this.walletId +
                ", actualBalance=" + this.actualBalance +
                ", reason='" + this.reason + '\'' +
                ", adjustedAt=" + this.adjustedAt +
                '}';
    }
    // endregion
}
