package com.pfms.service;

import com.pfms.domain.model.wallet.WalletStatus;
import com.pfms.presentation.dto.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.CreateWalletRequestDTO;
import com.pfms.presentation.dto.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.WalletResponseDTO;
import com.pfms.presentation.dto.WalletsSummaryDTO;

import java.util.UUID;

/**
 * Giao diện dịch vụ ứng dụng điều phối các ca sử dụng quản lý Ví tiền.
 */
public interface WalletService {

    // region Commands
    void initializeDefaultWallet();

    WalletResponseDTO createWallet(CreateWalletRequestDTO request);

    WalletResponseDTO updateWallet(UpdateWalletRequestDTO request);

    WalletResponseDTO archiveWallet(UUID walletId);

    WalletResponseDTO activateWallet(UUID walletId);

    void deleteWallet(UUID walletId);

    BalanceAdjustmentResponseDTO adjustBalance(AdjustBalanceRequestDTO request);
    // endregion

    // region Queries
    WalletsSummaryDTO getWalletsSummary(WalletStatus statusFilter);
    // endregion
}
