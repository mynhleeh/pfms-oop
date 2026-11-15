package com.pfms.presentation.transfer;

import com.pfms.presentation.common.BaseView;

import com.pfms.presentation.transfer.viewmodel.TransferItemViewModel;

import java.util.List;

/**
 * Hợp đồng hiển thị cho phân hệ Chuyển tiền Nội bộ theo mô hình MVP Passive View.
 * 100% không phụ thuộc các thư viện đồ họa Swing/AWT.
 */
public interface TransferView extends BaseView {

    void renderTransfers(List<TransferItemViewModel> transfers, String summaryText, String pageInfoText);

    void openTransferFundsDialog();
}
