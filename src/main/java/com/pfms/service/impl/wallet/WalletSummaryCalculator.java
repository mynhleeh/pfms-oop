package com.pfms.service.impl.wallet;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.common.Money;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.presentation.dto.wallet.WalletResponseDTO;
import com.pfms.presentation.dto.wallet.WalletsSummaryResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bộ tính toán nội bộ tổng hợp số dư hệ thống, tỷ trọng ví và ánh xạ DTO ví tiền.
 */
final class WalletSummaryCalculator {

    // region Constants
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final Locale VIETNAMESE_LOCALE = Locale.forLanguageTag("vi-VN");
    // endregion

    // region Constructors
    private WalletSummaryCalculator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    // endregion

    // region Operations
    static WalletsSummaryResponseDTO buildWalletsSummary(
            WalletRepository walletRepository, EntityStatus statusFilter) {

        Objects.requireNonNull(walletRepository, "WalletRepository không được để null");

        List<Wallet> allWallets = walletRepository.findAll();
        List<Wallet> filteredWallets = new ArrayList<>();
        Money totalSystemMoney = Money.ZERO;
        for (Wallet wallet : allWallets) {
            if (matchesStatusFilter(wallet, statusFilter)) {
                filteredWallets.add(wallet);
            }
            if (isIncludedInSystemTotal(wallet)) {
                totalSystemMoney = totalSystemMoney.add(wallet.getCurrentBalance());
            }
        }

        Collator viCollator = Collator.getInstance(VIETNAMESE_LOCALE);
        viCollator.setStrength(Collator.SECONDARY);
        Comparator<Wallet> walletOrder = Comparator
                .comparing(Wallet::getName, viCollator)
                .thenComparing(Wallet::getId);

        List<Wallet> sortedWallets = filteredWallets.stream()
                .sorted(walletOrder)
                .toList();

        BigDecimal totalBalance = totalSystemMoney.getAmount();
        List<WalletResponseDTO> walletDtos = sortedWallets.stream()
                .map(WalletSummaryCalculator::toResponseDTO)
                .toList();

        Map<UUID, BigDecimal> sortedShares = calculateWalletShares(sortedWallets, totalBalance);

        return new WalletsSummaryResponseDTO(walletDtos, totalBalance, totalSystemMoney.getCurrency(), sortedShares);
    }

    static WalletResponseDTO toResponseDTO(Wallet wallet) {
        Objects.requireNonNull(wallet, "Thực thể ví không được để null");
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
    private static boolean matchesStatusFilter(Wallet wallet, EntityStatus statusFilter) {
        return (statusFilter == null)
                ? wallet.getStatus() != EntityStatus.DELETED
                : wallet.getStatus() == statusFilter;
    }

    private static boolean isIncludedInSystemTotal(Wallet wallet) {
        return wallet.getStatus() == EntityStatus.ACTIVE
                || (wallet.getStatus() == EntityStatus.ARCHIVED && wallet.getCurrentBalance().isPositive());
    }

    private static Map<UUID, BigDecimal> calculateWalletShares(List<Wallet> wallets, BigDecimal totalBalance) {
        boolean isTotalZero = totalBalance.compareTo(BigDecimal.ZERO) == 0;
        Map<UUID, BigDecimal> rawShares = new LinkedHashMap<>();

        for (Wallet wallet : wallets) {
            if (!isIncludedInSystemTotal(wallet)) {
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

