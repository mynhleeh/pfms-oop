package com.pfms.presentation.contract;

import com.pfms.presentation.viewmodel.transfer.TransferItemViewModel;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Chuyển tiền Nội bộ theo mô hình MVP Passive View.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public interface TransferView extends BaseView {

    void renderTransfers(List<TransferItemViewModel> transfers, String summaryText, String pageInfoText);

    void openTransferFundsDialog();
}
