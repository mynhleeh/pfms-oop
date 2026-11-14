package com.pfms.service;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.presentation.dto.wallet.AdjustBalanceRequestDTO;
import com.pfms.presentation.dto.wallet.BalanceAdjustmentResponseDTO;
import com.pfms.presentation.dto.wallet.CreateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.UpdateWalletRequestDTO;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;

import java.util.UUID;

/**
 * Dịch vụ quản lý ví thanh toán và số dư tài chính.
 * Điều phối tạo ví, cập nhật thông tin, chuyển đổi trạng thái và kiểm kê điều chỉnh số dư.
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
    WalletsSummaryResponseDTO getWalletsSummary(EntityStatus statusFilter);
    // endregion
}
