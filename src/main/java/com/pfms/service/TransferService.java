package com.pfms.service;

import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferFundsRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;

import java.util.UUID;

/**
 * Dịch vụ chuyển tiền nội bộ giữa hai ví thanh toán.
 * Đảm bảo tính nguyên tử hai chiều, bảo toàn tổng tài sản và hạch toán phụ phí chuyển tiền.
 */
public interface TransferService {

    // region Commands
    TransferResponseDTO transferFunds(TransferFundsRequestDTO request);

    void deleteTransfer(UUID transferId);
    // endregion

    // region Queries
    TransferListResponseDTO filterTransfers(FilterTransfersRequestDTO request);
    // endregion
}

