package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Lớp cơ sở trừu tượng đại diện cho mọi giao dịch biến động dòng tiền trong hệ thống PFMS.
 * Định nghĩa các thuộc tính cốt lõi chung và cặp phương thức hợp đồng đa hình applyToWallet/revertFromWallet.
 */
public abstract class Transaction {

    // region State
    protected final UUID transactionId;
    protected Money amount;
    protected TransactionDate transactionDate;
    protected String note;
    protected TransactionFee fee;
    protected final LocalDateTime createdAt;
    // endregion

    // region Constructors
    protected Transaction(UUID transactionId, Money amount, TransactionDate transactionDate,
                          String note, TransactionFee fee) {
        this.transactionId = Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
        this.amount = validateAmount(amount);
        this.transactionDate = Objects.requireNonNull(transactionDate, "Ngày giao dịch không được để null");
        this.note = normalizeNote(note);
        this.fee = validateFee(fee, transactionId);
        this.createdAt = LocalDateTime.now();
    }
    // endregion

    // region Commands
    public abstract void applyToWallet();

    public abstract void revertFromWallet();

    protected void setAmount(Money amount) {
        this.amount = validateAmount(amount);
    }

    protected void setTransactionDate(TransactionDate transactionDate) {
        this.transactionDate = Objects.requireNonNull(transactionDate, "Ngày giao dịch không được để null");
    }

    protected void setNote(String note) {
        this.note = normalizeNote(note);
    }

    protected void setFee(TransactionFee fee) {
        this.fee = validateFee(fee, this.transactionId);
    }
    // endregion

    // region Queries
    public UUID getTransactionId() {
        return this.transactionId;
    }

    public Money getAmount() {
        return this.amount;
    }

    public TransactionDate getTransactionDate() {
        return this.transactionDate;
    }

    public String getNote() {
        return this.note;
    }

    public Optional<TransactionFee> getFee() {
        return Optional.ofNullable(this.fee);
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    // endregion

    // region Helpers
    private static Money validateAmount(Money amount) {
        if (amount == null || !amount.isGreaterThan(Money.ZERO)) {
            throw new BusinessException(ErrorCode.ERR_FIN_001, "Số tiền giao dịch không hợp lệ.");
        }
        return amount;
    }

    private static String normalizeNote(String rawNote) {
        if (rawNote == null) {
            return "";
        }
        String trimmed = rawNote.trim();
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Ghi chú giao dịch không được vượt quá 255 ký tự.");
        }
        return trimmed;
    }

    private static TransactionFee validateFee(TransactionFee fee, UUID transactionId) {
        if (fee != null && !fee.getParentTransactionId().equals(transactionId)) {
            throw new BusinessException(ErrorCode.ERR_FEE_001, "Mã giao dịch gốc của phí không khớp với giao dịch.");
        }
        return fee;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(this.transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "transactionId=" + transactionId +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", feeId=" + (fee != null ? fee.getFeeId() : "none") +
                ", note='" + note + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    // endregion
}

