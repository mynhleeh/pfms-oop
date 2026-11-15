package com.pfms.domain.model.transaction;

import com.pfms.common.util.Preconditions;
import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Giao dịch chuyển tiền nội bộ giữa hai ví trong hệ thống PFMS.
 * Trích nợ ví nguồn, ghi có ví đích và xử lý phụ phí chuyển tiền đi kèm (nếu
 * có).
 */
public class TransferTransaction extends Transaction {

    private static final String FEE_WALLET_MISMATCH_MESSAGE = "Ví của chi phí phải trùng với ví nguồn chuyển tiền.";

    // region State
    private final Wallet sourceWallet;
    private final Wallet destinationWallet;
    // endregion

    // region Constructors
    public TransferTransaction(
            UUID transactionId, Wallet sourceWallet, Wallet destinationWallet,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee) {
        this(transactionId, sourceWallet, destinationWallet, amount, transactionDate,
                note, fee, LocalDateTime.now(), true);
    }

    private TransferTransaction(
            UUID transactionId, Wallet sourceWallet, Wallet destinationWallet,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee,
            LocalDateTime createdAt, boolean requireActiveReferences) {

        super(transactionId, validateTransferAmount(amount), transactionDate, note, fee, createdAt);
        this.sourceWallet = validateWallet(sourceWallet, "Ví nguồn không được để null", requireActiveReferences);
        this.destinationWallet = validateWallet(
                destinationWallet, "Ví đích không được để null", requireActiveReferences);
        validateDistinctWallets(this.sourceWallet, this.destinationWallet);
        validateFeeWallet(fee, this.sourceWallet, FEE_WALLET_MISMATCH_MESSAGE);
    }

    public static TransferTransaction reconstitute(
            UUID transactionId, Wallet sourceWallet, Wallet destinationWallet,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee,
            LocalDateTime createdAt) {
        return new TransferTransaction(
                transactionId, sourceWallet, destinationWallet,
                amount, transactionDate, note, fee, createdAt, false);
    }
    // endregion

    // region Commands
    @Override
    public void applyToWallet() {
        validateWalletActive(this.sourceWallet);
        validateWalletActive(this.destinationWallet);
        validateFeeWallet(this.fee, this.sourceWallet, FEE_WALLET_MISMATCH_MESSAGE);

        Money totalDebit = getTotalSourceDeduction();
        if (!this.sourceWallet.hasSufficientBalance(totalDebit)) {
            throw new InsufficientBalanceException(
                    this.sourceWallet.getId(),
                    this.sourceWallet.getCurrentBalance(),
                    totalDebit);
        }

        this.sourceWallet.debit(totalDebit);
        this.destinationWallet.credit(this.amount);
    }

    @Override
    public void revertFromWallet() {
        revertFromWallet(this.sourceWallet, this.destinationWallet);
    }

    public void revertFromWallet(Wallet sourceWallet, Wallet destinationWallet) {
        validateLiveWalletsForRevert(sourceWallet, destinationWallet);

        if (!destinationWallet.hasSufficientBalance(this.amount)) {
            throw new InsufficientBalanceException(
                    destinationWallet.getId(),
                    destinationWallet.getCurrentBalance(),
                    this.amount);
        }

        destinationWallet.debit(this.amount);
        sourceWallet.credit(this.amount);
        if (this.fee != null) {
            sourceWallet.credit(this.fee.getAmount());
        }
    }
    // endregion

    // region Queries
    public Wallet getSourceWallet() {
        return this.sourceWallet;
    }

    public Wallet getDestinationWallet() {
        return this.destinationWallet;
    }

    public Money getTotalSourceDeduction() {
        return this.fee != null ? this.amount.add(this.fee.getAmount()) : this.amount;
    }
    // endregion

    // region Helpers
    private static Money validateTransferAmount(Money amount) {
        if (amount == null || !amount.isGreaterThan(Money.ZERO)) {
            throw new BusinessException(ErrorCode.ERR_TRF_002, "Số tiền chuyển phải là một số dương lớn hơn 0.");
        }
        return amount;
    }

    private static void validateDistinctWallets(Wallet source, Wallet destination) {
        if (Objects.equals(source.getId(), destination.getId())) {
            throw new BusinessException(ErrorCode.ERR_TRF_001,
                    "Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau.");
        }
    }

    private void validateLiveWalletsForRevert(Wallet sourceWallet, Wallet destinationWallet) {
        Objects.requireNonNull(sourceWallet, "Ví nguồn không được để null");
        Objects.requireNonNull(destinationWallet, "Ví đích không được để null");
        Preconditions.checkArgument(
                Objects.equals(this.sourceWallet.getId(), sourceWallet.getId()),
                "Ví nguồn không khớp với giao dịch chuyển tiền.");
        Preconditions.checkArgument(
                Objects.equals(this.destinationWallet.getId(), destinationWallet.getId()),
                "Ví đích không khớp với giao dịch chuyển tiền.");
        validateWalletActive(sourceWallet);
        validateWalletActive(destinationWallet);
        validateFeeWallet(this.fee, sourceWallet, FEE_WALLET_MISMATCH_MESSAGE);
    }
    // endregion

    // region Identity
    @Override
    public String toString() {
        return String.format(
                "TransferTransaction[id=%s, sourceWalletId=%s, destWalletId=%s, amount=%s, date=%s, fee=%s]",
                this.transactionId, this.sourceWallet.getId(), this.destinationWallet.getId(),
                this.amount.formatDisplay(), this.transactionDate.getIsoString(),
                this.fee != null ? this.fee.getFeeId() : "none");
    }
    // endregion
}
