package com.pfms.presentation.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO kết xuất dữ liệu chi tiết của một giao dịch thu chi trả về cho tầng giao diện.
 * Chứa đầy đủ thông tin giao dịch, số dư ví sau biến động và chi tiết phụ phí liên kết.
 */
public final class TransactionResponseDTO {

    // region State
    private final UUID transactionId;
    private final UUID walletId;
    private final String walletName;
    private final UUID categoryId;
    private final String categoryName;
    private final BigDecimal amount;
    private final String transactionType;
    private final LocalDate transactionDate;
    private final String note;
    private final boolean hasFee;
    private final BigDecimal feeAmount;
    private final UUID feeCategoryId;
    private final String feeCategoryName;
    private final BigDecimal walletBalanceAfter;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public TransactionResponseDTO(
            UUID transactionId,
            UUID walletId,
            String walletName,
            UUID categoryId,
            String categoryName,
            BigDecimal amount,
            String transactionType,
            LocalDate transactionDate,
            String note,
            BigDecimal walletBalanceAfter,
            LocalDateTime createdAt) {

        this(transactionId, walletId, walletName, categoryId, categoryName, amount,
                transactionType, transactionDate, note, false, BigDecimal.ZERO,
                null, "", walletBalanceAfter, createdAt);
    }

    public TransactionResponseDTO(
            UUID transactionId,
            UUID walletId,
            String walletName,
            UUID categoryId,
            String categoryName,
            BigDecimal amount,
            String transactionType,
            LocalDate transactionDate,
            String note,
            boolean hasFee,
            BigDecimal feeAmount,
            UUID feeCategoryId,
            String feeCategoryName,
            BigDecimal walletBalanceAfter,
            LocalDateTime createdAt) {

        this.transactionId = Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
        this.walletId = Objects.requireNonNull(walletId, "Mã ví không được để null");
        this.walletName = (walletName == null) ? null : walletName.trim();
        this.categoryId = Objects.requireNonNull(categoryId, "Mã danh mục không được để null");
        this.categoryName = (categoryName == null) ? null : categoryName.trim();
        this.amount = Objects.requireNonNull(amount, "Số tiền giao dịch không được để null");
        this.transactionType = Objects.requireNonNull(transactionType, "Loại giao dịch không được để null")
                .trim()
                .toUpperCase();
        this.walletBalanceAfter = Objects.requireNonNull(walletBalanceAfter, "Số dư ví sau giao dịch không được để null");

        BigDecimal normalizedFee = (feeAmount == null) ? BigDecimal.ZERO : feeAmount;
        if (normalizedFee.compareTo(BigDecimal.ZERO) > 0) {
            this.hasFee = true;
            this.feeAmount = normalizedFee;
            this.feeCategoryId = feeCategoryId;
            this.feeCategoryName = (feeCategoryName == null) ? "" : feeCategoryName.trim();
        } else {
            this.hasFee = false;
            this.feeAmount = BigDecimal.ZERO;
            this.feeCategoryId = null;
            this.feeCategoryName = "";
        }

        this.transactionDate = (transactionDate == null) ? LocalDate.now() : transactionDate;
        this.note = (note == null) ? "" : note.trim();
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters
    public UUID getTransactionId() {
        return this.transactionId;
    }

    public UUID getWalletId() {
        return this.walletId;
    }

    public String getWalletName() {
        return this.walletName;
    }

    public UUID getCategoryId() {
        return this.categoryId;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public String getNote() {
        return this.note;
    }

    public boolean hasFee() {
        return this.hasFee;
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

    public BigDecimal getWalletBalanceAfter() {
        return this.walletBalanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionResponseDTO that)) {
            return false;
        }
        return this.hasFee == that.hasFee
                && Objects.equals(this.transactionId, that.transactionId)
                && Objects.equals(this.walletId, that.walletId)
                && Objects.equals(this.walletName, that.walletName)
                && Objects.equals(this.categoryId, that.categoryId)
                && Objects.equals(this.categoryName, that.categoryName)
                && (this.amount.compareTo(that.amount) == 0)
                && Objects.equals(this.transactionType, that.transactionType)
                && Objects.equals(this.transactionDate, that.transactionDate)
                && Objects.equals(this.note, that.note)
                && (this.feeAmount.compareTo(that.feeAmount) == 0)
                && Objects.equals(this.feeCategoryId, that.feeCategoryId)
                && Objects.equals(this.feeCategoryName, that.feeCategoryName)
                && (this.walletBalanceAfter.compareTo(that.walletBalanceAfter) == 0)
                && Objects.equals(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionId,
                this.walletId,
                this.walletName,
                this.categoryId,
                this.categoryName,
                this.amount.stripTrailingZeros(),
                this.transactionType,
                this.transactionDate,
                this.note,
                this.hasFee,
                this.feeAmount.stripTrailingZeros(),
                this.feeCategoryId,
                this.feeCategoryName,
                this.walletBalanceAfter.stripTrailingZeros(),
                this.createdAt
        );
    }

    @Override
    public String toString() {
        return "TransactionResponseDTO{" +
                "transactionId=" + this.transactionId +
                ", walletId=" + this.walletId +
                ", walletName='" + this.walletName + '\'' +
                ", categoryId=" + this.categoryId +
                ", categoryName='" + this.categoryName + '\'' +
                ", amount=" + this.amount +
                ", transactionType='" + this.transactionType + '\'' +
                ", transactionDate=" + this.transactionDate +
                ", note='" + this.note + '\'' +
                ", hasFee=" + this.hasFee +
                ", feeAmount=" + this.feeAmount +
                ", feeCategoryId=" + this.feeCategoryId +
                ", feeCategoryName='" + this.feeCategoryName + '\'' +
                ", walletBalanceAfter=" + this.walletBalanceAfter +
                ", createdAt=" + this.createdAt +
                '}';
    }
    // endregion
}

