package com.pfms.presentation.transaction;


import com.pfms.presentation.dto.transaction.FilterTransactionsRequestDTO;
import com.pfms.presentation.dto.transaction.TransactionListResponseDTO;
import com.pfms.presentation.dto.transaction.TransactionResponseDTO;
import com.pfms.presentation.common.ViewUtils;
import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện phân hệ Giao dịch Thu &amp; Chi theo kiến trúc MVP Passive View.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public class TransactionPresenter {

    // region Fields
    private final TransactionView view;
    private final TransactionGUIController controller;
    private List<TransactionResponseDTO> currentTransactions = new ArrayList<>();
    private FilterTransactionsRequestDTO currentFilter;
    private int currentPage = 1, pageSize = 20, totalPages = 1;
    private long totalRecords = 0;
    // endregion

    // region Constructors
    public TransactionPresenter(TransactionView view, TransactionGUIController controller) {
        this.view = Objects.requireNonNull(view, "TransactionView không được null");
        this.controller = Objects.requireNonNull(controller, "TransactionGUIController không được null");
    }
    // endregion

    // region Actions
    public void onAddTransactionClicked() { this.view.openRecordDialog(); }

    public void onEditTransactionClicked(TransactionItemViewModel item) {
        if (item == null) { this.view.showWarning("Vui lòng chọn một giao dịch trong bảng để thao tác."); return; }
        this.view.openUpdateDialog(item);
    }

    public void deleteTransaction(TransactionItemViewModel item) {
        if (item == null) { this.view.showWarning("Vui lòng chọn một giao dịch trong bảng để thao tác."); return; }
        deleteTransaction(item.id());
    }

    public void deleteTransaction(UUID transactionId) {
        if (transactionId == null) { this.view.showWarning("Vui lòng chọn một giao dịch trong bảng để thao tác."); return; }
        String msg = "Bạn có chắc chắn muốn xóa giao dịch này không?\nThao tác này sẽ hoàn nguyên số dư ví liên quan.";
        if (this.view.confirmAction(msg)) {
            try {
                this.controller.handleDeleteTransaction(transactionId);
                this.view.showMessage("Đã xóa giao dịch thành công.");
                loadTransactions(this.currentFilter);
            } catch (Exception ex) { this.view.showError("Xóa giao dịch thất bại: " + ex.getMessage()); }
        }
    }
    // endregion

    // region Data
    public void loadTransactions(FilterTransactionsRequestDTO filter) {
        this.currentFilter = filter;
        if (filter != null) {
            if (filter.getPageNumber() > 0) this.currentPage = filter.getPageNumber();
            if (filter.getPageSize() > 0) this.pageSize = filter.getPageSize();
        }
        try {
            TransactionListResponseDTO res = this.controller.loadTransactions(filter);
            if (res != null) {
                this.totalPages = Math.max(res.getTotalPages(), 1);
                this.totalRecords = res.getTotalRecords();
                this.currentPage = Math.min(Math.max(res.getPageNumber(), 1), this.totalPages);
                this.currentTransactions = res.getItems() != null ? new ArrayList<>(res.getItems()) : new ArrayList<>();
                List<TransactionItemViewModel> items = new ArrayList<>();
                for (int i = 0; i < this.currentTransactions.size(); i++) {
                    items.add(toViewModel(this.currentTransactions.get(i), i + 1));
                }
                String summary = String.format("Tổng thu: %s VNĐ | Tổng chi: %s VNĐ | Dòng tiền thuần: %s VNĐ",
                        ViewUtils.formatAmount(res.getTotalIncome()),
                        ViewUtils.formatAmount(res.getTotalExpense()),
                        ViewUtils.formatAmount(res.getNetCashFlow()));
                String pageInfo = String.format("Trang %d / %d (Tổng %d giao dịch)", this.currentPage, this.totalPages, this.totalRecords);
                this.view.renderTransactions(items, summary, pageInfo);
            } else {
                this.totalPages = 1; this.totalRecords = 0; this.currentPage = 1;
                this.currentTransactions = new ArrayList<>();
                this.view.renderTransactions(Collections.emptyList(), "Tổng thu: 0 VNĐ | Tổng chi: 0 VNĐ | Dòng tiền thuần: 0 VNĐ", "Trang 1 / 1 (Tổng 0 giao dịch)");
            }
        } catch (Exception ex) { this.view.showError("Lỗi tải danh sách giao dịch: " + ex.getMessage()); }
    }

    public void nextPage() { changePage(1); }
    public void prevPage() { changePage(-1); }
    public void changePage(int delta) {
        int target = this.currentPage + delta;
        if (target >= 1 && target <= this.totalPages) { this.currentPage = target; loadPage(); }
    }
    // endregion

    // region Getters
    public TransactionView getView() { return this.view; }
    public TransactionGUIController getController() { return this.controller; }
    public FilterTransactionsRequestDTO getCurrentFilter() { return this.currentFilter; }
    public List<TransactionResponseDTO> getCurrentTransactions() { return Collections.unmodifiableList(this.currentTransactions); }
    public int getCurrentPage() { return this.currentPage; }
    public int getPageSize() { return this.pageSize; }
    public int getTotalPages() { return this.totalPages; }
    public long getTotalRecords() { return this.totalRecords; }
    // endregion

    // region Helpers
    private void loadPage() {
        if (this.currentFilter != null) {
            loadTransactions(new FilterTransactionsRequestDTO(
                    this.currentFilter.getStartDate(), this.currentFilter.getEndDate(),
                    this.currentFilter.getWalletIds(), this.currentFilter.getTransactionType(),
                    this.currentFilter.getCategoryIds(), this.currentFilter.getKeyword(),
                    this.currentPage, this.pageSize));
        } else {
            loadTransactions(new FilterTransactionsRequestDTO(null, null, null, null, null, null, this.currentPage, this.pageSize));
        }
    }

    private TransactionItemViewModel toViewModel(TransactionResponseDTO dto, int order) {
        String date = dto.getTransactionDate() != null ? ViewUtils.formatDate(dto.getTransactionDate()) : "-";
        String cat = dto.getCategoryName() != null ? dto.getCategoryName() : "-";
        String wallet = dto.getWalletName() != null ? dto.getWalletName() : "-";
        String amt = ViewUtils.formatCurrency(dto.getAmount());
        boolean income = "INCOME".equalsIgnoreCase(dto.getTransactionType());
        String note = dto.getNote() != null ? dto.getNote() : "";
        String bal = dto.getWalletBalanceAfter() != null ? ViewUtils.formatCurrency(dto.getWalletBalanceAfter()) : "-";
        return new TransactionItemViewModel(dto.getTransactionId(), order, date, cat, wallet, amt, income, note, bal);
    }
    // endregion
}
