package com.pfms.domain.model.transaction;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.category.Category;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.Note;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Giao dịch thu nhập hoặc chi tiêu tại một ví tiền trong hệ thống PFMS.
 * Hạch toán biến động số dư ví theo danh mục phân loại và xử lý phụ phí đi kèm
 * (nếu có).
 */
public class IncomeExpenseTransaction extends Transaction {

    private static final String WALLET_NULL_MESSAGE = "Ví thanh toán không được để null";
    private static final String FEE_WALLET_MISMATCH_MESSAGE = "Ví của chi phí phải trùng với ví của giao dịch.";

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
        this(transactionId, transactionType, wallet, category, amount, transactionDate,
                note, fee, LocalDateTime.now(), true);
    }

    private IncomeExpenseTransaction(
            UUID transactionId, TransactionType transactionType,
            Wallet wallet, Category category,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee,
            LocalDateTime createdAt, boolean requireActiveReferences) {

        super(transactionId, amount, transactionDate, note, fee, createdAt);
        this.transactionType = Objects.requireNonNull(transactionType, "Loại thu chi không được để null");
        this.wallet = validateWallet(wallet, WALLET_NULL_MESSAGE, requireActiveReferences);
        this.category = validateCategory(category, transactionType, requireActiveReferences);
        validateFeeWallet(fee, this.wallet, FEE_WALLET_MISMATCH_MESSAGE);
    }

    public static IncomeExpenseTransaction reconstitute(
            UUID transactionId, TransactionType transactionType,
            Wallet wallet, Category category,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee,
            LocalDateTime createdAt) {
        return new IncomeExpenseTransaction(
                transactionId, transactionType, wallet, category,
                amount, transactionDate, note, fee, createdAt, false);
    }
    // endregion

    // region Commands
    @Override
    public void applyToWallet() {
        applyToWallet(this.wallet);
    }

    private void applyToWallet(Wallet targetWallet) {
        validateWalletActive(targetWallet);
        validateFeeWallet(this.fee, targetWallet, FEE_WALLET_MISMATCH_MESSAGE);

        if (this.transactionType == TransactionType.INCOME) {
            if (this.fee != null) {
                Money projectedBalance = targetWallet.getCurrentBalance().add(this.amount);
                if (!projectedBalance.isGreaterThanOrEqual(this.fee.getAmount())) {
                    throw new InsufficientBalanceException(
                            targetWallet.getId(), projectedBalance, this.fee.getAmount());
                }
            }
            targetWallet.credit(this.amount);
            if (this.fee != null) {
                targetWallet.debit(this.fee.getAmount());
            }
        } else if (this.transactionType == TransactionType.EXPENSE) {
            Money totalDebit = this.fee != null ? this.amount.add(this.fee.getAmount()) : this.amount;
            if (!targetWallet.hasSufficientBalance(totalDebit)) {
                throw new InsufficientBalanceException(
                        targetWallet.getId(), targetWallet.getCurrentBalance(), totalDebit);
            }
            targetWallet.debit(this.amount);
            if (this.fee != null) {
                targetWallet.debit(this.fee.getAmount());
            }
        }
    }

    @Override
    public void revertFromWallet() {
        revertFromWallet(this.wallet);
    }

    public void revertFromWallet(Wallet liveWallet) {
        validateLiveWalletForRevert(liveWallet);

        if (this.transactionType == TransactionType.INCOME) {
            Money available = this.fee != null
                    ? liveWallet.getCurrentBalance().add(this.fee.getAmount())
                    : liveWallet.getCurrentBalance();
            if (!available.isGreaterThanOrEqual(this.amount)) {
                throw new InsufficientBalanceException(liveWallet.getId(), liveWallet.getCurrentBalance(),
                        this.amount);
            }
            if (this.fee != null) {
                liveWallet.credit(this.fee.getAmount());
            }
            liveWallet.debit(this.amount);
        } else if (this.transactionType == TransactionType.EXPENSE) {
            if (this.fee != null) {
                liveWallet.credit(this.fee.getAmount());
            }
            liveWallet.credit(this.amount);
        }
    }

    public void updateDetails(
            Wallet newWallet, Category newCategory, Money newAmount,
            TransactionDate newDate, String newNote, TransactionFee newFee) {
        Wallet resolvedCurrentWallet = (newWallet != null && Objects.equals(this.wallet.getId(), newWallet.getId()))
                ? newWallet
                : this.wallet;
        updateDetails(resolvedCurrentWallet, newWallet, newCategory, newAmount, newDate, newNote, newFee);
    }

    public void updateDetails(
            Wallet liveCurrentWallet, Wallet newWallet, Category newCategory, Money newAmount,
            TransactionDate newDate, String newNote, TransactionFee newFee) {

        validateLiveWalletForRevert(liveCurrentWallet);
        Wallet validWallet = validateWallet(newWallet, WALLET_NULL_MESSAGE, true);
        Category validCategory = validateCategory(newCategory, this.transactionType, !isSameCategory(newCategory));
        Money validAmount = validateAmount(newAmount);
        TransactionDate validDate = Objects.requireNonNull(newDate, "Ngày giao dịch không được để null");
        Note validNote = Note.of(newNote, "Ghi chú giao dịch");
        TransactionFee validFee = validateFee(newFee, this.transactionId);
        validateFeeWallet(validFee, validWallet, FEE_WALLET_MISMATCH_MESSAGE);

        Wallet oldWallet = this.wallet;
        Category oldCategory = this.category;
        Money oldAmount = this.amount;
        TransactionDate oldDate = this.transactionDate;
        Note oldNote = this.note;
        TransactionFee oldFee = this.fee;

        revertFromWallet(liveCurrentWallet);
        assignState(validWallet, validCategory, validAmount, validDate, validNote, validFee);

        try {
            applyToWallet(validWallet);
        } catch (RuntimeException e) {
            assignState(oldWallet, oldCategory, oldAmount, oldDate, oldNote, oldFee);
            applyToWallet(liveCurrentWallet);
            throw e;
        }
    }

    public void restoreFromSnapshot(IncomeExpenseTransaction snapshot) {
        Objects.requireNonNull(snapshot, "Bản sao giao dịch không được để null");
        assignState(snapshot.wallet, snapshot.category, snapshot.amount,
                snapshot.transactionDate, snapshot.note, snapshot.fee);
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
    private static Category validateCategory(
            Category category, TransactionType transactionType, boolean requireActive) {
        Objects.requireNonNull(category, "Danh mục không được để null");
        if (requireActive && !category.isActive()) {
            throw new BusinessException(ErrorCode.ERR_CAT_001, "Danh mục không ở trạng thái hoạt động.");
        }
        if (category.getTransactionType() != transactionType) {
            throw new BusinessException(ErrorCode.ERR_CAT_001, "Loại danh mục không tương thích với loại giao dịch.");
        }
        return category;
    }

    private boolean isSameCategory(Category candidate) {
        return candidate != null && Objects.equals(this.category.getCategoryId(), candidate.getCategoryId());
    }

    private void assignState(
            Wallet targetWallet, Category targetCategory, Money targetAmount,
            TransactionDate targetDate, Note targetNote, TransactionFee targetFee) {
        this.wallet = targetWallet;
        this.category = targetCategory;
        setAmount(targetAmount);
        setTransactionDate(targetDate);
        setNote(targetNote);
        setFee(targetFee);
    }

    private void validateLiveWalletForRevert(Wallet liveWallet) {
        Objects.requireNonNull(liveWallet, WALLET_NULL_MESSAGE);
        Preconditions.checkArgument(
                Objects.equals(this.wallet.getId(), liveWallet.getId()),
                "Ví thanh toán không khớp với giao dịch.");
        validateWalletActive(liveWallet);
        validateFeeWallet(this.fee, liveWallet, FEE_WALLET_MISMATCH_MESSAGE);
    }
    // endregion

    // region Identity
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
