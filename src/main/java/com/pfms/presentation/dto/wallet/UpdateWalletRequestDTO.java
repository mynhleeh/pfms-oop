package com.pfms.presentation.dto.wallet;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói dữ liệu yêu cầu cập nhật thông tin ví tài chính từ giao diện gửi
 * xuống tầng dịch vụ.
 */
public final class UpdateWalletRequestDTO {

    // region State
    private final UUID walletId;
    private final String name;
    private final String note;
    // endregion

    // region Constructors
    public UpdateWalletRequestDTO(UUID walletId, String name) {
        this(walletId, name, "");
    }

    public UpdateWalletRequestDTO(UUID walletId, String name, String note) {
        this.walletId = Objects.requireNonNull(walletId, "Mã ví cần cập nhật không được để null");
        this.name = (name == null) ? null : name.trim();
        this.note = (note == null) ? "" : note.trim();
    }
    // endregion

    // region Getters
    public UUID getWalletId() {
        return this.walletId;
    }

    public String getName() {
        return this.name;
    }

    public String getNote() {
        return this.note;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpdateWalletRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.walletId, that.walletId)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.walletId, this.name, this.note);
    }

    @Override
    public String toString() {
        return "UpdateWalletRequestDTO{" +
                "walletId=" + this.walletId +
                ", name='" + this.name + '\'' +
                ", note='" + this.note + '\'' +
                '}';
    }
    // endregion
}
