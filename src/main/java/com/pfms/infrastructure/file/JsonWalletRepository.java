package com.pfms.infrastructure.file;

import com.pfms.domain.model.common.EntityStatus;
import com.pfms.domain.model.data.BackupPayload;
import com.pfms.domain.model.wallet.Wallet;
import com.pfms.domain.repository.WalletRepository;
import com.pfms.infrastructure.file.support.JsonDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Cài đặt kho lưu trữ tệp JSON cho thực thể Ví tiền ({@link WalletRepository}).
 * Thao tác đồng bộ dữ liệu ví bền vững qua {@link JsonDataStore} theo chuẩn OOD 4.2.1.
 */
public class JsonWalletRepository implements WalletRepository {

    // region State
    private final JsonDataStore dataStore;
    // endregion

    // region Constructors
    public JsonWalletRepository(JsonDataStore dataStore) {
        this.dataStore = Objects.requireNonNull(dataStore, "JsonDataStore không được để null");
    }
    // endregion

    // region Commands
    @Override
    public Wallet save(Wallet entity) {
        Objects.requireNonNull(entity, "Thực thể ví không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Wallet> existing = current.getWallets();
            List<Wallet> updated = new ArrayList<>(existing.size() + 1);
            boolean replaced = false;

            for (Wallet w : existing) {
                if (w.getId().equals(entity.getId())) {
                    updated.add(entity);
                    replaced = true;
                } else {
                    updated.add(w);
                }
            }

            if (!replaced) {
                updated.add(entity);
            }

            BackupPayload next = new BackupPayload(
                    updated,
                    current.getNeedGroups(),
                    current.getCategories(),
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
            return entity;
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Objects.requireNonNull(id, "Mã ví không được để null");
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            List<Wallet> existing = current.getWallets();
            List<Wallet> updated = new ArrayList<>(existing.size());
            boolean removed = false;

            for (Wallet w : existing) {
                if (w.getId().equals(id)) {
                    removed = true;
                } else {
                    updated.add(w);
                }
            }

            if (removed) {
                BackupPayload next = new BackupPayload(
                        updated,
                        current.getNeedGroups(),
                        current.getCategories(),
                        current.getTransactions(),
                        current.getAdjustments());
                dataStore.save(next);
            }
            return removed;
        }
    }

    @Override
    public void deleteAll() {
        synchronized (dataStore) {
            BackupPayload current = dataStore.getPayload();
            BackupPayload next = new BackupPayload(
                    Collections.emptyList(),
                    current.getNeedGroups(),
                    current.getCategories(),
                    current.getTransactions(),
                    current.getAdjustments());
            dataStore.save(next);
        }
    }
    // endregion

    // region Queries
    @Override
    public Optional<Wallet> findById(UUID id) {
        Objects.requireNonNull(id, "Mã ví không được để null");
        return dataStore.getPayload().getWallets().stream()
                .filter(w -> w.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Wallet> findAll() {
        return dataStore.getPayload().getWallets();
    }

    @Override
    public boolean existsById(UUID id) {
        Objects.requireNonNull(id, "Mã ví không được để null");
        return dataStore.getPayload().getWallets().stream()
                .anyMatch(w -> w.getId().equals(id));
    }

    @Override
    public Optional<Wallet> findByName(String name) {
        Objects.requireNonNull(name, "Tên ví không được để null");
        String trimmed = name.trim();
        return dataStore.getPayload().getWallets().stream()
                .filter(w -> w.getStatus() != EntityStatus.DELETED
                        && w.getName().equalsIgnoreCase(trimmed))
                .findFirst();
    }

    @Override
    public boolean existsByName(String name) {
        Objects.requireNonNull(name, "Tên ví không được để null");
        String trimmed = name.trim();
        return dataStore.getPayload().getWallets().stream()
                .anyMatch(w -> w.getStatus() != EntityStatus.DELETED
                        && w.getName().equalsIgnoreCase(trimmed));
    }

    @Override
    public List<Wallet> findByStatus(EntityStatus status) {
        Objects.requireNonNull(status, "Trạng thái ví không được để null");
        return dataStore.getPayload().getWallets().stream()
                .filter(w -> w.getStatus() == status)
                .toList();
    }

    @Override
    public long countByStatus(EntityStatus status) {
        Objects.requireNonNull(status, "Trạng thái ví không được để null");
        return dataStore.getPayload().getWallets().stream()
                .filter(w -> w.getStatus() == status)
                .count();
    }

    @Override
    public Optional<Wallet> findDefaultWallet() {
        return dataStore.getPayload().getWallets().stream()
                .filter(w -> w.getStatus() != EntityStatus.DELETED
                        && w.isDefaultWallet())
                .findFirst();
    }
    // endregion
}
