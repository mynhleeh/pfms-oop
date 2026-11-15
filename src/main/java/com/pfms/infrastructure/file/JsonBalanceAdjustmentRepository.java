package com.pfms.infrastructure.file;

import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.wallet.BalanceAdjustmentRecord;
import com.pfms.domain.repository.BalanceAdjustmentRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cài đặt kho lưu trữ tệp JSON cho thực thể Bản ghi Kiểm kê Số dư ({@link BalanceAdjustmentRepository}).
 * Quản lý đồng bộ vết kiểm toán theo cơ chế chỉ ghi thêm (Append-only) qua {@link JsonDataStore} theo chuẩn OOD 4.2.4.
 */
public class JsonBalanceAdjustmentRepository implements BalanceAdjustmentRepository {

    // region State
    private final JsonDataStore dataStore;
    // endregion

    // region Constructors
    public JsonBalanceAdjustmentRepository(JsonDataStore dataStore) {
        this.dataStore = Objects.requireNonNull(dataStore, "JsonDataStore không được để null");
    }
    // endregion

    // region Commands
    @Override
    public BalanceAdjustmentRecord save(BalanceAdjustmentRecord entity) {
        Objects.requireNonNull(entity, "Thực thể bản ghi kiểm kê không được để null");
        UUID recordId = Objects.requireNonNull(entity.getRecordId(), "Mã bản ghi kiểm kê không được để null");

        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<BalanceAdjustmentRecord> existing = current.getAdjustments();

            for (BalanceAdjustmentRecord r : existing) {
                if (r.getRecordId().equals(recordId)) {
                    throw new UnsupportedOperationException(
                            "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ cập nhật bản ghi đã tồn tại.");
                }
            }

            List<BalanceAdjustmentRecord> updated = new ArrayList<>(existing.size() + 1);
            updated.addAll(existing);
            updated.add(entity);

            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    current.getNeedGroups(),
                    current.getCategories(),
                    current.getTransactions(),
                    updated);
            dataStore.save(next);
            return entity;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã bản ghi kiểm kê không được để null");
        throw new UnsupportedOperationException(
                "Bản ghi kiểm kê mang tính bất biến kiểm toán, không hỗ trợ thao tác xóa khỏi hệ thống.");
    }

    @Override
    public void deleteAll() {
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            BackupPayload next = new BackupPayload(
                    current.getWallets(),
                    current.getNeedGroups(),
                    current.getCategories(),
                    current.getTransactions(),
                    Collections.emptyList());
            dataStore.save(next);
        }
    }
    // endregion

    // region Queries
    @Override
    public Optional<BalanceAdjustmentRecord> findById(UUID id) {
        Objects.requireNonNull(id, "Mã bản ghi kiểm kê không được để null");
        return dataStore.getPayload().getAdjustments().stream()
                .filter(r -> r.getRecordId().equals(id))
                .findFirst();
    }

    @Override
    public List<BalanceAdjustmentRecord> findAll() {
        return dataStore.getPayload().getAdjustments();
    }

    @Override
    public boolean existsById(UUID id) {
        Objects.requireNonNull(id, "Mã bản ghi kiểm kê không được để null");
        return dataStore.getPayload().getAdjustments().stream()
                .anyMatch(r -> r.getRecordId().equals(id));
    }

    @Override
    public List<BalanceAdjustmentRecord> findByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        List<BalanceAdjustmentRecord> all = dataStore.getPayload().getAdjustments();

        List<IndexedRecord> matched = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            BalanceAdjustmentRecord record = all.get(i);
            if (record.getWalletId().equals(walletId)) {
                matched.add(new IndexedRecord(record, i));
            }
        }

        matched.sort((r1, r2) -> {
            int dateCompare = r2.record.getAdjustedAt().compareTo(r1.record.getAdjustedAt());
            if (dateCompare != 0) {
                return dateCompare;
            }
            return Integer.compare(r2.index, r1.index);
        });

        return matched.stream().map(ir -> ir.record).toList();
    }

    @Override
    public Optional<BalanceAdjustmentRecord> findLatestByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Mã ví kiểm kê không được để null");
        List<BalanceAdjustmentRecord> list = findByWalletId(walletId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
    // endregion

    // region Helper Classes
    private record IndexedRecord(BalanceAdjustmentRecord record, int index) {
    }
    // endregion
}
