package com.pfms.presentation.dto.transfer;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO yêu cầu xóa giao dịch chuyển tiền nội bộ và hoàn nguyên số dư hai ví
 */
public final class DeleteTransferRequestDTO {

    // region State
    private final UUID transferId;
    // endregion

    // region Constructors
    public DeleteTransferRequestDTO(UUID transferId) {
        this.transferId = Objects.requireNonNull(transferId, "Mã giao dịch chuyển tiền không được để null");
    }
    // endregion

    // region Getters
    public UUID getTransferId() {
        return this.transferId;
    }

    public UUID getId() {
        return this.transferId;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeleteTransferRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.transferId, that.transferId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transferId);
    }

    @Override
    public String toString() {
        return "DeleteTransferRequestDTO{" +
                "transferId=" + this.transferId +
                '}';
    }
    // endregion
}
