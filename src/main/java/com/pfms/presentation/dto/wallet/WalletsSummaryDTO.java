package com.pfms.presentation.dto.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

        this.totalBalance = Objects.requireNonNull(totalBalance, "Tổng số dư hệ thống không được để null");

        this.wallets = (wallets == null)
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(wallets));
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
