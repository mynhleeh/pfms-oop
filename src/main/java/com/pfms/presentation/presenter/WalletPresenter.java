package com.pfms.presentation.presenter;

import com.pfms.presentation.contract.WalletView;
import com.pfms.presentation.controller.WalletGUIController;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.view.common.ViewUtils;
import com.pfms.presentation.viewmodel.wallet.WalletItemViewModel;
import com.pfms.presentation.viewmodel.wallet.WalletsSummaryViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện phân hệ Quản lý Ví theo mô hình MVP.
 * Đóng gói toàn bộ logic trình bày, 100% không phụ thuộc thư viện đồ họa Swing.
 */
public class WalletPresenter {
    // region Fields
    private final WalletView view;
    private final WalletGUIController controller;
    private String currentFilter;
    // endregion

    // region Constructors
    public WalletPresenter(WalletView view, WalletGUIController controller) {
        this.view = Objects.requireNonNull(view, "WalletView không được để null");
        this.controller = Objects.requireNonNull(controller, "WalletGUIController không được để null");
    }
    // endregion

    // region Actions
    public void onAddWalletClicked() { this.view.openCreateWalletDialog(); }

    public void onEditWalletClicked(WalletItemViewModel wallet) {
        if (wallet == null) { this.view.showWarning("Vui lòng chọn một ví trong bảng để thao tác."); return; }
        this.view.openUpdateWalletDialog(wallet);
    }

    public void onAdjustBalanceClicked(WalletItemViewModel wallet) {
        if (wallet == null) { this.view.showWarning("Vui lòng chọn một ví trong bảng để thao tác."); return; }
        this.view.openAdjustBalanceDialog(wallet);
    }

    public void toggleStatus(UUID walletId, boolean isActive) {
        if (walletId == null) { this.view.showWarning("Vui lòng chọn một ví trong bảng để thao tác."); return; }
        try {
            if (isActive) {
                this.controller.handleArchiveWallet(walletId);
                this.view.showMessage("Đã chuyển ví sang trạng thái lưu trữ.");
            } else {
                this.controller.handleActivateWallet(walletId);
                this.view.showMessage("Đã kích hoạt lại ví thành công.");
            }
            loadData();
        } catch (Exception ex) { this.view.showError(ex.getMessage()); }
    }

    public void deleteWallet(UUID walletId) { deleteWallet(walletId, null); }

    public void deleteWallet(UUID walletId, String walletName) {
        if (walletId == null) { this.view.showWarning("Vui lòng chọn một ví trong bảng để thao tác."); return; }
        String name = (walletName != null && !walletName.isBlank()) ? "ví \"" + walletName + "\"" : "ví này";
        String msg = "Bạn có chắc chắn muốn xóa " + name + " không?\nThao tác này sẽ chuyển ví sang trạng thái đã xóa.";
        if (this.view.confirmAction(msg)) {
            try {
                this.controller.handleDeleteWallet(walletId);
                this.view.showMessage("Đã xóa ví thành công.");
                loadData();
            } catch (Exception ex) { this.view.showError(ex.getMessage()); }
        }
    }
    // endregion

    // region Data
    public void filterWallets(String filter) {
        this.currentFilter = (filter == null || "Tất cả".equalsIgnoreCase(filter) || "ALL".equalsIgnoreCase(filter)) ? null : filter;
        loadData();
    }

    public void loadData() {
        try {
            WalletsSummaryResponseDTO summary = (this.currentFilter == null)
                    ? this.controller.loadWalletsSummary() : this.controller.loadWalletsSummary(this.currentFilter);
            if (summary == null) {
                this.view.renderWallets(Collections.emptyList());
                this.view.renderSummary(new WalletsSummaryViewModel(BigDecimal.ZERO, "0 VNĐ", 0, 0));
                return;
            }
            List<WalletResponseDTO> dtoList = summary.getWallets();
            List<WalletItemViewModel> items = new ArrayList<>(dtoList.size());
            int active = 0, archived = 0;
            for (int i = 0; i < dtoList.size(); i++) {
                WalletResponseDTO dto = dtoList.get(i);
                if ("ACTIVE".equalsIgnoreCase(dto.getStatus())) active++;
                else if ("ARCHIVED".equalsIgnoreCase(dto.getStatus())) archived++;
                items.add(toViewModel(dto, i + 1));
            }
            this.view.renderWallets(items);
            this.view.renderSummary(new WalletsSummaryViewModel(summary.getTotalBalance(),
                    ViewUtils.formatCurrency(summary.getTotalBalance()), active, archived));
        } catch (Exception ex) { this.view.showError(ex.getMessage()); }
    }
    // endregion

    // region Getters
    public WalletView getView() { return this.view; }
    public WalletGUIController getController() { return this.controller; }
    // endregion

    // region Helpers
    private WalletItemViewModel toViewModel(WalletResponseDTO dto, int order) {
        boolean active = "ACTIVE".equalsIgnoreCase(dto.getStatus());
        String statusText = active ? "Hoạt động" : ("ARCHIVED".equalsIgnoreCase(dto.getStatus()) ? "Lưu trữ"
                : ("DELETED".equalsIgnoreCase(dto.getStatus()) ? "Đã xóa" : (dto.getStatus() == null ? "Không xác định" : dto.getStatus())));
        return new WalletItemViewModel(dto.getWalletId(), order, dto.getName(), dto.getBalance(),
                ViewUtils.formatCurrency(dto.getBalance()), statusText, dto.getNote(), active);
    }
    // endregion
}
