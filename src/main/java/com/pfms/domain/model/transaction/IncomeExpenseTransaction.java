package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;

import java.util.Objects;
import java.util.UUID;

/**
 * Giao dịch thu nhập hoặc chi tiêu tại một ví tiền trong hệ thống PFMS.
 * Hạch toán biến động số dư ví theo danh mục phân loại và xử lý phụ phí đi kèm
 * (nếu có).
 */
public class IncomeExpenseTransaction extends Transaction {

    // region State
    private final TransactionType transactionType;
    private Wallet wallet;
    private Category category;
    // endregion

    // region Constructors
    public IncomeExpenseTransaction(
            UUID transactionId, TransactionType transactionType,
            Wallet wallet, Category category,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee) {

        super(transactionId, amount, transactionDate, note, fee);
        this.transactionType = Objects.requireNonNull(transactionType, "Loại thu chi không được để null");
        this.wallet = validateWallet(wallet);
        this.category = validateCategory(category, transactionType);
        validateFeeWallet(fee, this.wallet);
    }
    // endregion

    // region Commands
    @Override
    public void applyToWallet() {
        validateWalletActive(this.wallet);

        if (this.transactionType == TransactionType.INCOME) {
            if (this.fee != null) {
                Money projectedBalance = this.wallet.getCurrentBalance().add(this.amount);
                if (!projectedBalance.isGreaterThanOrEqual(this.fee.getAmount())) {
                    throw new InsufficientBalanceException(this.wallet.getId(), projectedBalance, this.fee.getAmount());
                }
            }
            this.wallet.credit(this.amount);
            if (this.fee != null) {
                this.fee.applyFee();
            }
        } else if (this.transactionType == TransactionType.EXPENSE) {
            Money totalDebit = this.fee != null ? this.amount.add(this.fee.getAmount()) : this.amount;
            if (!this.wallet.hasSufficientBalance(totalDebit)) {
                throw new InsufficientBalanceException(this.wallet.getId(), this.wallet.getCurrentBalance(),
                        totalDebit);
            }
            this.wallet.debit(this.amount);
            if (this.fee != null) {
                this.fee.applyFee();
            }
        }
    }

    @Override
    public void revertFromWallet() {
        validateWalletActive(this.wallet);

        if (this.transactionType == TransactionType.INCOME) {
            Money available = this.fee != null
                    ? this.wallet.getCurrentBalance().add(this.fee.getAmount())
                    : this.wallet.getCurrentBalance();
            if (!available.isGreaterThanOrEqual(this.amount)) {
                throw new InsufficientBalanceException(this.wallet.getId(), this.wallet.getCurrentBalance(),
                        this.amount);
            }
            if (this.fee != null) {
                this.fee.revertFee();
            }
            this.wallet.debit(this.amount);
        } else if (this.transactionType == TransactionType.EXPENSE) {
            if (this.fee != null) {
                this.fee.revertFee();
            }
            this.wallet.credit(this.amount);
        }
    }

    public void updateDetails(
            Wallet newWallet, Category newCategory, Money newAmount,
            TransactionDate newDate, String newNote, TransactionFee newFee) {

        Wallet validWallet = validateWallet(newWallet);
        Category validCategory = validateCategory(newCategory, this.transactionType);
        Money validAmount = validateAmount(newAmount);
        TransactionDate validDate = Objects.requireNonNull(newDate, "Ngày giao dịch không được để null");
        Note validNote = Note.of(newNote, "Ghi chú giao dịch");
        TransactionFee validFee = validateFee(newFee, this.transactionId);
        validateFeeWallet(validFee, validWallet);

        Wallet oldWallet = this.wallet;
        Category oldCategory = this.category;
        Money oldAmount = this.amount;
        TransactionDate oldDate = this.transactionDate;
        Note oldNote = this.note;
        TransactionFee oldFee = this.fee;

        revertFromWallet();

        this.wallet = validWallet;
        this.category = validCategory;
        setAmount(validAmount);
        setTransactionDate(validDate);
        setNote(validNote);
        setFee(validFee);

        try {
            applyToWallet();
        } catch (Exception e) {
            this.wallet = oldWallet;
            this.category = oldCategory;
            setAmount(oldAmount);
            setTransactionDate(oldDate);
            setNote(oldNote);
            setFee(oldFee);
            applyToWallet();
            throw e;
        }
    }
    // endregion

    // region Queries
    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public Wallet getWallet() {
        return this.wallet;
    }

    public Category getCategory() {
        return this.category;
    }
    // endregion

    // region Helpers
    private static Wallet validateWallet(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví thanh toán không được để null");
        validateWalletActive(wallet);
        return wallet;
    }

    private static void validateWalletActive(Wallet wallet) {
        if (!wallet.isActive()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
    }

    private static Category validateCategory(Category category, TransactionType transactionType) {
        Objects.requireNonNull(category, "Danh mục không được để null");
        if (!category.isActive()) {
            throw new BusinessException(ErrorCode.ERR_CAT_001, "Danh mục không ở trạng thái hoạt động.");
        }
        if (category.getTransactionType() != transactionType) {
            throw new BusinessException(ErrorCode.ERR_CAT_001, "Loại danh mục không tương thích với loại giao dịch.");
        }
        return category;
    }

    private static void validateFeeWallet(TransactionFee fee, Wallet wallet) {
        if (fee != null && !fee.getWallet().equals(wallet)) {
            throw new BusinessException(ErrorCode.ERR_FEE_001, "Ví của chi phí phải trùng với ví của giao dịch.");
        }
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IncomeExpenseTransaction that = (IncomeExpenseTransaction) o;
        return Objects.equals(this.transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId);
    }

    @Override
    public String toString() {
        return String.format(
                "IncomeExpenseTransaction[id=%s, type=%s, walletId=%s, categoryId=%s, amount=%s, date=%s, fee=%s]",
                this.transactionId, this.transactionType, this.wallet.getId(),
                this.category.getCategoryId(), this.amount.formatDisplay(),
                this.transactionDate.getIsoString(), this.fee != null ? this.fee.getFeeId() : "none");
    }
    // endregion
}
