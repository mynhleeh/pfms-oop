package com.pfms.presentation.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu đầu vào ghi nhận giao dịch thu nhập hoặc chi tiêu từ
 * giao diện gửi xuống tầng dịch vụ.
 * Có thể kèm phụ phí phát sinh và danh mục chi phí trích từ cùng ví
 * (UC-TXN-01).
 */
public final class RecordTransactionRequestDTO {

    // region State
    private final UUID walletId;
    private final UUID categoryId;
    private final BigDecimal amount;
    private final LocalDate transactionDate;
    private final String note;
    private final BigDecimal feeAmount;
    private final UUID feeCategoryId;
    // endregion

    // region Constructors
    public RecordTransactionRequestDTO(UUID walletId, UUID categoryId, BigDecimal amount) {
        this(walletId, categoryId, amount, LocalDate.now(), "", BigDecimal.ZERO, null);
    }

    public RecordTransactionRequestDTO(
            UUID walletId,
            UUID categoryId,
            BigDecimal amount,
            LocalDate transactionDate,
            String note) {

        this(walletId, categoryId, amount, transactionDate, note, BigDecimal.ZERO, null);
    }

    public RecordTransactionRequestDTO(
            UUID walletId,
            UUID categoryId,
            BigDecimal amount,
            LocalDate transactionDate,
            String note,
            BigDecimal feeAmount,
            UUID feeCategoryId) {

        this.walletId = Objects.requireNonNull(walletId, "Mã ví không được để null");
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.amount = Objects.requireNonNull(amount, "Số tiền giao dịch không được để null");

        this.feeAmount = (feeAmount == null) ? BigDecimal.ZERO : feeAmount;
        this.transactionDate = (transactionDate == null) ? LocalDate.now() : transactionDate;
        this.note = (note == null) ? "" : note.trim();
        this.feeCategoryId = (this.feeAmount.compareTo(BigDecimal.ZERO) > 0) ? feeCategoryId : null;
    }
    // endregion

    // region Getters
    public UUID getWalletId() {
        return this.walletId;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
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
        if (!(o instanceof RecordTransactionRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.walletId, that.walletId)
                && Objects.equals(this.categoryId, that.categoryId)
                && (this.amount.compareTo(that.amount) == 0)
                && Objects.equals(this.transactionDate, that.transactionDate)
                && Objects.equals(this.note, that.note)
                && (this.feeAmount.compareTo(that.feeAmount) == 0)
                && Objects.equals(this.feeCategoryId, that.feeCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.walletId,
                this.categoryId,
                this.amount.stripTrailingZeros(),
                this.transactionDate,
                this.note,
                this.feeAmount.stripTrailingZeros(),
                this.feeCategoryId);
    }

    @Override
    public String toString() {
        return "RecordTransactionRequestDTO{" +
                "walletId=" + this.walletId +
                ", categoryId=" + this.categoryId +
                ", amount=" + this.amount +
                ", transactionDate=" + this.transactionDate +
                ", note='" + this.note + '\'' +
                ", feeAmount=" + this.feeAmount +
                ", feeCategoryId=" + this.feeCategoryId +
                '}';
    }
    // endregion
}
