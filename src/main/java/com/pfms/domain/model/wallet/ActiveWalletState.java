package com.pfms.domain.model.wallet;

import java.util.Objects;

import com.pfms.domain.model.common.Money;

/**
 * Trạng thái ví đang hoạt động (ACTIVE).
 * Cho phép tiếp nhận các biến động dòng tiền và thực hiện chuyển đổi vòng đời
 * sang lưu trữ hoặc xóa.
 */
public class ActiveWalletState implements WalletState {

    // region Commands
    @Override
    public void credit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        Objects.requireNonNull(amount, "Số tiền nạp không được để null");
        wallet.applyCredit(amount);
    }

    @Override
    public void debit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        Objects.requireNonNull(amount, "Số tiền rút không được để null");
        wallet.applyDebit(amount);
    }

    @Override
    public void archive(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        wallet.setState(new ArchivedWalletState());
    }

    @Override
    public void activate(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đang ở trạng thái hoạt động.");
    }

    @Override
    public void delete(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        wallet.setState(new DeletedWalletState());
    }
    // endregion

    // region Queries
    @Override
    public WalletStatus getStatus() {
        return WalletStatus.ACTIVE;
    }
    // endregion
}
