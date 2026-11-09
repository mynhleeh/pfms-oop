package com.pfms.domain.exception;

import java.util.Objects;

import com.pfms.domain.model.common.Money;

/**
 * Ngoại lệ ném ra khi số dư ví không đủ để thực hiện giao dịch hoặc thanh toán
 * chi phí.
 */
public class InsufficientBalanceException extends BusinessException {

    // region State
    private final String walletId;
    private final Money balance;
    private final Money amount;
    // endregion

    // region Constructors
    public InsufficientBalanceException(
            ErrorCode errorCode, String walletId, Money balance, Money amount) {

        super(errorCode, String.format(
                "Giao dịch bị từ chối: Số dư ví [%s] hiện có (%s) không đủ để thực hiện số tiền (%s).",
                walletId, balance, amount));

        this.walletId = Objects.requireNonNull(walletId, "Mã ví không được để null");
        this.balance = Objects.requireNonNull(balance, "Số dư không được để null");
        this.amount = Objects.requireNonNull(amount, "Số tiền không được để null");
    }

    public InsufficientBalanceException(String walletId, Money balance, Money amount) {
        this(ErrorCode.ERR_WAL_001, walletId, balance, amount);
    }
    // endregion

    // region Getters
    public String getWalletId() {
        return this.walletId;
    }

    public Money getBalance() {
        return this.balance;
    }

    public Money getAmount() {
        return this.amount;
    }
    // endregion
}
