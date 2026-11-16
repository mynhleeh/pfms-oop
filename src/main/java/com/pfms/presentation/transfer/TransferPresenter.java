package com.pfms.presentation.transfer;

import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện phân hệ Chuyển tiền Nội bộ theo mô hình MVP.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public class TransferPresenter {

    // region Fields
    private final TransferView view;
    private final TransferGUIController controller;
    private FilterTransfersRequestDTO currentFilter;
    private int currentPage = 1, pageSize = 20, totalPages = 1;
    private long totalRecords = 0;
    // endregion

    // region Constructors
    public TransferPresenter(TransferView view, TransferGUIController controller) {
        this.view = Objects.requireNonNull(view, "TransferView không được null");
        this.controller = Objects.requireNonNull(controller, "TransferGUIController không được null");
    }
    // endregion

    // region Actions
    public void onNewTransferClicked() { this.view.openTransferFundsDialog(); }

    public void deleteTransfer(TransferItemViewModel item) {
        if (item == null) { this.view.showWarning("Vui lòng chọn giao dịch chuyển tiền cần xóa!"); return; }
        deleteTransfer(item.id(), item.fromWalletName(), item.toWalletName());
    }

    public void deleteTransfer(UUID transferId, String sourceWallet, String destWallet) {
        if (transferId == null) { this.view.showWarning("Vui lòng chọn giao dịch chuyển tiền cần xóa!"); return; }
        String msg = "Bạn có chắc chắn muốn xóa giao dịch chuyển tiền từ ví \""
                + (sourceWallet != null ? sourceWallet : "") + "\" đến ví \""
                + (destWallet != null ? destWallet : "") + "\" không?";
        if (this.view.confirmAction(msg)) {
            try {
                this.controller.handleDeleteTransfer(transferId);
                this.view.showMessage("Đã xóa giao dịch chuyển tiền thành công!");
                loadTransfers(this.currentFilter);
            } catch (Exception ex) { this.view.showError("Xóa giao dịch chuyển tiền thất bại: " + ex.getMessage()); }
        }
    }
    // endregion

    // region Data
    public void loadTransfers(FilterTransfersRequestDTO filter) {
        this.currentFilter = filter;
        if (filter != null) {
            if (filter.getPageNumber() > 0) this.currentPage = filter.getPageNumber();
            if (filter.getPageSize() > 0) this.pageSize = filter.getPageSize();
        }
        try {
            TransferListResponseDTO res = this.controller.loadTransfers(filter);
            if (res != null) {
                this.totalPages = Math.max(res.getTotalPages(), 1);
                this.totalRecords = res.getTotalRecords();
                this.currentPage = Math.min(Math.max(res.getPageNumber(), 1), this.totalPages);
                List<TransferItemViewModel> items = new ArrayList<>();
                List<TransferResponseDTO> dtoList = res.getItems();
                if (dtoList != null) {
                    for (int i = 0; i < dtoList.size(); i++) items.add(toViewModel(dtoList.get(i), i + 1));
                }
                String summary = String.format("Tổng tiền chuyển: %s VNĐ | Tổng phí: %s VNĐ",
                        ViewUtils.formatAmount(res.getTotalTransferredAmount()), ViewUtils.formatAmount(res.getTotalFees()));
                String pageInfo = String.format("Trang %d / %d (Tổng %d giao dịch)", this.currentPage, this.totalPages, this.totalRecords);
                this.view.renderTransfers(items, summary, pageInfo);
            } else {
                this.totalPages = 1; this.totalRecords = 0; this.currentPage = 1;
                this.view.renderTransfers(Collections.emptyList(), "Tổng tiền chuyển: 0 VNĐ | Tổng phí: 0 VNĐ", "Trang 1 / 1 (Tổng 0 giao dịch)");
            }
        } catch (Exception ex) { this.view.showError("Không thể tải danh sách chuyển tiền: " + ex.getMessage()); }
    }

    public void nextPage() { changePage(1); }
    public void prevPage() { changePage(-1); }
    public void changePage(int delta) {
        int target = this.currentPage + delta;
        if (target >= 1 && target <= this.totalPages) { this.currentPage = target; loadPage(); }
    }
    // endregion

    // region Getters
    public TransferView getView() { return this.view; }
    public TransferGUIController getController() { return this.controller; }
    public FilterTransfersRequestDTO getCurrentFilter() { return this.currentFilter; }
    public int getCurrentPage() { return this.currentPage; }
    public int getPageSize() { return this.pageSize; }
    public int getTotalPages() { return this.totalPages; }
    public long getTotalRecords() { return this.totalRecords; }
    // endregion

    // region Helpers
    private void loadPage() {
        LocalDate s = this.currentFilter != null ? this.currentFilter.getStartDate() : null;
        LocalDate e = this.currentFilter != null ? this.currentFilter.getEndDate() : null;
        UUID src = this.currentFilter != null ? this.currentFilter.getSourceWalletId() : null;
        UUID dst = this.currentFilter != null ? this.currentFilter.getDestinationWalletId() : null;
        loadTransfers(new FilterTransfersRequestDTO(s, e, src, dst, this.currentPage, this.pageSize));
    }

    private TransferItemViewModel toViewModel(TransferResponseDTO dto, int order) {
        String date = dto.getTransferDate() != null ? ViewUtils.formatDate(dto.getTransferDate()) : "-";
        String from = dto.getSourceWalletName() != null ? dto.getSourceWalletName() : "-";
        String to = dto.getDestinationWalletName() != null ? dto.getDestinationWalletName() : "-";
        boolean hasFee = dto.getFeeAmount() != null && dto.getFeeAmount().compareTo(java.math.BigDecimal.ZERO) > 0;
        String feeFormatted = hasFee ? ViewUtils.formatCurrency(dto.getFeeAmount()) : "";
        return new TransferItemViewModel(dto.getTransferId(), order, date, from, to,
                ViewUtils.formatCurrency(dto.getAmount()), dto.getAmount(), dto.getNote(),
                dto.getSourceBalanceAfter(), dto.getDestinationBalanceAfter(), hasFee, feeFormatted);
    }
    // endregion
}
