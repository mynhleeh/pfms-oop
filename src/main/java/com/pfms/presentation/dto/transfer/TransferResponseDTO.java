package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO chứa kết quả giao dịch chuyển tiền giữa hai ví kèm phí và số dư sau giao
 * dịch
 */
public final class TransferResponseDTO {

    // region State
    private final UUID transferId;
    private final UUID sourceWalletId;
    private final String sourceWalletName;
    private final UUID destinationWalletId;
    private final String destinationWalletName;
    private final BigDecimal amount;
    private final LocalDate transferDate;
    private final String note;
    private final BigDecimal feeAmount;
    private final UUID feeCategoryId;
    private final String feeCategoryName;
    private final BigDecimal sourceBalanceAfter;
    private final BigDecimal destinationBalanceAfter;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public TransferResponseDTO(
            UUID transferId,
            UUID sourceWalletId,
            String sourceWalletName,
            UUID destinationWalletId,
            String destinationWalletName,
            BigDecimal amount,
            LocalDate transferDate,
            String note,
            BigDecimal sourceBalanceAfter,
            BigDecimal destinationBalanceAfter,
            LocalDateTime createdAt) {

        this(transferId, sourceWalletId, sourceWalletName, destinationWalletId,
                destinationWalletName, amount, transferDate, note, BigDecimal.ZERO,
                null, "", sourceBalanceAfter, destinationBalanceAfter, createdAt);
    }

