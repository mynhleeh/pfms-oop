package com.pfms.presentation.dto;

import java.math.BigDecimal;
import java.util.*;

/**
 * DTO đóng gói dữ liệu tổng hợp danh sách ví, tổng tài sản và cơ cấu phân bổ
 * trả về cho giao diện.
 */
public final class WalletsSummaryDTO {

    // region State
    private final List<WalletResponseDTO> wallets;
    private final BigDecimal totalBalance;
    private final String currency;
    private final Map<UUID, BigDecimal> walletShares;
    // endregion

    // region Constructors
    public WalletsSummaryDTO(
            List<WalletResponseDTO> wallets,
            BigDecimal totalBalance,
            String currency,
            Map<UUID, BigDecimal> walletShares) {

        if (totalBalance == null) {
            throw new IllegalArgumentException("Tổng số dư hệ thống không được để null");
        }
        if (totalBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tổng số dư hệ thống không được âm: " + totalBalance);
        }

        this.wallets = (wallets == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(wallets));
        this.totalBalance = totalBalance;
        this.currency = (currency == null || currency.trim().isEmpty()) ? "VND" : currency.trim();
        this.walletShares = (walletShares == null)
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(walletShares));
    }
    // endregion

    // region Getters
    public List<WalletResponseDTO> getWallets() {
        return this.wallets;
    }

    public int getWalletCount() {
        return this.wallets.size();
    }

    public BigDecimal getTotalBalance() {
        return this.totalBalance;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Map<UUID, BigDecimal> getWalletShares() {
        return this.walletShares;
    }

    public BigDecimal getWalletShare(UUID walletId) {
        if (walletId == null) {
            return BigDecimal.ZERO;
        }
        return this.walletShares.getOrDefault(walletId, BigDecimal.ZERO);
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletsSummaryDTO that)) {
            return false;
        }
        return Objects.equals(this.wallets, that.wallets)
                && (this.totalBalance.compareTo(that.totalBalance) == 0)
                && Objects.equals(this.currency, that.currency)
                && Objects.equals(this.walletShares, that.walletShares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.wallets,
                this.totalBalance.stripTrailingZeros(),
                this.currency,
                this.walletShares);
    }

    @Override
    public String toString() {
        return "WalletsSummaryDTO{" +
                "walletCount=" + this.wallets.size() +
                ", totalBalance=" + this.totalBalance +
                ", currency='" + this.currency + '\'' +
                ", sharesCount=" + this.walletShares.size() +
                '}';
    }
    // endregion
}
