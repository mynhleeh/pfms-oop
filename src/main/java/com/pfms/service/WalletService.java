package com.pfms.service;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryDTO;

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
    WalletsSummaryDTO getWalletsSummary(EntityStatus statusFilter);
    // endregion
}
