package com.pfms.domain.model.wallet;

import java.util.Objects;

import com.pfms.domain.model.common.Money;

/**
 * Trạng thái ví đã xóa (DELETED).
 * Trạng thái kết thúc (Terminal State), từ chối mọi hoạt động tài chính và
 * chuyển đổi trạng thái.
 */
public class DeletedWalletState implements WalletState {

    // region Commands
    @Override
    public void credit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không thể thực hiện thao tác.");
    }

    @Override
    public void debit(Wallet wallet, Money amount) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không thể thực hiện thao tác.");
    }

    @Override
    public void archive(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không thể thực hiện thao tác.");
    }

    @Override
    public void activate(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không thể thực hiện thao tác.");
    }

    @Override
    public void delete(Wallet wallet) {
        Objects.requireNonNull(wallet, "Ví không được để null");
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không thể thực hiện thao tác.");
    }
    // endregion

    // region Queries
    @Override
    public WalletStatus getStatus() {
        throw new IllegalStateException("Ví đã bị xóa khỏi hệ thống, không còn trạng thái vòng đời hợp lệ.");
    }
    // endregion
}
