package com.pfms.presentation.contract;

import com.pfms.presentation.viewmodel.transaction.TransactionItemViewModel;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Quản lý Giao dịch Thu &amp; Chi theo mô hình MVP Passive View.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public interface TransactionView extends BaseView {

    void renderTransactions(List<TransactionItemViewModel> txns, String summaryText, String pageInfoText);

    void openRecordDialog();

    void openUpdateDialog(TransactionItemViewModel txn);

    void setSelectedTypeFilter(String type);
}
