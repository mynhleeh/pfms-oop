package com.pfms.presentation.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO yêu cầu cập nhật giao dịch thu chi gửi từ giao diện xuống tầng dịch vụ (UC-TXN-03).
 * Đóng gói mã giao dịch cùng các thông tin cập nhật (ví mới, số tiền, ngày, danh mục, ghi chú, phụ phí)
 * phục vụ quy trình hoàn nguyên và hạch toán số dư ví hai pha.
 * Giá trị {@code newFeeAmount == null} biểu thị giữ nguyên chi phí hiện tại,
 * trong khi {@code BigDecimal.ZERO} yêu cầu xóa bỏ chi phí liên kết theo {@code FR-TXN-04}.
 */
public final class UpdateTransactionRequestDTO {

    // region State
    private final UUID transactionId;
    private final UUID newWalletId;
    private final BigDecimal newAmount;
    private final LocalDate newDate;
    private final UUID newCategoryId;
    private final String newNote;
    private final BigDecimal newFeeAmount;
    private final UUID newFeeCategoryId;
    // endregion

    // region Constructors
    public UpdateTransactionRequestDTO(
            UUID transactionId, UUID newWalletId, BigDecimal newAmount, UUID newCategoryId) {
        this(transactionId, newWalletId, newAmount, LocalDate.now(), newCategoryId, "", null, null);
    }

    public UpdateTransactionRequestDTO(
            UUID transactionId,
            UUID newWalletId,
            BigDecimal newAmount,
            LocalDate newDate,
            UUID newCategoryId,
            String newNote) {

        this(transactionId, newWalletId, newAmount, newDate, newCategoryId, newNote, null, null);
    }

    public UpdateTransactionRequestDTO(
            UUID transactionId,
            UUID newWalletId,
            BigDecimal newAmount,
            LocalDate newDate,
            UUID newCategoryId,
            String newNote,
            BigDecimal newFeeAmount,
            UUID newFeeCategoryId) {

        this.transactionId = Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
        this.newWalletId = Objects.requireNonNull(newWalletId, "Mã ví mới không được để null");
        this.newAmount = Objects.requireNonNull(newAmount, "Số tiền giao dịch mới không được để null");
        this.newCategoryId = Objects.requireNonNull(newCategoryId, "Mã danh mục mới không được để null");

        this.newFeeAmount = newFeeAmount;
        this.newFeeCategoryId = (this.newFeeAmount != null && this.newFeeAmount.compareTo(BigDecimal.ZERO) > 0)
                ? newFeeCategoryId
                : null;
        this.newDate = (newDate == null) ? LocalDate.now() : newDate;
        this.newNote = (newNote == null) ? "" : newNote.trim();
    }
    // endregion

    // region Getters
    public UUID getTransactionId() {
        return this.transactionId;
    }

    public UUID getNewWalletId() {
        return this.newWalletId;
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
        return this.newFeeAmount != null && this.newFeeAmount.compareTo(BigDecimal.ZERO) > 0;
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
                && Objects.equals(this.newWalletId, that.newWalletId)
                && sameBigDecimal(this.newAmount, that.newAmount)
                && Objects.equals(this.newDate, that.newDate)
                && Objects.equals(this.newCategoryId, that.newCategoryId)
                && Objects.equals(this.newNote, that.newNote)
                && sameBigDecimal(this.newFeeAmount, that.newFeeAmount)
                && Objects.equals(this.newFeeCategoryId, that.newFeeCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionId,
                this.newWalletId,
                normalizeForHash(this.newAmount),
                this.newDate,
                this.newCategoryId,
                this.newNote,
                normalizeForHash(this.newFeeAmount),
                this.newFeeCategoryId
        );
    }

    @Override
    public String toString() {
        return "UpdateTransactionRequestDTO{" +
                "transactionId=" + this.transactionId +
                ", newWalletId=" + this.newWalletId +
                ", newAmount=" + this.newAmount +
                ", newDate=" + this.newDate +
                ", newCategoryId=" + this.newCategoryId +
                ", newNote='" + this.newNote + '\'' +
                ", newFeeAmount=" + this.newFeeAmount +
                ", newFeeCategoryId=" + this.newFeeCategoryId +
                '}';
    }

    private static boolean sameBigDecimal(BigDecimal left, BigDecimal right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.compareTo(right) == 0;
    }

    private static BigDecimal normalizeForHash(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return (value.compareTo(BigDecimal.ZERO) == 0)
                ? BigDecimal.ZERO
                : value.stripTrailingZeros();
    }
    // endregion
}
