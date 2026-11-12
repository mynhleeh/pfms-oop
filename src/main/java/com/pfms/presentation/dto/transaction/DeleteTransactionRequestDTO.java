package com.pfms.presentation.dto.transaction;

import java.util.Objects;
import java.util.UUID;

/**
 * DTO đóng gói mã định danh giao dịch thu chi cần xóa gửi từ giao diện xuống tầng dịch vụ (UC-TXN-04).
 * Phục vụ quy trình kiểm tra số dư và hoàn nguyên dòng tiền về ví.
 */
public final class DeleteTransactionRequestDTO {

    // region State
    private final UUID transactionId;
    // endregion

    // region Constructors
    public DeleteTransactionRequestDTO(UUID transactionId) {
        this.transactionId = Objects.requireNonNull(transactionId, "Mã giao dịch không được để null");
    }
    // endregion

    // region Getters
    public UUID getTransactionId() {
        return this.transactionId;
    }

    public UUID getId() {
        return this.transactionId;
    }
    // endregion

    // region Identity
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeleteTransactionRequestDTO that)) {
            return false;
        }
        return Objects.equals(this.transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.transactionId);
    }

    @Override
    public String toString() {
        return "DeleteTransactionRequestDTO{" +
                "transactionId=" + this.transactionId +
                '}';
    }
    // endregion
}

