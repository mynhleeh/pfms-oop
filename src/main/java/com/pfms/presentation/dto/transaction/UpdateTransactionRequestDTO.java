package com.pfms.presentation.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO yêu cầu cập nhật giao dịch thu chi gửi từ giao diện xuống tầng dịch vụ (UC-TXN-03).
 * Đóng gói mã giao dịch cùng các thông tin cập nhật (số tiền, ngày, danh mục, ghi chú, phụ phí)
 * phục vụ quy trình hạch toán và hoàn nguyên số dư ví hai pha.
 */
public final class UpdateTransactionRequestDTO {

    // region State
    private final UUID transactionId;
    private final BigDecimal newAmount;
    private final LocalDate newDate;
    private final UUID newCategoryId;
    private final String newNote;
    private final BigDecimal newFeeAmount;
    private final UUID newFeeCategoryId;
    // endregion

    // region Constructors
    public UpdateTransactionRequestDTO(UUID transactionId, BigDecimal newAmount, UUID newCategoryId) {
        this(transactionId, newAmount, LocalDate.now(), newCategoryId, "", BigDecimal.ZERO, null);
    }

    public UpdateTransactionRequestDTO(
            UUID transactionId,
            BigDecimal newAmount,
            LocalDate newDate,
            UUID newCategoryId,
            String newNote) {

        this(transactionId, newAmount, newDate, newCategoryId, newNote, BigDecimal.ZERO, null);
    }

    public UpdateTransactionRequestDTO(
            UUID transactionId,
            BigDecimal newAmount,
            LocalDate newDate,
            UUID newCategoryId,
            String newNote,
            BigDecimal newFeeAmount,
            UUID newFeeCategoryId) {

        this.transactionId = Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
        this.newAmount = Objects.requireNonNull(newAmount, "Số tiền giao dịch mới không được để null");
        this.newCategoryId = Objects.requireNonNull(newCategoryId, "Mã danh mục mới không được để null");

        this.newFeeAmount = (newFeeAmount == null) ? BigDecimal.ZERO : newFeeAmount;
        this.newFeeCategoryId = (this.newFeeAmount.compareTo(BigDecimal.ZERO) > 0) ? newFeeCategoryId : null;
        this.newDate = (newDate == null) ? LocalDate.now() : newDate;
        this.newNote = (newNote == null) ? "" : newNote.trim();
    }
    // endregion

    // region Getters
    public UUID getTransactionId() {
        return this.transactionId;
    }

    public BigDecimal getNewAmount() {
        return this.newAmount;
    }

    public LocalDate getNewDate() {
        return this.newDate;
    }

    public UUID getNewCategoryId() {
        return this.newCategoryId;
    }

    public String getNewNote() {
        return this.newNote;
    }

    public BigDecimal getNewFeeAmount() {
        return this.newFeeAmount;
    }

    public UUID getNewFeeCategoryId() {
        return this.newFeeCategoryId;
    }

    public boolean hasFee() {
        return this.newFeeAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpdateTransactionRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.transactionId, that.transactionId)
                && (this.newAmount.compareTo(that.newAmount) == 0)
                && Objects.equals(this.newDate, that.newDate)
                && Objects.equals(this.newCategoryId, that.newCategoryId)
                && Objects.equals(this.newNote, that.newNote)
                && (this.newFeeAmount.compareTo(that.newFeeAmount) == 0)
                && Objects.equals(this.newFeeCategoryId, that.newFeeCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionId,
                this.newAmount.stripTrailingZeros(),
                this.newDate,
                this.newCategoryId,
                this.newNote,
                this.newFeeAmount.stripTrailingZeros(),
                this.newFeeCategoryId
        );
    }

    @Override
    public String toString() {
        return "UpdateTransactionRequestDTO{" +
                "transactionId=" + this.transactionId +
                ", newAmount=" + this.newAmount +
                ", newDate=" + this.newDate +
                ", newCategoryId=" + this.newCategoryId +
                ", newNote='" + this.newNote + '\'' +
                ", newFeeAmount=" + this.newFeeAmount +
                ", newFeeCategoryId=" + this.newFeeCategoryId +
                '}';
    }
    // endregion
}
