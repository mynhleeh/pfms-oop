package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.Wallet;

import java.util.Objects;
import java.util.UUID;

/**
 * Thực thể miền Chi phí giao dịch (TransactionFee) trong hệ thống PFMS.
 * Đại diện cho khoản phụ phí phát sinh phụ thuộc sinh tử kèm theo giao dịch thu chi hoặc chuyển tiền nội bộ.
 */
public class TransactionFee {

    // region State
    private final UUID feeId;
    private final UUID parentTransactionId;
    private Money amount;
    private Wallet wallet;
    private Category category;
    // endregion

    // region Constructors
    public TransactionFee(UUID feeId, UUID parentTransactionId, Money amount, Wallet wallet, Category category) {
        this.feeId = Objects.requireNonNull(feeId, "Mã chi phí không được để null");
        this.parentTransactionId = Objects.requireNonNull(parentTransactionId, "Mã giao dịch gốc không được để null");
        this.amount = validateAmount(amount);
        this.wallet = validateWallet(wallet);
        this.category = validateCategory(category);
    }
    // endregion

    // region Commands
    public void applyFee() {
        this.wallet.debit(this.amount);
    }

    public void revertFee() {
        this.wallet.credit(this.amount);
    }

    public void updateFee(Money newAmount, Category newCategory, Wallet newWallet) {
        this.amount = validateAmount(newAmount);
        this.category = validateCategory(newCategory);
        this.wallet = validateWallet(newWallet);
    }
    // endregion

    // region Queries
    public UUID getFeeId() {
        return this.feeId;
    }

    public UUID getParentTransactionId() {
        return this.parentTransactionId;
    }

    public Money getAmount() {
        return this.amount;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public Category getCategory() {
        return this.category;
    }
    // endregion

    // region Helpers
    private static Money validateAmount(Money amount) {
        if (amount == null || !amount.isGreaterThan(Money.ZERO)) {
            throw new BusinessException(ErrorCode.ERR_FEE_001, "Chi phí không hợp lệ.");
        }
        return amount;
    }

    private static Wallet validateWallet(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví thanh toán không được để null");
        if (!wallet.isActive()) {
            throw new BusinessException(ErrorCode.ERR_FEE_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
        return wallet;
    }

    private static Category validateCategory(Category category) {
        if (category == null) {
            throw new BusinessException(ErrorCode.ERR_FEE_002, "Vui lòng chọn danh mục cho phí giao dịch.");
        }
        if (!category.isActive() || category.getTransactionType() != TransactionType.EXPENSE) {
            throw new BusinessException(ErrorCode.ERR_FEE_003, "Danh mục phí không hợp lệ hoặc không tương thích với loại chi tiêu.");
        }
        return category;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionFee that = (TransactionFee) o;
        return Objects.equals(this.feeId, that.feeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.feeId);
    }

    @Override
    public String toString() {
        return "TransactionFee{" +
                "feeId=" + feeId +
                ", parentTransactionId=" + parentTransactionId +
                ", amount=" + amount +
                ", walletId=" + (wallet != null ? wallet.getId() : "null") +
                ", categoryId=" + (category != null ? category.getCategoryId() : "null") +
                '}';
    }
    // endregion
}

