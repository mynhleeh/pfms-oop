package com.pfms.domain.model.transaction;

import com.pfms.domain.exception.BusinessException;
import com.pfms.domain.exception.ErrorCode;
import com.pfms.domain.exception.InsufficientBalanceException;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.common.TransactionDate;
import com.pfms.domain.model.wallet.Wallet;

import java.util.Objects;
import java.util.UUID;

/**
 * Giao dịch chuyển tiền nội bộ giữa hai ví trong hệ thống PFMS.
 * Trích nợ ví nguồn, ghi có ví đích và xử lý phụ phí chuyển tiền đi kèm (nếu
 * có).
 */
public class TransferTransaction extends Transaction {

    // region State
    private final Wallet sourceWallet;
    private final Wallet destinationWallet;
    // endregion

    // region Constructors
    public TransferTransaction(
            UUID transactionId, Wallet sourceWallet, Wallet destinationWallet,
            Money amount, TransactionDate transactionDate,
            String note, TransactionFee fee) {

        super(transactionId, validateTransferAmount(amount), transactionDate, note, fee);
        this.sourceWallet = validateWallet(sourceWallet, "Ví nguồn không được để null");
        this.destinationWallet = validateWallet(destinationWallet, "Ví đích không được để null");
        validateDistinctWallets(this.sourceWallet, this.destinationWallet);
        validateFeeWallet(fee, this.sourceWallet);
    }
    // endregion

    // region Commands
    @Override
    public void applyToWallet() {
        validateWalletActive(this.sourceWallet);
        validateWalletActive(this.destinationWallet);

        Money totalDebit = getTotalSourceDeduction();
        if (!this.sourceWallet.hasSufficientBalance(totalDebit)) {
            throw new InsufficientBalanceException(
                    this.sourceWallet.getId(),
                    this.sourceWallet.getCurrentBalance(),
                    totalDebit);
        }

        this.sourceWallet.debit(this.amount);
        if (this.fee != null) {
            this.fee.applyFee();
        }
        this.destinationWallet.credit(this.amount);
    }

    @Override
    public void revertFromWallet() {
        validateWalletActive(this.sourceWallet);
        validateWalletActive(this.destinationWallet);

        if (!this.destinationWallet.hasSufficientBalance(this.amount)) {
            throw new InsufficientBalanceException(this.destinationWallet.getId(),
                    this.destinationWallet.getCurrentBalance(), this.amount);
        }

        this.destinationWallet.debit(this.amount);
        if (this.fee != null) {
            this.fee.revertFee();
        }
        this.sourceWallet.credit(this.amount);
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

    private static Wallet validateWallet(Wallet wallet, String nullMessage) {
        Objects.requireNonNull(wallet, nullMessage);
        validateWalletActive(wallet);
        return wallet;
    }

    private static void validateWalletActive(Wallet wallet) {
        if (!wallet.isActive()) {
            throw new BusinessException(ErrorCode.ERR_WAL_001, "Ví thanh toán không ở trạng thái hoạt động.");
        }
    }

    private static void validateDistinctWallets(Wallet source, Wallet destination) {
        if (Objects.equals(source.getId(), destination.getId())) {
            throw new BusinessException(ErrorCode.ERR_TRF_001,
                    "Giao dịch không hợp lệ: Ví chuyển và ví nhận phải khác nhau.");
        }
    }

    private static void validateFeeWallet(TransactionFee fee, Wallet sourceWallet) {
        if (fee != null && !fee.getWallet().equals(sourceWallet)) {
            throw new BusinessException(ErrorCode.ERR_FEE_001,
                    "Ví của chi phí phải trùng với ví nguồn chuyển tiền.");
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
        TransferTransaction that = (TransferTransaction) o;
        return Objects.equals(this.transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId);
    }

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
