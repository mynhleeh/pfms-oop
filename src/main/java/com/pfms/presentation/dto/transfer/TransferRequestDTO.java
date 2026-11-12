package com.pfms.presentation.dto.transfer;

import com.pfms.common.util.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO chứa thông tin yêu cầu chuyển tiền giữa hai ví và phí giao dịch đi kèm
 */
public final class TransferRequestDTO {

    // region State
    private final UUID sourceWalletId;
    private final UUID destinationWalletId;
    private final BigDecimal amount;
    private final LocalDate transferDate;
    private final String note;
    private final BigDecimal feeAmount;
    private final UUID feeCategoryId;
    // endregion

    // region Constructors
    public TransferRequestDTO(UUID sourceWalletId, UUID destinationWalletId, BigDecimal amount) {
        this(sourceWalletId, destinationWalletId, amount, LocalDate.now(), "", BigDecimal.ZERO, null);
    }

    public TransferRequestDTO(
            UUID sourceWalletId,
            UUID destinationWalletId,
            BigDecimal amount,
            LocalDate transferDate,
            String note) {

        this(sourceWalletId, destinationWalletId, amount, transferDate, note, BigDecimal.ZERO, null);
    }

    public TransferRequestDTO(
            UUID sourceWalletId,
            UUID destinationWalletId,
            BigDecimal amount,
            LocalDate transferDate,
            String note,
            BigDecimal feeAmount,
            UUID feeCategoryId) {

        this.sourceWalletId = Objects.requireNonNull(sourceWalletId, "Mã ví nguồn không được để null");
        this.destinationWalletId = Objects.requireNonNull(destinationWalletId, "Mã ví đích không được để null");
        Preconditions.checkArgument(
                !this.sourceWalletId.equals(this.destinationWalletId),
                "Ví nguồn và ví đích phải khác nhau: " + this.sourceWalletId);

        Objects.requireNonNull(amount, "Số tiền chuyển không được để null");
        this.amount = Preconditions.requirePositive(amount, "Số tiền chuyển phải lớn hơn 0: " + amount);

        BigDecimal normalizedFee = (feeAmount == null) ? BigDecimal.ZERO : feeAmount;
        this.feeAmount = Preconditions.requireNonNegative(
                normalizedFee, "Mức phí chuyển tiền không được âm: " + normalizedFee);
        Preconditions.checkArgument(
                !(this.feeAmount.compareTo(BigDecimal.ZERO) > 0 && feeCategoryId == null),
                "Khi phát sinh phí chuyển tiền, danh mục phí không được để trống");

        this.transferDate = (transferDate == null) ? LocalDate.now() : transferDate;
        this.note = (note == null) ? "" : note.trim();
        this.feeCategoryId = (this.feeAmount.compareTo(BigDecimal.ZERO) > 0) ? feeCategoryId : null;
    }
    // endregion

    // region Getters
    public UUID getSourceWalletId() {
        return this.sourceWalletId;
    }

    public UUID getDestinationWalletId() {
        return this.destinationWalletId;
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

    public BigDecimal getFeeAmount() {
        return this.feeAmount;
    }

    public UUID getFeeCategoryId() {
        return this.feeCategoryId;
    }

    public boolean hasFee() {
        return this.feeAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.sourceWalletId, that.sourceWalletId)
                && Objects.equals(this.destinationWalletId, that.destinationWalletId)
                && (this.amount.compareTo(that.amount) == 0)
                && Objects.equals(this.transferDate, that.transferDate)
                && Objects.equals(this.note, that.note)
                && (this.feeAmount.compareTo(that.feeAmount) == 0)
                && Objects.equals(this.feeCategoryId, that.feeCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.sourceWalletId,
                this.destinationWalletId,
                this.amount.stripTrailingZeros(),
                this.transferDate,
                this.note,
                this.feeAmount.stripTrailingZeros(),
                this.feeCategoryId);
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "sourceWalletId=" + this.sourceWalletId +
                ", destinationWalletId=" + this.destinationWalletId +
                ", amount=" + this.amount +
                ", transferDate=" + this.transferDate +
                ", note='" + this.note + '\'' +
                ", feeAmount=" + this.feeAmount +
                ", feeCategoryId=" + this.feeCategoryId +
                '}';
    }
    // endregion
}
