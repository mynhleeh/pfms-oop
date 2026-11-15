package com.pfms.presentation.transaction;

import com.pfms.presentation.common.BaseView;

import com.pfms.presentation.transaction.viewmodel.TransactionItemViewModel;

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
