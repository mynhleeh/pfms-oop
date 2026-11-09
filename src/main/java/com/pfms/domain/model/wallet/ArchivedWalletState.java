package com.pfms.domain.model.wallet;

import java.util.Objects;

import com.pfms.domain.model.common.Money;

/**
 * Trạng thái ví đã lưu trữ (ARCHIVED).
 * Đóng băng mọi giao dịch thu chi và chuyển tiền mới, chỉ cho phép kích hoạt
 * lại về ACTIVE.
 */
public class ArchivedWalletState implements WalletState {

    // region Commands
    @Override
    public void credit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Không thể nạp tiền vào ví đã lưu trữ.");
    }

    @Override
    public void debit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Không thể rút tiền từ ví đã lưu trữ.");
    }

    @Override
    public void archive(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã ở trạng thái lưu trữ.");
    }

    @Override
    public void activate(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        wallet.setState(new ActiveWalletState());
    }

    @Override
    public void delete(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException(
                "Không thể xóa ví đang lưu trữ. Cần kích hoạt ví trước khi thực hiện thao tác xóa.");
    }
    // endregion

    // region Queries
    @Override
    public WalletStatus getStatus() {
        return WalletStatus.ARCHIVED;
    }
    // endregion
}
