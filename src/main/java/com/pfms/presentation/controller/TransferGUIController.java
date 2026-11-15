package com.pfms.presentation.controller;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.transfer.FilterTransfersRequestDTO;
import com.pfms.presentation.dto.transfer.TransferFundsRequestDTO;
import com.pfms.presentation.dto.transfer.TransferListResponseDTO;
import com.pfms.presentation.dto.transfer.TransferResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.service.TransferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Chuyển tiền nội bộ (TRF).
 * Tiếp nhận dữ liệu biểu mẫu chuyển tiền, kiểm thực ví nguồn/đích, số tiền, ngày tháng, phụ phí và điều phối TransferService.
 */
public final class TransferGUIController {

    // region State
    private final TransferService transferService;
    // endregion

    // region Constructors
    public TransferGUIController(TransferService transferService) {
        this.transferService = Objects.requireNonNull(transferService, "TransferService không được để null");
    }
    // endregion

    // region Operations
    // region Transfer
    public TransferResponseDTO handleTransferFunds(
            UUID sourceWalletId,
            UUID destinationWalletId,
            String rawAmount,
            String rawDate,
            String rawNote,
            String rawFeeAmount,
            UUID feeCategoryId) {

        Preconditions.checkArgument(sourceWalletId != null, "Mã ví nguồn không được để null");
        Preconditions.checkArgument(destinationWalletId != null, "Mã ví đích không được để null");

        if (sourceWalletId.equals(destinationWalletId)) {
            throw new IllegalArgumentException("Ví nguồn và ví đích không được trùng nhau");
        }

        BigDecimal amount = InputValidator.validatePositiveAmount(rawAmount, "Số tiền chuyển");
        LocalDate date = InputValidator.parseDateOrToday(rawDate, "Ngày chuyển tiền");
        String note = InputValidator.validateTextLength(rawNote, 255, "Ghi chú");
        BigDecimal feeAmount = InputValidator.validateOptionalFee(rawFeeAmount, feeCategoryId);

        TransferFundsRequestDTO request = new TransferFundsRequestDTO(
                sourceWalletId, destinationWalletId, amount, date, note, feeAmount, feeCategoryId);
        return this.transferService.transferFunds(request);
    }

    public TransferResponseDTO handleTransferFunds(
            UUID sourceWalletId,
            UUID destinationWalletId,
            String rawAmount,
            String rawDate,
            String rawNote) {

        return handleTransferFunds(sourceWalletId, destinationWalletId, rawAmount, rawDate, rawNote, "0", null);
    }
    // endregion

    // region Delete
    public void handleDeleteTransfer(UUID transferId) {
        Preconditions.checkArgument(transferId != null, "Mã chuyển tiền không được để null");
        this.transferService.deleteTransfer(transferId);
    }
    // endregion

    // region Filter
    public TransferListResponseDTO loadTransfers(FilterTransfersRequestDTO filter) {
        return this.transferService.filterTransfers(
                filter == null ? new FilterTransfersRequestDTO() : filter);
    }

    public TransferListResponseDTO loadTransfers(
            String rawStartDate,
            String rawEndDate,
            UUID sourceWalletId,
            UUID destinationWalletId,
            int pageNumber,
            int pageSize) {

        LocalDate startDate = InputValidator.parseOptionalDate(rawStartDate, "Ngày bắt đầu");
        LocalDate endDate = InputValidator.parseOptionalDate(rawEndDate, "Ngày kết thúc");
        Preconditions.requireValidDateRange(startDate, endDate);

        FilterTransfersRequestDTO filter = new FilterTransfersRequestDTO(
                startDate, endDate, sourceWalletId, destinationWalletId, pageNumber, pageSize);
        return this.transferService.filterTransfers(filter);
    }

    public TransferListResponseDTO loadTransfers() {
        return this.transferService.filterTransfers(new FilterTransfersRequestDTO());
    }
    // endregion
    // endregion
}
