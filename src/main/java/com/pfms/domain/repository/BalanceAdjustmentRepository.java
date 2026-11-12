package com.pfms.domain.repository;

import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Kho lưu trữ lịch sử kiểm kê số dư ví. Quản lý vết kiểm toán theo cơ chế chỉ
 * ghi thêm.
 */
public interface BalanceAdjustmentRepository extends GenericRepository<BalanceAdjustmentRecord, UUID> {

    // region Queries

    List<BalanceAdjustmentRecord> findByWalletId(UUID walletId);

    Optional<BalanceAdjustmentRecord> findLatestByWalletId(UUID walletId);
    // endregion

    // region Commands

    @Override
    default boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã bản ghi kiểm kê không được để null");
        throw new UnsupportedOperationException(
                "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ thao tác xóa khỏi hệ thống.");
    }
    // endregion
}
