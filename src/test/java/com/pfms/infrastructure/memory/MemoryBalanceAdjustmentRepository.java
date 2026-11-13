package com.pfms.infrastructure.memory;

import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.repository.BalanceAdjustmentRepository;

import java.util.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Kho lưu trữ lịch sử kiểm kê số dư ví trong bộ nhớ RAM phục vụ môi trường kiểm thử.
 */
public class MemoryBalanceAdjustmentRepository
        extends MemoryGenericRepository<BalanceAdjustmentRecord, UUID>
        implements BalanceAdjustmentRepository {

    // region State
    private final AtomicLong sequenceGenerator = new AtomicLong(0);
    private final ConcurrentHashMap<UUID, Long> insertionSequence = new ConcurrentHashMap<>();
    // endregion

    // region Constructors
    public MemoryBalanceAdjustmentRepository() {
        super(BalanceAdjustmentRecord::getRecordId);
    }
    // endregion

    // region Commands
    @Override
    public BalanceAdjustmentRecord save(BalanceAdjustmentRecord entity) {
        Objects.requireNonNull(entity, "Thực thể bản ghi kiểm kê không được để null");
        UUID id = Objects.requireNonNull(entity.getRecordId(), "Mã bản ghi kiểm kê không được để null");
        if (storage.containsKey(id)) {
            throw new UnsupportedOperationException(
                    "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ cập nhật bản ghi đã tồn tại.");
        }
        insertionSequence.put(id, sequenceGenerator.incrementAndGet());
        storage.put(id, entity);
        return entity;
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã bản ghi kiểm kê không được để null");
        throw new UnsupportedOperationException(
                "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ thao tác xóa khỏi hệ thống.");
    }

    @Override
    public void clear() {
        super.clear();
        insertionSequence.clear();
        sequenceGenerator.set(0);
    }
    // endregion

    // region Queries
    @Override
    public List<BalanceAdjustmentRecord> findByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        List<BalanceAdjustmentRecord> matched = new ArrayList<>(storage.values().stream()
                .filter(record -> record.getWalletId().equals(walletId))
                .toList());

        matched.sort((r1, r2) -> {
            int dateCompare = r2.getAdjustedAt().compareTo(r1.getAdjustedAt());
            if (dateCompare != 0) {
                return dateCompare;
            }
            long seq1 = insertionSequence.getOrDefault(r1.getRecordId(), 0L);
            long seq2 = insertionSequence.getOrDefault(r2.getRecordId(), 0L);
            return Long.compare(seq2, seq1);
        });

        return Collections.unmodifiableList(matched);
    }

    @Override
    public Optional<BalanceAdjustmentRecord> findLatestByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        List<BalanceAdjustmentRecord> list = findByWalletId(walletId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
    // endregion
}