    public TransferResponseDTO(
            UUID transferId,
            UUID sourceWalletId,
            String sourceWalletName,
            UUID destinationWalletId,
            String destinationWalletName,
            BigDecimal amount,
            LocalDate transferDate,
            String note,
            BigDecimal feeAmount,
            UUID feeCategoryId,
            String feeCategoryName,
            BigDecimal sourceBalanceAfter,
            BigDecimal destinationBalanceAfter,
            LocalDateTime createdAt) {

        this.transferId = Objects.requireNonNull(transferId, "Mã giao dịch chuyển tiền không được để null");
        this.sourceWalletId = Objects.requireNonNull(sourceWalletId, "Mã ví nguồn không được để null");
        this.destinationWalletId = Objects.requireNonNull(destinationWalletId, "Mã ví đích không được để null");
        Preconditions.checkArgument(
                !this.sourceWalletId.equals(this.destinationWalletId),
                "Ví nguồn và ví đích phải khác nhau: " + this.sourceWalletId);

        this.sourceWalletName = Preconditions.requireNonBlank(
                sourceWalletName, "Tên ví nguồn không được để trống hoặc chỉ chứa khoảng trắng");
        this.destinationWalletName = Preconditions.requireNonBlank(
                destinationWalletName, "Tên ví đích không được để trống hoặc chỉ chứa khoảng trắng");
        this.amount = validateTransferAmount(amount);
        this.sourceBalanceAfter = validateBalanceAfter(sourceBalanceAfter, "Số dư ví nguồn sau chuyển");
        this.destinationBalanceAfter = validateBalanceAfter(destinationBalanceAfter, "Số dư ví đích sau chuyển");

        this.feeAmount = normalizeAndValidateFee(feeAmount);
        this.feeCategoryId = resolveFeeCategoryId(this.feeAmount, feeCategoryId);
        this.feeCategoryName = resolveFeeCategoryName(this.feeAmount, feeCategoryName);
        this.transferDate = (transferDate == null) ? LocalDate.now() : transferDate;
        this.note = (note == null) ? "" : note.trim();
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters
    public UUID getTransferId() {
        return this.transferId;
    }

    public UUID getId() {
        return this.transferId;
    }

    public UUID getSourceWalletId() {
        return this.sourceWalletId;
    }

    public String getSourceWalletName() {
        return this.sourceWalletName;
    }

    public UUID getDestinationWalletId() {
        return this.destinationWalletId;
    }

    public String getDestinationWalletName() {
        return this.destinationWalletName;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public LocalDate getTransferDate() {
        return this.transferDate;
    }

    public String getNote() {
        return this.note;
    }

    public boolean hasFee() {
        return this.feeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isHasFee() {
        return this.hasFee();
    }

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public UUID getFeeCategoryId() {
        return this.feeCategoryId;
    }

    public String getFeeCategoryName() {
        return this.feeCategoryName;
    }

    public BigDecimal getSourceBalanceAfter() {
        return this.sourceBalanceAfter;
    }

    public BigDecimal getDestinationBalanceAfter() {
        return this.destinationBalanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    // endregion

    // region Helpers
    private static BigDecimal validateTransferAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "Số tiền chuyển không được để null");
        return Preconditions.requirePositive(amount, "Số tiền chuyển phải lớn hơn 0: " + amount);
    }

    private static BigDecimal validateBalanceAfter(BigDecimal balance, String label) {
        Objects.requireNonNull(balance, label + " không được để null");
        return Preconditions.requireNonNegative(balance, label + " không được âm: " + balance);
    }

    private static BigDecimal normalizeAndValidateFee(BigDecimal feeAmount) {
        BigDecimal normalized = (feeAmount == null) ? BigDecimal.ZERO : feeAmount;
        return Preconditions.requireNonNegative(normalized, "Mức phí chuyển tiền không được âm: " + normalized);
    }

    private static UUID resolveFeeCategoryId(BigDecimal normalizedFee, UUID feeCategoryId) {
        if (normalizedFee.compareTo(BigDecimal.ZERO) > 0) {
            return Objects.requireNonNull(feeCategoryId, "Mã danh mục phí không được để null khi có phí");
        }
        return null;
    }

    private static String resolveFeeCategoryName(BigDecimal normalizedFee, String feeCategoryName) {
        if (normalizedFee.compareTo(BigDecimal.ZERO) > 0) {
            return Preconditions.requireNonBlank(feeCategoryName, "Tên danh mục phí không được để trống khi có phí");
        }
        return "";
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferResponseDTO that)) {
            return false;
        }
        return Objects.equals(this.transferId, that.transferId)
                && Objects.equals(this.sourceWalletId, that.sourceWalletId)
                && Objects.equals(this.sourceWalletName, that.sourceWalletName)
                && Objects.equals(this.destinationWalletId, that.destinationWalletId)
                && Objects.equals(this.destinationWalletName, that.destinationWalletName)
                && (this.amount.compareTo(that.amount) == 0)
                && Objects.equals(this.transferDate, that.transferDate)
                && Objects.equals(this.note, that.note)
                && (this.feeAmount.compareTo(that.feeAmount) == 0)
                && Objects.equals(this.feeCategoryId, that.feeCategoryId)
                && Objects.equals(this.feeCategoryName, that.feeCategoryName)
                && (this.sourceBalanceAfter.compareTo(that.sourceBalanceAfter) == 0)
                && (this.destinationBalanceAfter.compareTo(that.destinationBalanceAfter) == 0)
                && Objects.equals(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transferId,
                this.sourceWalletId,
                this.sourceWalletName,
                this.destinationWalletId,
                this.destinationWalletName,
                this.amount.stripTrailingZeros(),
                this.transferDate,
                this.note,
                this.feeAmount.stripTrailingZeros(),
                this.feeCategoryId,
                this.feeCategoryName,
                this.sourceBalanceAfter.stripTrailingZeros(),
                this.destinationBalanceAfter.stripTrailingZeros(),
                this.createdAt);
    }

    @Override
    public String toString() {
        return "TransferResponseDTO{" +
                "transferId=" + this.transferId +
                ", sourceWalletId=" + this.sourceWalletId +
                ", sourceWalletName='" + this.sourceWalletName + '\'' +
                ", destinationWalletId=" + this.destinationWalletId +
                ", destinationWalletName='" + this.destinationWalletName + '\'' +
                ", amount=" + this.amount +
                ", transferDate=" + this.transferDate +
                ", note='" + this.note + '\'' +
                ", feeAmount=" + this.feeAmount +
                ", feeCategoryId=" + this.feeCategoryId +
                ", feeCategoryName='" + this.feeCategoryName + '\'' +
                ", sourceBalanceAfter=" + this.sourceBalanceAfter +
                ", destinationBalanceAfter=" + this.destinationBalanceAfter +
                ", createdAt=" + this.createdAt +
                '}';
    }
    // endregion
}
