package com.pfms.domain.model.wallet;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Thực thể miền Bản ghi Điều chỉnh Số dư (BalanceAdjustmentRecord).
 * Đại diện sự kiện kiểm kê đối soát đưa số dư khả dụng về khớp thực tế quan sát được.
 */
public class BalanceAdjustmentRecord {

    // region State
    private final UUID recordId;
    private final UUID walletId;
    private final Money previousBalance;
    private final Money actualBalance;
    private final Money difference;
    private final TransactionDate adjustedAt;
    private final String reason;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public BalanceAdjustmentRecord(
            UUID recordId, UUID walletId, Money previousBalance,
            Money actualBalance, TransactionDate adjustedAt, String reason) {
        this(recordId, walletId, previousBalance, actualBalance, adjustedAt, reason, LocalDateTime.now());
    }

    public BalanceAdjustmentRecord(
            UUID recordId, String walletId, Money previousBalance,
            Money actualBalance, TransactionDate adjustedAt, String reason) {
        this(recordId, parseWalletId(walletId), previousBalance, actualBalance, adjustedAt, reason,
                LocalDateTime.now());
    }

    public BalanceAdjustmentRecord(
            UUID recordId, UUID walletId, Money previousBalance,
            Money actualBalance, TransactionDate adjustedAt, String reason, LocalDateTime createdAt) {
        this(recordId, walletId, previousBalance, actualBalance, adjustedAt, reason, createdAt, false);
    }

    private BalanceAdjustmentRecord(
            UUID recordId, UUID walletId, Money previousBalance,
            Money actualBalance, TransactionDate adjustedAt, String reason,
            LocalDateTime createdAt, boolean reconstituting) {

        this.recordId = Objects.requireNonNull(recordId, "Mã bản ghi kiểm kê không được để null");
        this.walletId = Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        if (reconstituting) {
            this.previousBalance = Objects.requireNonNull(previousBalance, "Số dư trước kiểm kê không được để null");
            this.actualBalance = Objects.requireNonNull(actualBalance, "Số dư thực tế không được để null");
            this.difference = this.actualBalance.subtract(this.previousBalance);
        } else {
            this.previousBalance = validatePreviousBalance(previousBalance);
            this.actualBalance = validateActualBalance(actualBalance);
            this.difference = calculateAndValidateDifference(this.actualBalance, this.previousBalance);
        }
        this.adjustedAt = Objects.requireNonNull(adjustedAt, "Mốc ngày kiểm kê không được để null");
        this.reason = normalizeReason(reason);
        this.createdAt = Objects.requireNonNull(createdAt, "Thời điểm tạo bản ghi kiểm kê không được để null");
    }

    public static BalanceAdjustmentRecord reconstitute(
            UUID recordId, UUID walletId, Money previousBalance,
            Money actualBalance, TransactionDate adjustedAt, String reason, LocalDateTime createdAt) {

        return new BalanceAdjustmentRecord(
                recordId, walletId, previousBalance, actualBalance, adjustedAt, reason, createdAt, true);
    }
    // endregion

    // region Queries
    public UUID getRecordId() {
        return this.recordId;
    }

    public UUID getWalletId() {
        return this.walletId;
    }

    public Money getPreviousBalance() {
        return this.previousBalance;
    }

    public Money getActualBalance() {
        return this.actualBalance;
    }

    public Money getDifference() {
        return this.difference;
    }

    public Money getAbsoluteDifference() {
        return this.difference.abs();
    }

    public TransactionDate getAdjustedAt() {
        return this.adjustedAt;
    }

    public String getReason() {
        return this.reason;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public boolean isSurplus() {
        return this.difference.isPositive();
    }
    // endregion

    // region Helpers
    private static UUID parseWalletId(String walletId) {
        Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        String trimmed = Preconditions.requireNonBlank(walletId, "Mã ví kiểm kê không được để trống");
        try {
            return UUID.fromString(trimmed);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Mã ví kiểm kê không đúng định dạng UUID: " + walletId, ex);
        }
    }

    private static Money validatePreviousBalance(Money previousBalance) {
        Objects.requireNonNull(previousBalance, "Số dư trước kiểm kê không được để null");
        if (previousBalance.isNegative()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001,
                    "Số dư trước điều chỉnh không được nhỏ hơn 0.");
        }
        return previousBalance;
    }

    private static Money validateActualBalance(Money actualBalance) {
        Objects.requireNonNull(actualBalance, "Số dư thực tế không được để null");
        if (actualBalance.isNegative()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001,
                    "Số dư thực tế quan sát không được nhỏ hơn 0.");
        }
        return actualBalance;
    }

    private static Money calculateAndValidateDifference(Money actualBalance, Money previousBalance) {
        Money diff = actualBalance.subtract(previousBalance);
        if (diff.isZero()) {
            throw new BusinessException(ErrorCode.ERR_FIN_001,
                    "Số dư quan sát trùng với số dư hệ thống, không có chênh lệch cần điều chỉnh.");
        }
        return diff;
    }

    private static String normalizeReason(String rawReason) {
        if (rawReason == null) {
            return "";
        }
        String trimmed = rawReason.trim();
        Preconditions.checkArgument(trimmed.length() <= 255, "Lý do điều chỉnh không được vượt quá 255 ký tự.");
        return trimmed;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BalanceAdjustmentRecord that = (BalanceAdjustmentRecord) o;
        return Objects.equals(this.recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.recordId);
    }

    @Override
    public String toString() {
        return String.format(
                "BalanceAdjustmentRecord[recordId=%s, walletId=%s, actualBalance=%s, difference=%s, adjustedAt=%s]",
                this.recordId, this.walletId, this.actualBalance.formatDisplay(),
                this.difference.formatDisplay(), this.adjustedAt.getIsoString());
    }
    // endregion
}
