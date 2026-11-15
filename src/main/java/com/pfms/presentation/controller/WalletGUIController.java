package com.pfms.presentation.controller;

import com.pfms.common.util.Preconditions;
import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;
import com.pfms.presentation.util.InputValidator;
import com.pfms.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Bộ điều phối giao diện đồ họa cho phân hệ Quản lý Ví (WAL).
 * Tiếp nhận dữ liệu biểu mẫu, kiểm thực đầu vào và điều phối tương tác với WalletService.
 */
public final class WalletGUIController {

    // region State
    private final WalletService walletService;
    // endregion

    // region Constructors
    public WalletGUIController(WalletService walletService) {
        this.walletService = Objects.requireNonNull(walletService, "WalletService không được để null");
    }
    // endregion

    // region Operations
    // region Create
    public WalletResponseDTO handleCreateWallet(String rawName, String rawNote) {
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên ví");
        String note = InputValidator.validateTextLength(rawNote, 255, "Ghi chú");

        CreateWalletRequestDTO request = new CreateWalletRequestDTO(name, note);
        return this.walletService.createWallet(request);
    }
    // endregion

    // region Update
    public WalletResponseDTO handleUpdateWallet(UUID walletId, String rawName, String rawNote) {
        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        String name = InputValidator.validateRequiredText(rawName, 50, "Tên ví");
        String note = InputValidator.validateTextLength(rawNote, 255, "Ghi chú");

        UpdateWalletRequestDTO request = new UpdateWalletRequestDTO(walletId, name, note);
        return this.walletService.updateWallet(request);
    }
    // endregion

    // region Adjust Balance
    public BalanceAdjustmentResponseDTO handleAdjustBalance(
            UUID walletId, String rawActualBalance, String rawReason, String rawDate) {

        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        BigDecimal actualBalance = InputValidator.validateNonNegativeAmount(rawActualBalance, "Số dư thực tế");
        String reason = InputValidator.validateRequiredText(rawReason, 255, "Lý do kiểm kê");
        LocalDate adjustedAt = InputValidator.validateDate(rawDate, "Ngày kiểm kê");

        AdjustBalanceRequestDTO request = new AdjustBalanceRequestDTO(walletId, actualBalance, reason, adjustedAt);
        return this.walletService.adjustBalance(request);
    }
    // endregion

    // region Archive
    public WalletResponseDTO handleArchiveWallet(UUID walletId) {
        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        return this.walletService.archiveWallet(walletId);
    }
    // endregion

    // region Activate
    public WalletResponseDTO handleActivateWallet(UUID walletId) {
        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        return this.walletService.activateWallet(walletId);
    }
    // endregion

    // region Delete
    public void handleDeleteWallet(UUID walletId) {
        Preconditions.checkArgument(walletId != null, "Mã ví không được để null");
        this.walletService.deleteWallet(walletId);
    }
    // endregion

    // region Summary
    public WalletsSummaryResponseDTO loadWalletsSummary(String statusFilter) {
        return this.walletService.getWalletsSummary(statusFilter);
    }

    public WalletsSummaryResponseDTO loadWalletsSummary() {
        return this.walletService.getWalletsSummary();
    }

    public WalletsSummaryResponseDTO getWalletsSummary() {
        return loadWalletsSummary();
    }
    // endregion
    // endregion
}
