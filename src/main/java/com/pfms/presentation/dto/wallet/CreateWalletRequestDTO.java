package com.pfms.presentation.dto.wallet;

import java.util.Objects;

import com.pfms.common.util.Preconditions;

/**
 * DTO đóng gói dữ liệu yêu cầu tạo ví mới từ giao diện gửi xuống tầng dịch vụ.
 */
public final class CreateWalletRequestDTO {

    // region State
    private final String name;
    private final String note;
    // endregion

    // region Constructors
    public CreateWalletRequestDTO(String name) {
        this(name, "");
    }

    public CreateWalletRequestDTO(String name, String note) {
        this.name = Preconditions.requireNonBlank(name, "Tên ví không được để trống hoặc chỉ chứa khoảng trắng");
        this.note = (note == null) ? "" : note.trim();
    }
    // endregion

    // region Getters
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
        if (!(o instanceof CreateWalletRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.note);
    }

    @Override
    public String toString() {
        return "CreateWalletRequestDTO{" +
                "name='" + this.name + '\'' +
                ", note='" + this.note + '\'' +
                '}';
    }
    // endregion
}
