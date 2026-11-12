package com.pfms.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.pfms.common.util.Preconditions;

/**
 * DTO đóng gói dữ liệu chi tiết ví tài chính trả về từ tầng dịch vụ lên giao
 * diện.
 */
public final class WalletResponseDTO {

    // region State
    private final UUID id;
    private final String name;
    private final BigDecimal balance;
    private final String currency;
    private final boolean defaultWallet;
    private final String status;
    private final String note;
    private final LocalDateTime createdAt;
    // endregion

    // region Constructors
    public WalletResponseDTO(
            UUID id,
            String name,
            BigDecimal balance,
            String currency,
            boolean defaultWallet,
            String status,
            String note,
            LocalDateTime createdAt) {

        this.id = Objects.requireNonNull(id, "Mã ví không được để null");
        this.name = Preconditions.requireNonBlank(name, "Tên ví không được để trống hoặc chỉ chứa khoảng trắng");
        this.balance = Objects.requireNonNull(balance, "Số dư ví không được để null");
        this.currency = (currency == null || currency.trim().isEmpty()) ? "VND" : currency.trim();
        this.defaultWallet = defaultWallet;
        this.status = (status == null || status.trim().isEmpty()) ? "ACTIVE" : status.trim();
        this.note = (note == null) ? "" : note.trim();
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
    }
    // endregion

    // region Getters
    public UUID getId() {
        return this.id;
    }

    public UUID getWalletId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public String getCurrency() {
        return this.currency;
    }

    public boolean isDefaultWallet() {
        return this.defaultWallet;
    }

    public boolean isDefault() {
        return this.defaultWallet;
    }

    public String getStatus() {
        return this.status;
    }

    public String getNote() {
        return this.note;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletResponseDTO that)) {
            return false;
        }
        return this.defaultWallet == that.defaultWallet
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && (this.balance.compareTo(that.balance) == 0)
                && Objects.equals(this.currency, that.currency)
                && Objects.equals(this.status, that.status)
                && Objects.equals(this.note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.name,
                this.balance.stripTrailingZeros(),
                this.currency,
                this.defaultWallet,
                this.status,
                this.note);
    }

    @Override
    public String toString() {
        return "WalletResponseDTO{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", balance=" + this.balance +
                ", currency='" + this.currency + '\'' +
                ", defaultWallet=" + this.defaultWallet +
                ", status='" + this.status + '\'' +
                ", note='" + this.note + '\'' +
                ", createdAt=" + this.createdAt +
                '}';
    }
    // endregion
}
