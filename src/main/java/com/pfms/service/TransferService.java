package com.pfms.service;

import com.pfms.presentation.dto.transfer.TransferFilterCriteriaDTO;
import com.pfms.presentation.dto.transfer.TransferHistoryResponseDTO;
import com.pfms.presentation.dto.transfer.TransferRequestDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;

import java.util.UUID;

/**
 * Giao diện dịch vụ điều phối nghiệp vụ chuyển tiền nội bộ giữa các ví.
 * Đảm bảo tính nguyên tử hai chiều, bảo toàn tổng tài sản và xử lý phụ phí đi kèm.
 */
public interface TransferService {

    // region Commands
    TransferResponseDTO transferFunds(TransferRequestDTO request);

    void deleteTransfer(UUID transferId);
    // endregion

    // region Queries
    TransferHistoryResponseDTO filterTransfers(TransferFilterCriteriaDTO request);
    // endregion
}

