package com.pfms.presentation.contract;

import com.pfms.presentation.viewmodel.wallet.WalletItemViewModel;
import com.pfms.presentation.viewmodel.wallet.WalletsSummaryViewModel;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Quản lý Ví trong kiến trúc MVP.
 * 100% không phụ thuộc các thư viện giao diện đồ họa Swing/AWT.
 */
public interface WalletView extends BaseView {

    void renderWallets(List<WalletItemViewModel> wallets);

    void renderSummary(WalletsSummaryViewModel summary);

    void openCreateWalletDialog();

    void openUpdateWalletDialog(WalletItemViewModel wallet);

    void openAdjustBalanceDialog(WalletItemViewModel wallet);
}
