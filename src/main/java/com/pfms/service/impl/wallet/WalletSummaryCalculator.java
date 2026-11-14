package com.pfms.service.impl.wallet;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ tính toán nội bộ tổng hợp số dư hệ thống, tỷ trọng ví và ánh xạ DTO ví tiền.
 */
final class WalletSummaryCalculator {

    // region Constants
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    // endregion

    // region Constructors
    private WalletSummaryCalculator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static WalletsSummaryResponseDTO buildWalletsSummary(
            WalletRepository walletRepository, EntityStatus statusFilter) {

        List<Wallet> allWallets = walletRepository.findAll();
        List<Wallet> filteredWallets = (statusFilter == null)
                ? allWallets
                : walletRepository.findByStatus(statusFilter);

        Money totalSystemMoney = allWallets.stream()
                .filter(WalletSummaryCalculator::isIncludedInSystemTotal)
                .map(Wallet::getCurrentBalance)
                .reduce(Money.ZERO, Money::add);

        BigDecimal totalBalance = totalSystemMoney.getAmount();
        List<WalletResponseDTO> walletDtos = filteredWallets.stream()
                .map(WalletSummaryCalculator::toResponseDTO)
                .toList();

        Map<UUID, BigDecimal> sortedShares = calculateWalletShares(filteredWallets, totalBalance);

        return new WalletsSummaryResponseDTO(walletDtos, totalBalance, totalSystemMoney.getCurrency(), sortedShares);
    }

    static WalletResponseDTO toResponseDTO(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId(),
                wallet.getName(),
                wallet.getCurrentBalance().getAmount(),
                wallet.getCurrentBalance().getCurrency(),
                wallet.isDefaultWallet(),
                wallet.getStatus().name(),
                wallet.getNote(),
                wallet.getCreatedAt());
    }
    // endregion

    // region Helpers
    private static boolean isIncludedInSystemTotal(Wallet wallet) {
        return wallet.getStatus() == EntityStatus.ACTIVE
                || (wallet.getStatus() == EntityStatus.ARCHIVED && wallet.getCurrentBalance().isPositive());
    }

    private static Map<UUID, BigDecimal> calculateWalletShares(List<Wallet> wallets, BigDecimal totalBalance) {
        boolean isTotalZero = totalBalance.compareTo(BigDecimal.ZERO) == 0;
        Map<UUID, BigDecimal> rawShares = new HashMap<>();

        for (Wallet wallet : wallets) {
            if (wallet.getStatus() == EntityStatus.ARCHIVED && wallet.getCurrentBalance().isZero()) {
                continue;
            }
            rawShares.put(wallet.getId(), calculateShare(wallet, totalBalance, isTotalZero));
        }

        return rawShares.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    private static BigDecimal calculateShare(Wallet wallet, BigDecimal totalBalance, boolean isTotalZero) {
        if (isTotalZero) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }
        return wallet.getCurrentBalance().getAmount()
                .multiply(HUNDRED)
                .divide(totalBalance, 4, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
    // endregion
}

